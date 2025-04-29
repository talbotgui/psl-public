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
package com.github.talbotgui.psl.socle.securite.apiclient.dto;

/** Classe image de la requête OIDC envoyée par le framework WEB OIDC. */
public record RequeteCreationTokenOidcDto(String code, String codeVerifier, String redirectUri, String grantType, String refreshToken) {

	/**
	 * Constructeur utilisable dans le cas d'une création de token OIDC à partir des informations échangées entre l'application et la page de
	 * connexion du fournisseur d'identité OIDC
	 */
	public RequeteCreationTokenOidcDto(String code, String codeVerifier, String redirectUri, String grantType) {
		this(code, codeVerifier, redirectUri, grantType, null);
	}

	/** Constructeur utilisable dans le cas d'un refresh de token OIDC à partir du seul refreshToken (le grantType="refresh_token"). */
	public RequeteCreationTokenOidcDto(String grantType, String refreshToken) {
		this(null, null, null, grantType, refreshToken);
	}

}
