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
package com.github.talbotgui.psl.socle.dbconfiguration.controleur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.service.ConfigurationService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class ConfigurationControleur implements ConfigurationAPI {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationControleur.class);

	@Autowired
	private ConfigurationService service;

	@Override
	public ConfigurationPubliqueDemarcheDto rechercherConfigurationPubliqueDeDemarche(String codeDemarche, String versionConfiguration) {
		LOGGER.trace("Lecture de la configuration publique d'une démarche pour '{}-{}'", codeDemarche, versionConfiguration);

		// L'API de lecture ne transforme pas le document en objet pour le re-sérialiser dans la foulée
		return this.service.rechercherConfigurationPubliqueDeDemarche(codeDemarche, versionConfiguration);
	}

	@Override
	public ConfigurationInterneDemarcheDto rechercherDerniereConfigurationInterneDeDemarche(final String codeDemarche) {
		LOGGER.trace("Lecture de la dernière configuration interne disponible d'une démarche pour '{}'", codeDemarche);

		// L'API de lecture ne transforme pas le document en objet pour le re-sérialiser dans la foulée
		return this.service.rechercherDerniereConfigurationInterneDeDemarche(codeDemarche);
	}

	@Override
	public ConfigurationPubliqueDemarcheDto rechercherDerniereConfigurationPubliqueDeDemarche(final String codeDemarche) {
		LOGGER.trace("Lecture de la dernière configuration publique disponible d'une démarche pour '{}'", codeDemarche);

		// L'API de lecture ne transforme pas le document en objet pour le re-sérialiser dans la foulée
		return this.service.rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);
	}
}