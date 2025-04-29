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
package com.github.talbotgui.psl.socle.dbdocument.controleur;

import java.nio.file.Paths;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.DbdocumentClient;
import com.github.talbotgui.psl.socle.dbdocument.application.SocleDbdocumentApplication;
import com.github.talbotgui.psl.socle.dbdocument.service.PieceJointeService;

//Configuration du test
@SpringBootTest(classes = SocleDbdocumentApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PieceJointeControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private PieceJointeService bouchonService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private PieceJointeControleur controleurAnePasUtiliser;

	/**
	 * Le client permettant l'appel au controleur (pas le client Feign qui est testé
	 * ailleurs mais le client utilisable en externe)
	 */
	private DbdocumentClient documentClient;

	/** Pour récupérer le port dynamique sur lequel est démarré le serveur */
	@LocalServerPort
	private int port;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	private String secret;

	/** Token JWT initialisé au début du test et utilisé tout le long */
	private String tokenJwtValableDurantLeTest;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		Mockito.reset(this.bouchonService);
		this.documentClient = new DbdocumentClient(null, "http://localhost:" + this.port, false);
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
		this.documentClient.enregistrerTokenJwtAutiliser(this.tokenJwtValableDurantLeTest);
	}

	@Test
	void test01sauvegarderPieceJointe() {
		//
		String id = "12345-67890";
		Mockito.doReturn(id).when(this.bouchonService).sauvegarderPieceJointe(Mockito.anyString(), Mockito.anyString(), Mockito.any());
		//
		String reponse = this.documentClient.sauvegarderPieceJointe("codedemarche", "codePieceJointe", Paths.get("pom.xml"));
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(id, reponse);
		Mockito.verify(this.bouchonService).sauvegarderPieceJointe(Mockito.anyString(), Mockito.anyString(), Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test02supprimerPieceJointeNonSoumise() {
		//
		String idPieceJointe = "12345-67890";
		//
		this.documentClient.supprimerPieceJointeNonSoumise(idPieceJointe);
		//
		Mockito.verify(this.bouchonService).supprimerPieceJointeNonSoumise(idPieceJointe);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test03associerPieceJointeAunTeledossier() {
		//
		String idPieceJointe = "12345-67890";
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		//
		this.documentClient.associerPieceJointeAunTeledossier(idPieceJointe, numeroTeledossier);
		//
		Mockito.verify(this.bouchonService).associerPieceJointeAunTeledossier(idPieceJointe, numeroTeledossier);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test04desassocierPiecesJointesDuTeledossier() {
		//
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		//
		this.documentClient.desassocierPiecesJointesDuTeledossier(numeroTeledossier);
		//
		Mockito.verify(this.bouchonService).desassocierPiecesJointesDuTeledossier(numeroTeledossier);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

}
