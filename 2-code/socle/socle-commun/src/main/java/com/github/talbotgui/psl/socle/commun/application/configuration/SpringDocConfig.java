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
package com.github.talbotgui.psl.socle.commun.application.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/** Configuration pour SpringDoc */
@Configuration
public class SpringDocConfig {

	/** Nom de l'application ADMINPSL. */
	private static final String NOM_APPLICATION_SOCLE_ADMINPSL = "socle-adminpsl";
	/** Nom de l'application SECURITE. */
	private static final String NOM_APPLICATION_SOCLE_SECURITE = "socle-securite";

	/** Nom de l'application venant de la configuration. */
	@Value("${spring.application.name}")
	private String nomApplication;

	@Bean
	public OpenAPI customOpenAPI() {
		// Configuration commune de tous les projets
		OpenAPI conf = new OpenAPI().info(new Info().title("Documentation API - " + this.nomApplication));

		// ADMINPSL est sécurisés avec un token JWT spécifique généré par une API propre (et pas via socle-securite)
		if (NOM_APPLICATION_SOCLE_ADMINPSL.equals(this.nomApplication)) {
			conf.components(new Components().addSecuritySchemes("bearer-admin", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
					.bearerFormat("JWT").in(SecurityScheme.In.HEADER).name(HttpHeaders.AUTHORIZATION)));
			conf.addSecurityItem(new SecurityRequirement().addList("bearer-admin", Arrays.asList("read", "write")));
		}
		// Tous les applicatifs sont sécurisés avec un token JWT (sauf socle-securite)
		else if (!NOM_APPLICATION_SOCLE_SECURITE.equals(this.nomApplication)) {
			conf.components(new Components().addSecuritySchemes("bearer-jwt", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
					.bearerFormat("JWT").in(SecurityScheme.In.HEADER).name(HttpHeaders.AUTHORIZATION)));
			conf.addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));
		}
		return conf;
	}
}
