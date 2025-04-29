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

public class LogUtils {

	/**
	 * Suppression des caractères dangereux d'une chaîne de caractères à logger.
	 *
	 * @param chaine Chaîne à traiter.
	 * @return Chaîne traitée.
	 */
	public static String nettoyerDonneesAvantDeLogguer(String chaine) {
		// Au cas où
		if (chaine == null) {
			return null;
		}

		// Nettoyage
		String chainePropre = chaine.replaceAll("[^A-Za-z0-9éèêàùç# '\"_,;:?&<>=/!§\\.\\-\\[\\]\\(\\){}]*", "").replace("..", ".");

		// Si la chaine est identique, on la renvoie
		if (chainePropre.equals(chaine)) {
			return chainePropre;
		}
		// Sinon, on indique que la chaine a été modifiée
		else {
			return ">LOG NETTOYE>" + chainePropre + "<LOG NETTOYE<";
		}
	}

	private LogUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}
}
