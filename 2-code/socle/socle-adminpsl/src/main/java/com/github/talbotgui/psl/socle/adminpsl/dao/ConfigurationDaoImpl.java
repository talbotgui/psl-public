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
package com.github.talbotgui.psl.socle.adminpsl.dao;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.github.talbotgui.psl.socle.adminpsl.dto.AdminConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.adminpsl.dto.AdminConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.adminpsl.dto.ElementConfigurationAjouterPourAdministration;
import com.github.talbotgui.psl.socle.adminpsl.exception.AdminpslException;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;

@Repository
public class ConfigurationDaoImpl extends AbstractMongoDao implements ConfigurationDao {

	@Autowired
	public ConfigurationDaoImpl(@Value("${spring.data.mongodb.database.dbconfiguration}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public String chargerVersionDeConfiguration(boolean publique, String codeDemarche, String id) {
		String nomCollection = this.definirNomCollection(publique);

		Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(id)//
				.and(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		requete.fields().exclude(ATTRIBUT_ID);
		return super.rechercherUn(requete, String.class, nomCollection);
	}

	/** Définit le nom de la collection à utiliser. */
	private String definirNomCollection(boolean publique) {
		return publique ? COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE : COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE;
	}

	@Override
	public String insererVersionDeConfiguration(boolean publique, Document document) {
		String nomCollection = this.definirNomCollection(publique);

		// Suppression de l'ID pour qu'il en reçoive un nouveau
		document.remove(ATTRIBUT_ID);

		// Insertion en base
		Document d = super.inserer(document, nomCollection);

		// Renvoi de l'ID
		return super.lireId(d);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ElementConfigurationAjouterPourAdministration> listerLesVersionsDeConfiguration(boolean publique, String codeDemarche) {
		String nomCollection = this.definirNomCollection(publique);
		Class<? extends ElementConfigurationAjouterPourAdministration> clazz = publique ? AdminConfigurationPubliqueDemarcheDto.class
				: AdminConfigurationInterneDemarcheDto.class;

		// Requête à mongoDB
		Query requete = Query.query(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_DATE_CREATION));
		requete.fields().include(ATTRIBUT_ID, ATTRIBUT_VERSION_CONFIGURATION, ATTRIBUT_DATE_CREATION, ATTRIBUT_CREATEUR,
				ATTRIBUT_COMMENTAIRE_CREATION);
		return (List<ElementConfigurationAjouterPourAdministration>) super.rechercherListe(requete, clazz, nomCollection);
	}

	@Override
	public String mettreAjourVersionDeConfiguration(boolean publique, String id, Document document) {
		String nomCollection = this.definirNomCollection(publique);

		// Suppression de l'ID pour que la mise à jour soit complète (sinon Mongo hurle)
		document.remove(ATTRIBUT_ID);

		// MaJ en base
		document = super.mettreAjour(id, document, nomCollection);

		// Renvoi de l'ID
		return super.lireId(document);
	}

	@Override
	public Document verifierCoherenceDuCodeDeDemarche(String codeDemarche, String id, String configuration) {
		// Parse du document
		Document document = Document.parse(configuration);

		// Vérification du code de démarche
		String codeDemarcheDansDocument = document.getString(ATTRIBUT_CODE_DEMARCHE);
		if (codeDemarcheDansDocument == null || !codeDemarcheDansDocument.equals(codeDemarche)) {
			throw new AdminpslException(AdminpslException.ERREUR_CODE_DEMARCHE_INCOHERENT);
		}

		return document;
	}
}
