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
package com.github.talbotgui.psl.socle.referentiel.cron;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.referentiel.client.CacheDeFichierService;
import com.github.talbotgui.psl.socle.referentiel.client.DataGouvClientImpl;
import com.github.talbotgui.psl.socle.referentiel.client.InseeClientImpl;
import com.github.talbotgui.psl.socle.referentiel.client.InseeOidcClientImpl;
import com.github.talbotgui.psl.socle.referentiel.service.InitialisationReferentielService;

import jakarta.annotation.PostConstruct;

/**
 * Composant applicatif spécifique permettant de démarrer à intervalle de temps régulier un traitement.
 */
@Component
public class CronManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronManager.class);

	@Autowired
	private CacheDeFichierService cache;

	// Clef de configuration permettant de désactiver le client INSEE
	@Value("${CronManager.desactiver:false}")
	private boolean desactiverCronManager;

	@Autowired
	private InitialisationReferentielService service;

	@PostConstruct
	public void raffraichirDonneesAuDemarrageDeLapplication() {
		// Si le paramétrage demande la désactivation de l'initialisation automatique
		if (this.desactiverCronManager) {
			LOGGER.info("Initialisation DESACTIVEE du cache au démarrage de l'application (cf. clef CronManager.desactiver)");
			return;
		}

		try {
			LOGGER.info("Initialisation des indexes au démarrage de l'application");
			this.service.initialiserLesIndexes();
		} catch (final Exception e) {
			LOGGER.error("Erreur au démarrage de l'application. cf logs plus tôt.");
			// L'exception doit remonter et faire échouer la création du bean.
			// Ainsi l'application ne démarrera pas.
			throw e;
		}
	}

	@Scheduled(cron = "0 0 3 * * ?")
	public void raffraichirDonneesSelonLaCron() {
		// Si le paramétrage demande la désactivation de l'initialisation automatique
		if (this.desactiverCronManager) {
			LOGGER.info("Initialisation DESACTIVEE du cache régulier (cf. clef CronManager.desactiver)");
			return;
		}

		LOGGER.info("Initialisation du cache régulier");

		// Les erreurs sont déjé logguées donc try/catch inutile
		this.cache.reinitialiserLesCaches(Arrays.asList(InseeOidcClientImpl.CODE_CACHE_COMMUNE_NAISSANCE, InseeOidcClientImpl.CODE_CACHE_COMMUNE_UGLE,
				InseeOidcClientImpl.CODE_CACHE_PAYS_NAISSANCE, InseeClientImpl.CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE,
				DataGouvClientImpl.CODE_CACHE_CODE_POSTAUX, DataGouvClientImpl.CODE_CACHE_PROTECTION_COMMUNES));
		this.service.initialiserLesIndexes();
	}
}
