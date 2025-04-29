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
package com.github.talbotgui.psl.socle.dbbrouillon.controleur;

import java.util.HashMap;

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

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.api.BrouillonAPI;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;
import com.github.talbotgui.psl.socle.dbbrouillon.service.BrouillonService;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractBrouillonControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private BrouillonService bouchonService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private BrouillonControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected BrouillonAPI leClientAutiliser;

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
	protected void test00Securite() {
		//
		BrouillonDto dto = new BrouillonDto();

		//
		CatchException.catchException(() -> this.leClientAutiliser.sauvegarderBrouillon(dto));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test01sauvegarderBrouillon() {
		//
		BrouillonDto brouillon = new BrouillonDto("code", "1.0", 2, new HashMap<>());
		String id = "12345-67890";
		Mockito.doReturn(id).when(this.bouchonService).sauvegarderBrouillon(Mockito.anyString(), Mockito.any());
		//
		String reponse = this.leClientAutiliser.sauvegarderBrouillon(brouillon);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(id, reponse.replace("\"", ""));
		Mockito.verify(this.bouchonService).sauvegarderBrouillon(Mockito.anyString(), Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test02telechargerBrouillon() {
		//
		BrouillonDto brouillon = new BrouillonDto("code", "1.0", 2, new HashMap<>());
		String idBrouillon = "12345-67890";
		Mockito.doReturn(brouillon).when(this.bouchonService).rechercherBrouillon(this.tokenJwtValableDurantLeTest, brouillon.getCodeDemarche(),
				idBrouillon);
		//
		BrouillonDto reponse = this.leClientAutiliser.telechargerBrouillon(brouillon.getCodeDemarche(), idBrouillon);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonService).rechercherBrouillon(this.tokenJwtValableDurantLeTest, brouillon.getCodeDemarche(), idBrouillon);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test03SupprimerBrouillon() {
		//
		String codeDemarche = "codedemarche";
		String id = "12345-67890";
		//
		this.leClientAutiliser.supprimerBrouillon(codeDemarche, id);
		//
		Mockito.verify(this.bouchonService).supprimerBrouillon(Mockito.anyString(), Mockito.eq(codeDemarche), Mockito.eq(id));
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test04obtenirAuthentificationBrouillon() {
		//
		BrouillonDto brouillon = new BrouillonDto("code", "1.0", 2, new HashMap<>());
		String auth = "auth";
		String idBrouillon = "12345-67890";
		Mockito.doReturn(auth).when(this.bouchonService).obtenirAuthentificationDunBrouillon(brouillon.getCodeDemarche(), idBrouillon);
		//
		String reponse = this.leClientAutiliser.obtenirAuthentificationDunBrouillon(brouillon.getCodeDemarche(), idBrouillon);
		//
		Assertions.assertEquals(auth, reponse.replace("\"", ""));
		Mockito.verify(this.bouchonService).obtenirAuthentificationDunBrouillon(brouillon.getCodeDemarche(), idBrouillon);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

}
