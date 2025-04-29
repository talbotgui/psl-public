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
package com.github.talbotgui.psl.socle.commun.securite;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;
import com.github.talbotgui.psl.socle.commun.utils.ChiffrementUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

/**
 * Classe de manipulation du token JWT : création, lecture et validation.
 *
 * @see https://www.javainuse.com/spring/boot-jwt
 */
@Service
public class JwtServiceImpl implements JwtService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

	/**
	 * Méthode de génération d'un token
	 *
	 * @param userDetails           Détails de l'utilisateur.
	 * @param secretAutiliser       Secret à utiliser
	 * @param dureeValiditeDemandee Durée de validité (en secondes)
	 * @param claims                Données supplémentaires lisibles.
	 * @param donneesAchiffrer      Données à chiffrer.
	 * @return
	 */
	private static String genererNouveauToken(UserDetails userDetails, String secretAutiliser, long dureeValiditeDemandee, Map<String, Object> claims,
			Map<String, Object> donneesAchiffrer) {

		// Vérification qu'un secret est bien passé
		if (ObjectUtils.isEmpty(secretAutiliser)) {
			LOGGER.warn("Paramètre 'secretAutiliser' nécessaire");
			// Erreur de sécurité donc pas de détails dans les données renvoyées en sortie de l'appel (le log suffit)
			throw new CommunException(CommunException.ERREUR_GENERIQUE);
		}

		// Au cas où et pour simplifier le reste du code plus bas
		if (donneesAchiffrer == null) {
			donneesAchiffrer = new HashMap<>();
		}

		// Copie de la map
		Map<String, Object> claimsAutiliser = new HashMap<>();

		// Copie des claims en clair dans la liste
		if (claims != null) {
			claimsAutiliser.putAll(claims);
		}

		// Pour une authentification par mot de passe, ajout du MDP dans les claims à chiffrer
		if (StringUtils.hasLength(userDetails.getPassword())) {
			donneesAchiffrer.put(CLEF_CLAIMS_MOT_DE_PASSE, userDetails.getPassword());
		}

		// Chiffremement des données à chiffrer puis insertion dans les claims
		if (donneesAchiffrer != null) {
			for (Map.Entry<String, Object> entree : donneesAchiffrer.entrySet()) {
				if (entree.getValue() != null) {
					String valeurChiffree = ChiffrementUtils.chiffrerChaineDeCaracteres(entree.getValue().toString(), secretAutiliser);
					claimsAutiliser.put(entree.getKey(), valeurChiffree);
				}
			}
		}

		// Chiffrement des groupes et intégration dans les claims
		if (userDetails.getAuthorities() != null) {
			String groupes = String.join(",", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
			String valeurChiffree = ChiffrementUtils.chiffrerChaineDeCaracteres(groupes, secretAutiliser);
			claimsAutiliser.put(CLEF_CLAIMS_AUTHORITIES, valeurChiffree);
		} else {
			// Pour bloquer les petits malins tentant d'ajouter ce CLAIMS dans les données en clair ou chiffrées
			claimsAutiliser.remove(CLEF_CLAIMS_AUTHORITIES);
		}

		return Jwts.builder()
				// Données spécifiques lisibles en décodant le token (ce n'est que du base64)
				.claims(claimsAutiliser)
				// Subject
				.subject(userDetails.getUsername())
				// Date de création
				.issuedAt(new Date(System.currentTimeMillis()))
				// Date d'expiration
				.expiration(new Date(System.currentTimeMillis() + dureeValiditeDemandee * 1000))
				// Signature du token avec le secret
				// à conserver cohérent avec la méthode extraireTousLesClaimsDuToken
				.signWith(Keys.hmacShaKeyFor(secretAutiliser.getBytes(StandardCharsets.UTF_8)))
				// Génération
				.compact();
	}

	/**
	 * Méthode de génération d'un token
	 *
	 * @param userDetails           Détails de l'utilisateur.
	 * @param secretDemande         Secret à utiliser
	 * @param dureeValiditeDemandee Durée de validité
	 * @param donneesAchiffrer      Données à chiffrer.
	 * @return
	 */
	public static String genererNouveauTokenPourLesTestsUniquement(UserDetails userDetails, String secretDemande, long dureeValiditeDemandee,
			Map<String, Object> donneesAchiffrer) {
		return genererNouveauToken(userDetails, secretDemande, dureeValiditeDemandee, null, donneesAchiffrer);
	}

	/** Durée de validité du token (en secondes). */
	@Value("${jwt.dureeValidite}")
	private long dureeValidite;

	@Value("${jwt.secretJWT}")
	private String secretJWT;

	@Value("${jwt.secretTokenTemporaire}")
	private String secretTokenTemporaire;

	@Override
	public String extraireClaimDuToken(String token, String clefClaim) {
		// Au cas où
		if (token == null) {
			return null;
		}

		// Lecture des claims du token
		Claims claims = this.extraireClaimsDuToken(token, this.secretJWT);

		// Récupération de la valeur demandée
		String valeur = null;
		if (claims != null) {
			valeur = claims.get(clefClaim) != null ? claims.get(clefClaim).toString() : null;

			// Déchiffrement si nécessaire
			if (valeur != null && ChiffrementUtils.verifierChaineEstChiffree(valeur)) {
				valeur = ChiffrementUtils.dechiffrerChaineDeCaracteres(valeur, this.secretJWT);
			}
		}

		return valeur;

	}

	/**
	 * Suppression du BEARER éventuel au début de la chaine de caractères puis décodage complet avec le secret fourni en paramètre.
	 *
	 * /!\ SANS DECHIFFREMENT DES VALEURS DE CLAIMS !!
	 *
	 *
	 * @param token  Token à traiter.
	 * @param secret Secret à utiliser.
	 * @return Claims du token (encore chiffrés pour certains).
	 */
	private Claims extraireClaimsDuToken(String token, String secret) {
		// Si le token commence par "Bearer ", on retire cette chaine
		if (token.toUpperCase().startsWith(JwtSecuriteFilter.BEARER)) {
			token = token.substring(JwtSecuriteFilter.BEARER.length());
		}

		// Le mode de signature ici doit être cohérent avec la méthode genererNouveauToken
		try {
			return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build()//
					.parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException e) {
			// Pour ne pas générer de stacktrace à chaque appel d'API avec un token expiré
			LOGGER.warn("Erreur de lecture d'un token - le token est expiré : {}", e.getMessage());
			return null;
		} catch (Exception e) {
			LOGGER.warn("Erreur de lecture d'un token", e);
			return null;
		}
	}

	@Override
	public Claims extraireEtDechiffrerClaimsDuToken(String token) {
		return this.extraireEtDechiffrerClaimsDuToken(token, this.secretJWT);
	}

	/**
	 * Déchiffrer le token et en extraire tous les claims.
	 *
	 * @param token  Token à traiter.
	 * @param secret Secret à utiliser.
	 * @return Valeurs obtenues.
	 */
	private Claims extraireEtDechiffrerClaimsDuToken(String token, String secret) {
		// Au cas où
		if (token == null) {
			return null;
		}

		// Lecture des claims du token
		Claims claims = this.extraireClaimsDuToken(token, secret);

		// Certaines données peuvent être chiffrées, on les déchiffre (Claims est immutable donc copie des données)
		if (claims != null) {
			Map<String, Object> copie = new HashMap<>(claims);
			for (Map.Entry<String, Object> entree : copie.entrySet()) {
				if (entree.getValue() != null && ChiffrementUtils.verifierChaineEstChiffree(entree.getValue().toString())) {
					String valeurDechiffree = ChiffrementUtils.dechiffrerChaineDeCaracteres(entree.getValue().toString(), secret);
					copie.put(entree.getKey(), valeurDechiffree);
				}
			}
			claims = new DefaultClaims(copie);
		}

		return claims;
	}

	@Override
	public String genererClefTemporaireSurTokenJwt(String tokenJwt, String clefUnique, Long dureeValiditeDemandee,
			Map<String, String> donneesSupplementaires) {

		// Lecture des claims du token
		Claims claimsActuels = this.extraireClaimsDuToken(tokenJwt, this.secretJWT);
		if (claimsActuels == null) {
			return null;
		}

		// Création d'une nouvelle liste de claims avec les données actuelles
		Map<String, Object> claims = new HashMap<>();

		// sans les données chiffrées du token
		claimsActuels.entrySet().forEach(e -> {
			if (e.getValue() != null && !ChiffrementUtils.verifierChaineEstChiffree(e.getValue().toString())) {
				claims.put(e.getKey(), e.getValue());
			}
		});

		// Création des claims à chiffrer
		Map<String, Object> claimsAchiffrer = new HashMap<>();
		claimsAchiffrer.put(CLEF_CLAIMS_VALEUR_UNIQUE, clefUnique);

		// Extraction du mail
		User userDetails = new User(claimsActuels.getSubject(), "", new ArrayList<>());

		// Recréation d'un token
		return genererNouveauToken(userDetails, this.secretTokenTemporaire, dureeValiditeDemandee, claims, claimsAchiffrer);
	}

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails Détails de l'utilisateur.
	 * @return Token généré.
	 */
	@Override
	public String genererNouveauToken(UserDetails userDetails) {
		return genererNouveauToken(userDetails, this.secretJWT, this.dureeValidite, null, null);
	}

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails Détails de l'utilisateur.
	 * @param claims      Données supplémentaires lisibles.
	 * @return Token généré.
	 */
	@Override
	public String genererNouveauToken(UserDetails userDetails, Map<String, Object> claims) {
		return genererNouveauToken(userDetails, this.secretJWT, this.dureeValidite, claims, null);
	}

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails      Détails de l'utilisateur.
	 * @param claims           Données supplémentaires lisibles.
	 * @param donneesAchiffrer Données à chiffrer.
	 * @return Token généré.
	 */
	@Override
	public String genererNouveauToken(UserDetails userDetails, Map<String, Object> claims, Map<String, Object> donneesAchiffrer) {
		return genererNouveauToken(userDetails, this.secretJWT, this.dureeValidite, claims, donneesAchiffrer);
	}

	/**
	 * Génération d'un token.
	 *
	 * @return Token généré.
	 */
	@Override
	public String genererNouveauTokenAnonyme() {
		return this.genererNouveauToken(USER_ANONYME);
	}

	/**
	 * Validation du token vis-à-vis des informations fournies.
	 *
	 * @param token Token à valider.
	 * @return null si le token est mauvais/expiré/... Sinon renvoi des données.
	 */
	@Override
	public Claims validerClefTemporaireSurTokenJwt(String token) {
		return this.validerToken(token, this.secretTokenTemporaire);
	}

	/**
	 * Validation du token vis-à-vis des informations fournies.
	 *
	 * @param token Token à valider.
	 * @return null si le token est mauvais/expiré/... Sinon renvoi des données.
	 */
	@Override
	public Claims validerToken(String token) {
		return this.validerToken(token, this.secretJWT);
	}

	private Claims validerToken(String token, String secretAutiliser) {
		// Validation et lecture
		try {
			Claims claims = this.extraireEtDechiffrerClaimsDuToken(token, secretAutiliser);

			// Si le token est expiré
			if (claims == null || claims.getExpiration().before(new Date())) {
				return null;
			}

			// Sinon renvoi des données
			return claims;
		}
		// en cas d'erreur, simplement renvoyé NULL
		catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			// Message purement informatif. Donc pas de stackTrace
			LOGGER.info("Erreur de validation du tokenPSL : {}", e.getMessage());
			return null;
		}
	}
}
