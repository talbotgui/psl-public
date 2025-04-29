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
package com.github.talbotgui.psl.socle.referentiel.controleur;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.referentiel.apiclient.api.ReferentielAPI;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.service.ReferentielService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class ReferentielControleur implements ReferentielAPI {

	@Autowired
	private ReferentielService referentielService;

	@Override
	public List<CommuneNaissanceDto> rechercherCommuneNaissance(String recherche) {
		return this.referentielService.rechercherCommuneNaissance(recherche);
	}

	@Override
	public List<CommuneActuelleDto> rechercherCommunesActuelles(String recherche) {
		return this.referentielService.rechercherCommunesActuelles(recherche);
	}

	@Override
	public List<CommuneUgleDto> rechercherCommuneUgle(String recherche) {
		return this.referentielService.rechercherCommuneUgle(recherche);
	}

	@Override
	public List<DepartementActuelDto> rechercherDepartementsActuels(String recherche) {
		return this.referentielService.rechercherDepartementsActuels(recherche);
	}

	@Override
	public List<PaysActuelDto> rechercherNationalitesDePaysActuel(String recherche) {
		return this.referentielService.rechercherNationalitesDePaysActuel(recherche);
	}

	@Override
	public List<PaysActuelDto> rechercherPaysActuels(String recherche) {
		return this.referentielService.rechercherPaysActuels(recherche);
	}

	@Override
	public List<PaysNaissanceDto> rechercherPaysNaissance(String recherche) {
		return this.referentielService.rechercherPaysNaissance(recherche);
	}

	@Override
	public List<RegionActuelleDto> rechercherRegionsActuelles(String recherche) {
		return this.referentielService.rechercherRegionsActuelles(recherche);
	}
}
