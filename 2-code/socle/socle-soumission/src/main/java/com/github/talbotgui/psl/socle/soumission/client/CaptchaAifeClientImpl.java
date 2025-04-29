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

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;
import com.github.talbotgui.psl.socle.soumission.client.dto.RequeteValidationValeurCaptchaAife;

import io.micrometer.tracing.Tracer;

@Service
public class CaptchaAifeClientImpl extends AbstractClientHttp implements CaptchaAifeClient {

	/** Début de la valeur d'entête Authorization (attention, l'API du partenaire est sensible aux majuscules). */
	private static final String BEARER = "Bearer ";

	/** Emplacement de la valeur dans le message renvoyé quand le captcha est désactivé. */
	private static final String CAPTCHA_DESACTIVE_CHAINE_A_RECHERCHER = "{}";

	/** Clef de désactivation du captcha AIFE. */
	private final boolean captchaAifeDesactive;

	private final String captchaAifeDesactiveMessageParDefaut;

	public String captchaAifeDesactiveValeurParDefaut;

	/** URL de l'API de génération des ressources. */
	private final String captchaUrl;

	/** Contenu du POST envoyé pour générer le token. */
	private final String tokenCorps;

	/** Moment à partir duquel on regénéère un token. */
	private long tokenTimeProchainAppel = 0;

	/** URL de génération de token. */
	private final String tokenUrl;

	/** URL de l'API de validation de la valeur saisie. */
	private final String validerUrl;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public CaptchaAifeClientImpl(Tracer traceur, @Value("${captchaaife.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${captchaaife.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${captchaaife.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${captchaaife.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${captchaaife.proxy.motDePasse:#{null}}") String motDePasse, @Value("${captchaaife.token.url}") String tokenUrl,
			@Value("${captchaaife.valider.url}") String validerUrl,

			@Value("${captchaaife.desactive:false}") boolean captchaAifeDesactive,
			@Value("${captchaaife.desactive.message:null}") String captchaAifeDesactiveMessageParDefaut,
			@Value("${captchaaife.desactive.valeur:null}") String captchaAifeDesactiveValeurParDefaut,
			@Value("${captchaaife.captcha.url}") String captchaUrl, @Value("${captchaaife.token.corps}") String tokenCorps) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.tokenUrl = tokenUrl;
		this.validerUrl = validerUrl;
		this.captchaAifeDesactive = captchaAifeDesactive;
		this.captchaAifeDesactiveMessageParDefaut = captchaAifeDesactiveMessageParDefaut;
		this.captchaAifeDesactiveValeurParDefaut = captchaAifeDesactiveValeurParDefaut;
		this.captchaUrl = captchaUrl;
		this.tokenCorps = tokenCorps;
	}

	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	@Override
	public String genererToken() {
		this.logger.info("Génération du token OIDC AIFE");
		long maintenant = (new Date()).getTime();
		TypeReference<ReponseTokenOIDC> typeRetour = new TypeReference<>() {
		};

		// Réutilisation potentielle du cache
		if (maintenant < this.tokenTimeProchainAppel) {
			return this.tokenJwt;
		}

		// Appel réel au token
		ReponseTokenOIDC token = super.executerRequetePost(this.tokenUrl, typeRetour, this.tokenCorps, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		// Stockage des informations permettant de ne pas appeler la création d'un token à chaque appel
		this.tokenTimeProchainAppel = maintenant + (Long.decode(token.expiresIn()) / 2);
		this.tokenJwt = token.accessToken();
		this.logger.trace("Token AIFE obtenu avec un expiresIn={} donc prochain appel à partir de la moitée de ce délai", token.expiresIn());

		// Renvoi du token
		return this.tokenJwt;
	}

	@Override
	public ReponseRessourceCaptchaAife obtenirContenuDuCaptcha(String parametre) {

		// Si le captcha est désactivé, inutile d'appeler le client
		if (this.captchaAifeDesactive) {
			String messageSiCaptchaDesactive = this.captchaAifeDesactiveMessageParDefaut.replace(CAPTCHA_DESACTIVE_CHAINE_A_RECHERCHER,
					this.captchaAifeDesactiveValeurParDefaut);
			return new ReponseRessourceCaptchaAife(messageSiCaptchaDesactive.getBytes(StandardCharsets.UTF_8), ContentType.TEXT_HTML.getMimeType());
		}

		// Entête de sécurité
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, BEARER + this.genererToken());

		// Appel sans transformation de la réponse
		HttpResponse<byte[]> reponse = super.executerRequeteGet(this.captchaUrl + parametre, REPONSE_ELLE_MEME, null, entetes);

		// Renvoi de la réponse
		return new ReponseRessourceCaptchaAife(reponse.body(), reponse.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse(null));
	}

	@Override
	public void purgerToken() {
		this.tokenJwt = null;
		this.tokenTimeProchainAppel = 0;
	}

	@Override
	public boolean validerDonneeSaisie(String valeurCaptchaSaisie, String idCaptchaGenere) {

		// Si le captcha est désactivé, la valeur founie doit correspondre à la constante
		if (this.captchaAifeDesactive) {
			return (this.captchaAifeDesactiveValeurParDefaut != null) && this.captchaAifeDesactiveValeurParDefaut.equals(valeurCaptchaSaisie);
		}

		// Entête de sécurité
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, BEARER + this.genererToken());

		// Données à envoyer
		RequeteValidationValeurCaptchaAife contenuRequeteValidation = new RequeteValidationValeurCaptchaAife(idCaptchaGenere, valeurCaptchaSaisie);

		// Appel
		String reponse = super.executerRequetePost(this.validerUrl, null, contenuRequeteValidation, MediaType.APPLICATION_JSON_VALUE, entetes);

		//
		return Boolean.TRUE.toString().equals(reponse);
	}
}
