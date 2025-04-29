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
package com.github.talbotgui.psl.socle.adminpsl.service;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;

public interface TransfertService {

	/**
	 * Génération de soumissions/transferts en série à partir des brouillons et des configurations publiques/internes présentes en base de données
	 *
	 * @param nbTransferts Nombre de transferts à générer
	 */
	void genererDesTeledossiers(int nbTransferts);

	/**
	 * Recherche paginée de transferts en base de données.
	 *
	 * @param requete Paramètres de la requête.
	 * @return DTO trouvés dans une page.
	 */
	Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(RequeteRechercheTransfertsDto requete);
}
