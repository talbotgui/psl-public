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
package com.github.talbotgui.psl.socle.dbconfiguration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;

@Service
public class ConfigurationDaoImpl extends AbstractMongoDao implements ConfigurationDao {

	@Autowired
	public ConfigurationDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public ConfigurationPubliqueDemarcheDto rechercherConfigurationPubliqueDeDemarche(String codeDemarche, String versionConfiguration) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche))//
				.addCriteria(Criteria.where(ATTRIBUT_VERSION_CONFIGURATION).is(versionConfiguration));
		return super.rechercherUn(requete, ConfigurationPubliqueDemarcheDto.class, COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
	}

	@Override
	public ConfigurationInterneDemarcheDto rechercherDerniereConfigurationInterneDeDemarche(String codeDemarche) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		requete.with(Sort.by(Direction.DESC, ATTRIBUT_VERSION_CONFIGURATION));
		return super.rechercherUn(requete, ConfigurationInterneDemarcheDto.class, COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
	}

	@Override
	public ConfigurationPubliqueDemarcheDto rechercherDerniereConfigurationPubliqueDeDemarche(String codeDemarche) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_CODE_DEMARCHE).is(codeDemarche));
		requete.with(Sort.by(Direction.DESC, ATTRIBUT_VERSION_CONFIGURATION));
		return super.rechercherUn(requete, ConfigurationPubliqueDemarcheDto.class, COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
	}
}
