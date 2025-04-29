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
package com.github.talbotgui.psl.socle.dbbrouillon.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.DbbrouillonClient;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
// "name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbbrouillon", contextId = "BrouillonApi")
public interface BrouillonAPI {

	/**
	 * Obtenir l'authentification nécessaire pour un brouillon donné (son identifiant).
	 *
	 * @param codeDemarche Code de la démarche.
	 * @param idBrouillon  Identifiant du brouillon
	 * @return Le type d'authentification nécessaire
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbbrouillonClient.URI_AUTHENTIFICATION_BROUILLON)
	String obtenirAuthentificationDunBrouillon(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idBrouillon") String idBrouillon);

	/**
	 * API exposant le service de sauvegarde d'un brouillon.
	 *
	 * @param dto Données soumises.
	 * @return Numéro de télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbbrouillonClient.URI_SAUVEGARDE_BROUILLON, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String sauvegarderBrouillon(@Valid @RequestBody BrouillonDto dto);

	/**
	 * Supprimer le brouillon.
	 *
	 * @param codeDemarche Code de démarche.
	 * @param idBrouillon  Identifiant du brouillon.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbbrouillonClient.URI_SUPPRESSION_BROUILLON)
	void supprimerBrouillon(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idBrouillon") String idBrouillon);

	/**
	 * Téléchargement du brouillon par son identifiant.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @param idBrouillon  Identifiant du brouillon
	 * @return Les données du document
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbbrouillonClient.URI_LECTURE_BROUILLON)
	BrouillonDto telechargerBrouillon(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @PathVariable(name = "codeDemarche") String codeDemarche,
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idBrouillon") String idBrouillon);

}