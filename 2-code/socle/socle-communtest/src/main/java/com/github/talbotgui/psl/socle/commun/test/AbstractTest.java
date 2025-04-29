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
package com.github.talbotgui.psl.socle.commun.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;

/**
 * Classe de base de tous les tests : - les méthodes de test s'exécute dans l'ordre du nom de la méthode (ordre alphabétique) - les fichiers de
 * propriétés sont tous chargés : les communs, les communs de test, les spécifiques à l'application et les spécifiques aux tests de l'application -
 * une ligne de log ************* est générée entre chaque test
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestPropertySource(locations = {
		// la configuration de bootstrap (pré-démarrage de spring) de chaque micro-service
		"classpath:/bootstrap.properties",
		// désactivé car le serveur d'admin n'est pas actif durant les tests (mutualisé dans le projet socle-communtest)
		// "classpath:/config/adminClient.properties",
		// les communs présents dans le projet service-config (mutualisés dans le projet socle-communtest)
		"classpath:/config/jwt.properties", "classpath:/config/log.properties", "classpath:/config/serviceregistry.properties",
		"classpath:/config/mongodb.properties", "classpath:/config/doc.properties", "classpath:/config/oidc.properties",
		// le commun à tous les tests (dans le projet socle-communtest mais pas disponible à travers service-config)
		"classpath:/application-tests.properties",
		// le spécifique à l'application (nommé selon le nom de l'application) (mutualisé dans le projet socle-communtest)
		"classpath:/config/${spring.application.name}.properties",
		// les spécifiques aux tests de l'application (présent dans le classpath des tests de chaque micro-service)
		"classpath:/application-testsspecifique.properties" })
public class AbstractTest {

	/** Logger spécifique aux tests */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);

	/** Entre chaque test, une ligne *********** */
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		LOGGER.info("**********************");
		String nomDeLaClasseDeTest = testInfo.getTestClass().isPresent() ? testInfo.getTestClass().get().getName() : "inconnue";
		LOGGER.info("Exécution du test {}.{}", nomDeLaClasseDeTest, testInfo.getDisplayName());
	}
}
