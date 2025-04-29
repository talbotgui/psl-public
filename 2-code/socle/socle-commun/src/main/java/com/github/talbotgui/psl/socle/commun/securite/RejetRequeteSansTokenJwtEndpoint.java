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
package com.github.talbotgui.psl.socle.commun.securite;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commun.utils.SleuthUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Composant spécifique à Spring (Endpoint) rejetant toute requête non authentifiée.
 *
 * @see com.github.talbotgui.psl.socle.commun.application.configuration.WebSecurityConfig
 */
@Component
public class RejetRequeteSansTokenJwtEndpoint implements AuthenticationEntryPoint, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Renvoi, en cas de rejet de la requête, d'un message JSON. Le format de l'erreur est basé sur celui renvoyé par la Gateway (quand celle-ci ne
	 * trouve pas d'instance de micro-service par exemple).
	 *
	 * Méthode à conserver cohérente avec AbstractException.toJson().
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		// Au lieu de renvoyer une exception pré-formatée de Spring
		// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

		// Création d'une réponse JSON
		String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String referencePourInvestigation = SleuthUtils.calculerReferencePourInvestigation();
		// A garder cohérent avec ExceptionHandler.transformerExceptionEnJson, RetreiveMessageErrorDecoder.PATTERN_EXCEPTION_SOCLE et
		// RejetRequeteSansTokenJwtEndpoint.commence
		String message = "{\"type\":\"psl\",\"status\":401,\"error\":\"Unauthorized\",\"requestId\":\"" + referencePourInvestigation
				+ "\",\"timestamp\":\"" + date + "\"}";

		// Envoi
		response.setStatus(401);
		response.getWriter().write(message);
	}
}
