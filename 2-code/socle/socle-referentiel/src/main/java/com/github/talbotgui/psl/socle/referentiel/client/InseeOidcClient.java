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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.util.List;

import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.dto.oauth2.Oauth2TokenDto;

public interface InseeOidcClient {

	/**
	 * Appel à l'API OIDC pour générer un token d'appel aux APIs sécurisées de l'INSEE
	 * 
	 * @return le token.
	 */
	Oauth2TokenDto genererToken();

	/**
	 * Téléchargement des communes qui ont existé.
	 * 
	 * @param token Token OIDC
	 */
	List<CommuneNaissanceDto> telechargerLesCommunesDeNaissance(Oauth2TokenDto token);

	/**
	 * Téléchargement des communes UGLE.
	 * 
	 * @param token Token OIDC
	 */
	List<CommuneUgleDto> telechargerLesCommunesUgle(Oauth2TokenDto token);

	/**
	 * Téléchargement des pays qui ont existé.
	 * 
	 * @param token Token OIDC
	 */
	List<PaysNaissanceDto> telechargerLesPaysDeNaissance(Oauth2TokenDto token);
}