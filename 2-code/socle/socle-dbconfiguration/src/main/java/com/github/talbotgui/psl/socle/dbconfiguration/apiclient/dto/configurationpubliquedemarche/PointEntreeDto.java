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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche;

import java.util.List;

/**
 * Classe décrivant les données de configuration d'une démarche envoyée dans le navigateur de l'usager pour paramétrer l'application WEB et permettre
 * la saisie des données.
 *
 * @see configurationdemarchegeneral.model.ts
 */
public record PointEntreeDto(

		/** Type d'authentification associée */
		String authentification,

		/** Paramètres d'URL à récupérer pour ce point d'entrée */
		List<ParametrePointEntreeDto> parametres) {
}