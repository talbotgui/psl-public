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
package com.github.talbotgui.psl.socle.serviceadmin.application;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class AjoutEntetesFiltre extends OncePerRequestFilter {

	@Value("${cors.allowCredentials}")
	private String corsAllowCredentials;

	@Value("${cors.allowedHeaders}")
	private String corsAllowedHeaders;

	@Value("${cors.allowedMethods}")
	private String corsAllowedMethods;

	@Value("${cors.allowedOrigins}")
	private String corsAllowedOrigins;

	@Value("${cors.maxage}")
	private String corsMaxage;

	/** Ajout des entête CORS sur une réponse HTTP */
	private void ajouterLesEntetesAlaReponse(HttpServletResponse response) {
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, this.corsAllowedOrigins);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.corsAllowedMethods);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, this.corsMaxage);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, this.corsAllowedHeaders);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, this.corsAllowCredentials);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Traitement d'une requete OPTIONS
		if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
			this.ajouterLesEntetesAlaReponse(response);
			response.setStatus(HttpStatus.OK.value());
		}

		// Traitement de toutes les autres requêtes (oui les entêtes CORS doivent être présents sur toutes les requêtes maintenant)
		else {
			this.ajouterLesEntetesAlaReponse(response);
			filterChain.doFilter(request, response);
		}
	}
}
