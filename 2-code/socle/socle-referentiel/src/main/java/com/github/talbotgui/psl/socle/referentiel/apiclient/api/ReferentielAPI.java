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
package com.github.talbotgui.psl.socle.referentiel.apiclient.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.referentiel.apiclient.ReferentielClient;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-referentiel", contextId = "ReferentielAPI")
public interface ReferentielAPI {

	/**
	 * Recherche d'une commune de naissance (commune existante et passées).
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de communes.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_COMMUNE_NAISSANCE)
	List<CommuneNaissanceDto> rechercherCommuneNaissance(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'une commune actuelle.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de communes.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_COMMUNE_ACTUELLE)
	List<CommuneActuelleDto> rechercherCommunesActuelles(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'une commune associée aux élections.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de communes.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_COMMUNE_UGLE)
	List<CommuneUgleDto> rechercherCommuneUgle(@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'un département actuel.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de départements.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_DEPARTEMENT_ACTUEL)
	List<DepartementActuelDto> rechercherDepartementsActuels(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'une nationalité de pays actuel.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de pays.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_NATIONALITE_PAYS_ACTUEL)
	List<PaysActuelDto> rechercherNationalitesDePaysActuel(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'un pays actuel.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de pays.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_PAYS_ACTUEL)
	List<PaysActuelDto> rechercherPaysActuels(@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'un pays de naissance (existant et passée).
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de pays.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_PAYS_NAISSANCE)
	List<PaysNaissanceDto> rechercherPaysNaissance(@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

	/**
	 * Recherche d'une région actuelle.
	 *
	 * @param recherche Chaîne de caractères à rechercher.
	 * @return Liste de régions.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(ReferentielClient.URI_REGION_ACTUELLE)
	List<RegionActuelleDto> rechercherRegionsActuelles(
			@Pattern(regexp = RegexUtils.RECHERCHE) @RequestParam(value = "recherche") final String recherche);

}