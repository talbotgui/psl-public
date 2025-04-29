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

import java.util.Arrays;
import java.util.List;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.DbdocumentClient;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.application.SocleDbdocumentApplication;
import com.github.talbotgui.psl.socle.dbdocument.service.DocumentService;

//Configuration du test
@SpringBootTest(classes = SocleDbdocumentApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class DocumentControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private DocumentService bouchonService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private DocumentControleur controleurAnePasUtiliser;

	/**
	 * Le client permettant l'appel au controleur (pas le client Feign qui est testé
	 * ailleurs mais le client utilisable en externe)
	 */
	private DbdocumentClient documentClient;

	/** Composant de gestion des token JWT */
	@Autowired
	private JwtService jwtService;

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
	void test01sauvegarderDocumentGenereDeTeledossier() {
		//
		String id = "12345-67890";
		Mockito.doReturn(id).when(this.bouchonService).sauvegarderDocumentGenereDeTeledossier(Mockito.any());
		//
		String reponse = this.documentClient.sauvegarderDocumentGenereDeTeledossier("codedemarche", "numeroTeledossier", "codeDocument", "lib", "nom",
				false, null, "contentType", "coucou".getBytes());
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(id, reponse);
		Mockito.verify(this.bouchonService).sauvegarderDocumentGenereDeTeledossier(Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test02rechercherDocumentsVisibleDuTeledossier() {
		//
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		String codeDemarche = "codedemarche";
		List<MessageMetadonneesDocumentDto> liste = Arrays.asList(
				new MessageMetadonneesDocumentDto(codeDemarche, numeroTeledossier, "code1", "l1", "nom1", true, 0, "application/pdf"),
				new MessageMetadonneesDocumentDto(codeDemarche, numeroTeledossier, "code1", "l1", "nom1", true, 1, "application/pdf"));
		Mockito.doReturn(liste).when(this.bouchonService).rechercherDocumentsVisibleDuTeledossier(numeroTeledossier);
		//
		List<MessageMetadonneesDocumentDto> reponse = this.documentClient.rechercherDocumentsVisibleDunTeledossier(numeroTeledossier);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(liste.size(), reponse.size());
		Mockito.verify(this.bouchonService).rechercherDocumentsVisibleDuTeledossier(numeroTeledossier);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test03demanderClefDeTelechargement() {
		//
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		String codeDocument = "codeDocument";
		String clef = "clef";
		Mockito.doReturn(clef).when(this.bouchonService).genererEtEnregistrerClefUniqueDeTelechargement(this.tokenJwtValableDurantLeTest,
				numeroTeledossier, codeDocument);
		//
		String clefObtenue = this.documentClient.demanderClefDeTelechargement(numeroTeledossier, codeDocument);
		//
		Assertions.assertNotNull(clefObtenue);
		Assertions.assertEquals(clef, clefObtenue);
		Mockito.verify(this.bouchonService).genererEtEnregistrerClefUniqueDeTelechargement(this.tokenJwtValableDurantLeTest, numeroTeledossier,
				codeDocument);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test04rechercherDocumentVisibleDuTeledossier() {
		//
		String codeDemarche = "codedemarche";
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		String codeDocument = "codeDocument";
		String clefUniqueTelechargement = this.jwtService.genererClefTemporaireSurTokenJwt(this.tokenJwtValableDurantLeTest, "clefUnique", 100L,
				Map.of());
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto(codeDemarche, numeroTeledossier, codeDocument, "lib", "", null, 1,
				"application/json", 1, "123", "");
		Mockito.doReturn(doc).when(this.bouchonService).rechercherDocumentVisibleDuTeledossier(numeroTeledossier, codeDocument);
		//
		String reponse = this.documentClient.telechargerDocumentVisibleDuTeledossier(numeroTeledossier, codeDocument, clefUniqueTelechargement);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonService).rechercherDocumentVisibleDuTeledossier(numeroTeledossier, codeDocument);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test05supprimerDocumentsDunTeledossier() {
		//
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		//
		this.documentClient.supprimerDocumentsDunTeledossier(numeroTeledossier);
		//
		Mockito.verify(this.bouchonService).supprimerDocumentsDuTeledossier(numeroTeledossier);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}
}
