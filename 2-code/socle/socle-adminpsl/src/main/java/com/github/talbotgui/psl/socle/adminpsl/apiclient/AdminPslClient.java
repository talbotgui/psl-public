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
package com.github.talbotgui.psl.socle.adminpsl.apiclient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.api.AdminPslAPI;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class AdminPslClient extends AbstractClientHttp implements AdminPslAPI {

	/** Parametre d'une URL pour le code de démarche. */
	private static final String PARAMETRE_CODE_DEMARCHE = "{codeDemarche}";
	/** Parametre d'une URL pour l'id de la configuration. */
	private static final String PARAMETRE_ID_CONFIGURATION = "{idConfiguration}";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public AdminPslClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	@Override
	public Object chargerDetailsDunTransfert(String idTransert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object chargerVersionDeConfigurationInterne(String codeDemarche, String idConfiguration) {
		String url = URI_CONFIG_INTERNE_MODIFICATION_ET_LECTURE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche)//
				.replace(PARAMETRE_ID_CONFIGURATION, idConfiguration);

		return super.executerRequeteGet(url, new TypeReference<>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Object chargerVersionDeConfigurationPublique(String codeDemarche, String idConfiguration) {
		String url = URI_CONFIG_PUBLIQUE_MODIFICATION_ET_LECTURE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche)//
				.replace(PARAMETRE_ID_CONFIGURATION, idConfiguration);

		return super.executerRequeteGet(url, new TypeReference<>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public String connexion(String nomUtilisateur, String motDePasse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void creerNouvelleDemarche(String codeDemarche) {
		super.executerRequetePost(URI_DEMARCHES_CREATION_ET_LISTE, new TypeReference<Void>() {
		}, codeDemarche, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public String creerVersionDeConfigurationInterne(String codeDemarche, String configuration) {
		String url = URI_CONFIG_INTERNE_CREATION_ET_LISTE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche);

		return super.executerRequetePost(url, new TypeReference<String>() {
		}, configuration, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public String creerVersionDeConfigurationPublique(String codeDemarche, String configuration) {
		String url = URI_CONFIG_PUBLIQUE_CREATION_ET_LISTE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche);

		return super.executerRequetePost(url, new TypeReference<String>() {
		}, configuration, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public void genererDesTeledossiers(int nbTransferts) {
		super.executerRequeteGet(URI_GENERATION_TRANSFERTS.replace("{nbTransferts}", Integer.toString(nbTransferts)), new TypeReference<Void>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Collection<String> listerLesDemarches() {
		return super.executerRequeteGet(URI_DEMARCHES_CREATION_ET_LISTE, new TypeReference<Collection<String>>() {
		}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Map<String, String> listerLesVersionsDeConfigurationInterne(String codeDemarche) {
		return super.executerRequeteGet(URI_CONFIG_INTERNE_CREATION_ET_LISTE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche),
				new TypeReference<Map<String, String>>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Map<String, String> listerLesVersionsDeConfigurationPublique(String codeDemarche) {
		return super.executerRequeteGet(URI_CONFIG_PUBLIQUE_CREATION_ET_LISTE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche),
				new TypeReference<Map<String, String>>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public String modifierVersionDeConfigurationInterne(String codeDemarche, String idConfiguration, String configuration) {
		String url = URI_CONFIG_INTERNE_MODIFICATION_ET_LECTURE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche).replace(PARAMETRE_ID_CONFIGURATION,
				idConfiguration);

		return super.executerRequetePost(url, new TypeReference<String>() {
		}, configuration, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public String modifierVersionDeConfigurationPublique(String codeDemarche, String idConfiguration, String configuration) {
		String url = URI_CONFIG_PUBLIQUE_MODIFICATION_ET_LECTURE.replace(PARAMETRE_CODE_DEMARCHE, codeDemarche).replace(PARAMETRE_ID_CONFIGURATION,
				idConfiguration);

		return super.executerRequetePost(url, new TypeReference<String>() {
		}, configuration, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(RequeteRechercheTransfertsDto parametresRecherche) {
		return super.executerRequetePost(URI_TRANSFERT_LISTE, new TypeReference<Page<ResultatRechercheTransfertsDto>>() {
		}, parametresRecherche, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}
}
