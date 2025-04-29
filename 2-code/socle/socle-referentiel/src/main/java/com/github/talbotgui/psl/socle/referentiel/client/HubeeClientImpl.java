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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.AbonneHubeeDto;
import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.AbonneHubeeDtoInterne;
import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.TokenHubeeDto;

import io.micrometer.tracing.Tracer;

/**
 * Chargement du référentiel des abonnés HUBEE
 */
@Service
public class HubeeClientImpl extends AbstractClientHttp implements HubeeClient {

	/** Préfixe du code du cache des abonnés HUBEEs. */
	public static final String CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE = "referentiel-insee-referentielGeographique";

	private static final String CODE_CACHE_REQUETE_ABONNES = "referentiel-hubee-abonnes";

	private static final String CODE_CACHE_REQUETE_TOKEN = "referentiel-hubee-token";

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** Corps de l'appel OIDC de création du token HUBEE */
	@Value("${hubee.token.corps}")
	private String corpsAppelOidc;

	/** Nombre de résultats demandés par appel à l'API HUBEE. */
	@Value("${hubee.abonnes.nbResultatsParPage}")
	private int nbResultatsParPage;

	/** URL de l'API des abonnés HUBEE */
	@Value("${hubee.abonnes.url}")
	private String urlAbonnes;

	/** URL de l'API de génération d'un token. */
	@Value("${hubee.token.url}")
	private String urlToken;

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
	public HubeeClientImpl(Tracer traceur, @Value("${hubee.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${hubee.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${hubee.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${hubee.proxy.nomUtilisateur:#{null}}") String nomUtilisateur, @Value("${hubee.proxy.motDePasse:#{null}}") String motDePasse) {
		// Les différentes URL sont toutes différentes les unes des autres. Donc "".
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	/** Appel à l'API de création du token HUBEE. */
	private String genererTokenOidcHubee() {
		// Log
		this.logger.info("Génération du token OIDC de l'API HUBEE");
		TypeReference<TokenHubeeDto> typeRetour = new TypeReference<>() {
		};

		// Si le cache est disponible
		String accessToken = null;
		if (this.cache.verifierActivation() && this.cache.definirNomFichierDunCache(CODE_CACHE_REQUETE_TOKEN).toFile().exists()) {
			accessToken = new String(this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE_REQUETE_TOKEN), StandardCharsets.UTF_8);
		} else {
			accessToken = super.executerRequetePost(this.urlToken, typeRetour, this.corpsAppelOidc, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.accessToken();
			this.cache.sauvegarderContenuDuCache(CODE_CACHE_REQUETE_TOKEN, accessToken.getBytes(StandardCharsets.UTF_8));
		}

		this.logger.info("Génération du token OIDC de l'API HUBEE avec succès");
		return accessToken;
	}

	/**
	 * Appel à l'API HUBEE des abonnés.
	 *
	 * @param token              Token à utiliser
	 * @param nbResultatsParPage Nombre de résultats par page.
	 * @param indexPage          Index de la page.
	 * @param resultats          Map des résultats (cumulatif)
	 * @return Nombre de résultats de la page
	 */
	private int rechercherUnePageDeResultat(String token, int nbResultatsParPage, int indexPage, MultiValueMap<String, AbonneHubeeDto> resultats) {
		this.logger.info("  Recherche des abonnés HUBEE (page {} de {} résultats)", indexPage, nbResultatsParPage);

		TypeReference<Collection<AbonneHubeeDtoInterne>> typeRetour = new TypeReference<>() {
		};

		// Si le cache est disponible, on prend
		Collection<AbonneHubeeDtoInterne> pages = null;
		if (this.cache.verifierActivation() && this.cache.definirNomFichierDunCache(CODE_CACHE_REQUETE_ABONNES + indexPage).toFile().exists()) {
			String contenuCache = new String(this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE_REQUETE_ABONNES + indexPage),
					StandardCharsets.UTF_8);
			try {
				pages = new ObjectMapper().readValue(contenuCache, typeRetour);
			} catch (JsonProcessingException e) {
				this.logger.error("Erreur à l'utilisation du cache {}", CODE_CACHE_REQUETE_ABONNES + indexPage, e);
				// Pas de throw car le traitement fonctionne toujours
			}
		}

		// Si cache non disponible ou inutilisable, appel réel
		if (pages == null) {

			// Préparation de l'appel
			String offset = Integer.toString((indexPage - 1) * nbResultatsParPage);
			List<String> parametres = Arrays.asList("offSet", offset, "maxResult", Integer.toString(nbResultatsParPage), "status", "Actif");
			List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + token, //
					HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			// Appel
			pages = super.executerRequeteGet(this.urlAbonnes, typeRetour, parametres, entetes);

			// Sauvegarde en cache
			try {
				this.cache.sauvegarderContenuDuCache(CODE_CACHE_REQUETE_ABONNES + indexPage, new ObjectMapper().writeValueAsBytes(pages));
			} catch (JsonProcessingException e) {
				this.logger.error("Erreur à la sauvegarde du cache {}", CODE_CACHE_REQUETE_ABONNES + indexPage, e);
				// Pas de throw car le traitement fonctionne toujours
			}
		}

		// Log
		this.logger.info("  Recherche des abonnés HUBEE retournant {} résultats", pages.size());

		// Résultat obtenu en
		for (AbonneHubeeDtoInterne dtoInterne : pages) {
			// transformant les objets du Hubee dans un DTO propre à nos besoin
			AbonneHubeeDto dto = new AbonneHubeeDto(dtoInterne.subscriber().companyRegister(), dtoInterne.processCode());
			// Ajoutant le résultat à la liste
			resultats.add(dto.siret(), dto);
		}

		// Renvoi du nombre de résultats dans la page
		return pages.size();
	}

	@Override
	public Map<String, List<AbonneHubeeDto>> telechargerLeReferentielDesAbonnesActifsHubee() {
		this.logger.info("Téléchargement des abonnés actifs de l'API HUBEE");

		// Appel à la création du token OIDC des APIs HUBEE
		String token = this.genererTokenOidcHubee();

		// Initialisation de la liste des résulats
		MultiValueMap<String, AbonneHubeeDto> abonnes = new LinkedMultiValueMap<>();

		// Appel en boucle tant que 1000 résultats sont remontés
		int indexPage = 1;
		int nbResultatsDeLaPage;
		do {
			nbResultatsDeLaPage = this.rechercherUnePageDeResultat(token, this.nbResultatsParPage, indexPage, abonnes);
			indexPage++;
		} while (nbResultatsDeLaPage == this.nbResultatsParPage);

		if (this.logger.isInfoEnabled()) {
			int nbAbonnements = abonnes.values().stream().map(l -> l == null ? 0 : l.size()).reduce(0, Integer::sum);
			this.logger.info("  {} abonnés (SIRET) actifs ({} abonnements différents) ont été retournés par l'API HUBEE ", abonnes.size(),
					nbAbonnements);
		}
		return abonnes;
	}

}
