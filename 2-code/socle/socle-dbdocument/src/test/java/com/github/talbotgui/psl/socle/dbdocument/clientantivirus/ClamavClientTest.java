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
package com.github.talbotgui.psl.socle.dbdocument.clientantivirus;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import com.mongodb.assertions.Assertions;

/**
 * Classe d'intégration toujours commentée quand commitée.
 *
 * Un serveur avec Clamav est nécessaire pour sa bonne exécution.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class ClamavClientTest {

	/**
	 * Chaine de caractère toujours refusée par les antivirus.
	 *
	 * @see https://fr.wikipedia.org/wiki/Fichier_de_test_Eicar
	 */
	private static final String CHAINE_EICAR = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

	private static final int PARAM_DELAI_MAX = 1000;
	private static final String PARAM_HOTE = "xx.xx.xx.xx";
	private static final int PARAM_PORT_BON = 3310;
	private static final int PARAM_PORT_MAUVAIS = 3311;

	// Test plus utilisable car aucune instance de clamAV n'est disponible
	// @Test
	void test01ping01Ok() {
		//
		Assumptions.assumeTrue(this.verifierServeurPresent());
		ClamavClient client = new ClamavClient(PARAM_HOTE, PARAM_PORT_BON, PARAM_DELAI_MAX);
		//
		boolean estActif = client.verifierPresenceProcessusAntivirus();
		//
		Assertions.assertTrue(estActif);
	}

	// Test plus utilisable car aucune instance de clamAV n'est disponible
	// @Test
	void test01ping02Ko() {
		//
		Assumptions.assumeTrue(this.verifierServeurPresent());
		ClamavClient client = new ClamavClient(PARAM_HOTE, PARAM_PORT_MAUVAIS, PARAM_DELAI_MAX);
		//
		boolean estActif = client.verifierPresenceProcessusAntivirus();
		//
		Assertions.assertFalse(estActif);
	}

	// Test plus utilisable car aucune instance de clamAV n'est disponible
	// @Test
	void test02analyse01Ok() throws IOException {
		//
		Assumptions.assumeTrue(this.verifierServeurPresent());
		ClamavClient client = new ClamavClient(PARAM_HOTE, PARAM_PORT_BON, PARAM_DELAI_MAX);
		//
		boolean reponse = client.analyserFichier(Files.readAllBytes(Paths.get("pom.xml")));
		//
		Assertions.assertTrue(reponse);
	}

	// Test plus utilisable car aucune instance de clamAV n'est disponible
	// @Test
	void test02analyse02Ko() {
		//
		Assumptions.assumeTrue(this.verifierServeurPresent());
		ClamavClient client = new ClamavClient(PARAM_HOTE, PARAM_PORT_BON, PARAM_DELAI_MAX);
		//
		boolean reponse = client.analyserFichier(CHAINE_EICAR.getBytes());
		//
		Assertions.assertFalse(reponse);
	}

	/**
	 * Méthode simpliste vérifiant que le serveur est accessible.
	 *
	 * @return TRUE si le serveur est accessible
	 */
	private boolean verifierServeurPresent() {
		try (Socket s = new Socket(PARAM_HOTE, PARAM_PORT_BON)) {
			s.setSoTimeout(2000);
		}
		// En cas d'erreur différente
		catch (final IOException e) {
			return false;
		}
		return true;
	}
}
