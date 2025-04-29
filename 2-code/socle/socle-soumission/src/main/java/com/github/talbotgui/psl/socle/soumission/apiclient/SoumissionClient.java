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
package com.github.talbotgui.psl.socle.soumission.apiclient;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.CaptchaAPI;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.SoumissionAPI;
import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class SoumissionClient extends AbstractClientHttp implements SoumissionAPI, CaptchaAPI {

	/** URL spécifique à l'API du captcha. */
	public static final String URI_CAPTCHA = SoumissionClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "captcha";

	/** URL spécifique aux ressources du captcha. */
	public static final String URI_CAPTCHA_RESSOURCE = SoumissionClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "captchaRessource";

	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/soumission";

	/** URL spécifique à la soumssion d'un télé-dossier */
	public static final String URI_SOUMETTRE = SoumissionClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "soumettre";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public SoumissionClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	// Cette méthode traite bien de données personnelles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public ResponseEntity<byte[]> obtenirContenuDuCaptcha(String get, String c, String t) {
		List<String> parametres = Arrays.asList("get", get, "c", c, "t", t);
		HttpResponse<byte[]> reponse = super.executerRequeteGet(URI_CAPTCHA_RESSOURCE, REPONSE_ELLE_MEME, parametres,
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
		return new ResponseEntity<>(reponse.body(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> obtenirHtmlDuCaptcha() {
		HttpResponse<byte[]> reponse = super.executerRequeteGet(URI_CAPTCHA, REPONSE_ELLE_MEME, null,
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
		return new ResponseEntity<>(reponse.body(), HttpStatus.OK);
	}

	/**
	 * Soumission d'un télé-dossier.
	 *
	 * @param dto Contenu de la soumission.
	 * @return l'identifiant du télé-dossier.
	 */
	@Override
	public String soumettreUnTeledossier(DonneesDeSoumissionDto dto) {
		return super.executerRequetePost(URI_SOUMETTRE, new TypeReference<String>() {
		}, dto, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}
}
