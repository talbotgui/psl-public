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
package com.github.talbotgui.psl.socle.securite.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.securite.apiclient.SecuriteClient;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteAuthentificationMotDePasseDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-securite", contextId = "SecuriteAPI")
public interface SecuriteAPI {

	/**
	 * Recherche des informations de l'utilisateur connecté (via le token fourni).
	 *
	 * @return Les informations de l'utilisateur
	 */
	@GetMapping(value = SecuriteClient.URI_INFO_USAGER, produces = MediaType.APPLICATION_JSON_VALUE)
	InformationSpUsagerDto chargerInformationUtilisateur();

	/** Appel au fournisseur OIDC puis de l'API token et enfin génération d'un token PSL contenant toutes ces informations. */
	@PostMapping(value = SecuriteClient.URI_TOKEN_OIDC, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	ReponseTokenOIDC creerTokenOidc(
			@RequestBody MultiValueMap<@Pattern(regexp = RegexUtils.OIDC_PARAM_CLEF) String, @Pattern(regexp = RegexUtils.OIDC_PARAM_VALEUR) Object> paramMap)
			throws Exception;

	/**
	 * Création d'un token PSL à partir d'un nom d'utilisateur et d'un mot de passe.
	 *
	 * @param requete Paramètres d'authentification.
	 * @return Token PSL
	 */
	@PostMapping(value = SecuriteClient.URI_AUTHENTIFICATION_MOT_DE_PASSE, produces = MediaType.APPLICATION_JSON_VALUE)
	ReponseJwtDto sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(@Valid @RequestBody RequeteAuthentificationMotDePasseDto requete);

	/** Création d'un token PSL en mode anonyme (nécessaire pour tous les appels HTTP à une API du socle. */
	@PostMapping(value = SecuriteClient.URI_AUTHENTIFICATION_ANONYME, produces = MediaType.APPLICATION_JSON_VALUE)
	ReponseJwtDto sauthentifierEnAnonyme();
}