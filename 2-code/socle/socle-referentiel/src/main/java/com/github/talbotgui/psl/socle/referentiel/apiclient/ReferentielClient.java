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
package com.github.talbotgui.psl.socle.referentiel.apiclient;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.referentiel.apiclient.api.ReferentielAPI;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class ReferentielClient extends AbstractClientHttp implements ReferentielAPI {

	/** URL spécifique à la recherche d'une commune actuelle */
	public static final String URI_COMMUNE_ACTUELLE = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "communeActuelle";
	/** URL spécifique à la recherche d'une commune de naissance */
	public static final String URI_COMMUNE_NAISSANCE = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "communeNaissance";
	/** URL spécifique à la recherche d'une commune UGLE */
	public static final String URI_COMMUNE_UGLE = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "communeUgle";
	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/referentiel";
	/** URL spécifique à la recherche d'un département actuel */
	public static final String URI_DEPARTEMENT_ACTUEL = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "departementActuel";
	/** URL spécifique à la recherche d'une nationalité de pays actuel */
	public static final String URI_NATIONALITE_PAYS_ACTUEL = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "nationalitePaysActuel";
	/** URL spécifique à la recherche d'un pays actuel */
	public static final String URI_PAYS_ACTUEL = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "paysActuel";
	/** URL spécifique à la recherche d'un pays de naissance */
	public static final String URI_PAYS_NAISSANCE = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "paysNaissance";
	/** URL spécifique à la recherche d'une région actuelle */
	public static final String URI_REGION_ACTUELLE = ReferentielClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "regionActuelle";

	/** @see AbstractClientHttp */
	public ReferentielClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	@Override
	public List<CommuneNaissanceDto> rechercherCommuneNaissance(String recherche) {
		return super.executerRequeteGet(URI_COMMUNE_NAISSANCE + "?recherche=" + recherche, new TypeReference<List<CommuneNaissanceDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<CommuneActuelleDto> rechercherCommunesActuelles(String recherche) {
		return super.executerRequeteGet(URI_COMMUNE_ACTUELLE + "?recherche=" + recherche, new TypeReference<List<CommuneActuelleDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));

	}

	@Override
	public List<CommuneUgleDto> rechercherCommuneUgle(String recherche) {
		return super.executerRequeteGet(URI_COMMUNE_UGLE + "?recherche=" + recherche, new TypeReference<List<CommuneUgleDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<DepartementActuelDto> rechercherDepartementsActuels(String recherche) {
		return super.executerRequeteGet(URI_DEPARTEMENT_ACTUEL + "?recherche=" + recherche, new TypeReference<List<DepartementActuelDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<PaysActuelDto> rechercherNationalitesDePaysActuel(String recherche) {
		return super.executerRequeteGet(URI_NATIONALITE_PAYS_ACTUEL + "?recherche=" + recherche, new TypeReference<List<PaysActuelDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<PaysActuelDto> rechercherPaysActuels(String recherche) {
		return super.executerRequeteGet(URI_PAYS_ACTUEL + "?recherche=" + recherche, new TypeReference<List<PaysActuelDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<PaysNaissanceDto> rechercherPaysNaissance(String recherche) {
		return super.executerRequeteGet(URI_PAYS_NAISSANCE + "?recherche=" + recherche, new TypeReference<List<PaysNaissanceDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public List<RegionActuelleDto> rechercherRegionsActuelles(String recherche) {
		return super.executerRequeteGet(URI_REGION_ACTUELLE + "?recherche=" + recherche, new TypeReference<List<RegionActuelleDto>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

}
