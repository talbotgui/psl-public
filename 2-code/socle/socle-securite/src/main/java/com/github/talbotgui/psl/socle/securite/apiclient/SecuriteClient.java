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
package com.github.talbotgui.psl.socle.securite.apiclient;

import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.securite.apiclient.api.SecuriteAPI;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteAuthentificationMotDePasseDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class SecuriteClient extends AbstractClientHttp implements SecuriteAPI {

	/** URL spécifique à l'authentification anonyme */
	public static final String URI_AUTHENTIFICATION_ANONYME = SecuriteClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "authentificationAnonyme";

	/** URL spécifique à l'authentification par mot de passe */
	public static final String URI_AUTHENTIFICATION_MOT_DE_PASSE = SecuriteClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "authentificationMotDePasse";

	/** URL spécifique à l'authentification OIDC */
	public static final String URI_AUTHENTIFICATION_OIDC = SecuriteClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ JwtSecuriteFilter.URI_AUTHENTIFICATION_OIDC;

	/** URL de base du projet socle-securite à l'authentification */
	public static final String URI_DE_BASE = JwtSecuriteFilter.URI_DE_BASE_SECURITE;

	/** URL spécifique à la récupération des informations personnelles */
	public static final String URI_INFO_USAGER = SecuriteClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "info";

	/** URL spécifique à l'authentification anonyme */
	public static final String URI_TOKEN_OIDC = SecuriteClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "token";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public SecuriteClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	@Override
	public InformationSpUsagerDto chargerInformationUtilisateur() {
		return super.executerRequeteGet(URI_INFO_USAGER, new TypeReference<InformationSpUsagerDto>() {
		});
	}

	// Cette méthode traite bien de données personnelles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public ReponseTokenOIDC creerTokenOidc(MultiValueMap<String, Object> paramMap) throws Exception {
		TypeReference<ReponseTokenOIDC> type = new TypeReference<ReponseTokenOIDC>() {
		};
		return super.executerRequetePost(URI_AUTHENTIFICATION_OIDC, type, paramMap);
	}

	@Override
	public ReponseJwtDto sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(RequeteAuthentificationMotDePasseDto requete) {
		TypeReference<ReponseJwtDto> type = new TypeReference<ReponseJwtDto>() {
		};
		return super.executerRequetePost(URI_AUTHENTIFICATION_MOT_DE_PASSE, type, requete);
	}

	@Override
	public ReponseJwtDto sauthentifierEnAnonyme() {
		return super.executerRequetePost(URI_AUTHENTIFICATION_ANONYME, new TypeReference<ReponseJwtDto>() {
		}, "");
	}
}
