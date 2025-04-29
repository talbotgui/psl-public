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
package com.github.talbotgui.psl.socle.commun.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;
import com.github.talbotgui.psl.socle.commun.feign.PslFeignException;
import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;

import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestionnaire d'erreur globale qui transforme les exceptions en réponse HTTP. *
 */
@ControllerAdvice
public class ExceptionHandler {

	/** Langue par défaut. */
	public static final String LANGUE_PAR_DEFAUT = "FR";

	/** Libellé à intégrer dans le message par défaut. */
	private static final Map<String, String> LIBELLE_MESSAGE_PAR_DEFAUT = Map.of(//
			LANGUE_PAR_DEFAUT, "Erreur inconnue", //
			"EN", "Unknown error", //
			"DE", "Unbekannter Fehler"//
	);

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

	/** Message de log. */
	private static final String MESSAGE_LOG = "Erreur traitée sur la requête {}";

	/** Message de log avec le message intégré. */
	private static final String MESSAGE_LOG_SANS_STACKTRACE = "Erreur traitée sur la requête {} : {}";

	/** Message envoyé par défaut pour toute exception qui n'est pas une exception spécifique du socle */
	private static final String MESSAGE_PAR_DEFAUT = "{\"status\":500,\"error\":\"xx\",\"message\":\"xx\"}";

	private static final Map<String, Map<String, String>> messagesI18n = new HashMap<>();

	/**
	 * Chargement en mémoire du contenu du fichier de properties lié à une classe.
	 *
	 * @param nomFichierPourCetteClasse Chemin complet dans le classpath du fichier à charger.
	 */
	private void chargerLibellesDunPropertiesDexception(String nomFichierPourCetteClasse) {
		if (!messagesI18n.containsKey(nomFichierPourCetteClasse)) {

			// Chargement du fichier des libellés liés à cette classe
			try {
				Resource[] r = ResourcePatternUtils.getResourcePatternResolver(null).getResources(nomFichierPourCetteClasse);
				if (r != null && r.length == 1) {
					String contenuFichier = IOUtils.toString(r[0].getInputStream(), StandardCharsets.UTF_8);

					// les lignes de commentaires (commençant par #) sont ignorées
					// Pas de multi-ligne (toutes doivent contenir un "=")
					Map<String, String> libelles = Arrays.asList(contenuFichier.replace("\r", "").split("\n")).stream()//
							.filter(ligne -> ligne.contains("=") && !ligne.startsWith("#"))//
							.collect(Collectors.toMap(//
									ligne -> ligne.substring(0, ligne.indexOf("=")), //
									ligne -> ligne.substring(ligne.indexOf("=") + 1)//
							));
					messagesI18n.put(nomFichierPourCetteClasse, libelles);
				} else {
					LOG.warn("Aucun fichier '{}'", nomFichierPourCetteClasse);
				}
			} catch (IOException e) {
				LOG.warn("Erreur au chargement du fichier '{}'", nomFichierPourCetteClasse, e);
			}
		}
	}

