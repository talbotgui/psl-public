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
package com.github.talbotgui.psl.socle.commun.utils;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Classe utilitaire pour manipuler Velocity plus facilement.
 */
public class VelocityUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(VelocityUtils.class);

	/**
	 * Création d'un contexte avec 2 map de données.
	 *
	 * @param donnees1 Premier jeu de données.
	 * @param donnees2 Seconde jeu de données
	 * @return Contexte créé.
	 */
	public static VelocityContext creerContexteVelocityAvecEchapementXmlSystematique(Map<String, String> donnees1, Map<String, String> donnees2) {
		VelocityContext contexte = new VelocityContext();

		if (donnees1 != null) {
			for (Map.Entry<String, String> entree : donnees1.entrySet()) {
				contexte.put(entree.getKey(), org.apache.commons.lang.StringEscapeUtils.escapeXml(entree.getValue()));
			}
		}
		if (donnees2 != null) {
			for (Map.Entry<String, String> entree : donnees2.entrySet()) {
				contexte.put(entree.getKey(), org.apache.commons.lang.StringEscapeUtils.escapeXml(entree.getValue()));
			}
		}

		LOGGER.debug("Contexte Velocity créé avec {} clefs", contexte.getKeys().length);

		return contexte;
	}

	/**
	 * Résolution d'un template Velocity.
	 *
	 * @param contexte             Contexte Velocity.
	 * @param template             Template.
	 * @param codeDemarche         Code de la démarche.
	 * @param idGenerationPourLogs ID du brouillon.
	 * @return Sortie de la résolution du template.
	 */
	public static String resoudreTemplate(VelocityContext contexte, String template, String codeDemarche, String idGenerationPourLogs) {

		// Au cas où
		if (!StringUtils.hasLength(template)) {
			return null;
		}

		// Résolution
		StringWriter sortie = new StringWriter();
		boolean resultatVelocityContenu = Velocity.evaluate(contexte, sortie, "template-" + codeDemarche + "-" + idGenerationPourLogs, template);

		// Log en cas d'erreur
		if (!resultatVelocityContenu) {
			LOGGER.warn("Erreur à la résolution d'un template '{}' de la démarche {}", idGenerationPourLogs, codeDemarche);
		}

		// Renvoi du résultat
		return sortie.toString();
	}

	/** Constructeur privé */
	private VelocityUtils() {
		// Pour éviter l'instanciation
	}
}
