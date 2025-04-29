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
package com.github.talbotgui.psl.socle.referentielexterne.client;

import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ReponseBanDto;

public interface BanClient {

	static final String LIMITE_NB_RESULTATS = "20";
	static final String PARAM_CODE_INSEE = "citycode";
	static final String PARAM_LIMITE = "limit";
	static final String PARAM_REQUETE = "q";
	static final String PARAM_TYPE = "type";
	static final String TYPE_ADRESSE = "housenumber";
	static final String TYPE_COMMUNE = "municipality";

	/**
	 * Interroge la BAN pour trouver une adresse.
	 *
	 * @param recherche Requête.
	 * @param codeInsee Un code INSEE (optionnel)
	 * @return Reponse de la BAN.
	 */
	ReponseBanDto rechercherAdresseBan(String recherche, String codeInsee);

	/**
	 * Interroge la BAN pour trouver une commune.
	 *
	 * @param recherche Requête.
	 * @return Reponse de la BAN.
	 */
	ReponseBanDto rechercherCommuneBan(String recherche);

}