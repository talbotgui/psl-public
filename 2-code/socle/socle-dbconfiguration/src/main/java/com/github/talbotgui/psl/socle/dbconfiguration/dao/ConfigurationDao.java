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

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;

public interface ConfigurationDao {

	/**
	 * Lecture d'une version précise de la configuration publique d'une démarche.
	 *
	 * @param codeDemarche         Code de la démarche.
	 * @param versionConfiguration Version de la configuration.
	 * @return Configuration trouvée
	 */
	ConfigurationPubliqueDemarcheDto rechercherConfigurationPubliqueDeDemarche(String codeDemarche, String versionConfiguration);

	/**
	 * Lecture de la dernière configuration internedisponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	ConfigurationInterneDemarcheDto rechercherDerniereConfigurationInterneDeDemarche(String codeDemarche);

	/**
	 * Lecture de la dernière configuration publique disponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	ConfigurationPubliqueDemarcheDto rechercherDerniereConfigurationPubliqueDeDemarche(String codeDemarche);
}