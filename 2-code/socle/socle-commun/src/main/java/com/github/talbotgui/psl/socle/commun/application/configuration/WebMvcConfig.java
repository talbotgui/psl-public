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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

/** Configuration de la partie WEB/REST */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

	@Value("${server.port}")
	private int port;

	/** Autorisation des appels CORS */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	/** Méthode appelée juste après le démarrage de l'application. */
	@PostConstruct
	public void init() {
		// Affichage de quelques informations utiles au démarrage
		if (this.port == 0) {
			LOGGER.info("L'application est démarrée sur un port dynamique.");
		} else {
			LOGGER.info("L'application est démarrée sur le port {}.", this.port);
		}
	}
}
