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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.nio.file.Path;
import java.util.List;

public interface CacheDeFichierService {

	/**
	 * Calcul du nom d'un fichier dans le répertoire du cache.
	 * 
	 * @param codeDuCache Code du cache.
	 * @return Path du fichier de cache
	 */
	public Path definirNomFichierDunCache(String codeDuCache);

	/**
	 * Lecture du cache s'il est actif et disponible.
	 * 
	 * @param codeDuCache Code du cache.
	 * @return Contenu du fichier ou null
	 */
	byte[] obtenirContenuDuCacheSiActifEtDisponible(String codeDuCache);

	/**
	 * Suppression des fichiers de cache
	 * 
	 * @param codes Code des caches à supprimer.
	 */
	void reinitialiserLesCaches(List<String> codes);

	/**
	 * Sauvegarde du contenu envoyé dans un fichier de cache.
	 * 
	 * @param codeDuCache Code du cache permettant de récupérer le contenu.
	 * @param contenu     Contenu à sauvegarder.
	 */
	void sauvegarderContenuDuCache(String codeDuCache, byte[] contenu);

	/** Renvoi de la statut d'activation. */
	public boolean verifierActivation();
}