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
package com.github.talbotgui.psl.socle.soumission.controleur;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.soumission.apiclient.api.CaptchaAPI;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;
import com.github.talbotgui.psl.socle.soumission.service.captcha.CaptchaService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class CaptchaControleur implements CaptchaAPI {

	private final CaptchaService service;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public CaptchaControleur(CaptchaService service) {
		super();
		this.service = service;
	}

	@Override
	public ResponseEntity<byte[]> obtenirContenuDuCaptcha(String get, String c, String t) {
		// Appel au client
		ReponseRessourceCaptchaAife reponseCaptcha = this.service.obtenirContenuDuCaptcha(get, c, t);

		// Création de la liste des entêtes
		MultiValueMap<String, String> entetes = new LinkedMultiValueMap<>();
		entetes.add(HttpHeaders.CONTENT_TYPE, reponseCaptcha.enteteContentType());

		// Renvoi de la réponse (contenu+statut+entetes)
		return new ResponseEntity<>(reponseCaptcha.contenu(), entetes, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> obtenirHtmlDuCaptcha() {
		return this.obtenirContenuDuCaptcha(null, null, null);
	}
}