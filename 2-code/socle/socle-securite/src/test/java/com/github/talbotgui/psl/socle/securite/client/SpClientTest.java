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
package com.github.talbotgui.psl.socle.securite.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.talbotgui.psl.socle.commun.oidc.OidcClient;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.securite.application.SocleSecuriteApplication;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpCompteDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;

/** Ce test n'est pas automatisable car les tokens sont à récupérer à la main. */
//Configuration du test
@SpringBootTest(classes = SocleSecuriteApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génère qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SpClientTest extends AbstractTest {

	/** Instance du client à tester. */
	@Autowired
	private SpClient clientAtester;

	/** Paramètre OIDC. */
	@Value("${oidc.clientId}")
	private String clientId;

	/** Paramètre OIDC. */
	@Value("${oidc.clientSecret}")
	private String clientSecret;

	/** Cette variable n'est là que pour se faire injecter ses membres bouchonnés. */
	@Autowired
	private OidcClient oidcClient;

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test01creerAccessTokenDepuisDonnesDePostman() {
		//
		String grantType = "authorization_code";
		String redirectUri = "http://dev-psl.guillaumetalbot.com/mademarche";
		// Ces données sont disponibles, dans la console de Postman, sur la requête 'https://localhost:8080/socle/securite/token', après le clic sur
		// le bouton "Get new access token" dans la requête 024
		// Ces données ne doivent pas être commitées
		String code = "";
		String codeVerifier = "";
		String clientId = "";
		String clientSecret = "";
		//
		ReponseTokenOIDC reponse = this.oidcClient.creerAccessToken(grantType, code, redirectUri, codeVerifier, clientId, clientSecret, null);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotNull(reponse.accessToken());
		// Cette donnée est sensible et n'est donc pas dans les logs
		System.err.println(reponse.accessToken());
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test02ApiInformationUsager() {
		// AccessToken et refreshToken à obtenir depuis l'application Front Angular (dans la console) ou depuis le test précédent
		// Ces données ne doivent pas être commitées
		String accessToken = "";
		//
		InformationSpUsagerDto resultat = this.clientAtester.chargerDonneesPersonnelles(this.clientId, this.clientSecret, accessToken);
		//
		Assertions.assertNotNull(resultat);
		Assertions.assertNotNull(resultat.getEmail());
		// Cette donnée est sensible et n'est donc pas dans les logs
		System.err.println(resultat);
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test03ApiInformationCompte() {
		// AccessToken et refreshToken à obtenir depuis l'application Front Angular (dans la console) ou depuis le test précédent
		// Ces données ne doivent pas être commitées
		String accessToken = "";
		//
		InformationSpCompteDto resultat = this.clientAtester.chargerDonneesCompte(this.clientId, this.clientSecret, accessToken);
		//
		Assertions.assertNotNull(resultat);
		Assertions.assertNotNull(resultat.getEmail());
		// Cette donnée est sensible et n'est donc pas dans les logs
		System.err.println(resultat);
	}
}
