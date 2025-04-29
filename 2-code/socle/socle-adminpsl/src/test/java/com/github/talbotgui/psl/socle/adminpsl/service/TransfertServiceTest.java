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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.application.SocleAdminPslApplication;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.SoumissionAPI;

/** Classe de test de TransfertService. */
@SpringBootTest(classes = SocleAdminPslApplication.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
//Sans le PER_CLASS car les @BeforeAll et @AfterAll ne fonctionnent pas (Spring démarre avant l'exécution du @BeforeAll qui démarre la BDD)
//@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class TransfertServiceTest extends AbstractAdminpslServiceTest {

	/** Brouillon n°1 d'EtatCivil. */
	private static final String CHEMIN_BROUILLON_REEL_ETATCIVIL = "../../front/projects/etatcivil/public/bouchonapi/brouillon1.json";

	/** Conf interne d'EtatCivil. */
	private static final String CHEMIN_CONFIGURATION_INTERNE_REEL_ETATCIVIL = "../socle-dbconfiguration/src/main/resources/db/etatCivil-ConfigurationInterneDemarche-1.0.0.json";

	/** Conf publique d'EtatCivil. */
	private static final String CHEMIN_CONFIGURATION_PUBLIQUE_REEL_ETATCIVIL = "../../front/projects/etatcivil/public/bouchonapi/param.json";

	/** Bouchon injecté. */
	@MockitoBean
	private SoumissionAPI bouchonClientAppelSoumission;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private TransfertService transfertService;

	/**
	 * Génération de 'nb' documents de transfert vide.
	 *
	 * @param nb Nombre de documents à générer
	 */
	private void genererDesDocumentsDeTransfertVide(int nb) {
		for (int i = 0; i < nb; i++) {
			MessageSauvegardeDocumentDto dto = new MessageSauvegardeDocumentDto();
			dto.setCodeDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT);
			dto.setNumeroTeledossier(Integer.toString(i));
			this.mongoTemplateDocument.save(dto, AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		}
	}

	// @Test
	void test01GenererDesTeledossiers() throws IOException {
		//
		Document documentBrouillon = Document.parse(Files.readString(Paths.get(CHEMIN_BROUILLON_REEL_ETATCIVIL)));
		this.mongoTemplateBrouillon.save(documentBrouillon, AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		Document documentConfPub = Document.parse(Files.readString(Paths.get(CHEMIN_CONFIGURATION_PUBLIQUE_REEL_ETATCIVIL)));
		this.mongoTemplateConfiguration.save(documentConfPub, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		Document documentConfInt = Document.parse(Files.readString(Paths.get(CHEMIN_CONFIGURATION_INTERNE_REEL_ETATCIVIL)));
		this.mongoTemplateConfiguration.save(documentConfInt, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		Mockito.doReturn("").when(this.bouchonClientAppelSoumission).soumettreUnTeledossier(Mockito.any());
		//
		this.transfertService.genererDesTeledossiers(10);
		//
		Mockito.verify(this.bouchonClientAppelSoumission, Mockito.times(10)).soumettreUnTeledossier(Mockito.any());
	}

	@Test
	void test02rechercherTransferts01baseVide() {
		//
		int noPage = 0;
		int nbParPage = 5;
		//
		Page<ResultatRechercheTransfertsDto> page = this.transfertService
				.rechercherDesTeledossiers(new RequeteRechercheTransfertsDto(null, null, null, null, noPage, nbParPage));
		//
		Assertions.assertNotNull(page);
		Assertions.assertNotNull(page.getResultats());
		Assertions.assertEquals(0, page.getNombreTotalResultats());
		Assertions.assertTrue(page.getResultats().isEmpty());
		Assertions.assertEquals(noPage, page.getNumeroPage());
		Assertions.assertEquals(nbParPage, page.getTaillePage());
	}

	@Test
	void test02rechercherTransferts02baseNonVideEtRequeteVide() {
		//
		int noPage = 0;
		int nbParPage = 5;
		this.genererDesDocumentsDeTransfertVide(11);
		//
		Page<ResultatRechercheTransfertsDto> page = this.transfertService
				.rechercherDesTeledossiers(new RequeteRechercheTransfertsDto(null, null, null, null, noPage, nbParPage));
		//
		Assertions.assertNotNull(page);
		Assertions.assertNotNull(page.getResultats());
		Assertions.assertEquals(11, page.getNombreTotalResultats());
		Assertions.assertEquals(nbParPage, page.getResultats().size());
		Assertions.assertEquals(noPage, page.getNumeroPage());
		Assertions.assertEquals(nbParPage, page.getTaillePage());
	}

	@Test
	void test02rechercherTransferts03baseNonVideEtRequeteNonVide() {
		//
		int noPage = 0;
		int nbParPage = 5;
		this.genererDesDocumentsDeTransfertVide(11);
		//
		Page<ResultatRechercheTransfertsDto> page = this.transfertService
				.rechercherDesTeledossiers(new RequeteRechercheTransfertsDto(null, null, null, "0", noPage, nbParPage));
		//
		Assertions.assertNotNull(page);
		Assertions.assertNotNull(page.getResultats());
		Assertions.assertEquals(1, page.getNombreTotalResultats());
		Assertions.assertEquals(1, page.getResultats().size());
		Assertions.assertEquals(noPage, page.getNumeroPage());
		Assertions.assertEquals(nbParPage, page.getTaillePage());
	}

}
