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
package com.github.talbotgui.psl.socle.commun.securite.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Surchage de la classe WebAuthenticationDetails de Spring pour ajouter l'entête de langue.
 */
public class WebAuthenticationDetailsDto extends WebAuthenticationDetails {
	private static final long serialVersionUID = 1L;

	/** Valeur de l'entête 'Accept-Language' fourni dans la requête venant du navigateur. */
	private final String enteteAcceptLanguage;

	public WebAuthenticationDetailsDto(HttpServletRequest request) {
		super(request);
		this.enteteAcceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
	}

	public String getEnteteAcceptLanguage() {
		return this.enteteAcceptLanguage;
	}

}
