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
package com.github.talbotgui.psl.socle.dbconfiguration.application;

import java.io.IOException;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commundb.application.AbstractInitialisationJeuDeDonnees;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.utils.InitialisationMongoDBUtils;
import com.github.talbotgui.psl.socle.commundb.utils.TemplateUtils;
import com.mongodb.client.MongoCollection;

/**
 * Classe initialisant (à la demande et si besoin) un jeu de données de dev (depuis les bouchons des projets Front Angular).
 */
@Component
public class InitialisationJeuDeDonneesPourConfigurationInterne extends AbstractInitialisationJeuDeDonnees {

	/** LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(InitialisationJeuDeDonneesPourConfigurationInterne.class);

	/** Pattern de recherche des fichiers de configuration interne de démarche présents dans le classpath */
	private static final String PATTERN_FICHIER_DE_CONFIGURATION_INTERNE = "*-ConfigurationInterneDemarche-*";

	@Override
	protected String getNomCollectionMongoDB() {
		return AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE;
	}

	@Override
	protected void initialiserJeuDeDonnees() throws IOException {
		// Recherche des configurations internes
		Map<Resource, String> fichiersConfigurationInterne = InitialisationMongoDBUtils
				.rechercherDocumentsDansLeClasspath(TemplateUtils.PATTERN_FICHIER_DANS_LE_CLASSPATH + PATTERN_FICHIER_DE_CONFIGURATION_INTERNE);

		// Récupération de la collection MongoDB
		MongoCollection<Document> collection = this.clientMongo.getDatabase(super.baseDeDonneeMongoDB).getCollection(this.getNomCollectionMongoDB());

		// Pour chaque fichier de configuration interne
		ResourcePatternResolver resourceResolver = ResourcePatternUtils.getResourcePatternResolver(null);
		for (Map.Entry<Resource, String> fichierConfigurationInterne : fichiersConfigurationInterne.entrySet()) {
			Resource fichier = fichierConfigurationInterne.getKey();
			String json = fichierConfigurationInterne.getValue();

			// Recherche des templates de document à intégrer dans cette configuration interne
			json = TemplateUtils.integrerTemplatesDeDocument(json, resourceResolver);

			// Insertion en base
			LOGGER.info("Fichier \"{}\" inséré dans la collection", fichier.getURI());
			collection.insertOne(Document.parse(json));
		}
	}
}
