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
package com.github.talbotgui.psl.socle.adminpsl.dao;

import java.util.List;

import org.bson.Document;

import com.github.talbotgui.psl.socle.adminpsl.dto.ElementConfigurationAjouterPourAdministration;

public interface ConfigurationDao {

	/**
	 * Retourne la liste des ID et dates des versions de configuration d'une démarche.
	 *
	 * @param publique     Configuration publique ou privée
	 * @param codeDemarche Code de la démarche.
	 * @param id           Id de la version.
	 * @return la liste des ID et dates des versions
	 */
	String chargerVersionDeConfiguration(boolean publique, String codeDemarche, String id);

	/**
	 * Sauvegarde d'une nouvelle version de configuration de démarche.
	 *
	 * @param publique Configuration publique ou privée
	 * @param document Document à sauvegarder.
	 * @return ID de la sauvegarde
	 */
	String insererVersionDeConfiguration(boolean publique, Document document);

	/**
	 * Retourne la liste des ID et dates des versions de configuration d'une démarche.
	 *
	 * @param publique     Configuration publique ou privée
	 * @param codeDemarche Code de la démarche à utiliser pour filter.
	 * @return la liste des DTO
	 */
	List<ElementConfigurationAjouterPourAdministration> listerLesVersionsDeConfiguration(boolean publique, String codeDemarche);

	/**
	 * Sauvegarde d'une nouvelle version de configuration de démarche.
	 *
	 * @param publique Configuration publique ou privée
	 * @param id       ID de la configuration à mettre à jour.
	 * @param document Document à sauvegarder.
	 * @return ID de la sauvegarde
	 */
	String mettreAjourVersionDeConfiguration(boolean publique, String id, Document document);

	/**
	 * Vérification de la cohérence de l'ID et du code de démarche.
	 *
	 * @param codeDemarche  Code de démarche fourni en paramètre.
	 * @param id            ID fourni en paramètre.
	 * @param configuration Données à sauvegarder.
	 * @return Document parsé si les données sont cohérentes.
	 */
	Document verifierCoherenceDuCodeDeDemarche(String codeDemarche, String id, String configuration);
}