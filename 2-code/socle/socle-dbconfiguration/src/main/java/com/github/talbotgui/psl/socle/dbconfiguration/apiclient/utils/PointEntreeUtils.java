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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PointEntreeDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.exception.ConfigurationException;

public class PointEntreeUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(PointEntreeUtils.class);

	/**
	 * Recherche du point d'entrée correspondant avec les données soumises car les paramètres de l'URL d'arrivée dans la démarche sont toujours dans
	 * les données soumises.
	 *
	 * @param pointsEntree Points d'entrée de la configuration publique.
	 * @param donnees      Données soumises ou du brouillon.
	 * @return potentiellement null.
	 */
	public static PointEntreeDto rechercherPointEntreeCorrespondant(List<PointEntreeDto> pointsEntree, Map<String, String> donnees) {

		// Recherche des points d'entrée correspondants
		List<PointEntreeDto> pointsEntreeCorrespondants = new ArrayList<>();
		if (pointsEntree != null) {

			// Recherche des points d'entree pour lesquels tous les paramètres sont couverts
			pointsEntreeCorrespondants = pointsEntree.stream().filter(pe -> pe.parametres() == null || pe.parametres().stream().allMatch(ppe -> {
				String donneePresentePourCeParametre = donnees.get(ppe.parametre());
				return StringUtils.hasLength(donneePresentePourCeParametre) && ppe.valeurs().contains(donneePresentePourCeParametre);
			})).toList();

			// Si plusieurs points d'entrée sont trouvés mais que l'un d'eux est sans paramètre, on retire ce dernier
			if (pointsEntreeCorrespondants.size() > 1) {
				pointsEntreeCorrespondants = pointsEntreeCorrespondants.stream().filter(pe -> pe.parametres() != null).toList();
			}
		}

		// Si on a trop de points d'entrée trouvé, on a un problème
		if (pointsEntreeCorrespondants.size() > 1) {
			LOGGER.warn("Trop de points d'entrée trouvés : {}", pointsEntreeCorrespondants);
			throw new ConfigurationException(ConfigurationException.ERREUR_POINT_ENTREE_CORRESPONDANT_TROP_NOMBREUX);
		}

		// Si on a un point d'entrée, on le renvoie
		else if (pointsEntreeCorrespondants.size() == 1) {
			return pointsEntreeCorrespondants.get(0);
		}

		// Sinon aucun point trouvé
		else {
			return null;
		}
	}

	private PointEntreeUtils() {
		// Constructeur privé pour classe utilitaire
	}
}
