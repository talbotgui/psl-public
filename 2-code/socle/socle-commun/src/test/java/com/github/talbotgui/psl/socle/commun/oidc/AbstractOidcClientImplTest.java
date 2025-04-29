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
package com.github.talbotgui.psl.socle.commun.oidc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;

/**
 * Tests d'intégration pour les appels à Keycloak.
 */
class AbstractOidcClientImplTest {

	/** Instance de test. */
	private static OidcClient clientOidc;

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOidcClientImplTest.class);

	/** URL pour le test */
	private static final String URL_INFO_TOKEN = "https://xxx.service-public.fr/realms/service-public/protocol/openid-connect/token";

	@BeforeAll
	static void avantTouteLaClasse() {
		clientOidc = new AbstractOidcClientImpl(null, URL_INFO_TOKEN, true, "localhost", 81, null, null) {
		};
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test01CreerAccessToken() {
		/* code non testable à la main car il s'inscrit dans un échange OIDC précis et rapide. */
		// clientOidc.creerAccessToken();
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test02RaffraichirAccessToken() {
		//
		String clientId = "";
		String clientSecret = "";
		String refreshToken = "";
		//
		ReponseTokenOIDC reponse = clientOidc.raffraichirAccessToken(clientId, clientSecret, refreshToken);
		//
		Assertions.assertNotNull(reponse);
		LOGGER.info("reponse : {}", reponse);
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test02RaffraichirAccessTokenSiBesoin01tokenValide() {
		//
		String clientId = "";
		String clientSecret = "";
		String accessToken = "";
		String refreshToken = "";
		//
		String reponse = clientOidc.raffraichirAccessTokenSiNecessaire(clientId, clientSecret, accessToken, refreshToken);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(accessToken, reponse);
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test02RaffraichirAccessTokenSiBesoin02tokenExpire() {
		//
		String clientId = "";
		String clientSecret = "";
		String accessToken = "";
		String refreshToken = "";
		//
		String reponse = clientOidc.raffraichirAccessTokenSiNecessaire(clientId, clientSecret, accessToken, refreshToken);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotEquals(accessToken, reponse);
	}
}
