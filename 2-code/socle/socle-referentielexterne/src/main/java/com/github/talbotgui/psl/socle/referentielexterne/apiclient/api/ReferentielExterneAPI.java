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
package com.github.talbotgui.psl.socle.referentielexterne.apiclient.api;

import java.util.Collection;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.ReferentielExterneClient;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-referentielexterne", contextId = "ReferentielExterneAPI")
public interface ReferentielExterneAPI {

	/**
	 * Recherche d'une adresse dans la BAN.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @param codeInsee Code INSEE (optionnel).
	 * @return une liste d'adresses possibles.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielExterneClient.URI_ADRESSE_BAN)
	Collection<Map<String, String>> rechercherAdresseBan(//
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche,
			@Pattern(regexp = RegexUtils.CODE_INSEE) @RequestParam(value = "codeInsee", required = false) final String codeInsee);

	/**
	 * Recherche d'une commune dans la BAN.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return une liste de commune.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielExterneClient.URI_COMMUNE_BAN)
	public Collection<Map<String, String>> rechercherCommuneBan(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

}