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
package com.github.talbotgui.psl.socle.dbdocument.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.StatutDocumentTransfert;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

@Service
public class TransfertDaoImpl extends AbstractMongoDao implements TransfertDao {

	@Autowired
	public TransfertDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public void modifierEtatDuTransfert(String idDocumentTransfert, StatutDocumentTransfert statut) {
		// La modification à réaliser
		Update modifications = new Update();
		modifications.set(ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT, statut.name());

		// Déclenchement
		super.mettreAjourUnAttribut(idDocumentTransfert, modifications, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);
	}

	@Override
	public MessageSauvegardeDocumentDto rechercherDocumentOuPieceJointeDuTeledossier(String idDocument) {
		// Requête de recherche par ID
		Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(idDocument));
		try {
			// On tente de trouver le document parmi les documents générés
			return super.rechercherUn(requete, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);
		} catch (MongodbException e) {
			// Si le document généré n'existe pas
			if (AbstractException.equals(e, MongodbException.ERREUR_DOCUMENT_NON_EXISTANT)) {
				// On cherche dans les pièces jointes
				return super.rechercherUn(requete, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_PIECE_JOINTE);
			}
			// Sinon throw de l'exception
			else {
				throw e;
			}
		}
	}

	/**
	 * Recherche du premier document décrivant un transfert à réaliser (tri par date de création descendant) et pose d'un verrou.
	 *
	 * @param statut Statut du document à rechercher (null si le document ne doit pas avoir de statut).
	 * @return l'ID du document.
	 */
	private String rechercherEtVerrouillerUnDocumentDeTranfertAtraiter(StatutDocumentTransfert statut) {
		// La modification à réaliser
		Update modifications = new Update();
		modifications.set(ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT, StatutDocumentTransfert.EN_COURS.name());

		// Création de la requête en fonction du statut fourni
		Criteria criteresFiltre = Criteria.where(ATTRIBUT_CODE_DOCUMENT).is(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT);
		if (statut != null) {
			criteresFiltre.andOperator(Criteria.where(ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT).is(statut.name()));
		} else {
			criteresFiltre.andOperator(Criteria.where(ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT).isNull());
		}
		Query requete = Query.query(criteresFiltre);
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_DATE_CREATION));

		// recherche & modification
		MessageMetadonneesDocumentDto document = super.rechercherPremierDocumentEtModifier(requete, modifications,
				MessageMetadonneesDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);

		// Renvoi de l'ID si le document est trouvé
		return document != null ? document.getId() : null;
	}

	// @see https://www.mongodb.com/community/forums/t/preventing-concurrent-updates-to-ensure-every-thread-gets-a-unique-id-value/14378/4
	@Override
	public String rechercherEtVerrouillerUnDocumentDeTransfertAtraiter() {

		// Recherche d'un document de transfert jamais traité
		String id = this.rechercherEtVerrouillerUnDocumentDeTranfertAtraiter(null);

		// Si aucun trouvé
		if (id == null) {
			// Recheche d'un document de tranfert au statut EN_ERREUR
			id = this.rechercherEtVerrouillerUnDocumentDeTranfertAtraiter(StatutDocumentTransfert.EN_ERREUR);
		}

		// Renvoi de l'éventuel ID trouvé
		return id;
	}
}
