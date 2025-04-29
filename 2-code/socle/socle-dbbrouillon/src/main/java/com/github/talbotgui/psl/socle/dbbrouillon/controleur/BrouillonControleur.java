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
package com.github.talbotgui.psl.socle.dbbrouillon.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.api.BrouillonAPI;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;
import com.github.talbotgui.psl.socle.dbbrouillon.service.BrouillonService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class BrouillonControleur implements BrouillonAPI {

	@Autowired
	private BrouillonService service;

	@Override
	public String obtenirAuthentificationDunBrouillon(String codeDemarche, String idBrouillon) {
		String auth = this.service.obtenirAuthentificationDunBrouillon(codeDemarche, idBrouillon);
		return "\"" + (auth==null?"":auth) + "\"";
	}

	@Override
	public String sauvegarderBrouillon(BrouillonDto dto) {
		// Lecture des entêtes
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Encadrement de la chaine de caractères retournée par des " pour en faire du JSON valide
		return "\"" + this.service.sauvegarderBrouillon(tokenJwt, dto) + "\"";
	}

	@Override
	public void supprimerBrouillon(String codeDemarche, String idBrouillon) {
		// Lecture des entêtes
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Suppression
		this.service.supprimerBrouillon(tokenJwt, codeDemarche, idBrouillon);
	}

	@Override
	public BrouillonDto telechargerBrouillon(final String codeDemarche, final String idBrouillon) {
		// Lecture des entêtes
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Chargement du brouillon
		return this.service.rechercherBrouillon(tokenJwt, codeDemarche, idBrouillon);
	}
}