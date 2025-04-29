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
package com.github.talbotgui.psl.socle.commun.apiclient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.catchexception.CatchException;

@TestMethodOrder(MethodOrderer.MethodName.class)
class AbstractClientHttpTest {
	private static final String BON_PROXY = "localhost";

	private static final String CONTENU_PAGE_ATTENDU = "junit-1";

	private static final String FIN_URL = "/java/dej/index.htm";

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientHttpTest.class);

	private static final String MAUVAIS_PROXY = "serveurInexistant";

	private static final int PROXY_PORT = 81;

	private static final String URL_DE_BASE = "https://www.jmdoudoux.fr";

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
	}

	@Test
	void test01EnvironnementSansProxy01AppelSansProxy() {
		// Ce test ne peux s'exécuter correctement si le proxy est présent dans l'environnement d'exécution
		Assumptions.assumeFalse(this.testerPresenceDuProxy(), "Environnement incompatible avec ce test");

		// ARRANGE
		ClientHttpDeTest client = new ClientHttpDeTest(null, URL_DE_BASE, true);

		// ACT
		String reponse = client.executerRequeteGet(FIN_URL);

		// ASSERT
		Assertions.assertNotNull(reponse);
		Assertions.assertTrue(reponse.contains(CONTENU_PAGE_ATTENDU));
	}

	@Test
	void test01EnvironnementSansProxy02AppelAvecProxy() {
		// Ce test ne peux s'exécuter correctement si le proxy est présent dans l'environnement d'exécution
		Assumptions.assumeFalse(this.testerPresenceDuProxy(), "Environnement incompatible avec ce test");

		// ARRANGE
		ClientHttpDeTest client = new ClientHttpDeTest(null, URL_DE_BASE, true, MAUVAIS_PROXY, 80);

		// ACT
		String reponse = client.executerRequeteGet(FIN_URL);

		// ASSERT
		Assertions.assertNotNull(reponse);
		Assertions.assertTrue(reponse.contains(CONTENU_PAGE_ATTENDU));
	}

	@Test()
	void test02EnvironnementAvecProxy01AppelSansProxy() {
		// Ce test ne peux s'exécuter correctement si le proxy est présent dans l'environnement d'exécution
		Assumptions.assumeTrue(this.testerPresenceDuProxy(), "Environnement incompatible avec ce test");

		// ARRANGE
		ClientHttpDeTest client = new ClientHttpDeTest(null, URL_DE_BASE, true);

		// ACT
		CatchException.catchException(() -> client.executerRequeteGet(FIN_URL));

		// ASSERT
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), ApiClientException.ERREUR_APPEL),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(ConnectException.class, CatchException.caughtException().getCause().getClass());
	}

	@Test
	void test02EnvironnementAvecProxy02AppelAvecProxyInvalide() {
		// Ce test ne peux s'exécuter correctement si le proxy est présent dans l'environnement d'exécution
		Assumptions.assumeTrue(this.testerPresenceDuProxy(), "Environnement incompatible avec ce test");

		// ARRANGE
		ClientHttpDeTest client = new ClientHttpDeTest(null, URL_DE_BASE, true, MAUVAIS_PROXY, PROXY_PORT);

		// ACT
		CatchException.catchException(() -> client.executerRequeteGet(FIN_URL));

		// ASSERT
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), ApiClientException.ERREUR_APPEL),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(ConnectException.class, CatchException.caughtException().getCause().getClass());
	}

	@Test
	void test02EnvironnementAvecProxy02AppelAvecProxyValide() {
		// Ce test ne peux s'exécuter correctement QUE si le proxy est présent dans l'environnement d'exécution
		if (!this.testerPresenceDuProxy()) {
			LOGGER.info("Environnement incompatible avec ce test");
			return;
		}

		// ARRANGE
		ClientHttpDeTest client = new ClientHttpDeTest(null, URL_DE_BASE, true, BON_PROXY, PROXY_PORT);

		// ACT
		String reponse = client.executerRequeteGet(FIN_URL);

		// ASSERT
		Assertions.assertNotNull(reponse);
		Assertions.assertTrue(reponse.contains(CONTENU_PAGE_ATTENDU));
	}

	/**
	 * Méthode vérifiant la présence du serveur de proxy
	 *
	 * @return TRUE si le proxy est présent dans l'environnement
	 */
	private boolean testerPresenceDuProxy() {
		try (Socket s = new Socket(BON_PROXY, PROXY_PORT)) {
			return true;
		} catch (final IOException ex) {
			return false;
		}
	}

}
