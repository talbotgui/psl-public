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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.micrometer.tracing.Tracer;

/**
 * Cette implémentation n'est pas un composant Spring !!
 *
 * Car tous les micro-services n'ont pas besoin d'un client OIDC et donc de disposer des paramètres OIDC nécessaires au constructeur.
 *
 * Donc les micro-services en ayant besoin doivent déclarer un composant qui hérite de cette classe
 */
public abstract class AbstractOidcClientImpl extends AbstractClientHttp implements OidcClient {

	/** URL de l'API des token. */
	private final String urlInfoToken;

	/** Constructeur à surcharger dans la classe parent en activant les annotations mise, ici, en commentaire */
	/* @Autowired */
	protected AbstractOidcClientImpl(Tracer traceur, /* @Value("${oidc.urls.token}") */String urlInfoToken,
			/* @Value("${oidc.desactiverSSL:false}") */ boolean desactiverSSL, /* @Value("${oidc.proxy.hoteDuProxy:#{null}}") */ String hoteDuProxy,
			/* @Value("${oidc.proxy.portDuProxy:0}") */int portDuProxy, /* @Value("${oidc.proxy.nomUtilisateur:#{null}}") */ String nomUtilisateur,
			/* @Value("${oidc.proxy.motDePasse:#{null}}") */String motDePasse) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.urlInfoToken = urlInfoToken;
	}

	// Cette méthode traite bien de données personnelles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public ReponseTokenOIDC creerAccessToken(String grantType, String code, String redirectUri, String codeVerifier, String clientId,
			String clientSecret, String refreshToken) {

		// Exécution du corps
		Map<String, Object> corps = new HashMap<>();
		if (StringUtils.hasLength(grantType)) {
			corps.put(PARAM_GRANT_TYPE, grantType);
		}
		if (StringUtils.hasLength(code)) {
			corps.put(PARAM_CODE, code);
		}
		if (StringUtils.hasLength(redirectUri)) {
			corps.put(PARAM_REDIRECT_URI, redirectUri);
		}
		if (StringUtils.hasLength(codeVerifier)) {
			corps.put(PARAM_CODE_VERIFIER, codeVerifier);
		}
		if (StringUtils.hasLength(clientId)) {
			corps.put(PARAM_CLIENT_ID, clientId);
		}
		if (StringUtils.hasLength(clientSecret)) {
			corps.put(PARAM_CLIENT_SECRET, clientSecret);
		}
		if (StringUtils.hasLength(refreshToken)) {
			corps.put(PARAM_REFRESH_TOKEN, refreshToken);
		}

		// Appel à l'API
		TypeReference<ReponseTokenOIDC> typeRetour = new TypeReference<>() {
		};
		return super.executerRequetePost(this.urlInfoToken, typeRetour, corps, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	}

	@Override
	public ReponseTokenOIDC raffraichirAccessToken(String clientId, String clientSecret, String refreshToken) {

		// Exécution de la requête
		Map<String, Object> corps = Map.of(PARAM_GRANT_TYPE, VALEUR_GRANT_TYPE_REFRESH_TOKEN, //
				PARAM_CLIENT_ID, clientId, //
				PARAM_CLIENT_SECRET, clientSecret, //
				PARAM_REFRESH_TOKEN, refreshToken);

		// Appel à l'API
		TypeReference<ReponseTokenOIDC> typeRetour = new TypeReference<>() {
		};
		return super.executerRequetePost(this.urlInfoToken, typeRetour, corps, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	}

	@Override
	public String raffraichirAccessTokenSiNecessaire(String clientId, String clientSecret, String accessToken, String refreshToken) {

		// Retrait de la signature du token
		String[] tableauDeString = accessToken.split("\\.");
		tableauDeString[2] = "";
		String accessTokenSansSignature = String.join(".", tableauDeString);

		try {
			// Tentative de parse (exception si expiré)
			Claims claims = Jwts.parser().build().parseSignedClaims(accessTokenSansSignature).getPayload();

			// Si le délai est de moins de 10 secondes, on le concidère comme expiré
			long delai = claims.getExpiration().getTime() - new Date().getTime();
			if (delai < 10000) {
				throw new ExpiredJwtException(null, claims, "token expiré dans moins de 10 secondes");
			}

		} catch (final ExpiredJwtException e) {
			// raffraichissement du token
			accessToken = this.raffraichirAccessToken(clientId, clientSecret, refreshToken).accessToken();
		}

		return accessToken;
	}
}
