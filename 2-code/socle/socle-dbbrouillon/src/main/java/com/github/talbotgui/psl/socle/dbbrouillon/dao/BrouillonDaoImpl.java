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
package com.github.talbotgui.psl.socle.dbbrouillon.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;

@Service
public class BrouillonDaoImpl extends AbstractMongoDao implements BrouillonDao {

	@Autowired
	public BrouillonDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public BrouillonDto rechercherBrouillon(String codeDemarche, String idBrouillon) {
		Criteria criteres = Criteria.where(ATTRIBUT_ID).is(idBrouillon)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		Query requete = Query.query(criteres);
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_ORDRE_PRESENTATION));
		return super.rechercherUn(requete, BrouillonDto.class, COLLECTION_MONGODB_POUR_BROUILLON);
	}

	@Override
	public String sauvegarderBrouillon(BrouillonDto dto) {
		if (dto.getId() != null) {
			return super.mettreAjour(dto.getId(), dto, COLLECTION_MONGODB_POUR_BROUILLON).getId();
		} else {
			return super.inserer(dto, COLLECTION_MONGODB_POUR_BROUILLON).getId();
		}
	}

	@Override
	public void supprimerBrouillon(String codeDemarche, String idBrouillon) {
		Criteria criteres = Criteria.where(ATTRIBUT_ID).is(idBrouillon)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		Query requete = Query.query(criteres);
		super.supprimerParRequete(requete, COLLECTION_MONGODB_POUR_BROUILLON);
	}
}
