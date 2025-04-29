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
package com.github.talbotgui.psl.socle.commun.securite;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenJwtUtils {

	/**
	 * Permet de récupérer le token JWT depuis le contexte de sécurité de Spring.
	 *
	 * @return Le token JWT (ou null à défaut)
	 */
	public static String obtenirTokenJwtDepuisSecuriteSpring() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth instanceof UsernamePasswordAuthenticationToken upaAuth && upaAuth.getCredentials() instanceof String cred) {
			return cred;
		}
		return null;
	}

	private TokenJwtUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}
}
