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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.dto.oauth2.Oauth2TokenDto;
import com.github.talbotgui.psl.socle.referentiel.exception.ReferentielException;

import io.micrometer.tracing.Tracer;

/**
 * Equivalent des requétes envoyés :
 *
 * <pre>{@code
 * curl -k -X POST --proxy http://xxxx:xx -H "Content-Type:application/x-www-form-urlencoded" -d "grant_type=password&client_id=xxxx&client_secret=xxxx&username=xxxx&password=xxxx" https://xxx.auth.insee.net/auth/realms/elire/protocol/openid-connect/token
 * }</pre>
 *
 * <pre>{@code
 * curl -k -X GET --proxy http://xxxx:xx -H "Authorization: Bearer xxxx_access_token_xxxxxxxxxxx" https://xxx.insee.fr/referentiel/v1/service-public/lieu-naissance/commune?nombre=42000
 * }</pre>
 */
@Service
public class InseeOidcClientImpl extends AbstractClientHttp implements InseeOidcClient {

	/** Code du cache des communes de naissance. */
	public static final String CODE_CACHE_COMMUNE_NAISSANCE = "referentiel-insee-communesNaissance";

	/** Code du cache des communes UGLE. */
	public static final String CODE_CACHE_COMMUNE_UGLE = "referentiel-insee-communesUgle";

	/** Code du cache des pays de naissance. */
	public static final String CODE_CACHE_PAYS_NAISSANCE = "referentiel-insee-paysNaissance";

	/** Code du cache du token. */
	public static final String CODE_CACHE_TOKEN = "referentiel-insee-token";

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** Corps de l'appel POST permettant de récupérer le token OAUTH2 */
	@Value("${insee.token.corps}")
	private String corpsAppelOauth2;

	/** URL de l'API des communes de naissance */
	@Value("${insee.communeNaissance.url}")
	private String urlCommuneNaissance;

	/** URL de l'API des communes UGLE */
	@Value("${insee.communeUgle.url}")
	private String urlCommuneUgle;

	/** URL de l'API des pays de naissance */
	@Value("${insee.paysNaissance.url}")
	private String urlPaysNaissance;

	/** URL de l'API OAUTH2 */
	@Value("${insee.token.url}")
	private String urlTokenOauth2;

	/**
	 * Constructeur.
	 *
	 * @param desactiverSSL  Flag demandant la désactivation du contrôle du certificat SSL.
	 * @param hoteDuProxy    Hôte du proxy - si présent avec le port, un proxy est défini.
	 * @param portDuProxy    Port du proxy - si présent avec l'hôte, un proxy est défini.
	 * @param nomUtilisateur Login du proxy - si présent avec le mot de passe, une authentification de proxy est définie.
	 * @param motDePasse     Mot de passe du proxy - si présent avec le login, une authentification de proxy est définie.
	 */
	@Autowired
	public InseeOidcClientImpl(Tracer traceur, @Value("${insee.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${insee.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${insee.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${insee.proxy.nomUtilisateur:#{null}}") String nomUtilisateur, @Value("${insee.proxy.motDePasse:#{null}}") String motDePasse) {
		// Les différentes URL sont toutes différentes les unes des autres. Donc "".
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	@Override
	public Oauth2TokenDto genererToken() {
		this.logger.info("Génération du token OIDC INSEE");
		TypeReference<Oauth2TokenDto> typeRetour = new TypeReference<>() {
		};

		// Vérification de la présence du cache
		if (this.cache.verifierActivation() && this.cache.definirNomFichierDunCache(CODE_CACHE_TOKEN).toFile().exists()) {
			try {
				return new ObjectMapper().readValue(this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE_TOKEN), Oauth2TokenDto.class);
			} catch (IOException e) {
				this.logger.error("Erreur de chargement du cache {}", CODE_CACHE_TOKEN, e);
				// Pas de throw car la fonctionnalité fonctionnera néanmoins
			}
		}

		// Sinon appel réel
		Oauth2TokenDto token = super.executerRequetePost(this.urlTokenOauth2, typeRetour, this.corpsAppelOauth2,
				MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		// Sauvegarde dans le cache
		try {
			this.cache.sauvegarderContenuDuCache(CODE_CACHE_TOKEN, new ObjectMapper().writeValueAsBytes(token));
		} catch (JsonProcessingException e) {
			this.logger.error("Erreur de sauvegarde du cache {}", CODE_CACHE_TOKEN, e);
			// Pas de throw car la fonctionnalité fonctionnera néanmoins
		}

		return token;
	}

	/**
	 * Appel à l'API de téléchargement.
	 *
	 * @param <T>           Type de données à retourner.
	 * @param codeCache     Code du cache.
	 * @param url           URL à appeler.
	 * @param token         Token généré précédemment.
	 * @param typeReference Type de référence à utiliser pour faire l'appel.
	 * @return La liste des objets du bon type.
	 */
	private <T> List<T> telecharger(String codeCache, String url, Oauth2TokenDto token, TypeReference<List<T>> typeReference) {

		// Appel au cache
		List<T> reponseTypee;
		String reponse = new String(this.cache.obtenirContenuDuCacheSiActifEtDisponible(codeCache), StandardCharsets.UTF_8);

		// Si le cache est inactif ou vide
		if (reponse.isEmpty()) {
			// Exécution de la requête
			reponseTypee = super.executerRequeteGet(url, typeReference, Arrays.asList(),
					Arrays.asList(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken()));

			// Sauvegarde de la réponse dans le cache (vérification du statut avant de sérialiser avec Jackson)
			try {
				if (this.cache.verifierActivation()) {
					this.cache.sauvegarderContenuDuCache(codeCache, new ObjectMapper().writeValueAsBytes(reponseTypee));
				}
			} catch (JsonProcessingException e) {
				throw new ReferentielException(ReferentielException.ERREUR_APPEL_EXTERNE, e);
			}
		}

		// sinon
		else {
			try {
				reponseTypee = new ObjectMapper().readValue(reponse, typeReference);
			} catch (JsonProcessingException e) {
				throw new ReferentielException(ReferentielException.ERREUR_LECTURE_CACHE, e);
			}
		}

		// Renvoi de la réponse
		return reponseTypee;

	}

	@Override
	public List<CommuneNaissanceDto> telechargerLesCommunesDeNaissance(Oauth2TokenDto token) {
		this.logger.info("Chargement des communes de naissance");
		return this.telecharger(CODE_CACHE_COMMUNE_NAISSANCE, this.urlCommuneNaissance, token, new TypeReference<List<CommuneNaissanceDto>>() {
		});
	}

	@Override
	public List<CommuneUgleDto> telechargerLesCommunesUgle(Oauth2TokenDto token) {
		this.logger.info("Chargement des communes UGLE");
		return this.telecharger(CODE_CACHE_COMMUNE_UGLE, this.urlCommuneUgle, token, new TypeReference<List<CommuneUgleDto>>() {
		});
	}

	@Override
	public List<PaysNaissanceDto> telechargerLesPaysDeNaissance(Oauth2TokenDto token) {
		this.logger.info("Chargement des pays de naissance");
		return this.telecharger(CODE_CACHE_PAYS_NAISSANCE, this.urlPaysNaissance, token, new TypeReference<List<PaysNaissanceDto>>() {
		});
	}

}
