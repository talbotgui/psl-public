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
package com.github.talbotgui.psl.socle.soumission.controleur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.soumission.apiclient.SoumissionClient;
import com.github.talbotgui.psl.socle.soumission.application.SocleSoumissionApplication;
import com.googlecode.catchexception.CatchException;

/**
 * Classe de test spécifique au Controleur se basant sur les cas de test décrits dans la classe AbstractReferentielExterneControleurTest
 */
//Configuration du test
@SpringBootTest(classes = SocleSoumissionApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class CaptchaControleurTest extends AbstractCaptchaControleurTest {

	/** Pour récupérer le port dynamique sur lequel est démarré le serveur */
	@LocalServerPort
	private int port;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		this.leClientAutiliser = new SoumissionClient(null, "http://localhost:" + this.port, false);
		((SoumissionClient) this.leClientAutiliser).enregistrerTokenJwtAutiliser(super.tokenJwtValableDurantLeTest);
	}

	@Override
	@Test
	void test00Securite01() {
		// ARRANGE supplémentaire spécifique à ce client
		String tokenInvalide = "tokenInvalide";
		((SoumissionClient) this.leClientAutiliser).enregistrerTokenJwtAutiliser(tokenInvalide);

		// ACT : appeler la méthode parente
		super.test00Securite01();

		// ASSERT : test du type de l'exception retourné
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), ApiClientException.ERREUR_CODE_RETOUR),
				CatchException.caughtException().getMessage());
	}
}