	/** Extraction de la langue fournie dans la requête HTTP (entête ACCEPT_LANGUAGE). */
	private String extraireLangue(HttpServletRequest req) {
		// Si un entête de langue est présent
		String langueEnEntete = req.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
		if (langueEnEntete != null && langueEnEntete.length() >= 2) {

			// On en récupère le code minimal en majuscule
			return langueEnEntete.toUpperCase().substring(0, 2);
		}
		// Sinon langue par défaut
		else {
			return LANGUE_PAR_DEFAUT;
		}
	}

	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> gererErreurGenerique(final HttpServletRequest req, final Exception e) {

		// En cas de donnée invalide en entrée d'une API, est créée une exception standard pour qu'elle soit traitée selon le standard PSL
		Exception exceptionAtraiter;
		if (e instanceof MethodArgumentNotValidException) {
			// Pas de paramètres dans l'exception pour ne pas diffuser d'informations techniques
			exceptionAtraiter = new CommunException(CommunException.ERREUR_VALIDATION_DONNEES, e);
		}
		// Sinon doit être traitée l'exception lancée
		else {
			exceptionAtraiter = e;
		}

		// Pour toute exception du projet, on utilise le code HTTP et le message paramétrés dans l'ExceptionId
		if (exceptionAtraiter instanceof AbstractException ex) {

			// Log
			if (NiveauException.INFORMATION.equals(ex.getId().getNiveau())) {
				LOG.info(MESSAGE_LOG_SANS_STACKTRACE, req.getRequestURI(), exceptionAtraiter.getMessage());
			} else if (NiveauException.WARNING.equals(ex.getId().getNiveau())) {
				LOG.warn(MESSAGE_LOG_SANS_STACKTRACE, req.getRequestURI(), exceptionAtraiter.getMessage());
			} else {
				LOG.error(MESSAGE_LOG, req.getRequestURI(), exceptionAtraiter);
			}

			// récupération de la langue
			String langueRetenue = this.extraireLangue(req);

			// Création de la réponse JSON à partir de l'exception
			String exceptionEnJson = this.transformerExceptionEnJson(ex, langueRetenue);

			// Renvoi d'un message spécifique
			return ResponseEntity.status(HttpStatus.valueOf(ex.getId().getCodeHttp())) //
					.contentType(MediaType.APPLICATION_JSON)//
					.body(exceptionEnJson);
		}

		// En cas d'appel sur une API existante mais pas avec la bonne méthode (DELETE au lieu de POST par exemple)
		else if (exceptionAtraiter instanceof HttpRequestMethodNotSupportedException) {
			// pas de log (poluant pour rien)
			return ResponseEntity.notFound().build();
		}

		// Pour toute autre exception,
		else {
			// log
			LOG.error(MESSAGE_LOG, req.getRequestURI(), exceptionAtraiter);

			// Choix de la langue (par défaut, la première de la liste)
			String langueRetenue = this.extraireLangue(req);
			if (!LIBELLE_MESSAGE_PAR_DEFAUT.containsKey(langueRetenue)) {
				langueRetenue = LANGUE_PAR_DEFAUT;
			}

			// on renvoi le message par défaut
			return ResponseEntity.internalServerError() //
					.contentType(MediaType.APPLICATION_JSON)//
					.body(MESSAGE_PAR_DEFAUT.replaceAll("xx", LIBELLE_MESSAGE_PAR_DEFAUT.get(langueRetenue)));
		}
	}

	/**
	 * Création du JSON à partir de l'exception.
	 *
	 * Méthode à conserver en cohérence avec RejetRequeteSansTokenJwtEndpoint.commence() et ExceptionHandler
	 *
	 * @param exceptionAtraiter Exception à traiter.
	 * @param langueRetenue     Langue à utiliser.
	 * @return JSON généré.
	 */
	private String transformerExceptionEnJson(AbstractException exceptionAtraiter, String langueRetenue) {

		// Calcul du nom du fichier lié à cette classe
		String nomFichierPourCetteClasse = "classpath:/" + exceptionAtraiter.getClass().getCanonicalName().replace('.', '/') + ".properties";

		// La classe PslFeignException n'est pas à internationnaliser
		if (!(exceptionAtraiter instanceof PslFeignException)) {
			// Chargement du fichier de libellé associé à cette classe
			this.chargerLibellesDunPropertiesDexception(nomFichierPourCetteClasse);
		}

		Map<String, String> libelles = messagesI18n.get(nomFichierPourCetteClasse);
		String messageAutiliserDansLeJson;
		// Si le fichier de libellé n'est pas chargé
		if (libelles == null) {
			// utilisation du message généré par le code Java avec le message présent dans le code Java
			messageAutiliserDansLeJson = exceptionAtraiter.getMessage();
		}
		// Si le fichier de libellé est bien chargé
		else {
			// Utilisation du message avec la langue dans la clef
			String clef = exceptionAtraiter.getId().getId();
			if (!LANGUE_PAR_DEFAUT.equals(langueRetenue)) {
				clef += "." + langueRetenue;
			}
			String messageSansParametre = libelles.get(clef);
			// Si pas dispo, utilisation du message sans langue dans la clef
			if (messageSansParametre == null) {
				messageSansParametre = libelles.get(exceptionAtraiter.getId().getId());
				LOG.warn("Pas de message pour la clef '{}' dans le fichier '{}'", clef, nomFichierPourCetteClasse);
			}

			// Ajout des paramètres
			messageAutiliserDansLeJson = AbstractException.integrerParametresDansMessage(messageSansParametre, exceptionAtraiter.getParametres());
		}

		// Création du JSON
		// A garder cohérent avec ExceptionHandler.transformerExceptionEnJson, RetreiveMessageErrorDecoder.PATTERN_EXCEPTION_SOCLE et
		// RejetRequeteSansTokenJwtEndpoint.commence
		String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		return "{\"type\":\"psl\",\"status\":" + exceptionAtraiter.getId().getCodeHttp() + ",\"error\":\"" + messageAutiliserDansLeJson
				+ "\",\"requestId\":\"" + exceptionAtraiter.getReferencePourInvestigation() + "\",\"timestamp\":\"" + date + "\"}";
	}
}