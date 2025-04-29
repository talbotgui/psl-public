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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.oidc.OidcClient;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteCreationTokenOidcDto;
import com.github.talbotgui.psl.socle.securite.application.SocleSecuriteApplication;
import com.github.talbotgui.psl.socle.securite.client.SpClient;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpAdresseDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpCompteDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpTelephoneDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;
import com.github.talbotgui.psl.socle.securite.client.dto.TypeDeCompteSP;
import com.github.talbotgui.psl.socle.securite.exception.SecuriteException;
import com.googlecode.catchexception.CatchException;

import io.jsonwebtoken.Claims;

// Configuration du test
@SpringBootTest(classes = SocleSecuriteApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génère qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class OidcServiceImplTest extends AbstractTest {

	private static final String ACCESS_ET_ID_TOKEN_SIMULE = "a." + new String(
			Base64.getEncoder().encode("{\"email\":\"etabage-0159@yopmail.com\"}".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8) + ".c";
	private static final InformationSpCompteDto DONNEES_COMPTE_VALIDES = new InformationSpCompteDto("etabage-0159@yopmail.com", "UUID-xxxx-yyyy-zzzz",
			TypeDeCompteSP.particulier, false);
	private static final InformationSpUsagerDto DONNEES_PERSONNELLES_INVALIDES = new InformationSpUsagerDto();
	private static final InformationSpUsagerDto DONNEES_PERSONNELLES_VALIDES = new InformationSpUsagerDto("nom", "prenom1 prenom2", 1, new Date(),
			new InformationSpAdresseDto("voie", "lieuDit", "batiment", "appartement", "Amiens", "France", "80000", "80021", "complément", "Somme"),
			"M", "80021", "12345", "Amiens", "Somme", "etabage-0159@yopmail.com", "NOM", "France", "Célibataire",
			new InformationSpTelephoneDto("0123456789", "+33"), new InformationSpTelephoneDto("0623456789", "+33"));
	private static final ReponseTokenOIDC REPONSE_TOKEN_OIDC_INVALIDE = new ReponseTokenOIDC(null, null, null, null, null, null, null, null);
	private static final ReponseTokenOIDC REPONSE_TOKEN_OIDC_VALIDE = new ReponseTokenOIDC(ACCESS_ET_ID_TOKEN_SIMULE, "2", "200000", "refreshToken",
			"tokenType", ACCESS_ET_ID_TOKEN_SIMULE, "sessionState", "lesScopes");
	private static final ReponseTokenOIDC REPONSE_TOKEN_OIDC_VALIDE_2 = new ReponseTokenOIDC(ACCESS_ET_ID_TOKEN_SIMULE, "2", "200000",
			"refreshToken2", "tokenType", ACCESS_ET_ID_TOKEN_SIMULE, "sessionState2", "lesScopes");
	private static final RequeteCreationTokenOidcDto REQUETE_CREATION_TOKEN_VALIDE = new RequeteCreationTokenOidcDto("code", "codeVerifier",
			"redirectUri", "grantType");

	/**
	 * Sauvegarde du token PSL valide créé au premier test pour être utilisé dans
	 * les test de refresh.
	 */
	private static String tokenPslValideCreeAuPremierTest;

	/**
	 * Bouchon du client d'appel OIDC présent dans le service (déclaration de
	 * l'implémentation pour que Mockito sache l'instancier).
	 */
	@MockitoBean
	private OidcClient bouchonOidcClient;

	/**
	 * Bouchon du client d'appel SP présent dans le service (déclaration de
	 * l'implémentation pour que Mockito sache l'instancier).
	 */
	@MockitoBean
	private SpClient bouchonSpClient;

	/** Paramètre OIDC. */
	@Value("${oidc.clientId}")
	private String clientId;

	/** Paramètre OIDC. */
	@Value("${oidc.clientSecret}")
	private String clientSecret;

	/** Classe utile aux vérifications. */
	@Autowired
	private JwtService jwtService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private OidcService oidcService;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	protected String secret;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		Mockito.reset(this.bouchonOidcClient, this.bouchonSpClient);
	}

	/**
	 * Méthode extrayant la partie non chiffrée du token.
	 *
	 * @param tokenPSL
	 * @return
	 */
	private Map<String, String> extrairePartiePubliqueDuTokenEtDecoder(String tokenPSL) {

		// découpage du token
		Assertions.assertNotNull(tokenPSL);
		String[] decoupage = tokenPSL.split("\\.");
		Assertions.assertEquals(3, decoupage.length);

		// Décodage depuis le base64
		String json = new String(Base64.getDecoder().decode(decoupage[1].getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

		// Parse du JSON
		Map<String, String> resultat;
		try {
			resultat = new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>() {
			});
		} catch (final JsonProcessingException e) {
			Assertions.fail(e);
			resultat = new HashMap<>();
		}

		// Renvoi du résultat
		return resultat;
	}

	@Test
	void test01creerLeToken01CaNominal() {
		//
		Mockito.doReturn(REPONSE_TOKEN_OIDC_VALIDE).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.isNull());
		Mockito.doReturn(DONNEES_PERSONNELLES_VALIDES).when(this.bouchonSpClient).chargerDonneesPersonnelles(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());
		Mockito.doReturn(DONNEES_COMPTE_VALIDES).when(this.bouchonSpClient).chargerDonneesCompte(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		ReponseTokenOIDC tokenPSL = this.oidcService.creerOuRaffrachirLeToken(REQUETE_CREATION_TOKEN_VALIDE);
		tokenPslValideCreeAuPremierTest = tokenPSL.accessToken();

		//
		Assertions.assertNotNull(tokenPSL);
		Assertions.assertEquals(tokenPSL.accessToken(), tokenPSL.refreshToken());
		Assertions.assertEquals(REPONSE_TOKEN_OIDC_VALIDE.expiresIn(), tokenPSL.expiresIn());
		Assertions.assertEquals(REPONSE_TOKEN_OIDC_VALIDE.refreshExpiresIn(), tokenPSL.refreshExpiresIn());
		Map<String, String> partiePubliqueDuToken = this.extrairePartiePubliqueDuTokenEtDecoder(tokenPSL.accessToken());
		Assertions.assertEquals(DONNEES_PERSONNELLES_VALIDES.getEmail(), partiePubliqueDuToken.get("sub"));
	}

	@Test
	void test01creerLeToken02AppelCreationTokenOIDCechoue() {
		//
		Mockito.doThrow(new RuntimeException("ko")).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.isNull());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(REQUETE_CREATION_TOKEN_VALIDE));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("c1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test01creerLeToken03AppelCreationTokenOIDCokMaisReponseInvalide() {
		//
		Mockito.doReturn(REPONSE_TOKEN_OIDC_INVALIDE).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.isNull());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(REQUETE_CREATION_TOKEN_VALIDE));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("c2", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test01creerLeToken04AppelApiDonneesPersoEchoue() {
		//
		Mockito.doReturn(REPONSE_TOKEN_OIDC_VALIDE).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.isNull());
		Mockito.doThrow(new RuntimeException("ko")).when(this.bouchonSpClient).chargerDonneesPersonnelles(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(REQUETE_CREATION_TOKEN_VALIDE));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("cduec1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test01creerLeToken05AppelApiDonneesPersoVide() {
		//
		Mockito.doReturn(REPONSE_TOKEN_OIDC_VALIDE).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.isNull());
		Mockito.doReturn(DONNEES_PERSONNELLES_INVALIDES).when(this.bouchonSpClient).chargerDonneesPersonnelles(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(REQUETE_CREATION_TOKEN_VALIDE));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("cduec2", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02raffrachirLeToken01CasNominal() {
		//
		Mockito.doReturn(REPONSE_TOKEN_OIDC_VALIDE_2).when(this.bouchonOidcClient).creerAccessToken(
				Mockito.eq(OidcClient.VALEUR_GRANT_TYPE_REFRESH_TOKEN), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
				Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.anyString());
		Mockito.doReturn(DONNEES_PERSONNELLES_VALIDES).when(this.bouchonSpClient).chargerDonneesPersonnelles(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());
		Mockito.doReturn(DONNEES_COMPTE_VALIDES).when(this.bouchonSpClient).chargerDonneesCompte(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		ReponseTokenOIDC tokenPSL = this.oidcService.creerOuRaffrachirLeToken(
				new RequeteCreationTokenOidcDto(OidcClient.VALEUR_GRANT_TYPE_REFRESH_TOKEN, tokenPslValideCreeAuPremierTest));

		//
		Assertions.assertNotNull(tokenPSL);
		Assertions.assertEquals(tokenPSL.accessToken(), tokenPSL.refreshToken());
		Assertions.assertNotEquals(tokenPslValideCreeAuPremierTest, tokenPSL.accessToken());
		Assertions.assertEquals(REPONSE_TOKEN_OIDC_VALIDE.expiresIn(), tokenPSL.expiresIn());
		Assertions.assertEquals(REPONSE_TOKEN_OIDC_VALIDE.refreshExpiresIn(), tokenPSL.refreshExpiresIn());
		Map<String, String> partiePubliqueDuToken = this.extrairePartiePubliqueDuTokenEtDecoder(tokenPSL.accessToken());
		Assertions.assertEquals(DONNEES_PERSONNELLES_VALIDES.getEmail(), partiePubliqueDuToken.get("sub"));
	}

	@Test
	void test02raffrachirLeToken02TokenPSLInvalide() {
		//

		//
		CatchException.catchException(
				() -> this.oidcService.creerOuRaffrachirLeToken(new RequeteCreationTokenOidcDto("refresh_token", "tokenPslInvalide")));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("r1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02raffrachirLeToken03AppelCreationTokenOIDCechoue() {
		//
		Mockito.doThrow(new RuntimeException("ko")).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.isNull(),
				Mockito.isNull(), Mockito.isNull(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(
				new RequeteCreationTokenOidcDto(OidcClient.VALEUR_GRANT_TYPE_REFRESH_TOKEN, tokenPslValideCreeAuPremierTest)));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("r4", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02raffrachirLeToken04AppelCreationTokenOIDCavecReponseInvalide() {
		//
		Mockito.doReturn(null).when(this.bouchonOidcClient).creerAccessToken(Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),
				Mockito.isNull(), Mockito.eq(this.clientId), Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		CatchException.catchException(() -> this.oidcService.creerOuRaffrachirLeToken(
				new RequeteCreationTokenOidcDto(OidcClient.VALEUR_GRANT_TYPE_REFRESH_TOKEN, tokenPslValideCreeAuPremierTest)));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.ACCESSTOKEN_NON_ENREGISTRABLE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("r5", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test03chargerDonneesUsagerEtCompteAvecUnTokenPsl01CasNominal() {
		//
		Mockito.doReturn(DONNEES_PERSONNELLES_VALIDES).when(this.bouchonSpClient).chargerDonneesPersonnelles(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());
		Mockito.doReturn(DONNEES_COMPTE_VALIDES).when(this.bouchonSpClient).chargerDonneesCompte(Mockito.eq(this.clientId),
				Mockito.eq(this.clientSecret), Mockito.anyString());

		//
		InformationSpUsagerDto infoUsager = this.oidcService.chargerDonneesUsagerEtCompteAvecUnTokenPsl(tokenPslValideCreeAuPremierTest);

		//
		Assertions.assertNotNull(infoUsager);
	}

	@Test
	void test03chargerDonneesUsagerEtCompteAvecUnTokenPsl02TokenInvalide() {
		//

		//
		CatchException.catchException(() -> this.oidcService.chargerDonneesUsagerEtCompteAvecUnTokenPsl("tokenPslInvalide"));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_USAGER_INDISPONIBLES),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationAnonyme() {
		//

		//
		ReponseJwtDto token = this.oidcService.sauthentifierEnAnonyme();

		//
		Assertions.assertNotNull(token);
		Claims claimsPSL = this.jwtService.validerToken(token.token());
		Assertions.assertNotNull(claimsPSL);
		Assertions.assertEquals(JwtService.USER_ANONYME.getUsername(), claimsPSL.getSubject());
	}

	@Test
	void test04authentificationParMotDePasse01casNominalLimite() {
		//
		String nomUtilisateur = "moi@exemple.com        ";
		String motDePasse = "aZ.!#$%&'\"*+/=?^_`{|}~-@";
		//
		ReponseJwtDto token = this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse);
		//
		Assertions.assertNotNull(token);
		Claims claimsPSL = this.jwtService.validerToken(token.token());
		Assertions.assertNotNull(claimsPSL);
		Assertions.assertEquals(nomUtilisateur.trim().toUpperCase(), claimsPSL.getSubject());
		Assertions.assertEquals(motDePasse, claimsPSL.get(JwtService.CLEF_CLAIMS_MOT_DE_PASSE));
	}

	@Test
	void test04authentificationParMotDePasse01casNominalSimple() {
		//
		String nomUtilisateur = "moi@exemple.com";
		String motDePasse = "UnBon motDeP4553";
		//
		ReponseJwtDto token = this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse);
		//
		Assertions.assertNotNull(token);
		Claims claimsPSL = this.jwtService.validerToken(token.token());
		Assertions.assertNotNull(claimsPSL);
		Assertions.assertEquals(nomUtilisateur.toUpperCase(), claimsPSL.getSubject());
		Assertions.assertEquals(motDePasse, claimsPSL.get(JwtService.CLEF_CLAIMS_MOT_DE_PASSE));
	}

	@Test
	void test04authentificationParMotDePasse02nomutilisateurVide() {
		//
		String nomUtilisateur = "";
		String motDePasse = "UnBon motDeP4553";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationParMotDePasse03mauvaisNomutilisateur() {
		//
		String nomUtilisateur = "moi@ exemple.com";
		String motDePasse = "UnBon motDeP4553";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("2", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationParMotDePasse04motDePasseVide() {
		//
		String nomUtilisateur = "moi@exemple.com";
		String motDePasse = "";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("3", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationParMotDePasse05mauvaisMotDePasse01Longueur() {
		//
		String nomUtilisateur = "moi@exemple.com";
		String motDePasse = "aze";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("4-1", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationParMotDePasse05mauvaisMotDePasse02patternGlobal() {
		//
		String nomUtilisateur = "moi@exemple.com";
		String motDePasse = "µµµµµµµµµ";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("4-2", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test04authentificationParMotDePasse05mauvaisMotDePasse03CriteresManquants() {
		//
		String nomUtilisateur = "moi@exemple.com";
		String motDePasse = "aze01234567";
		//
		CatchException.catchException(() -> this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(nomUtilisateur, motDePasse));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.DONNEES_AUTHENTIFICATION_INVALIDE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals("4-3", ((SecuriteException) CatchException.caughtException()).getParametres()[0]);
	}
}
