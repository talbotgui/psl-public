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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.DbconfigurationClient;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbconfiguration", contextId = "ConfigurationApi")
public interface ConfigurationAPI {

	/**
	 * Lecture de la configuration publique précise d'une démarche.
	 *
	 * @param codeDemarche         Code de la démarche.
	 * @param versionConfiguration Version de la configuration.
	 * @return Configuration trouvée
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbconfigurationClient.URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE_AVEC_VERSION)
	ConfigurationPubliqueDemarcheDto rechercherConfigurationPubliqueDeDemarche(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") final String codeDemarche,
			@Pattern(regexp = RegexUtils.VERSION_CONFIGURATION) @PathVariable(name = "versionConfiguration") final String versionConfiguration);

	/**
	 * Lecture de la dernière configuration interne disponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbconfigurationClient.URI_LECTURE_CONFIGURATION_INTERNE_DEMARCHE)
	ConfigurationInterneDemarcheDto rechercherDerniereConfigurationInterneDeDemarche(
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") final String codeDemarche);

	/**
	 * Lecture de la dernière configuration publique disponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbconfigurationClient.URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE)
	ConfigurationPubliqueDemarcheDto rechercherDerniereConfigurationPubliqueDeDemarche(
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche);

}