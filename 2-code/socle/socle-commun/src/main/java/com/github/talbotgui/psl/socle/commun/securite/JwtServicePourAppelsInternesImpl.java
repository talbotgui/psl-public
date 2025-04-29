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

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class JwtServicePourAppelsInternesImpl implements JwtServicePourAppelsInternes {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtServicePourAppelsInternesImpl.class);

	@Autowired
	private JwtService jwtService;

	@Override
	public void genererEtSauvegarderNouveauTokenInterne() {

		// Ce log est en débug pour ne pas générer des centaines de lignes de log inutiles
		LOGGER.debug("Début de la recréation d'un token anonyme");

		// Création d'un nouveau token anonyme
		String token = this.jwtService.genererNouveauTokenAnonyme();
		Claims claims = this.jwtService.validerToken(token);
		String tokenComplet = JwtSecuriteFilter.BEARER + token;

		// Sauvegarde des données dans le contexte Spring pour usage ultérieur (cf. JwtSecuriteFilter)
		UserDetails utilisateur = new User(claims.getSubject(), "", new ArrayList<>());
		UsernamePasswordAuthenticationToken authenticationSpring = new UsernamePasswordAuthenticationToken(utilisateur, tokenComplet,
				utilisateur.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationSpring);

		// Ce log est en débug pour ne pas générer des centaines de lignes de log inutiles
		LOGGER.debug("Fin de la recréation d'un token anonyme");
	}

}
