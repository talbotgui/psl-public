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
package com.github.talbotgui.psl.socle.servicegateway.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * Configuration CORS de la gateway pour traiter les requêtes OPTIONS uniquement.
 *
 * Les données renvoyées sont paramétrées dans le fichier de configuration propre à la Gateway
 */
@Configuration
public class WebSecurityConfig {

	@Value("${cors.allowedHeaders}")
	private String corsAllowedHeaders;

	@Value("${cors.allowedMethods}")
	private String corsAllowedMethods;

	@Value("${cors.allowedOrigins}")
	private String corsAllowedOrigins;

	@Value("${cors.maxage}")
	private String corsMaxage;

	/** Ajout des entête CORS sur une réponse HTTP */
	private void ajouterLesEntetesAlaReponse(ServerHttpResponse response) {
		response.getHeaders().add("Access-Control-Allow-Origin", this.corsAllowedOrigins);
		response.getHeaders().add("Access-Control-Allow-Methods", this.corsAllowedMethods);
		response.getHeaders().add("Access-Control-Max-Age", this.corsMaxage);
		response.getHeaders().add("Access-Control-Allow-Headers", this.corsAllowedHeaders);
	}

	/**
	 * Création du filtre traitant des requêtes OPTIONS
	 *
	 * @return
	 */
	@Bean
	public WebFilter creerCorsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();

			// Traitement d'une requete OPTIONS
			if (HttpMethod.OPTIONS.equals(request.getMethod()) && CorsUtils.isCorsRequest(request)) {
				ServerHttpResponse response = ctx.getResponse();
				this.ajouterLesEntetesAlaReponse(response);
				response.setStatusCode(HttpStatus.OK);
				return Mono.empty();
			}

			// Traitement de toutes les autres requêtes (oui les entêtes CORS doivent être présents sur toutes les requêtes maintenant)
			else {
				this.ajouterLesEntetesAlaReponse(ctx.getResponse());
				return chain.filter(ctx);
			}
		};
	}
}
