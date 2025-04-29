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
package com.github.talbotgui.psl.socle.referentielexterne.apiclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.api.ReferentielExterneAPI;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class ReferentielExterneClient extends AbstractClientHttp implements ReferentielExterneAPI {

	/** URL spécifique à la rechercher d'une adresse dans la BAN */
	public static final String URI_ADRESSE_BAN = ReferentielExterneClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "adresseban";
	/** URL spécifique à la rechercher d'une commune dans la BAN */
	public static final String URI_COMMUNE_BAN = ReferentielExterneClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "communeban";
	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/referentielexterne";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public ReferentielExterneClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	@Override
	public Collection<Map<String, String>> rechercherAdresseBan(final String recherche, final String codeInsee) {
		List<String> params = new ArrayList<>(Arrays.asList("recherche", recherche));
		if (codeInsee != null) {
			params.addAll(Arrays.asList("codeInsee", codeInsee));
		}
		return super.executerRequeteGet(URI_ADRESSE_BAN, new TypeReference<Collection<Map<String, String>>>() {
		}, params, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Collection<Map<String, String>> rechercherCommuneBan(final String recherche) {
		List<String> params = Arrays.asList("recherche", recherche);
		return super.executerRequeteGet(URI_COMMUNE_BAN, new TypeReference<Collection<Map<String, String>>>() {
		}, params, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

}
