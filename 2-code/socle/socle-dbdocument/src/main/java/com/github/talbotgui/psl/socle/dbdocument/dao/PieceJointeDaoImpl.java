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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe;

@Service
public class PieceJointeDaoImpl extends AbstractMongoDao implements PieceJointeDao {

	@Autowired
	public PieceJointeDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public void associerPieceJointeAunTeledossier(String idPieceJointe, String numeroTeledossier) {
		Update update = new Update();
		update.set(ATTRIBUT_NUMERO_TELEDOSSIER, numeroTeledossier);

		Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(idPieceJointe));
		super.modifierPremierElementTrouve(requete, PieceJointe.class, COLLECTION_MONGODB_POUR_PIECE_JOINTE, update);
	}

	@Override
	public void desassocierPiecesJointesDuTeledossier(String numeroTeledossier) {
		Update update = new Update();
		update.unset(ATTRIBUT_NUMERO_TELEDOSSIER);

		Query requete = Query.query(Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier));

		super.modifierTousLesElementsTrouves(requete, PieceJointe.class, COLLECTION_MONGODB_POUR_PIECE_JOINTE, update);
	}

	@Override
	public PieceJointe rechercherPieceJointe(String codeDemarche, String codePieceJointe) {
		Criteria criteres = Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_PIECE_JOINTE).is(codePieceJointe));
		Query requete = Query.query(criteres);
		return super.rechercherUn(requete, PieceJointe.class, COLLECTION_MONGODB_POUR_PIECE_JOINTE);
	}

	@Override
	public List<PieceJointe> rechercherPiecesJointes(String codeDemarche, String codePieceJointe) {
		Criteria criteres = Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_PIECE_JOINTE).is(codePieceJointe));
		Query requete = Query.query(criteres);
		return super.rechercherListe(requete, PieceJointe.class, COLLECTION_MONGODB_POUR_PIECE_JOINTE);
	}

	@Override
	public String sauvegarderPieceJointe(PieceJointe dto) {
		return super.inserer(dto, COLLECTION_MONGODB_POUR_PIECE_JOINTE).getId();
	}

	@Override
	public void supprimerPieceJointeNonSoumise(String idPieceJointe) {
		super.supprimerParId(idPieceJointe, COLLECTION_MONGODB_POUR_PIECE_JOINTE);
	}

	@Override
	public void supprimerPiecesJointesDuTeledossier(String numeroTeledossier) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier));
		super.supprimerParRequete(requete, COLLECTION_MONGODB_POUR_PIECE_JOINTE);

	}
}
