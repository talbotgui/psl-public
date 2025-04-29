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
package com.github.talbotgui.psl.socle.dbconfiguration.clientapi.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.talbotgui.psl.socle.commun.feign.PslFeignException;
import com.github.talbotgui.psl.socle.dbconfiguration.controleur.AbstractConfigurationControleurTest;
import com.googlecode.catchexception.CatchException;

/** Classe de test spécifique au client Feign se basant sur les cas de test décrits dans la classe AbstractConfigurationExterneControleurTest */
// Test d'API Feign
@SpringBootTest(classes = ApplicationPourTestFeign.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		// pour ne pas déclencher d'initialisation des données au démarrage du contexte
		properties = { "jdd.initialisation=false" })
class ConfigurationAPITest extends AbstractConfigurationControleurTest {

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		// Insertion du token JWT dans la sécurité Spring pour qu'il soit transmis dans l'appel via le client Feign (@see
		// com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter)
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("personne", super.tokenJwtValableDurantLeTest));
	}

	/** API à tester */
	@Autowired
	void setApi(ConfigurationAPIForTest api) {
		super.leClientAutiliser = api;
	}

	@Override
	@Test
	protected void test00Securite() {
		// Arrange supplémentaire spécifique à ce client
		String tokenInvalide = "tokenInvalide";
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("personne", tokenInvalide));

		// Act = appeler la méthode parente
		super.test00Securite();

		// Assert = test du type de l'exception retourné
		Assertions.assertEquals(PslFeignException.class, CatchException.caughtException().getClass());
	}
}
