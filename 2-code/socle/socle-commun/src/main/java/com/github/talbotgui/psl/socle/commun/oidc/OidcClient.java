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
package com.github.talbotgui.psl.socle.commun.oidc;

import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;

public interface OidcClient {

	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_CLIENT_ID = "client_id";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_CLIENT_SECRET = "client_secret";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_CODE = "code";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_CODE_VERIFIER = "code_verifier";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_GRANT_TYPE = "grant_type";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_REDIRECT_URI = "redirect_uri";
	/** Paramètre de corps de requête OIDC. */
	static final String PARAM_REFRESH_TOKEN = "refresh_token";

	/** Valeur possible pour le paramètre de corps de requête OIDC grant_type. */
	static final String VALEUR_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

	/**
	 * Méthode appelant la création d'un accessToken depuis le Back. Ainsi les éléments de sécurités sont paramétrés dans le socle et non dans
	 * l'application WEB.
	 *
	 * @param grantType
	 * @param code
	 * @param redirectUri
	 * @param codeVerifier
	 * @param clientId
	 * @param clientSecret
	 * @param refreshToken
	 * @return Un accessToken
	 */
	ReponseTokenOIDC creerAccessToken(String grantType, String code, String redirectUri, String codeVerifier, String clientId, String clientSecret,
			String refreshToken);

	/**
	 * Méthode de raffraichissement d'un accessToken depuis le refreshToken.
	 *
	 * @param clientId
	 * @param clientSecret
	 * @param refreshToken
	 * @return Un accessToken
	 */
	ReponseTokenOIDC raffraichirAccessToken(String clientId, String clientSecret, String refreshToken);

	/**
	 * Méthode de raffraichissement d'un accessToken depuis le refreshToken.
	 *
	 * @param clientId
	 * @param clientSecret
	 * @param accessToken
	 * @param refreshToken
	 * @return Un accessToken
	 */
	String raffraichirAccessTokenSiNecessaire(String clientId, String clientSecret, String accessToken, String refreshToken);
}
