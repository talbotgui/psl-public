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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.bson.Document;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbdocument.application.SocleDbdocumentApplication;
import com.github.talbotgui.psl.socle.dbdocument.clientantivirus.ClamavClient;
import com.googlecode.catchexception.CatchException;
import com.mongodb.client.MongoClients;

/** Classe de test de PieceJointeService. */
@SpringBootTest(classes = SocleDbdocumentApplication.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
// Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class PieceJointeServiceTest extends AbstractMongoTest {

	static final File PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS = new File(
			"./src/test/resources/jdd/configurationPubliqueDeDemarche-PieceJointeServiceTest.json");

	static final File PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS_2 = new File(
			"./src/test/resources/jdd/configurationPubliqueDeDemarcheSansPj-PieceJointeServiceTest.json");

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	/** Bouchon du client antivirus. */
	@MockitoBean
	private ClamavClient bouchonClientAntivirus;

	/**
	 * Bouchon du client d'appel à l'API Document présent dans le service. Mockito
	 * va utiliser l'implémentation créée ici pour la bouchonner.
	 */
	@MockitoBean
	private ConfigurationAPI bouchonConfigurationAPI;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	private MongoTemplate mongoTemplate;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private PieceJointeService service;

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

		// Destruction/recréation de la collection des pièces jointes
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);

		Mockito.reset(this.bouchonConfigurationAPI, this.bouchonClientAntivirus);
	}

	@Test
	void test01SauvegardePieceJointe01CasNominal() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		//
		String id = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		Assertions.assertNotNull(id);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_PIECE_JOINTE).is(codePj));
		Document documentTrouve = this.mongoTemplate.findOne(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNotNull(documentTrouve);
		Assertions.assertEquals(id, documentTrouve.get("_id").toString());
	}

	@Test
	void test01SauvegardePieceJointe02AvecUnePjExistante() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		String idNouvellePj = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		Assertions.assertNotNull(idNouvellePj);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_PIECE_JOINTE).is(codePj));
		List<Document> documentsTrouves = this.mongoTemplate.find(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNotNull(documentsTrouves);
		Assertions.assertEquals(1, documentsTrouves.size());
		Assertions.assertEquals(idNouvellePj, documentsTrouves.get(0).get("_id").toString());
	}

	@Test
	void test01SauvegardePieceJointe03AvecPlusieursPjsExistantes() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		String idNouvellePj = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		Assertions.assertNotNull(idNouvellePj);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_PIECE_JOINTE).is(codePj));
		List<Document> documentsTrouves = this.mongoTemplate.find(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNotNull(documentsTrouves);
		Assertions.assertEquals(1, documentsTrouves.size());
		Assertions.assertEquals(idNouvellePj, documentsTrouves.get(0).get("_id").toString());
	}

	@Test
	void test02SauvegardePieceJointeEchouee01PasDeDemarche() throws IOException {
		//
		String codeDemarche = "codeDemarcheInconnue";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		MongodbException ex = new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		Mockito.doThrow(ex).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02SauvegardePieceJointeEchouee02PasDePjDansLaDemarche() throws IOException {
		//
		String codeDemarche = "codeDemarcheSansPj";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS_2,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(2, ((MongodbException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02SauvegardePieceJointeEchouee03PasCettePjDansLaDemarche() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1Inconnue";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(3, ((MongodbException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02SauvegardePieceJointeEchouee04DocumentTropGros() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		byte[] contenu = new byte[1 + (10 * 1024 * 1024)];
		new Random().nextBytes(contenu);
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", new ByteArrayInputStream(contenu));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(4, ((MongodbException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02SauvegardePieceJointeEchouee05MauvaisTypeDeclareDeDocument() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/pdf", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(4, ((MongodbException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test02SauvegardePieceJointeEchouee06MauvaisTypeReelDeDocument() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml",
				Files.newInputStream(Paths.get("src/main/resources/bootstrap.properties")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		//
		CatchException.catchException(() -> this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
		Assertions.assertEquals(6, ((MongodbException) CatchException.caughtException()).getParametres()[0]);
	}

	@Test
	void test03SupprimerPieceJointe01CasNominal() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		String id = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		//
		this.service.supprimerPieceJointeNonSoumise(id);
		//
		Assertions.assertNotNull(id);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_PIECE_JOINTE).is(codePj));
		Document documentTrouve = this.mongoTemplate.findOne(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNull(documentTrouve);
	}

	@Test
	void test03SupprimerPieceJointe02NonExistant() {
		//
		String id = "toto";
		//
		CatchException.catchException(() -> this.service.supprimerPieceJointeNonSoumise(id));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test04AssocierPieceJointe01CasNominal() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		String id = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		//
		this.service.associerPieceJointeAunTeledossier(id, numeroTeledossier);
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier));
		Document documentTrouve = this.mongoTemplate.findOne(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNotNull(documentTrouve);
	}

	@Test
	void test04AssocierPieceJointe02NonExistant() {
		//
		String id = "toto";
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		//
		CatchException.catchException(() -> this.service.associerPieceJointeAunTeledossier(id, numeroTeledossier));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test05supprimerPiecesJointesDunTeledossier() throws IOException {
		//
		String codeDemarche = "codedemarche";
		String codePj = "pj1";
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		MultipartFile fichier = new MockMultipartFile("nom", "nomOriginal", "application/xml", Files.newInputStream(Paths.get("pom.xml")));
		ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(PATH_CHEMIN_FICHIER_CONFIGURATION_POUR_TESTS,
				ConfigurationPubliqueDemarcheDto.class);
		Mockito.doReturn(configuration).when(this.bouchonConfigurationAPI).rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
		Mockito.doReturn(true).when(this.bouchonClientAntivirus).analyserFichier(Mockito.any());
		String idPj = this.service.sauvegarderPieceJointe(codeDemarche, codePj, fichier);
		this.service.associerPieceJointeAunTeledossier(idPj, numeroTeledossier);
		//
		this.service.desassocierPiecesJointesDuTeledossier(numeroTeledossier);
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier));
		Document documentTrouve = this.mongoTemplate.findOne(requete, Document.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		Assertions.assertNull(documentTrouve);
	}
}
