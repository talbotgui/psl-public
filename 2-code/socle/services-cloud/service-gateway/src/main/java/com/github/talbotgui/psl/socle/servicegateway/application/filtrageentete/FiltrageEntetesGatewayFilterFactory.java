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
package com.github.talbotgui.psl.socle.servicegateway.application.filtrageentete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * Filtre de la gateway pour éliminer les entêtes non autorisés des requêtes entrantes.
 */
@Component
public class FiltrageEntetesGatewayFilterFactory extends AbstractGatewayFilterFactory<ConfigurationFiltrageEntete> {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(FiltrageEntetesGatewayFilterFactory.class);

	/** Constructeur. */
	public FiltrageEntetesGatewayFilterFactory() {
		super(ConfigurationFiltrageEntete.class);
	}

	@Override
	public GatewayFilter apply(ConfigurationFiltrageEntete config) {

		// Lecture du paramétrage
		List<String> listeDesEntetesAutorises = this.creerListeDesEntetesAutorisesEnMajuscules(config.getListeEntetesAutorises());

		// Traitement de la requête/réponse
		return (exchange, chain) -> {

			// Pre-processing
			this.supprimerLesEntetesNonAutorises(exchange, listeDesEntetesAutorises);

			// Appel au micro-service
			return chain.filter(exchange);

			// Post-processing éventuel si besoin
			// .then(Mono.fromRunnable(() -> {}))
		};
	}

	/**
	 * Transformation de la chaine de configuration en une liste d'entête en majuscule.
	 *
	 * @param chaineDesEntetesAutorises liste d'entêtes séparés par une ','.
	 * @return Une liste d'entêtes en majuscule.
	 */
	// méthode protected pour être testable depuis le même package (le classe dans son ensemble n'est pas testable facilement
	protected List<String> creerListeDesEntetesAutorisesEnMajuscules(String chaineDesEntetesAutorises) {
		// Liste vide par défaut
		List<String> liste = new ArrayList<>();

		// Si la liste n'est pas vide, passage en majuscule et découpage
		if (StringUtils.hasLength(chaineDesEntetesAutorises)) {
			chaineDesEntetesAutorises = chaineDesEntetesAutorises.toUpperCase().replace(" ", "");
			liste.addAll(Arrays.asList(chaineDesEntetesAutorises.split(",")));
		}

		// Renvoi de la liste
		return liste;

	}

	/**
	 * Suppression des entêtes non présents dans la liste des entêtes autorisés.
	 *
	 * @param exchangeOriginal         Echange original à dupliquer, modifier et renvoyer.
	 * @param listeDesEntetesAutorises Liste des entêtes autorisés.
	 */
	// méthode protected pour être testable depuis le même package (le classe dans son ensemble n'est pas testable facilement
	protected ServerWebExchange supprimerLesEntetesNonAutorises(ServerWebExchange exchangeOriginal, List<String> listeDesEntetesAutorises) {

		// Initialisation de "traceId" pour avoir, dans les logs, l'ID de la requête (l'ID généré par la gateway)
		// Mais le traceId de MicroMeter (anciennement Sleuth) n'est pas initialisé
		MDC.put("traceId", exchangeOriginal.getRequest().getId());

		// extraction des entêtes non présents dans les entêtes autorisés
		List<String> entetesAsupprimer = exchangeOriginal.getRequest().getHeaders().keySet().stream()//
				.filter(e -> !listeDesEntetesAutorises.contains(e.toUpperCase()))//
				.toList();

		// si aucun entête à supprimer, on sort avec l'échange original (gain de performance)
		if (entetesAsupprimer.isEmpty()) {
			return exchangeOriginal;
		}

		// log à fin d'analyse de sécurité
		if (LOGGER.isInfoEnabled()) {
			// Attention, il est possible d'injecter, dans les logs, des données venant du navigateur de l'usager à travers le nom de l'entête.
			LOGGER.info("Suppression des entêtes {} en entrée de la gateway",
					entetesAsupprimer.toString().replaceAll("[^A-Za-z0-9_\\-,\\[\\] ]*", ""));
		}

		return exchangeOriginal
				// duplication de l'échange pour pouvoir en modifier le contenu
				.mutate()
				// modification de la requête via une callback
				.request(builder ->
				// modification des entêtes
				builder.headers(headers -> {
					// suppression des entêtes non autorisés
					for (final String e : entetesAsupprimer) {
						headers.remove(e);
					}
				}))
				// Construction de l'échange à partir des modifications demandées
				.build();
	}
}
