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
package com.github.talbotgui.psl.socle.adminpsl.apiclient.api;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

public interface AdminPslConfigInterneAPI {

	/** URI de la liste des versions de configuration publique et de la création. */
	public static final String URI_CONFIG_INTERNE_CREATION_ET_LISTE = AdminPslAPI.URI_BASE + "/config/{codeDemarche}/interne";

	/** URI de la lecture et modification de configuration publique. */
	public static final String URI_CONFIG_INTERNE_MODIFICATION_ET_LECTURE = AdminPslAPI.URI_BASE + "/config/{codeDemarche}/interne/{idConfiguration}";

	@SecurityRequirement(name = "bearer-admin")
	@GetMapping(value = URI_CONFIG_INTERNE_MODIFICATION_ET_LECTURE, produces = MediaType.APPLICATION_JSON_VALUE)
	Object chargerVersionDeConfigurationInterne(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idConfiguration") String idConfiguration);

	@SecurityRequirement(name = "bearer-admin")
	@PostMapping(value = URI_CONFIG_INTERNE_CREATION_ET_LISTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String creerVersionDeConfigurationInterne(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.JSON) @RequestBody String configuration);

	@SecurityRequirement(name = "bearer-admin")
	@GetMapping(value = URI_CONFIG_INTERNE_CREATION_ET_LISTE, produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String, String> listerLesVersionsDeConfigurationInterne(
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche);

	@SecurityRequirement(name = "bearer-admin")
	@PostMapping(value = URI_CONFIG_INTERNE_MODIFICATION_ET_LECTURE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String modifierVersionDeConfigurationInterne(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idConfiguration") String idConfiguration,
			@Pattern(regexp = RegexUtils.JSON) @RequestBody String configuration);
}