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
package com.github.talbotgui.psl.socle.referentielexterne.controleur;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.referentielexterne.apiclient.api.ReferentielExterneAPI;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ResultatBanDto;
import com.github.talbotgui.psl.socle.referentielexterne.client.BanClient;

/**
 * Principal controleur REST du projet
 */
@RestController
public class ReferentielExterneControleur implements ReferentielExterneAPI {

	@Autowired
	private BanClient clientBan;

	@Override
	public Collection<Map<String, String>> rechercherAdresseBan(String recherche, String codeInsee) {
		// On prend les résultats
		return this.clientBan.rechercherAdresseBan(recherche, codeInsee).features().stream()
				// Dont on extrait la Map des properties
				.map(ResultatBanDto::properties)
				// Puis qu'on collecte sous forme de liste
				.toList();
	}

	@Override
	public Collection<Map<String, String>> rechercherCommuneBan(String recherche) {
		// On prend les résultats
		return this.clientBan.rechercherCommuneBan(recherche).features().stream()
				// Dont on extrait la Map des properties
				// Auquelles on ajoute un libellé utile
				.map(element -> {
					Map<String, String> map = new HashMap<>(element.properties());
					map.put("libelleLong", map.get("city") + " (" + map.get("citycode") + ")");
					return map;
				})
				// Puis qu'on collecte sous forme de liste
				.toList();
	}
}
