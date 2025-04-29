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
package com.github.talbotgui.psl.socle.soumission.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.soumission.apiclient.SoumissionClient;
import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-soumission", contextId = "SoumissionAPI")
public interface SoumissionAPI {

	/**
	 * API exposant le service de soumission.
	 *
	 * @param dto Données soumises.
	 * @return Numéro de télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = SoumissionClient.URI_SOUMETTRE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String soumettreUnTeledossier(@Valid @RequestBody DonneesDeSoumissionDto dto);
}