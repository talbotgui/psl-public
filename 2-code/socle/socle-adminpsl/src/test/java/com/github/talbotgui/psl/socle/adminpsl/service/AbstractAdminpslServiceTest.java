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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.IndexOptions;

/** Constantes de test. */
abstract class AbstractAdminpslServiceTest extends AbstractMongoTest {
	static final String CODE_DEMARCHE_ETATCIVIL = "etatcivil";
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS_1 = new File(
			"../socle-dbconfiguration/src/main/resources/db/etatCivil-ConfigurationInterneDemarche-1.0.0.json").getPath();
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS_2 = new File(
			"../socle-dbconfiguration/src/main/resources/db/jechangedecoordonnees-ConfigurationInterneDemarche-1.0.0.json").getPath();
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS_1 = new File(
			"../../front/projects/generique/public/bouchonapi/etatcivil-param.json").getPath();
	static final String PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS_2 = new File(
			"../../front/projects/specifique/public/bouchonapi/param.json").getPath();

	@Value("${spring.data.mongodb.database.dbbrouillon}")
	private String baseDeDonneeBrouillonMongoDB;
	@Value("${spring.data.mongodb.database.dbconfiguration}")
	private String baseDeDonneeConfigurationMongoDB;
	@Value("${spring.data.mongodb.database.dbdocument}")
	private String baseDeDonneeDocumentMongoDB;

	/** Jeu de données : configuration interne 1. */
	protected String contenuFichierInterne1;
	/** Jeu de données : configuration interne 2. */
	protected String contenuFichierInterne2;
	/** Jeu de données : configuration publique 1. */
	protected String contenuFichierPublic1;
	/** Jeu de données : configuration publique 2. */
	protected String contenuFichierPublic2;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	protected MongoTemplate mongoTemplateBrouillon;
	protected MongoTemplate mongoTemplateConfiguration;
	protected MongoTemplate mongoTemplateDocument;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	/** Après tous les tests. */
	@AfterEach
	void apresChaqueTest() {
		// pour permettre au micro-service de configuration, à son démarrage, d'initialiser un jeu de donnée.
		this.mongoTemplateConfiguration.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		this.mongoTemplateConfiguration.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		this.mongoTemplateBrouillon.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		this.mongoTemplateDocument.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		this.mongoTemplateDocument.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
	}

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la méthode parente
		super.avantChaqueTest(testInfo);

		// Création du mongoTemplate si ce n'est pas déjà fait
		final String chaineConnexion = "mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB;
		final MongoClient client = MongoClients.create(chaineConnexion);
		if (this.mongoTemplateConfiguration == null) {
			this.mongoTemplateConfiguration = new MongoTemplate(client, this.baseDeDonneeConfigurationMongoDB);
		}
		if (this.mongoTemplateBrouillon == null) {
			this.mongoTemplateBrouillon = new MongoTemplate(client, this.baseDeDonneeBrouillonMongoDB);
		}
		if (this.mongoTemplateDocument == null) {
			this.mongoTemplateDocument = new MongoTemplate(client, this.baseDeDonneeDocumentMongoDB);
		}

		// Destruction/recréation des collections
		this.mongoTemplateConfiguration.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		this.mongoTemplateConfiguration.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		this.mongoTemplateBrouillon.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		this.mongoTemplateDocument.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT);
		this.mongoTemplateDocument.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);
		this.mongoTemplateConfiguration.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTIONS_CONFIGURATION, new IndexOptions().unique(true));
		this.mongoTemplateConfiguration.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTIONS_CONFIGURATION, new IndexOptions().unique(true));
		this.mongoTemplateBrouillon.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		this.mongoTemplateDocument.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_DOCUMENT)//
				.createIndex(AbstractMongoDao.INDEX_COLLECTION_DOCUMENT, new IndexOptions().unique(true));
		this.mongoTemplateDocument.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_PIECE_JOINTE);

		// Chargement du contenu des fichiers de test
		try {
			this.contenuFichierPublic1 = Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS_1));
			this.contenuFichierPublic2 = Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_PUBLIQUE_POUR_TESTS_2));
			this.contenuFichierInterne1 = Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS_1));
			this.contenuFichierInterne2 = Files.readString(Paths.get(PATH_CHEMIN_FICHIER_CONFIGURATION_INTERNE_POUR_TESTS_2));
		} catch (final IOException e) {
			Assertions.fail(e);
		}
	}
}
