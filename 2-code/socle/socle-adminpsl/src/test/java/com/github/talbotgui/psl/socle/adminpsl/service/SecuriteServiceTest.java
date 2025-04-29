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
package com.github.talbotgui.psl.socle.adminpsl.service;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.UtilisateurConnecte;
import com.github.talbotgui.psl.socle.adminpsl.application.SocleAdminPslApplication;
import com.github.talbotgui.psl.socle.adminpsl.client.LdapClient;
import com.github.talbotgui.psl.socle.adminpsl.exception.SecuriteException;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.googlecode.catchexception.CatchException;

//Configuration du test
@SpringBootTest(classes = SocleAdminPslApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génère qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SecuriteServiceTest extends AbstractTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecuriteServiceTest.class);

	@MockitoBean
	private LdapClient ldapClientBouchon;

	// Implémentation déclarée pour les besoins de Mockito
	@Autowired
	private SecuriteService securiteService;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		LOGGER.info("**********************");
		Mockito.reset(this.ldapClientBouchon);
	}

	@Test
	void test01SeConnecterEtCreerJeton01casNominal() {
		//
		Mockito.doReturn(null).when(this.ldapClientBouchon).seConnecter(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		UtilisateurConnecte utilisateur = new UtilisateurConnecte("login", "nom", Arrays.asList("groupe1"));
		Mockito.doReturn(utilisateur).when(this.ldapClientBouchon).chargerDonneesUtilisateur(Mockito.isNull(), Mockito.anyString(),
				Mockito.anyString());
		//
		this.securiteService.seConnecterEtCreerJeton("admin1", "admin");
		//
	}

	@Test
	void test01SeConnecterEtCreerJeton02casErreur() {
		//
		Mockito.doThrow(new SecuriteException(SecuriteException.CONNEXION_NON_AUTORISEE))//
				.when(this.ldapClientBouchon).seConnecter(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		//
		CatchException.catchException(() -> this.securiteService.seConnecterEtCreerJeton("admin1", "mauvaisMotDePasse"));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), SecuriteException.CONNEXION_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
	}
}
