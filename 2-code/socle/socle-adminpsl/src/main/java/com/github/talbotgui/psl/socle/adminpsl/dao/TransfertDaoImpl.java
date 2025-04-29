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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

@Repository
public class TransfertDaoImpl extends AbstractMongoDao implements TransfertDao {

	@Autowired
	public TransfertDaoImpl(@Value("${spring.data.mongodb.database.dbdocument}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(RequeteRechercheTransfertsDto requete) {

		// **** Construction de la requête à mongoDB - debut

		// Liste des critères à appliquer
		List<Criteria> criteres = new ArrayList<>();

		// Ajout du filtre à partir des codes de démarche
		if (requete.codesDemarche() != null && !requete.codesDemarche().isEmpty()) {
			criteres.add(Criteria.where(ATTRIBUT_CODE_DEMARCHE).in(requete.codesDemarche()));
		}

		// Date de début
		if (requete.dateDebut() != null) {
			criteres.add(Criteria.where(ATTRIBUT_DATE_CREATION).gte(requete.dateDebut()));
		}

		// Date de fin
		if (requete.dateFin() != null) {
			criteres.add(Criteria.where(ATTRIBUT_DATE_CREATION).lte(requete.dateFin()));
		}

		// Numéro de télé-dossier
		if (StringUtils.hasLength(requete.numeroTeledossier())) {
			criteres.add(Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(requete.numeroTeledossier()));
		}

		// Base fixe (on ne peut pas envoyer une requête vide) + critères précédemment créés
		Criteria criteria = Criteria.where(ATTRIBUT_CODE_DOCUMENT).is(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT);
		if (!criteres.isEmpty()) {
			criteria.andOperator(criteres);
		}

		// **** Construction de la requête à mongoDB - fin

		// Création de la requête de comptage
		Query requeteComptage = Query.query(criteria);

		// Création de la requete de recherche
		Query requeteRecherche = Query.query(criteria);
		requeteRecherche.with(PageRequest.of(requete.numeroPage(), requete.nombreElementsParPage()));
		requeteRecherche.with(Sort.by(Direction.ASC, ATTRIBUT_DATE_CREATION));
		requeteRecherche.fields().include(ATTRIBUT_NUMERO_TELEDOSSIER, ATTRIBUT_DATE_CREATION, ATTRIBUT_CODE_DEMARCHE);

		// Comptage des données
		long nbElementsTotal = super.compterElementsParRequete(requeteComptage, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);

		// Recherche des données
		List<ResultatRechercheTransfertsDto> resultats = super.rechercherListe(requeteRecherche, ResultatRechercheTransfertsDto.class,
				COLLECTION_MONGODB_POUR_DOCUMENT);

		// Création du DTO en sortie
		return new Page<>(requete.numeroPage(), requete.nombreElementsParPage(), nbElementsTotal, resultats);
	}

}
