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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

public class MotDePasseUtils {

	/** Instance de hachoir. */
	private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	/**
	 * Petite inversion des deux premiers caractères du mot de passe pour compliquer la récupération du mot de passe depuis la valeur du hash (si elle
	 * fuite)
	 *
	 * @param nomUtilisateur
	 * @param motDePasse
	 * @return
	 */
	private static String creerChaineAhasher(String nomUtilisateur, String motDePasse) {
		return nomUtilisateur + ":" + motDePasse.substring(0, 2) + motDePasse.substring(2);
	}

	/**
	 * Génération d'un hash.
	 *
	 * @param nomUtilisateur Nom d'utilisateur.
	 * @param motDePasse     Mot de passe.
	 * @return Hash à stocker.
	 */
	public static String hasherNomUtilisateurEtMotDePasse(String nomUtilisateur, String motDePasse) {
		if (StringUtils.hasLength(nomUtilisateur) && StringUtils.hasLength(motDePasse)) {
			return encoder.encode(creerChaineAhasher(nomUtilisateur, motDePasse));
		} else {
			return null;
		}
	}

	/**
	 * Vérification du hash versus le nom d'utilisateur et le mot de passe.
	 *
	 * @param valeurHashee   Hash à comparer
	 * @param nomUtilisateur Nom d'utilisateur.
	 * @param motDePasse     Mot de passe.
	 * @return TRUE si le hash correspond au nom d'utilisateur + mot de passe
	 */
	public static boolean verifierCorrespondance(String valeurHashee, String nomUtilisateur, String motDePasse) {
		return encoder.matches(creerChaineAhasher(nomUtilisateur, motDePasse), valeurHashee);
	}

	private MotDePasseUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}
}
