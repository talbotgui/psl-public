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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public interface AdminPslTransfertAPI {

	/** URI de génération de soumissions en masse. */
	public static final String URI_GENERATION_TRANSFERTS = AdminPslAPI.URI_BASE + "/genererDesTransferts/{nbTransferts}";

	/** URI des détails d'un transfert. */
	public static final String URI_TRANSFERT_DETAILS = AdminPslAPI.URI_BASE + "/transfert/{idTransfert}";

	/** URI de la liste des transferts via une recherche multi-critères. */
	public static final String URI_TRANSFERT_LISTE = AdminPslAPI.URI_BASE + "/transfert";

	@GetMapping(URI_TRANSFERT_DETAILS)
	Object chargerDetailsDunTransfert(@Pattern(regexp = RegexUtils.ID) @PathVariable("idTransert") String idTransert);

	@GetMapping(URI_GENERATION_TRANSFERTS)
	void genererDesTeledossiers(@Min(0) @PathVariable("nbTransferts") int nbTransferts);

	@SecurityRequirement(name = "bearer-admin")
	@PostMapping(value = URI_TRANSFERT_LISTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(@Valid @RequestBody RequeteRechercheTransfertsDto parametresRecherche);

}