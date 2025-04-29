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
package com.github.talbotgui.psl.socle.soumission.service.soumission;

import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;

public interface SoumissionService {

	/**
	 * Soumission d'un télé-dossier.
	 *
	 * @param tokenJwt Le token JWT utilisé à l'appel
	 * @param dto      Les données soumises.
	 * @return Le numéro de télé-dossier
	 */
	String soumettreUnTeledossier(String tokenJwt, DonneesDeSoumissionDto dto);

}