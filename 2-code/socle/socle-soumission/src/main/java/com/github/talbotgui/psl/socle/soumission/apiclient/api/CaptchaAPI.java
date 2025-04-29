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
package com.github.talbotgui.psl.socle.soumission.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.soumission.apiclient.SoumissionClient;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-soumission", contextId = "CaptchaAPI")
public interface CaptchaAPI {

	/**
	 * Téléchargement d'une ressource. Attention, cette API n'est pas sécurisée car ces ressources (images, CSS, JS) sont chargés depuis les balises
	 * du navigateur et donc sans entête de sécurité.
	 *
	 * @param get Parametres d'appel à l'API Captcha présent dans le code HTML retourné par le fournisseur de Captcha.
	 * @param c   Parametres d'appel à l'API Captcha présent dans le code HTML retourné par le fournisseur de Captcha.
	 * @param t   Parametres d'appel à l'API Captcha présent dans le code HTML retourné par le fournisseur de Captcha.
	 * @return Le contenu de la ressource.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = SoumissionClient.URI_CAPTCHA_RESSOURCE)
	ResponseEntity<byte[]> obtenirContenuDuCaptcha(//
			@Pattern(regexp = RegexUtils.ALPHABETIQUE_ETENDUE) String get, //
			@Pattern(regexp = RegexUtils.ALPHANUMERIQUE) String c, //
			@Pattern(regexp = RegexUtils.ALPHANUMERIQUE) String t);

	/**
	 * Téléchargement du code HTML du captcha.
	 *
	 * @return Le contenu HTML.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = SoumissionClient.URI_CAPTCHA)
	ResponseEntity<byte[]> obtenirHtmlDuCaptcha();
}