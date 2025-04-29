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
package com.github.talbotgui.psl.socle.commun.test;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.securite.JwtServiceImpl;

/**
 * Cette classe permet de générer un token JWT dans le cadre des tests
 * automatisés.
 */
public class JwtPourLesTestsUtils {

	/** Délai de validité d'un token JWT de test. */
	private static final long DELAI_VALIDATION_JWT_POUR_UN_TEST = 60000;

	/**
	 * Génère un token JWT valide et l'ajoute aux entêtes envoyés à l'API
	 *
	 * @param secretAutiliser Le secret utilisé pour générer les tokens JWT.
	 * @return un nouveau Token JWT valide pour 60 secondes
	 * @see com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils#DELAI_VALIDATION_JWT_POUR_UN_TEST
	 */
	public static String genererTokenJwtPourUnTest(String secretAutiliser) {

		// Génération d'un token Anonyme de quelques secondes
		String token = JwtServiceImpl.genererNouveauTokenPourLesTestsUniquement(JwtService.USER_ANONYME, secretAutiliser,
				DELAI_VALIDATION_JWT_POUR_UN_TEST, null);

		// renvoi du token
		return JwtSecuriteFilter.BEARER + token;
	}

	/**
	 * Génère un token JWT valide (comme s'il était issu d'une authentification par
	 * mot de passe) et l'ajoute aux entêtes envoyés à l'API
	 *
	 * @param secretAutiliser Le secret utilisé pour générer les tokens JWT.
	 * @return un nouveau Token JWT valide pour 60 secondes
	 * @see com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils#DELAI_VALIDATION_JWT_POUR_UN_TEST
	 */
	public static String genererTokenJwtPourUnTestAvecUnMotDePasse(String secretAutiliser) {

		// Génération d'un token Anonyme de quelques secondes
		UserDetails userDetails = new User("test@test.com", "aA*12345678", new ArrayList<>());
		String token = JwtServiceImpl.genererNouveauTokenPourLesTestsUniquement(userDetails, secretAutiliser, DELAI_VALIDATION_JWT_POUR_UN_TEST,
				null);

		// renvoi du token
		return JwtSecuriteFilter.BEARER + token;
	}

	/**
	 * Génère un token JWT valide et l'ajoute aux entêtes envoyés à l'API. Le tout
	 * avec le group ADMIN_SUPERVISION.
	 *
	 * @param secretAutiliser Le secret utilisé pour générer les tokens JWT.
	 * @return un nouveau Token JWT valide pour 60 secondes
	 * @see com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils#DELAI_VALIDATION_JWT_POUR_UN_TEST
	 */
	public static String genererTokenJwtPourUnTestDansAdminPsl(String secretAutiliser) {

		// Génération d'un token Anonyme de quelques secondes
		String token = JwtServiceImpl.genererNouveauTokenPourLesTestsUniquement(JwtService.USER_ADMIN, secretAutiliser,
				DELAI_VALIDATION_JWT_POUR_UN_TEST, null);

		// renvoi du token
		return JwtSecuriteFilter.BEARER + token;
	}

	private JwtPourLesTestsUtils() {
		// Constructeur privé pour bloquer l'instanciation de cette classe utilitaire
	}
}
