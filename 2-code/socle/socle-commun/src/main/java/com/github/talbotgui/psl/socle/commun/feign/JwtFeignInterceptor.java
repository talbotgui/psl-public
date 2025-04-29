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
package com.github.talbotgui.psl.socle.commun.feign;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commun.securite.dto.WebAuthenticationDetailsDto;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Cet intercepteur, spécifique aux appels via Feign, récupère des entêtes HTTP
 * présents dans la requête HTTP originelle pour les insérer dans l'appel Feign
 * :
 * <ul>
 * <li>le token JWT est récupéré depuis le contexte de sécurité du traitement de
 * la requête</li>
 * <li></li>
 * </ul>
 */
@Component
public class JwtFeignInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {

		// Si l'utilisateur est authentifié (normalement, c'est systématique)
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication instanceof UsernamePasswordAuthenticationToken auth) {
			// Cas standard d'un appel avec un entête JWT
			if (auth.getCredentials() instanceof String) {
				requestTemplate.header(HttpHeaders.AUTHORIZATION, auth.getCredentials().toString());
			}
			// Cas else non traité : rien à faire

			// Si un entête 'AcceptLanguage' est présent dans la requête en cours de
			// traitement, il est propagé à travers Feign vers le micro-service
			// suivant
			if (authentication.getDetails() != null && authentication.getDetails() instanceof WebAuthenticationDetailsDto) {
				WebAuthenticationDetailsDto details = (WebAuthenticationDetailsDto) authentication.getDetails();
				if (details.getEnteteAcceptLanguage() != null) {
					requestTemplate.header(HttpHeaders.ACCEPT_LANGUAGE, details.getEnteteAcceptLanguage());
				}
			}
			// Cas else non traité : rien à faire
		}
		// Cas else non traité : rien à faire
	}
}
