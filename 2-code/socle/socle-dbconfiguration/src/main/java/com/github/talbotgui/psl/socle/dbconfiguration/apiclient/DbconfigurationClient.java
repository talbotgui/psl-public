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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class DbconfigurationClient extends AbstractClientHttp implements ConfigurationAPI {

	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/configuration";
	/** URL spécifique à la lecture de la configuration */
	public static final String URI_LECTURE_CONFIGURATION_INTERNE_DEMARCHE = DbconfigurationClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ "demarche/{codeDemarche}/interne";
	/** URL spécifique à la lecture de la configuration */
	public static final String URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE = DbconfigurationClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ "demarche/{codeDemarche}";
	/** URL spécifique à la lecture de la configuration */
	public static final String URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE_AVEC_VERSION = DbconfigurationClient.URI_DE_BASE + PREFIXE_URI_INTERNE
			+ "demarche/{codeDemarche}/{versionConfiguration}";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public DbconfigurationClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/**
	 * Lecture de la dernière configuration publique disponible d'une démarche.
	 *
	 * @param codeDemarche         Code de la démarche.
	 * @param versionConfiguration Version de la configuration.
	 * @return Configuration trouvée
	 */
	@Override
	public ConfigurationPubliqueDemarcheDto rechercherConfigurationPubliqueDeDemarche(String codeDemarche, String versionConfiguration) {
		return super.executerRequeteGet(URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE_AVEC_VERSION.replace("{codeDemarche}", codeDemarche)
				.replace("{versionConfiguration}", versionConfiguration), new TypeReference<ConfigurationPubliqueDemarcheDto>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Lecture de la dernière configuration interne disponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	@Override
	public ConfigurationInterneDemarcheDto rechercherDerniereConfigurationInterneDeDemarche(String codeDemarche) {
		return super.executerRequeteGet(URI_LECTURE_CONFIGURATION_INTERNE_DEMARCHE.replace("{codeDemarche}", codeDemarche),
				new TypeReference<ConfigurationInterneDemarcheDto>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Lecture de la dernière configuration publique disponible d'une démarche.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @return Configuration trouvée
	 */
	@Override
	public ConfigurationPubliqueDemarcheDto rechercherDerniereConfigurationPubliqueDeDemarche(String codeDemarche) {
		return super.executerRequeteGet(URI_LECTURE_CONFIGURATION_PUBLIQUE_DEMARCHE.replace("{codeDemarche}", codeDemarche),
				new TypeReference<ConfigurationPubliqueDemarcheDto>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}
}
