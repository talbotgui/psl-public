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

import java.util.List;

import org.bson.Document;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.application.SocleDbdocumentApplication;
import com.googlecode.catchexception.CatchException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.IndexOptions;

/** Classe de test de DocumentService. */
@SpringBootTest(classes = SocleDbdocumentApplication.class)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class DocumentServiceTest extends AbstractMongoTest {

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	@Autowired
	private JwtService jwtService;

	private MongoTemplate mongoTemplate;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	@Autowired
	private DocumentService service;

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
	}

	@Test
	void test01SauvegardeDocument01CasNominal() {
		//
		MessageSauvegardeDocumentDto document = new MessageSauvegardeDocumentDto("codedemarche", "teledossier", "CODE", "lib", "nom", null,
				null, "application/pdf", null, "contenuDocument", "md5");
		//
		String id = this.service.sauvegarderDocumentGenereDeTeledossier(document);
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(document.getNumeroTeledossier()));
		MessageSauvegardeDocumentDto documentTrouve = this.mongoTemplate.findOne(requete, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertNotNull(documentTrouve);
		Assertions.assertEquals(id, documentTrouve.getId());
	}

	@Test
	void test01SauvegardeDocument02RejetPourDoublon() {
		//
		MessageSauvegardeDocumentDto document1 = new MessageSauvegardeDocumentDto("codedemarche", "teledossier", "CODE", "lib", "nom", null,
				null, "application/pdf", 16, "contenuDocument", "md5");
		MessageSauvegardeDocumentDto document2 = new MessageSauvegardeDocumentDto("codedemarche", "teledossier", "CODE", "lib2", "nom2", null,
				null, "application/pdf", 16, "contenuDocument2", "md52");
		//
		String id = this.service.sauvegarderDocumentGenereDeTeledossier(document1);
		CatchException.catchException(() -> this.service.sauvegarderDocumentGenereDeTeledossier(document2));
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(document1.getNumeroTeledossier()));
		MessageSauvegardeDocumentDto documentTrouve = this.mongoTemplate.findOne(requete, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertNotNull(documentTrouve);
		Assertions.assertEquals(id, documentTrouve.getId());
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(MongodbException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_EN_DOUBLE),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02ListerLesDocumentsVisiblesDeLusager() {
		//
		String teledossier = "teledossier";
		MessageSauvegardeDocumentDto document1 = new MessageSauvegardeDocumentDto("codedemarche", teledossier, "CODE1", "lib1", "nom", null,
				null, "application/pdf", 16, "contenuDocument", "md5");
		MessageSauvegardeDocumentDto document2 = new MessageSauvegardeDocumentDto("codedemarche", teledossier, "CODE2", "lib2", "nom2", true, 1,
				"application/pdf", 16, "contenuDocument2", "md52");
		MessageSauvegardeDocumentDto document3 = new MessageSauvegardeDocumentDto("codedemarche", teledossier, "CODE3", "lib3", "nom3", true, 0,
				"application/pdf", 16, "contenuDocument3", "md53");
		this.service.sauvegarderDocumentGenereDeTeledossier(document1);
		this.service.sauvegarderDocumentGenereDeTeledossier(document2);
		this.service.sauvegarderDocumentGenereDeTeledossier(document3);
		//
		List<MessageMetadonneesDocumentDto> documents = this.service.rechercherDocumentsVisibleDuTeledossier(teledossier);
		//
		Assertions.assertNotNull(documents);
		Assertions.assertEquals(2, documents.size());
		Assertions.assertEquals(document3.getNomDocument(), documents.get(0).getNomDocument());
		Assertions.assertEquals(document2.getNomDocument(), documents.get(1).getNomDocument());
	}

	@Test
	void test03DemanderClefTelechargement() {
		//
		String teledossier = "teledossier";
		String codeDocument = "codeDocument";
		String token = this.jwtService.genererNouveauTokenAnonyme();
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto("codedemarche", teledossier, codeDocument, "lib", "nom", null, null,
				"t", 16, "c", "md5");
		this.service.sauvegarderDocumentGenereDeTeledossier(doc);
		//
		String clef = this.service.genererEtEnregistrerClefUniqueDeTelechargement(token, teledossier, codeDocument);
		//
		Assertions.assertNotNull(clef);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(teledossier));
		MessageSauvegardeDocumentDto documentTrouve = this.mongoTemplate.findOne(requete, MessageSauvegardeDocumentDto.class,
				AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertNotNull(documentTrouve);
	}

	@Test
	void test04Telecharger01ClefVide() {
		//
		String teledossier = "teledossier";
		String codeDocument = "codeDocument";
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto("codedemarche", teledossier, codeDocument, "lib", "nom", null, null,
				"t", 16, "c", "md5");
		this.service.sauvegarderDocumentGenereDeTeledossier(doc);
		//
		CatchException.catchException(() -> this.service.rechercherDocumentVisibleDuTeledossier(teledossier, codeDocument));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(MongodbException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test04Telecharger02ClefValide() {
		//
		String teledossier = "teledossier";
		String codeDocument = "codeDocument";
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto("codedemarche", teledossier, codeDocument, "lib", "nom", null, null,
				"t", 16, "c", "md5");
		this.service.sauvegarderDocumentGenereDeTeledossier(doc);

		String token = this.jwtService.genererNouveauTokenAnonyme();
		String clefTemporaireChiffre = this.service.genererEtEnregistrerClefUniqueDeTelechargement(token, teledossier, codeDocument);
		UsernamePasswordAuthenticationToken authenticationSpring = new UsernamePasswordAuthenticationToken(null, clefTemporaireChiffre, null);
		SecurityContextHolder.getContext().setAuthentication(authenticationSpring);

		//
		MessageSauvegardeDocumentDto reponse = this.service.rechercherDocumentVisibleDuTeledossier(teledossier, codeDocument);

		//
		Assertions.assertNotNull(reponse);
	}

	@Test
	void test04Telecharger03ClefDejaUtilisee() {
		//
		String teledossier = "teledossier";
		String codeDocument = "codeDocument";
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto("codedemarche", teledossier, codeDocument, "lib", "nom", null, null,
				"t", 16, "c", "md5");
		this.service.sauvegarderDocumentGenereDeTeledossier(doc);

		String token = this.jwtService.genererNouveauTokenAnonyme();
		String clefTemporaireChiffre = this.service.genererEtEnregistrerClefUniqueDeTelechargement(token, teledossier, codeDocument);
		SecurityContextHolder.getContext().getAuthentication().getCredentials();
		UsernamePasswordAuthenticationToken authenticationSpring = new UsernamePasswordAuthenticationToken(null, clefTemporaireChiffre, null);
		SecurityContextHolder.getContext().setAuthentication(authenticationSpring);

		this.service.rechercherDocumentVisibleDuTeledossier(teledossier, codeDocument);

		//
		CatchException.catchException(() -> this.service.rechercherDocumentVisibleDuTeledossier(teledossier, codeDocument));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(MongodbException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test05supprimerDocumentsDuTeledossier() {
		//
		String teledossier = "teledossier";
		String codeDocument = "codeDocument";
		MessageSauvegardeDocumentDto doc = new MessageSauvegardeDocumentDto("codedemarche", teledossier, codeDocument, "lib", "nom", null, null,
				"t", 16, "c", "md5");
		this.service.sauvegarderDocumentGenereDeTeledossier(doc);
		//
		this.service.supprimerDocumentsDuTeledossier(teledossier);
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(teledossier));
		Document documentTrouve = this.mongoTemplate.findOne(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		Assertions.assertNull(documentTrouve);
	}
}
