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
package com.github.talbotgui.psl.socle.dbbrouillon.application;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commundb.application.AbstractInitialisationJeuDeDonnees;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.utils.InitialisationMongoDBUtils;

/**
 * Classe initialisant (à la demande et si besoin) un jeu de données de dev (depuis les bouchons des projets Front Angular).
 */
@Component
public class InitialisationJeuDeDonnees extends AbstractInitialisationJeuDeDonnees {

	/** Début du nom des fichiers de brouillon dans les assets des projets Front Angular */
	private static final String PREFIX_FICHIER_BROUILLON_DANS_ASSETS_ANGULAR = "brouillon";

	@Override
	protected String getNomCollectionMongoDB() {
		return AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON;
	}

	@Override
	protected void initialiserJeuDeDonnees() throws IOException {
		// Initialisation de la collection
		InitialisationMongoDBUtils.initialiserCollection(super.clientMongo, super.baseDeDonneeMongoDB, this.getNomCollectionMongoDB(), null);

		// Insertion des brouillons présents dans les sources des projets FRONT (répertoire de bouchon)
		InitialisationMongoDBUtils.insererDocumentsDepuisLesFichiersDeBouchonDesProjetsFront(this.clientMongo, super.baseDeDonneeMongoDB,
				this.getNomCollectionMongoDB(), PREFIX_FICHIER_BROUILLON_DANS_ASSETS_ANGULAR);
	}
}
