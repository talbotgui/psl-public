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
package com.github.talbotgui.psl.socle.commundb.utils;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

public class TemplateUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtils.class);

	/** Pattern de recherche des fichiers de configuration interne de démarche présents dans le classpath */
	public static final String PATTERN_FICHIER_DANS_LE_CLASSPATH = "classpath*:db/";

	/** Pattern de nom des fichiers de configuration interne présent dans le classpath */
	public static final String PATTERN_FICHIER_TEMPLATE_A_SUBSTITUER = "----([^-]*)----";

	/**
	 * Intégration des templates dans le fichier JSON.
	 * 
	 * @param json             JSON à traiter.
	 * @param resourceResolver ResourceResolver nécessaire.
	 * @return Le JSON traité
	 */
	public static String integrerTemplatesDeDocument(String json, ResourcePatternResolver resourceResolver) throws IOException {
		int nbFichiersIntegres = 0;
		Matcher matcher = Pattern.compile(PATTERN_FICHIER_TEMPLATE_A_SUBSTITUER).matcher(json);

		// Pour chaque template à substituer
		while (matcher.find()) {
			String chaineComplete = matcher.group();
			String nomFichier = matcher.group(1);

			// lecture du fichier
			Resource[] fichierTemplate = resourceResolver.getResources(PATTERN_FICHIER_DANS_LE_CLASSPATH + nomFichier);

			// Si trouvé, on intègre/substitue le document trouvé à la chaine complète
			if (fichierTemplate.length == 1) {
				String base64 = Base64.getEncoder()
						.encodeToString(InitialisationMongoDBUtils.lireUnFichierDepuisLeJar(fichierTemplate[0]).getBytes());
				json = json.replace(chaineComplete, base64);
				nbFichiersIntegres++;
			} else {
				LOGGER.error("Impossible de trouver le template de document {}", nomFichier);
			}
		}

		LOGGER.info("Intégration de {} template(s)", nbFichiersIntegres);

		return json;
	}

	/** Constructeur privé pour bloquer l'instanciation ! */
	private TemplateUtils() {
		// Rien à faire
	}

}
