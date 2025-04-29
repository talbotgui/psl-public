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

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;
import com.github.talbotgui.psl.socle.soumission.client.CaptchaAifeClient;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;

@Service
public class CaptchaServiceImpl implements CaptchaService {

	private final CaptchaAifeClient client;
	private final JwtService jwtService;

	/** Paramètres pour obtenir le code HTML du catcha */
	private final String parametresPourObtenirLeHtml;

	/** URL du socle PSL permettant d'obtenir le captcha. */
	private final String urlPubliqueApplicatif;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public CaptchaServiceImpl(CaptchaAifeClient client, JwtService jwtService,
			@Value("${captchaaife.parametrespourobtenirlehtml}") String parametresPourObtenirLeHtml,
			@Value("${captchaaife.urlpubliqueapplicatif}") String urlPubliqueApplicatif) {
		super();
		this.client = client;
		this.jwtService = jwtService;
		this.parametresPourObtenirLeHtml = parametresPourObtenirLeHtml;
		this.urlPubliqueApplicatif = urlPubliqueApplicatif;
	}

	@Override
	public ReponseRessourceCaptchaAife obtenirContenuDuCaptcha(String get, String c, String t) {

		// Si pas de paramètre, on demande le HTML
		String parametreAppelAuClient;
		if (StringUtils.isEmpty(get) && StringUtils.isEmpty(c) && StringUtils.isEmpty(t)) {
			parametreAppelAuClient = this.parametresPourObtenirLeHtml;
		} else {
			parametreAppelAuClient = "get=" + get + "&c=" + c + "&t=" + t;
		}

		// Appel au captcha
		ReponseRessourceCaptchaAife reponseCaptcha = this.client.obtenirContenuDuCaptcha(parametreAppelAuClient);

		// Si la demande est le code HTML du captcha
		if (StringUtils.isEmpty(get) && StringUtils.isEmpty(c) && StringUtils.isEmpty(t) && (reponseCaptcha.contenu() != null)) {
			// remplacement de l'URL du captcha par celle de notre API
			String contenuReponse = new String(reponseCaptcha.contenu(), StandardCharsets.UTF_8);
			contenuReponse = contenuReponse.replace(CAPTCHA_URL_PAR_DEFAUT, this.urlPubliqueApplicatif);

			// Ajout de la clef unique de sécurité dans chaque requête
			String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();
			String clefTemporaire = this.jwtService.genererClefTemporaireSurTokenJwt(tokenJwt, t, 3600L, null);
			String paramClefTemporaire = JwtSecuriteFilter.PARAMETRE_CLEF + "=" + clefTemporaire + "&";
			contenuReponse = contenuReponse.replace(this.urlPubliqueApplicatif, this.urlPubliqueApplicatif + paramClefTemporaire);

			// Renvoi
			return new ReponseRessourceCaptchaAife(contenuReponse.getBytes(StandardCharsets.UTF_8), reponseCaptcha.enteteContentType());
		}

		// Sinon renvoi de la réponse sans y toucher
		else {
			return reponseCaptcha;
		}
	}

}
