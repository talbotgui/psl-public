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
package com.github.talbotgui.psl.socle.soumission.client;

import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;

public interface CaptchaAifeClient {

	/**
	 * Génération d'un token OIDC de l'API AIFE.
	 *
	 * @return Token valide.
	 */
	String genererToken();

	/**
	 * Téléchargement d'une ressource.
	 *
	 * @param parametres Parametres d'appel à l'API Captcha.
	 * @return Le contenu de la ressource.
	 */
	ReponseRessourceCaptchaAife obtenirContenuDuCaptcha(String parametres);

	/** Suppression du token généré. */
	void purgerToken();

	/**
	 * Validation d'une valeur saisie dans le captcha. Bon si pas d'exception.
	 *
	 * @param valeurCaptchaSaisie Valeur saisie.
	 * @param idCaptchaGenere     ID du captcha.
	 */
	boolean validerDonneeSaisie(String valeurCaptchaSaisie, String idCaptchaGenere);

}