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

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;

@Repository
public class ConfigurationPubliqueDaoImpl extends AbstractMongoDao implements ConfigurationPubliqueDao {

	@Autowired
	public ConfigurationPubliqueDaoImpl(@Value("${spring.data.mongodb.database.dbconfiguration}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public Set<String> listerCodesDemarche() {
		// Exécution de la requête
		return super.listerDistinct(ATTRIBUT_CODE_DEMARCHE, String.class, COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
	}
}
