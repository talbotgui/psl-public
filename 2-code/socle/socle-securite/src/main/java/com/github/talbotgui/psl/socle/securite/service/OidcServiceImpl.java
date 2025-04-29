/*
This file is part of the talbotgui/psl project.
Authors: talbotgui.

This program is offered under a commercial and under the AGPL license.
For commercial licensing, contact me at talbotgui@gmail.com.
For AGPL licensing, see below.

AGPL licensing:
This program is free software: you can redistribute it and/or modify 
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

AGPL license is available in LICENSE.md file and https://www.gnu.org/licenses/#AGPL
 */
package com.github.talbotgui.psl.socle.securite.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.oidc.OidcClient;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteCreationTokenOidcDto;
import com.github.talbotgui.psl.socle.securite.client.SpClient;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpCompteDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;
import com.github.talbotgui.psl.socle.securite.exception.SecuriteException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.DateFormats;

@Service
public class OidcServiceImpl implements OidcService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OidcServiceImpl.class);

	/** Pattern vérifiant les caractères autorisés dans un mot de passe. */
	private static final String PATTERN_MOT_DE_PASSE_GLOBAL = "^[a-zA-Z0-9 \\.!#$%&'\"*+\\/=?^_`{|}~\\-@]*$";

	/** Pattern vérifiant la présence d'au moins un caractère de chaque type */
	// /!\ à garder cohérent avec PATTERN_MOT_DE_PASSE_GLOBAL
	private static final List<String> PATTERNS_CRITERES_MOT_DE_PASSE = Arrays.asList("^.*[a-z].*$", "^.*[A-Z].*$", "^.*[0-9].*$",
			"^.*[ \\.!#$%&'\"*+\\/=?^_`{|}~\\-@].*$");

	/**
	 * Regex de validation des nom d'utilisateur. (https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address)
	 */
	// Cette regex est dupliquée ici car son besoin est très précis et, si une modification est à faire, il ne faut peut-être pas modifié RegexUtils.
	private static final String REGEX_VALIDATION_EMAIL = RegexUtils.EMAIL;

	/** Paramètre OIDC. */
	private final String clientId;

	/** Paramètre OIDC. */
	private final String clientSecret;

	private final JwtService jwtService;
	private final OidcClient oidcClient;
	private final SpClient spClient;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public OidcServiceImpl(@Value("${oidc.clientId}") String clientId, @Value("${oidc.clientSecret}") String clientSecret, JwtService jwtService,
			OidcClient oidcClient, SpClient spClient) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.jwtService = jwtService;
		this.oidcClient = oidcClient;
		this.spClient = spClient;

		// Message d'avertissement concernant les logs TRACE de cette classe.
		if (LOGGER.isTraceEnabled()) {
			LOGGER.warn("Le niveau de log TRACE pour cette classe ne doit EN AUCUN CAS étre actif en production !! (données sensibles/personnelles)");
		}
	}

	/**
	 * Chargement des données de l'usager et du compte (2 requêtes) pour vérifier que le token est bon.
	 *
	 * @param accessTokenSP AccessToken SP.
	 * @return Les données de l'usager et du compte dans un même DTO.
	 */
	private InformationSpUsagerDto chargerDonneesUsagerEtCompte(String accessTokenSP) {

		// Recherche des données de l'usager connecté
		InformationSpUsagerDto donneesUsager;
		try {
			donneesUsager = this.spClient.chargerDonneesPersonnelles(this.clientId, this.clientSecret, accessTokenSP);
		} catch (Exception e) {
			// Ce log DEBUG pour disposer d'un moyen d'obtenir la stacktraced
			LOGGER.debug("Erreur à la recherche des données de l'usager connecté", e);
			LOGGER.warn("Erreur de chargement des données personnelles : {}", e.getMessage());
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "cduec1");
		}

		// Si les données retournées sont insuffisantes
		if ((donneesUsager == null) || !StringUtils.hasLength(donneesUsager.getEmail())) {
			LOGGER.warn("Erreur de chargement des données personnelles : les données sont incomplètes");
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "cduec2");
		}

		try {
			// Recherche des données du compte connecté
			InformationSpCompteDto donneesCompte = this.spClient.chargerDonneesCompte(this.clientId, this.clientSecret, accessTokenSP);

			// Copie des données obtenues dans les données de l'usager
			donneesUsager.setAccountType(donneesCompte.getAccountType());
			donneesUsager.setFranceConnect(donneesCompte.getFranceConnect());
			donneesUsager.setEmailTechnique(donneesCompte.getEmail());
			donneesUsager.setUuidSp(donneesCompte.getSub());

		} catch (Exception e) {
			// Ce log DEBUG pour disposer d'un moyen d'obtenir la stacktrace
			LOGGER.debug("Erreur à la recherche des données du compte connecté", e);
			LOGGER.warn("Erreur de chargement des données du compte : {}", e.getMessage());
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "cduec3");
		}
		return donneesUsager;
	}

	@Override
	public InformationSpUsagerDto chargerDonneesUsagerEtCompteAvecUnTokenPsl(String tokenPSL) {
		// Validation et lecture du token
		Claims claimsPsl = this.jwtService.validerToken(tokenPSL);
		if (claimsPsl == null) {
			LOGGER.warn("Token PSL invalide");
			throw new SecuriteException(SecuriteException.DONNEES_USAGER_INDISPONIBLES, "1");
		}

		// Lecture des claims
		String emailDansSubject = claimsPsl.getSubject();
		String accessTokenOidcExistant = claimsPsl.get(JwtService.CLEF_CLAIMS_ACCESS_TOKEN_OIDC, String.class);

		// Si le token PSL est anonyme, on renvoi une donnée fixe
		if (JwtService.EMAIL_UTILISATEUR_ANONYMOUS.equals(emailDansSubject)) {
			return new InformationSpUsagerDto(JwtService.EMAIL_UTILISATEUR_ANONYMOUS, "", "");
		}

		// Sinon, chargement des données depuis le fournisseur OIDC
		else {
			return this.chargerDonneesUsagerEtCompte(accessTokenOidcExistant);
		}
	}

	/**
	 * Méthode créant un token auprès du fournisseur OIDC à partir des informations échangées entre l'application FRONT et la page de connexion du
	 * fournisseur OIDC.
	 *
	 * Ce token est ensuite encapsulé dans un token PSL.
	 *
	 * @param requete Requête TOKEN OIDC
	 * @return un token PSL
	 */
	private ReponseTokenOIDC creerLeToken(RequeteCreationTokenOidcDto requete) {

		// Appel au fournisseur OIDC
		ReponseTokenOIDC reponseOidc;
		try {
			reponseOidc = this.oidcClient.creerAccessToken(requete.grantType(), requete.code(), requete.redirectUri(), requete.codeVerifier(),
					this.clientId, this.clientSecret, requete.refreshToken());
		} catch (Exception e) {
			// Ce log DEBUG pour disposer d'un moyen d'obtenir la stacktrace
			LOGGER.warn("Erreur de création du token OIDC", e);
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "c1");
		}

		// Validation de la réponse obtenue
		if ((reponseOidc == null) || !StringUtils.hasLength(reponseOidc.accessToken()) || !StringUtils.hasLength(reponseOidc.refreshToken())) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "c2");
		}

		// Chargement des données de l'usager et du compte (2 requêtes) pour vérifier que le token est bon
		InformationSpUsagerDto donneesUsager = this.chargerDonneesUsagerEtCompte(reponseOidc.accessToken());

		// Création du token PSL à partir du token OIDC
		return this.creerTokenPslApartirDuTokenOIDC(reponseOidc, donneesUsager);
	}

	@Override
	public ReponseTokenOIDC creerOuRaffrachirLeToken(RequeteCreationTokenOidcDto requete) {

		// Pour une création de token
		if (!"refresh_token".equals(requete.grantType())) {
			return this.creerLeToken(requete);
		} else {
			return this.raffraichirLeToken(requete);
		}
	}

	/**
	 * Création d'un token PSL à partir des informations fournies.
	 *
	 * @param userDetails Détails de l'utilisateur connecté.
	 * @return Token PSL.
	 */
	private ReponseJwtDto creerTokenPsl(UserDetails userDetails) {

		// Création d'un token
		String token = this.jwtService.genererNouveauToken(userDetails);

		// Renvoi du JWT
		return new ReponseJwtDto(token);
	}

	/**
	 * Création d'un token PSL à partir des données obtenues du fournisseur OIDC.
	 *
	 * @param reponseOidc   Réponse de création/refresh de token OIDC.
	 * @param donneesUsager Données de l'usager SP.
	 * @return Un token PSL complet contenant les tokens OIDC.
	 */
	private ReponseTokenOIDC creerTokenPslApartirDuTokenOIDC(ReponseTokenOIDC reponseOidc, InformationSpUsagerDto donneesUsager) {
		// Calcul de la date de péremption de l'accessToken OIDC
		Date dateExpirationRefreshOidcExistant = new Date(new Date().getTime() + (Integer.parseInt(reponseOidc.expiresIn()) * 1000));
		Date dateExpirationAccessOidcExistant = new Date(new Date().getTime() + (Integer.parseInt(reponseOidc.refreshExpiresIn()) * 1000));

		// Extraction du mail venant de la partie publique de l'accessToken
		String emailSP = this.extraireEmailDeLaPartiePubliqueDuToken(reponseOidc.accessToken());

		// Création de la liste des données à mettre en clair dans le token
		Map<String, Object> claims = new HashMap<>(Map.of(//
				Claims.SUBJECT, emailSP, //
				JwtService.CLEF_CLAIMS_FC, donneesUsager.getFranceConnect(), //
				JwtService.CLEF_CLAIMS_ACCOUNT_TYPE, donneesUsager.getAccountType()//
		));

		// Création de la liste des données à chiffrer dans le token
		Map<String, Object> donneesAchiffrer = Map.of(//
				JwtService.CLEF_CLAIMS_UUID_SP, donneesUsager.getUuidSp(), //
				JwtService.CLEF_CLAIMS_ACCESS_TOKEN_OIDC, reponseOidc.accessToken(), //
				JwtService.CLEF_CLAIMS_REFRESH_TOKEN_OIDC, reponseOidc.refreshToken(), //
				JwtService.CLEF_CLAIMS_EXPIRATION_ACCESSTOKEN_OIDC, DateFormats.formatIso8601(dateExpirationAccessOidcExistant), //
				JwtService.CLEF_CLAIMS_EXPIRATION_REFRESHTOKEN_OIDC, DateFormats.formatIso8601(dateExpirationRefreshOidcExistant)//
		);

		// Recréation d'un nouveau token PSL
		UserDetails utilisateur = new User(emailSP, "", new ArrayList<>());
		String nouveauToken = this.jwtService.genererNouveauToken(utilisateur, claims, donneesAchiffrer);

		// Renvoi du token PSL contenant :
		// - les réelles dates d'expiration (access et refresh)
		// - le token custom PSL dans l'accessToken (pour être utilisé à chaque appel d'API).
		// - le token custom PSL dans le refreshToken (pour être utilisé au raffraichissement du token PSL).
		// - l'ID token n'est pas renvoyé pour ne pas divulguer d'informations.
		// - scope et state sont déjà connus de l'application FRONT.
		return new ReponseTokenOIDC(nouveauToken, reponseOidc.expiresIn(), reponseOidc.refreshExpiresIn(), nouveauToken, "PSL", "",
				reponseOidc.sessionState(), reponseOidc.scope());
	}

	/**
	 * Méthode extrayant le contenu de l'attribut 'email' de la partie publique du token.
	 *
	 * @param token Token à lire.
	 * @return Email trouvé (null si rien)
	 */
	private String extraireEmailDeLaPartiePubliqueDuToken(String token) {

		// Découpage du token
		String[] tokenDecoupe = token.split("\\.");
		if (tokenDecoupe.length != 3) {
			LOGGER.warn("Token mal formaté");
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "e1");
		}

		// Décodage de la partie publique
		String partiePubliqueTokenDecode = new String(Base64.getDecoder().decode(tokenDecoupe[1].getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8);

		// Parse du Json
		Map<String, Object> resultat;
		try {
			resultat = new ObjectMapper().readValue(partiePubliqueTokenDecode, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonProcessingException e) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "e2");
		}

		// Vérification du contenu des données
		Object email = resultat.get("email");
		if (email == null) {
			LOGGER.warn("Pas d'email dans le JSON de la partie publique du token");
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "e3");
		}

		// Renvoi de l'email
		return resultat.get("email").toString();
	}

	/**
	 * Méthode créant un token auprès du fournisseur OIDC à partir des informations échangées entre l'application FRONT et la page de connexion du
	 * fournisseur OIDC.
	 *
	 * Ce token est ensuite encapsulé dans un token PSL.
	 *
	 * @param requete Requête TOKEN OIDC
	 * @return un token PSL
	 */
	private ReponseTokenOIDC raffraichirLeToken(RequeteCreationTokenOidcDto requete) {
		String tokenPSL = requete.refreshToken();

		// Validation et lecture du token
		Claims claimsPsl = this.jwtService.validerToken(tokenPSL);
		if (claimsPsl == null) {
			LOGGER.warn("Token PSL invalide");
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "r1");
		}

		// Lecture des claims
		String emailUtilisateur = claimsPsl.getSubject();
		String refreshTokenOidcExistant = claimsPsl.get(JwtService.CLEF_CLAIMS_REFRESH_TOKEN_OIDC, String.class);
		String motDePasseEventuel = claimsPsl.get(JwtService.CLEF_CLAIMS_MOT_DE_PASSE, String.class);
		Date dateExpirationAccessOidcExistant = claimsPsl.get(JwtService.CLEF_CLAIMS_EXPIRATION_ACCESSTOKEN_OIDC, Date.class);
		Date dateExpirationRefreshOidcExistant = claimsPsl.get(JwtService.CLEF_CLAIMS_EXPIRATION_REFRESHTOKEN_OIDC, Date.class);

		// Si le token est anonyme, il est raffraichit
		if (StringUtils.hasLength(emailUtilisateur) && JwtService.EMAIL_UTILISATEUR_ANONYMOUS.equals(emailUtilisateur)) {
			ReponseJwtDto tokenAnonyme = this.sauthentifierEnAnonyme();
			return new ReponseTokenOIDC(tokenAnonyme.token(), null, null, tokenAnonyme.token(), null, null, null, null);
		}

		// Si le token est issu d'une authentification par nom d'utilisateur et mot de passe
		if (StringUtils.hasLength(motDePasseEventuel)) {
			ReponseJwtDto tokenAnonyme = this.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(emailUtilisateur, motDePasseEventuel);
			return new ReponseTokenOIDC(tokenAnonyme.token(), null, null, tokenAnonyme.token(), null, null, null, null);
		}

		// Validation des données du token
		if (!StringUtils.hasLength(refreshTokenOidcExistant) || (dateExpirationAccessOidcExistant == null)
				|| (dateExpirationRefreshOidcExistant == null)) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "r2");
		}
		if (dateExpirationRefreshOidcExistant.before(new Date())) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "r3");
		}

		// Raffraichissement du token
		ReponseTokenOIDC reponseOidc;
		try {
			reponseOidc = this.oidcClient.creerAccessToken(requete.grantType(), requete.code(), requete.redirectUri(), requete.codeVerifier(),
					this.clientId, this.clientSecret, refreshTokenOidcExistant);
			LOGGER.trace("RefreshToken réalisé : {}", reponseOidc);
		} catch (Exception e) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, e, "r4");
		}

		if ((reponseOidc == null) || !StringUtils.hasLength(reponseOidc.accessToken()) || !StringUtils.hasLength(reponseOidc.refreshToken())) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "r5");
		}

		// Extraction de quelques données des tokens OIDC obtenus
		String emailAccessTokenSP = this.extraireEmailDeLaPartiePubliqueDuToken(reponseOidc.accessToken());
		String emailIdTokenSP = this.extraireEmailDeLaPartiePubliqueDuToken(reponseOidc.idToken());

		// Validation cohérence des données dans le token PSL vis-à-vis des données dans les tokens OIDC
		if ((emailUtilisateur == null) || !emailAccessTokenSP.equals(emailUtilisateur) || !emailIdTokenSP.equals(emailUtilisateur)) {
			throw new SecuriteException(SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE, "r6");
		}

		// Chargement des données de l'usager et du compte (2 requêtes) pour vérifier que le token est bon
		InformationSpUsagerDto donneesUsager = this.chargerDonneesUsagerEtCompte(reponseOidc.accessToken());

		// Création du token PSL à partir du token OIDC
		return this.creerTokenPslApartirDuTokenOIDC(reponseOidc, donneesUsager);
	}

	@Override
	public ReponseJwtDto sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(String nomUtilisateur, String motDePasse) {

		// Validation de la présence des données
		if (!StringUtils.hasLength(nomUtilisateur)) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "1");
		}
		if (!StringUtils.hasLength(motDePasse)) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "3");
		}

		// Trim systématique du nom d'utilisateur pour éviter les invalidations à cause d'un simple espace
		nomUtilisateur = nomUtilisateur.trim();

		// Validation des contenus
		if (!Pattern.matches(REGEX_VALIDATION_EMAIL, nomUtilisateur)) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "2");
		}
		this.validerMotDePasse(motDePasse);

		// Uppercase systématique du nom d'utilisateur pour être insensible à la CASSE à l'usage du login
		nomUtilisateur = nomUtilisateur.trim().toUpperCase();

		// Création et renvoi du token
		return this.creerTokenPsl(new User(nomUtilisateur, motDePasse, new ArrayList<>()));
	}

	@Override
	public ReponseJwtDto sauthentifierEnAnonyme() {
		return this.creerTokenPsl(JwtService.USER_ANONYME);
	}

	/**
	 * Validation du mot de passe pour avoir au moins 8 caractères de long, sans caractères tordus (cf. PATTERN_MOT_DE_PASSE) et 3 critères parmi :
	 * <ul>
	 * <li>un chiffre</li>
	 * <li>un caractère spécial</li>
	 * <li>une majuscule</li>
	 * <li>une minuscule</li>
	 * </ul>
	 *
	 * @param motDePasse
	 */
	private void validerMotDePasse(String motDePasse) {
		// Contrôle de longueur
		if (motDePasse.length() < 8) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "4-1");
		}

		// Contrôle des caractères interdits
		if (!Pattern.matches(PATTERN_MOT_DE_PASSE_GLOBAL, motDePasse)) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "4-2");
		}

		// Contrôle des critères
		long nbCriteresRespectes = PATTERNS_CRITERES_MOT_DE_PASSE.stream().filter(p -> Pattern.matches(p, motDePasse)).count();
		if (nbCriteresRespectes < 3) {
			throw new SecuriteException(SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE, "4-3");
		}
	}
}
