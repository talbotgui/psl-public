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
package com.github.talbotgui.psl.socle.dbdocument.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.StatutDocumentTransfert;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe;
import com.github.talbotgui.psl.socle.dbdocument.application.SocleDbdocumentApplication;
import com.github.talbotgui.psl.socle.dbdocument.dao.PieceJointeDao;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.IndexOptions;

/** Classe de test de DocumentService. */
@SpringBootTest(classes = SocleDbdocumentApplication.class)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class DocumentTransfertServiceTest extends AbstractMongoTest {

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	/** Instance de service utilisée pour créer un jeu de données en base */
	@Autowired
	private DocumentService documentService;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	private MongoTemplate mongoTemplate;

	/** Instance de DAO utilisée pour créer un jeu de données en base (le service réalse trop de contrôles pour les besoins de ces tests) */
	@Autowired
	private PieceJointeDao pjDao;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	@Autowired
	private DocumentTransfertService service;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la méthode parente
		super.avantChaqueTest(testInfo);
		// Création du mongoTemplate si ce n'est pas déjà fait
		if (this.mongoTemplate == null) {
			String chaineConnexion = "mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB;
			this.mongoTemplate = new MongoTemplate(MongoClients.create(chaineConnexion), this.baseDeDonneeMongoDB);
		}
		// Destruction/recréation des collections
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTION_DOCUMENT, new IndexOptions().unique(true));
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
	}

	@Test
	void test01NotifierEchecDuTraitementDunDocumentDeTransfert() {
		//
		String idDocumentSauvegarde = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		//
		this.service.notifierEchecDuTraitementDunDocumentDeTransfert(idDocumentSauvegarde);
		//
		Query requete = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT).is(StatutDocumentTransfert.EN_ERREUR.name()));
		MessageSauvegardeDocumentDto documentTrouve = this.mongoTemplate.findOne(requete, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertNotNull(documentTrouve);
	}

	@Test
	void test02RechercheDunTransfertAtraiter01CasAucunDocument() {
		//
		//
		String id = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		//
		Assertions.assertNull(id);
	}

	@Test
	void test02RechercheDunTransfertAtraiter02CasSimpleAvecUnSeulDocumentNonTraite() {
		//
		String idDocumentSauvegarde = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		//
		String idPremierAppel = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idSecondAppel = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		//
		Assertions.assertEquals(idDocumentSauvegarde, idPremierAppel);
		Assertions.assertNull(idSecondAppel);
	}

	@Test
	void test02RechercheDunTransfertAtraiter03CasSimpleAvecUnSeulDocumentEnErreur() {
		//
		String idDocumentSauvegarde = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		this.service.notifierEchecDuTraitementDunDocumentDeTransfert(idDocumentSauvegarde);
		//
		String idPremierAppel = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idSecondAppel = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		//
		Assertions.assertEquals(idDocumentSauvegarde, idPremierAppel);
		Assertions.assertNull(idSecondAppel);
	}

	@Test
	void test02RechercheDunTransfertAtraiter04CasAvecPlusieursDocumentsEtPlusieursStatuts() {
		//
		String idDocumentSauvegardeErreur1 = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		this.service.notifierEchecDuTraitementDunDocumentDeTransfert(idDocumentSauvegardeErreur1);
		String idDocumentSauvegardeNonTraite1 = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		String idDocumentSauvegardeErreur2 = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		this.service.notifierEchecDuTraitementDunDocumentDeTransfert(idDocumentSauvegardeErreur2);
		String idDocumentSauvegardeNonTraite2 = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		//
		String idResultatAppel1 = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idResultatAppel2 = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idResultatAppel3 = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idResultatAppel4 = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		String idResultatAppel5 = this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
		//
		Assertions.assertEquals(idDocumentSauvegardeNonTraite1, idResultatAppel1);
		Assertions.assertEquals(idDocumentSauvegardeNonTraite2, idResultatAppel2);
		Assertions.assertEquals(idDocumentSauvegardeErreur1, idResultatAppel3);
		Assertions.assertEquals(idDocumentSauvegardeErreur2, idResultatAppel4);
		Assertions.assertNull(idResultatAppel5);
	}

	@Test
	void test03RechercherDocumentOuPieceJointeDuTeledossier() {
		//
		String idDocumentSauvegarde = this.documentService
				.sauvegarderDocumentGenereDeTeledossier(DocumentTransfertObjectMother.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		//
		MessageSauvegardeDocumentDto documentRecupere = this.service.rechercherDocumentOuPieceJointeDuTeledossier(idDocumentSauvegarde);
		//
		Assertions.assertNotNull(documentRecupere);
	}

	@Test
	void test04PurgerUnTeledossierApresTransfert() {
		//
		MessageSauvegardeDocumentDto documentTransfert = DocumentTransfertObjectMother
				.creerDocument(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT);
		this.documentService.sauvegarderDocumentGenereDeTeledossier(documentTransfert);

		MessageSauvegardeDocumentDto documentGenere1 = DocumentTransfertObjectMother.creerDocument("toto",
				documentTransfert.getNumeroTeledossier());
		this.documentService.sauvegarderDocumentGenereDeTeledossier(documentGenere1);

		PieceJointe pieceJointe = DocumentTransfertObjectMother.creerPieceJointe();
		String idPieceJointe = this.pjDao.sauvegarderPieceJointe(pieceJointe);
		this.pjDao.associerPieceJointeAunTeledossier(idPieceJointe, documentTransfert.getNumeroTeledossier());
		//
		this.service.purgerUnTeledossierApresTransfert(documentTransfert.getNumeroTeledossier());
		//
		Query requeteNbDocumentTransfert = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_DOCUMENT).is(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		long nbDocumentTransfert = this.mongoTemplate.count(requeteNbDocumentTransfert, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertEquals(1, nbDocumentTransfert);
		Query requeteNbDocumentAutre = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_DOCUMENT).ne(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT));
		long nbDocumentAutre = this.mongoTemplate.count(requeteNbDocumentAutre, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertEquals(0, nbDocumentAutre);
		Query requeteNbPieceJointe = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_PIECE_JOINTE).exists(true));
		nbDocumentAutre = this.mongoTemplate.count(requeteNbPieceJointe, PieceJointe.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertEquals(0, nbDocumentAutre);
	}

}
