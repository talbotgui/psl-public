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
package com.github.talbotgui.psl.socle.adminpsl.service;

import java.util.Map;

public interface ConfigurationService {

	/**
	 * Retourne une configuration.
	 *
	 * @param publique     Configuration publique ou privée
	 * @param codeDemarche Code de la démarche.
	 * @param id           ID de document.
	 * @return la configuration.
	 */
	String chargerVersionDeConfiguration(boolean publique, String codeDemarche, String id);

	/**
	 * Création d'une configuration interne et publique pour une nouvelle démarche.
	 *
	 * @param codeDemarche Un code de démarche non existant.
	 * @param token        Token de l'usager.
	 */
	void creerNouvelleDemarche(String codeDemarche, String token);

	/**
	 * Retourne la liste des ID et dates des versions de configuration d'une démarche.
	 *
	 * @param publique     Configuration publique ou privée
	 * @param codeDemarche Code de la démarche à utiliser pour filter.
	 * @return la liste des ID et dates des versions
	 */
	Map<String, String> listerLesVersionsDeConfiguration(boolean publique, String codeDemarche);

	/**
	 * Sauvegarde d'une version de configuration de démarche.
	 *
	 * @param publique      Configuration publique ou privée
	 * @param codeDemarche  Code de la démarche
	 * @param id            ID de la configuration si déjà existante.
	 * @param configuration Configuration à sauvegarder.
	 * @param tokenJwt      Token de l'usager.
	 * @return ID de la sauvegarde
	 */
	String sauvegarderVersionDeConfiguration(boolean publique, String codeDemarche, String id, String configuration, String tokenJwt);

}