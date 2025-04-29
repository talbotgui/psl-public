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
package com.github.talbotgui.psl.socle.dbdocument.clientantivirus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.github.talbotgui.psl.socle.dbdocument.exception.DocumentException;

/**
 * Client d'appel à ClamAV.
 *
 * Fortement inspiré de https://github.com/stevefavez/filevscan/blob/master/src/main/java/ch/vs/sci/vscan/clamav/ClamAVClient.java
 *
 */
@Component
public class ClamavClient {
	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ClamavClient.class);

	/** Timeout par défaut. */
	public static final int PARAM_DELAI_ATTENTE_MAX = 500;
	/**
	 * Taille de chaque partie du fichier envoyée.
	 *
	 * Ne pas dépasser le paramètre StreamMaxLength défini dans le fichier clamd.conf. Sinon clamd répondra "INSTREAM size limit exceeded".
	 */
	public static final int PARAM_TAILLE_CHUNK = 2048;

	/** Réponse possible en cas de mauvais paramétrage. */
	private static final String REPONSE_ERREUR_INSTREAM_SIZE_LIMIT_EXCEEDED = "INSTREAM size limit exceeded.";
	/** Partie de réponse non souhaitée. */
	private static final String REPONSE_FOUND = "FOUND";
	/** Partie de réponse souhaitée. */
	private static final String REPONSE_OK = "OK";
	/** Valeur du PONG (réponse du PING) attendue de clamd. */
	private static final byte[] REPONSE_PONG_ATTENDUE = "PONG".getBytes(StandardCharsets.US_ASCII);

	/** Requête d'envoi de flux. */
	private static final byte[] REQUETE_ENVOI_FLUX = "zINSTREAM\0".getBytes(StandardCharsets.US_ASCII);
	/** Valeur de PING envoyée à clamd pour vérifier qu'il est UP. */
	private static final byte[] REQUETE_PING = "zPING\0".getBytes(StandardCharsets.US_ASCII);

	private int delaiAttenteMax;
	private String hote;
	private int port;

	public ClamavClient(@Value("${antivirus.clamav.hote:null}") String hote, //
			@Value("${antivirus.clamav.port:0}") int port, //
			@Value("${antivirus.clamav.delaiAttenteMax:500}") int delaiAttenteMax) {
		super();
		this.hote = hote;
		this.port = port;
		this.delaiAttenteMax = delaiAttenteMax;
	}

	/**
	 * Analyse du fichier passé en paramètre.
	 *
	 * @param flux Flux à analyser
	 * @return réponse
	 **/
	public boolean analyserFichier(byte[] flux) {
		// Si l'antivirus n'est pas configuré, log et fin du traitement
		if (!this.verifierAntivirusEstConfigure()) {
			return false;
		}

		// log
		LOGGER.info("Début de l'analyse antivirale");
		Date debut = new Date();

		// Création d'un flux à partir des octets
		try (ByteArrayInputStream bis = new ByteArrayInputStream(flux)) {

			// Appel à l'antivirus
			String reponseTechnique = this.appelAntivirus(bis);

			// Vérification de la réponse
			boolean reponse = reponseTechnique.contains(REPONSE_OK) && !reponseTechnique.contains(REPONSE_FOUND);

			// Log et retour
			LOGGER.info("Fin de l'analyse antivirale : {} en {}ms pour {}o", reponse, new Date().getTime() - debut.getTime(), flux.length);
			return reponse;

		} catch (IOException e) {
			throw new DocumentException(DocumentException.SAUVEGARDE_IMPOSSIBLE, e);
		}
	}

	/**
	 * Envoi du fichier passé en paramètre à clamd par petits morceaux (chunks).
	 *
	 * Attention, le flux entrant n'est pas fermé par cette méthode !!!
	 *
	 * @param fluxAanalyser Flux à scanner.
	 * @return réponse.
	 */
	private String appelAntivirus(InputStream fluxAanalyser) throws IOException {

		// Création de la connexion, du flux sortant et définition du délai d'attente maximal
		try (Socket s = new Socket(this.hote, this.port); OutputStream fluxReponseClamd = new BufferedOutputStream(s.getOutputStream())) {
			s.setSoTimeout(this.delaiAttenteMax);

			// Initialisation de la demande
			fluxReponseClamd.write(REQUETE_ENVOI_FLUX);
			fluxReponseClamd.flush();

			// Lecture du flux à analyser partie par partie
			byte[] partie = new byte[PARAM_TAILLE_CHUNK];
			try (InputStream fluxRequeteClamd = s.getInputStream()) {
				int taillePartieLue = fluxAanalyser.read(partie);

				// Tant que le flux à analyser n'est pas fini
				while (taillePartieLue >= 0) {
					byte[] taillePartie = ByteBuffer.allocate(4).putInt(taillePartieLue).array();

					// Envoi du flux à clamd
					// Le format de donnée doit être : '<length><data>'
					fluxReponseClamd.write(taillePartie);
					fluxReponseClamd.write(partie, 0, taillePartieLue);

					// En cas de problème
					if (fluxRequeteClamd.available() > 0) {
						String message = this.lireEtVerifierLaReponse(StreamUtils.copyToByteArray(fluxRequeteClamd));
						LOGGER.warn("L'analyse antivirale a renvoyé '{}'", message);
						throw new DocumentException(DocumentException.SAUVEGARDE_IMPOSSIBLE);
					}

					// Lecture de la réponse au fur et à mesure
					taillePartieLue = fluxAanalyser.read(partie);
				}

				// Fin du scan
				fluxReponseClamd.write(new byte[] { 0, 0, 0, 0 });
				fluxReponseClamd.flush();

				// Lecture de la réponse
				return this.lireEtVerifierLaReponse(StreamUtils.copyToByteArray(fluxRequeteClamd));
			}
		}
	}

	/**
	 * Transformation des bytes de la réponse en String et vérification des message d'erreur.
	 *
	 * @param reponse Réponse en bytes
	 * @return Réponse en String
	 */
	private String lireEtVerifierLaReponse(byte[] reponse) {

		// Transformation en string
		String r = new String(reponse, StandardCharsets.US_ASCII);

		// Vérification du contenu en cas d'erreur
		if (r.startsWith(REPONSE_ERREUR_INSTREAM_SIZE_LIMIT_EXCEEDED)) {
			LOGGER.warn("Les configurations du micro-service et de l'antivirus ne sont pas compatibles : {}", r);
			throw new DocumentException(DocumentException.SAUVEGARDE_IMPOSSIBLE);
		}

		// Renvoi de la réponse
		return r;
	}

	/** Méthode vérifiant la bonne configuration de l'antivirus */
	private boolean verifierAntivirusEstConfigure() {
		if (this.port == 0 || this.hote == null) {
			LOGGER.error("L'antivirus n'est pas actuellement paramétré");
			return false;
		}
		return true;
	}

	/**
	 * PING du clamd pour vérifier qu'il est IP.
	 *
	 * @return true si le clamd est UP.
	 */
	public boolean verifierPresenceProcessusAntivirus() {
		// Si l'antivirus n'est pas configuré, log et fin du traitement
		if (!this.verifierAntivirusEstConfigure()) {
			return false;
		}

		// Création de la connexion, du flux sortant et définition du délai d'attente maximal
		try (Socket s = new Socket(this.hote, this.port); OutputStream outs = s.getOutputStream()) {
			s.setSoTimeout(this.delaiAttenteMax);

			// Envoi du PING
			outs.write(REQUETE_PING);
			outs.flush();

			// Lecture de la réponse
			byte[] b = new byte[REPONSE_PONG_ATTENDUE.length];
			InputStream inputStream = s.getInputStream();
			int copyIndex = 0;
			int readResult;
			do {
				readResult = inputStream.read(b, copyIndex, Math.max(b.length - copyIndex, 0));
				copyIndex += readResult;
			} while (readResult > 0);

			// Renvoi TRUE si la réponse reçue est la bonne
			return Arrays.equals(b, REPONSE_PONG_ATTENDUE);
		}
		// Si la connexion n'est pas possible
		catch (ConnectException e) {
			return false;
		}
		// En cas d'erreur différente
		catch (IOException e) {
			throw new DocumentException(DocumentException.SAUVEGARDE_IMPOSSIBLE, e);
		}
	}
}
