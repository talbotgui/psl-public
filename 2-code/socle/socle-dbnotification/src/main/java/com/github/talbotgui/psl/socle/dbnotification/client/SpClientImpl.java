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
package com.github.talbotgui.psl.socle.dbnotification.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationTeledossierAvecPremiereNotification;

import io.micrometer.tracing.Tracer;

/**
 * Client d'appel à SP.
 */
@Component
public class SpClientImpl extends AbstractClientHttp implements SpClient {

	/** Valeur retournée quand l'appel à l'API de token est bouchonnée. */
	private static final String BOUCHON_TOKEN = "Ey==";

	/** Flag d'activation du bouchon SP */
	private final boolean bouchonActif;

	/** Corps de la requête permettant de générer un token. */
	private final String tokenCorps;

	/** URL permettant de créer une notification sur un télédossier existant. */
	private final String urlCreationNotification;

	/** URL permettant de créer un télé-dossier. */
	private final String urlCreationTeledossier;

	/** URL permettant de générer un token. */
	private final String urlToken;

	@Autowired
	public SpClientImpl(Tracer traceur,
			// URLs :
			@Value("${oidc.urls.token}") String urlToken, @Value("${sp.urls.creationNotification}") String urlCreationNotification,
			@Value("${sp.urls.creationTeledossier}") String urlCreationTeledossier,
			// Paramètres de token :
			@Value("${oidc.token.corps}") String tokenCorps,
			// Communs :
			@Value("${oidc.desactiverSSL:false}") boolean desactiverSSL, @Value("${oidc.proxy.hoteDuProxy:#{null}}") String hoteDuProxy,
			@Value("${oidc.proxy.portDuProxy:0}") int portDuProxy, @Value("${oidc.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${oidc.proxy.motDePasse:#{null}}") String motDePasse, @Value("${sp.bouchon:#{false}}") boolean bouchonActif) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.urlToken = urlToken;
		this.tokenCorps = tokenCorps;
		this.urlCreationNotification = urlCreationNotification;
		this.urlCreationTeledossier = urlCreationTeledossier;
		this.bouchonActif = bouchonActif;
	}

	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public void creerNouvelleNotificationDansUnTeledossierExistantDejaDansTdbParticulier(String token, RequeteCreationNotification requete) {
		// Log
		this.logger.info("Création d'une nouvelle notification pour le télé-dossier '{}' déjà existant", requete.idDemarcheComplementaire());

		// Si le bouchon est actif
		if (this.bouchonActif) {
			this.logger.warn("Attention, le bouchon SP est actif !");
			return;
		}

		// Création des entêtes avec le token
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + token);

		// Appel HTTP
		String url = this.urlCreationNotification.replaceFirst("idTeledossier", requete.idDemarcheComplementaire());
		TypeReference<String> typeRetour = new TypeReference<>() {
		};
		super.executerRequetePost(url, typeRetour, requete, null, entetes);

		this.logger.info("Création d'une notification avec succès");
	}

	@Override
	public void creerTeledossierParticulierEtPremiereNotification(String token, RequeteCreationTeledossierAvecPremiereNotification requete) {
		// Log
		this.logger.info("Création d'un télé-dossier particulier '{}'", requete.id());

		// Si le bouchon est actif
		if (this.bouchonActif) {
			this.logger.warn("Attention, le bouchon SP est actif !");
			return;
		}

		// Création des entêtes avec le token
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + token);

		// Appel HTTP
		TypeReference<Void> typeRetour = new TypeReference<>() {
		};
		super.executerRequetePost(this.urlCreationTeledossier, typeRetour, requete, null, entetes);

		this.logger.info("Création d'un télé-dossier particulier avec succès");
	}

	/**
	 * Appel à l'API permettant de créer un token.
	 *
	 * @return Token SP valide
	 */
	@Override
	public String creerTokenOidcTechnique() {
		// Log
		this.logger.info("Génération du token OIDC pour les appels aux APIs SP");

		// Si le bouchon est actif
		if (this.bouchonActif) {
			this.logger.warn("Attention, le bouchon SP est actif !");
			return BOUCHON_TOKEN;
		}

		// Appel HTTP
		String token = super.executerRequetePost(this.urlToken, null, this.tokenCorps, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		this.logger.info("Génération du token OIDC avec succès");
		return token;
	}
}
