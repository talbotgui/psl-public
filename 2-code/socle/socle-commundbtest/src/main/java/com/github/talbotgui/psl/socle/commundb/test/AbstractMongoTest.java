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
package com.github.talbotgui.psl.socle.commundb.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;

/**
 * Depuis Spring-2.6.x, le démarrage de MongoDB ne se fait plus. La classe ProgrammeDemarrantMongoDB étant là pour ça, elle est utilisée.
 */
public class AbstractMongoTest extends AbstractTest {

	/** Chemin vers le répertoire des LOGs. */
	private static final String CHEMIN_PID = "./.log";

	/** Logger spécifique aux tests */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoTest.class);

	/** Arrêt de la base MongoDB (vide) après les tests. */
	@AfterAll
	static void arreterLaBaseApresLesTests() {
		ProgrammeDemarrantMongoDBDansLesTests.arreterMongoDB();
	}

	/**
	 * Démarrage de la base MongoDB avant les tests.
	 *
	 * @throws Exception
	 */
	@BeforeAll
	static void demarrerLaBaseAvantLesTests() {
		try {
			ProgrammeDemarrantMongoDBDansLesTests.demarrerUneBaseDeDonneesMongoDB(CHEMIN_PID);
		} catch (final Exception e) {
			LOGGER.info("Erreur de démarrage de la base", e);
			Assertions.fail(e);
		}
	}
}
