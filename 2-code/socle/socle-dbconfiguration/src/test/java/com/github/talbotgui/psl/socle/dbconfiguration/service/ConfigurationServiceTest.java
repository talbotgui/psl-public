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
package com.github.talbotgui.psl.socle.dbconfiguration.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.DtoTest;
import com.github.talbotgui.psl.socle.dbconfiguration.application.SocleDbconfigurationApplication;
import com.googlecode.catchexception.CatchException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.IndexOptions;

/** Classe de test de ConfigurationService. */
@SpringBootTest(classes = SocleDbconfigurationApplication.class)
//Sans le PER_CLASS car les @BeforeAll et @AfterAll ne fonctionnent pas (Spring démarre avant l'exécution du @BeforeAll qui démarre la BDD)
//@TestInstance(Lifecycle.PER_CLASS)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class ConfigurationServiceTest extends AbstractMongoTest {

	/** Code de la démarche définie dans les fichiers ci-dessous */
	private static final String CODE_DEMARCHE = "bibliotheque";
	/** Fichier de configuration utilisés dans ce test. */
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS = "./src/main/resources/db/bibliotheque-ConfigurationInterneDemarche-1.0.0.json";
	/** Fichier de configuration utilisés dans ce test. */
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS = DtoTest.FICHIER_JSON_TEST_1.getPath();

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	private MongoTemplate mongoTemplate;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	@Autowired
	private ConfigurationService service;

	/** Avant chaque test MAIS AUSSI A LA FIN DE LA CLASSE. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la méthode parente
		super.avantChaqueTest(testInfo);
		// Création du mongoTemplate si ce n'est pas déjà fait
		if (this.mongoTemplate == null) {
			final String chaineConnexion = "mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB;
			this.mongoTemplate = new MongoTemplate(MongoClients.create(chaineConnexion), this.baseDeDonneeMongoDB);
		}
		// Destruction/recréation des collections
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTIONS_CONFIGURATION, new IndexOptions().unique(true));
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTIONS_CONFIGURATION, new IndexOptions().unique(true));
	}

	@Test
	void test01Recherche01UnSeulDocumentExistant() throws IOException {
		//
		final Document document = Document.parse(Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS)));
		this.mongoTemplate.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		//
		final ConfigurationPubliqueDemarcheDto configuration = this.service.rechercherDerniereConfigurationPubliqueDeDemarche(CODE_DEMARCHE);
		//
		Assertions.assertNotNull(configuration);
	}

	@Test
	void test01Recherche02DerniereVersion() throws IOException {
		//
		final String json = Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS));
		this.mongoTemplate.save(Document.parse(json), AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		final String jsonv101 = json.replace("1.0.0", "1.0.1");
		this.mongoTemplate.save(Document.parse(jsonv101), AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		final String jsonv001 = json.replace("1.0.0", "0.0.1");
		this.mongoTemplate.save(Document.parse(jsonv001), AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		//
		final ConfigurationPubliqueDemarcheDto configuration = this.service.rechercherDerniereConfigurationPubliqueDeDemarche(CODE_DEMARCHE);
		//
		Assertions.assertNotNull(configuration);
	}

	@Test
	void test01Recherche03AucunDocument() {
		//
		//
		CatchException.catchException(() -> this.service.rechercherDerniereConfigurationPubliqueDeDemarche(CODE_DEMARCHE));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(MongodbException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02rechercherConfigurationPubliqueDeDemarche() throws IOException {
		//
		final Document document = Document.parse(Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS)));
		this.mongoTemplate.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		final String version = document.getString(AbstractMongoDao.ATTRIBUT_VERSION_CONFIGURATION);
		//
		final ConfigurationPubliqueDemarcheDto configuration = this.service.rechercherConfigurationPubliqueDeDemarche(CODE_DEMARCHE, version);
		//
		Assertions.assertNotNull(configuration);
	}

	@Test
	void test03rechercherDerniereConfigurationInterneDeDemarche() throws IOException {
		//
		final Document document = Document.parse(Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS)));
		this.mongoTemplate.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		//
		final ConfigurationInterneDemarcheDto configuration = this.service.rechercherDerniereConfigurationInterneDeDemarche(CODE_DEMARCHE);
		//
		Assertions.assertNotNull(configuration);
	}
}
