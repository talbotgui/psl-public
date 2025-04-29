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
package com.github.talbotgui.psl.socle.dbconfiguration.controleur;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.service.ConfigurationService;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractConfigurationControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private ConfigurationService bouchonService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private ConfigurationControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected ConfigurationAPI leClientAutiliser;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	protected String secret;

	/** Token JWT initialisé au début du test et utilisé tout le long */
	protected String tokenJwtValableDurantLeTest;

	/** Avant chaque méthode de test. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la classe parente
		super.avantChaqueTest(testInfo);
		// Reset du bouchon
		Mockito.reset(this.bouchonService);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
	}

	@Test
	void test00GestionErreur() {
		//
		String codeDemarche = "codedemarche";
		String codeApi = "monApi";
		AbstractException e = new ApiClientException(ApiClientException.ERREUR_APPEL, codeApi);
		Mockito.doThrow(e).when(this.bouchonService).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);

		//
		CatchException.catchException(() -> this.leClientAutiliser.rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verify(this.bouchonService).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.verifyNoMoreInteractions(this.bouchonService);

	}

	@Test
	protected void test00Securite() {
		//
		String codeDemarche = "codedemarche";

		//
		CatchException.catchException(() -> this.leClientAutiliser.rechercherDerniereConfigurationInterneDeDemarche(codeDemarche));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test01RechercherDerniereConfigurationPublique() {
		//
		String codeDemarche = "codedemarche";
		ConfigurationPubliqueDemarcheDto configuration = new ConfigurationPubliqueDemarcheDto(codeDemarche, null);
		Mockito.doReturn(configuration).when(this.bouchonService).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);

		//
		ConfigurationPubliqueDemarcheDto reponse = this.leClientAutiliser.rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonService).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test02RechercherConfigurationPublique() {
		//
		String codeDemarche = "codedemarche";
		String versionConfiguration = "1.0";
		ConfigurationPubliqueDemarcheDto configuration = new ConfigurationPubliqueDemarcheDto(codeDemarche, versionConfiguration);
		Mockito.doReturn(configuration).when(this.bouchonService).rechercherConfigurationPubliqueDeDemarche(codeDemarche, versionConfiguration);

		//
		ConfigurationPubliqueDemarcheDto reponse = this.leClientAutiliser.rechercherConfigurationPubliqueDeDemarche(codeDemarche,
				versionConfiguration);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonService).rechercherConfigurationPubliqueDeDemarche(codeDemarche, versionConfiguration);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test03RechercherDerniereConfigurationInterne() {
		//
		String codeDemarche = "codedemarche";
		ConfigurationInterneDemarcheDto configuration = new ConfigurationInterneDemarcheDto(codeDemarche, "", Collections.emptyList(), Map.of());
		Mockito.doReturn(configuration).when(this.bouchonService).rechercherDerniereConfigurationInterneDeDemarche(codeDemarche);

		//
		ConfigurationInterneDemarcheDto reponse = this.leClientAutiliser.rechercherDerniereConfigurationInterneDeDemarche(codeDemarche);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonService).rechercherDerniereConfigurationInterneDeDemarche(codeDemarche);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}
}
