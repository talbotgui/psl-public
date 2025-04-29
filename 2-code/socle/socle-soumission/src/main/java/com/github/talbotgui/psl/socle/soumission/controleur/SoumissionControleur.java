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
package com.github.talbotgui.psl.socle.soumission.controleur;

import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.SoumissionAPI;
import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;
import com.github.talbotgui.psl.socle.soumission.service.soumission.SoumissionService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class SoumissionControleur implements SoumissionAPI {

	private final SoumissionService service;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public SoumissionControleur(SoumissionService service) {
		super();
		this.service = service;
	}

	@Override
	public String soumettreUnTeledossier(DonneesDeSoumissionDto dto) {
		// Lecture des entêtes
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();
		// Encadrement de la chaine de caractères retournée par des " pour en faire du JSON valide
		return "\"" + this.service.soumettreUnTeledossier(tokenJwt, dto) + "\"";
	}
}