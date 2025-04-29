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
package com.github.talbotgui.psl.socle.referentielexterne.client;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.utils.HttpClientUtils;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ReponseBanDto;

import io.micrometer.tracing.Tracer;

@Service
public class BanClientImpl extends AbstractClientHttp implements BanClient {

	public static ReponseBanDto creerReponseBanVide() {
		return new ReponseBanDto(null, new ArrayList<>(), null, null, null, null, null, null);
	}

	@Autowired
	public BanClientImpl(Tracer traceur, @Value("${ban.url}") String urlDeBase, @Value("${ban.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${ban.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${ban.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${ban.proxy.nomUtilisateur:#{null}}") String nomUtilisateur, @Value("${ban.proxy.motDePasse:#{null}}") String motDePasse) {
		super(traceur, urlDeBase, desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/** Interrogation de la BAN avec l'URL passée en paramètre */
	private ReponseBanDto interrogerBan(String url) {
		this.logger.info("Appel à '{}'.", LogUtils.nettoyerDonneesAvantDeLogguer(url));

		// En cas de desactivation
		if (super.urlDeBase == null) {
			return creerReponseBanVide();
		}

		return super.executerRequeteGet(url, new TypeReference<ReponseBanDto>() {
		});

	}

	@Override
	public ReponseBanDto rechercherAdresseBan(String recherche, String codeInsee) {

		// Création de l'URL
		String url;
		if (codeInsee == null) {
			url = HttpClientUtils.creerURL("", PARAM_TYPE, TYPE_ADRESSE, PARAM_LIMITE, LIMITE_NB_RESULTATS, PARAM_REQUETE, recherche);
		} else {
			url = HttpClientUtils.creerURL("", PARAM_TYPE, TYPE_ADRESSE, PARAM_LIMITE, LIMITE_NB_RESULTATS, PARAM_REQUETE, recherche,
					PARAM_CODE_INSEE, codeInsee);
		}

		// Appel à l'API
		return this.interrogerBan(url);

	}

	@Override
	public ReponseBanDto rechercherCommuneBan(String recherche) {

		// Création de l'URL
		String url = HttpClientUtils.creerURL("", PARAM_TYPE, TYPE_COMMUNE, PARAM_LIMITE, LIMITE_NB_RESULTATS, PARAM_REQUETE, recherche);

		// Appel à l'API
		return this.interrogerBan(url);

	}

}
