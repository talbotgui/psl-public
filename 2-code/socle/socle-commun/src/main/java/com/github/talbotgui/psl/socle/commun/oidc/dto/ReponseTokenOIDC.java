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
package com.github.talbotgui.psl.socle.commun.oidc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Classe image de la reponse OIDC renvoyée par le fournisseur d'identité OIDC. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReponseTokenOIDC(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") String expiresIn,
		@JsonProperty("refresh_expires_in") String refreshExpiresIn, @JsonProperty("refresh_token") String refreshToken,
		@JsonProperty("token_type") String tokenType, @JsonProperty("id_token") String idToken, @JsonProperty("session_state") String sessionState,
		@JsonProperty("scope") String scope) {
}
