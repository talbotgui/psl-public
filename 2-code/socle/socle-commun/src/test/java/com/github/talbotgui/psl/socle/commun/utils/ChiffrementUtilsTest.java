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
package com.github.talbotgui.psl.socle.commun.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ChiffrementUtilsTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChiffrementUtilsTest.class);

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
	}

	@Test
	void test01chiffrementEtDechiffrement() {
		//
		String donnees = "azertyuiop";
		// Chaîne de caractères de chiffrement autorisée (ce n'est pas un secret)
		String secret = "b2premJlZm9zaXUxKjDDqXBva21qbA==";
		//
		String donneesChiffrees = ChiffrementUtils.chiffrerChaineDeCaracteres(donnees, secret);
		String donneesDeChiffrees = ChiffrementUtils.dechiffrerChaineDeCaracteres(donneesChiffrees, secret);
		//
		Assertions.assertNotNull(donneesChiffrees);
		Assertions.assertNotEquals(donnees, donneesChiffrees);
		Assertions.assertEquals(donnees, donneesDeChiffrees);
	}

	@Test
	void test02verifierChaineEstChiffree01Oui() {
		//
		String donnees = "azertyuiop";
		// Chaîne de caractères de chiffrement autorisée (ce n'est pas un secret)
		String secret = "b2premJlZm9zaXUxKjDDqXBva21qbA==";
		String donneesChiffrees = ChiffrementUtils.chiffrerChaineDeCaracteres(donnees, secret);
		//
		boolean resultat = ChiffrementUtils.verifierChaineEstChiffree(donneesChiffrees);
		//
		Assertions.assertTrue(resultat);
	}

	@Test
	void test02verifierChaineEstChiffree02Non() {
		//
		String donnees = "azertyuiop";
		//
		boolean resultat = ChiffrementUtils.verifierChaineEstChiffree(donnees);
		//
		Assertions.assertFalse(resultat);
	}

}
