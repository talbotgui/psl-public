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
package com.github.talbotgui.psl.socle.commundb.application;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.talbotgui.psl.socle.commundb.utils.InitialisationMongoDBUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import jakarta.annotation.PostConstruct;

/** Classe abstraite mutualisant le comportement de vérification de la pertinence de l'initialisation d'une collection MongoDB. */
public abstract class AbstractInitialisationJeuDeDonnees {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInitialisationJeuDeDonnees.class);

	/** Paramètre de connexion à MongoDB */
	@Value("${spring.data.mongodb.database}")
	protected String baseDeDonneeMongoDB;

	/** Connexion à MongoDB */
	protected MongoClient clientMongo;

	/** Paramètre de connexion à MongoDB */
	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	/** Flag indiquant si un jeu de données doit être renseigné au démarrage du microservice si la collection MongoDB est vide */
	@Value("${jdd.initialisation:false}")
	private Boolean indicateurInitialisationJdd;

	/** Paramètre de connexion à MongoDB */
	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	/** Nom de la collection qui sera initialisée dans MongoDB. */
	protected abstract String getNomCollectionMongoDB();

	/** Execution de l'initialisation du jeu de données à partir des données de DEV. */
	protected abstract void initialiserJeuDeDonnees() throws IOException;

	/** Méthode exécutée au démarrage de l'applicatif */
	@PostConstruct
	public void initialiserSiNecessaireLeJeuDeDonneesAuDemarrage() throws Exception {

		// Si indicateurInitialisationJdd est FALSE, pas d'initialisation
		if (this.indicateurInitialisationJdd == null || !this.indicateurInitialisationJdd.booleanValue()) {
			return;
		}

		// Connexion à MongoDB et comptage des objets dans la collection
		String nomCollectionMongoDB = this.getNomCollectionMongoDB();
		this.clientMongo = MongoClients.create("mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB);

		// Initialisation de la collection
		long nbDocuments = InitialisationMongoDBUtils.initialiserCollection(this.clientMongo, this.baseDeDonneeMongoDB,
				this.getNomCollectionMongoDB(), null);

		// Si la collection est vide
		if (nbDocuments == 0) {
			LOGGER.warn(
					"Initialisation du jeu de données avec les données de développement pour la collection {}. Ceci ne doit pas s'exécuter en production !!",
					nomCollectionMongoDB);
			// Exécution de l'initialisation du jeu de données
			this.initialiserJeuDeDonnees();
		} else {
			LOGGER.info("Initialisation du jeu de données inutile car {} document(s) existe(nt) déjà", nbDocuments);
		}
	}
}
