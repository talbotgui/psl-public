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
package com.github.talbotgui.psl.socle.commun.feign;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;
import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;

import feign.Response;
import feign.codec.ErrorDecoder;

/** Composant traitant les erreurs retournées par un client Feign quand un micro-service en appelle un autre. */
@Component
public class RetreiveMessageErrorDecoder implements ErrorDecoder {

	/**
	 * Code d'erreur retourné par le décodeur pour un exception PSL renvoyée par le micro-service (le code original n'est pas dans le JSON renvoyé en
	 * sortie).
	 */
	private static final String CODE_ERREUR = "CODE_ERREUR_APPEL_MS";

	/** Logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RetreiveMessageErrorDecoder.class);

	/** Pattern permettant de identifier/parser une exception du socle. */
	// A garder cohérent avec ExceptionHandler.transformerExceptionEnJson, RetreiveMessageErrorDecoder.PATTERN_EXCEPTION_SOCLE et
	// RejetRequeteSansTokenJwtEndpoint.commence
	private static final Pattern PATTERN_EXCEPTION_SOCLE = Pattern.compile(
			"\\{\"type\":\"psl\",\"status\":([0-9]+),\"error\":\"([^\"]+)\",\"requestId\":\"([a-zA-Z0-9\\-]+)\",\"timestamp\":\"([0-9\\-\\.T:]+)\"\\}");

	/** Decoder de base. */
	private final ErrorDecoder errorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response reponse) {

		// Extraction du contenu de la réponse
		int codeHttpReponse = reponse.status();
		String contenuReponse = "";
		try {
			if (reponse.body() != null) {
				contenuReponse = IOUtils.toString(reponse.body().asInputStream(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			LOGGER.warn("Erreur durant la lecture d'une réponse venant d'un client Feign pour un appel '{}' avec le code Http '{}'", methodKey,
					codeHttpReponse);
		}

		// Trace
		LOGGER.warn("Appel, via Feign, à '{}' mais réponse '{}' avec le contenu '{}'", methodKey, codeHttpReponse,
				LogUtils.nettoyerDonneesAvantDeLogguer(contenuReponse));

		// Si l'erreur est l'absence d'une instance du micro-service demandé, renvoi d'une 404
		if (codeHttpReponse == 503 && contenuReponse.startsWith("Load balancer does not contain an instance for the service ")) {
			return new CommunException(CommunException.ERREUR_GENERIQUE_MICROSERVICE_ABSENT);
		}

		// Si l'erreur est une erreur standard générée par le socle
		Matcher matcher = PATTERN_EXCEPTION_SOCLE.matcher(contenuReponse);
		if (matcher.matches()) {

			// Extraction des données
			int codeHttp = Integer.parseInt(matcher.group(1));
			String message = matcher.group(2);

			// Log de niveau INFO dans ce micro-service car l'exception originale est déjà logguée dans le micro-service qui l'a générée
			NiveauException niveau = NiveauException.INFORMATION;

			// réinstanciation d'une exception qui ne sera pas traitée par l'i18n dans la classe ExceptionHandler
			return new PslFeignException(new ExceptionId(CODE_ERREUR, niveau, codeHttp, message));
		}

		// Sinon, gestion standard
		return this.errorDecoder.decode(methodKey, reponse);
	}
}
