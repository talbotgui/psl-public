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
package com.github.talbotgui.psl.socle.soumission.service.captcha;

import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;

public interface CaptchaService {

	/** URL de l'API retournée dans le code HTML. */
	String CAPTCHA_URL_PAR_DEFAUT = "/api/simple-captcha-endpoint?";

	/**
	 * Appel au client AIFE avec les bons paramètres et traitement de la réponse pour passer par les APIs du socle pour requêter les ressources.
	 * 
	 * @param get Paramètre de l'API AIFE.
	 * @param c   Paramètre de l'API AIFE.
	 * @param t   Paramètre de l'API AIFE.
	 * @return Réponse de l'API transformée
	 */
	ReponseRessourceCaptchaAife obtenirContenuDuCaptcha(String get, String c, String t);

}