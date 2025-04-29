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
package com.github.talbotgui.psl.socle.dbbrouillon.dao;

import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;

public interface BrouillonDao {

	/**
	 * Recherche d'un brouillon avec le bon code de démarche et le bon identifiant.
	 *
	 * @param codeDemarche Code de démarche.
	 * @param idBrouillon  Identifiant de brouillon.
	 * @return Brouillon trouvé.
	 */
	BrouillonDto rechercherBrouillon(String codeDemarche, String idBrouillon);

	/**
	 * Sauvegarde d'un brouillon.
	 *
	 * @param dto Données du brouillon.
	 * @return Identifiant du brouillon sauvegardé
	 */
	String sauvegarderBrouillon(BrouillonDto dto);

	/**
	 * Suppression d'un brouillon.
	 * 
	 * @param codeDemarche Code d'une démarche.
	 * @param idBrouillon  ID d'un brouillon.
	 */
	void supprimerBrouillon(String codeDemarche, String idBrouillon);

}