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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe créant, dans "swagger-ui.html", un groupe d'API pour chaque micro-service déclaré dans les routes (cf. application-specifique.properties)
 */
@Configuration
public class SpringDocConfig {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringDocConfig.class);

	private static final CharSequence PREFIX_NOM_PROJET_DB = "db";
	private static final String PREFIXE_NOM_APPLICATION = "socle-";

	@Bean
	public List<GroupedOpenApi> apis(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiParameters) {
		// Lecture des routes
		List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

		// Au cas où
		if (definitions != null) {
			// Création des groupes en commençant par parcourir les routes
			Set<SwaggerUrl> urls = definitions.stream()
					// filtrage des routes commençant par "socle-"
					.filter(routeDefinition -> routeDefinition.getId().startsWith(PREFIXE_NOM_APPLICATION))
					// Création du groupe
					.map(routeDefinition -> {
						// Lecture des données
						String uri = routeDefinition.getPredicates().get(0).getArgs().get("_genkey_0").replaceFirst("/\\*\\*", "/restdoc");
						String nomDeGroupe = routeDefinition.getId().replace(PREFIXE_NOM_APPLICATION, "").replace(PREFIX_NOM_PROJET_DB, "");
						// Création d'une URL
						return new SwaggerUrl(nomDeGroupe, uri, routeDefinition.getId());
					})
					// en set
					.collect(Collectors.toSet());

			// Sauvegarde dans la conf
			swaggerUiParameters.setUrls(urls);

			// log
			LOGGER.info("{} groupes créés dans la configuration SpringDoc pour {} routes existantes : {}", urls.size(), definitions.size(), urls);
		}

		// renvoi des groupes (liste vide finalement car le boulot est fait dans swaggerUiParameters.getUrls())
		return new ArrayList<>();
	}
}
