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
package com.github.talbotgui.psl.socle.commun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;

/**
 * Classe utilitaire pour manipuler SSLContext.
 */
public class SslContextUtils {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SslContextUtils.class);

	/**
	 * Créer un contexte SSL avec un certificat précis.
	 *
	 * @param cheminFichier
	 * @return
	 */
	public static SSLContext creerContexteSslAvecUnTruststorePrecis(String cheminFichier, String motDePasse) {

		// Log
		LOGGER.info("Création d'un contexte SSL avec le certificat {}", cheminFichier);

		try {
			// Création d'un keystore avec le certificat
			KeyStore keyStore = SslContextUtils.lireKeyStore(cheminFichier, motDePasse);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, motDePasse.toCharArray());

			// Création d'un contexte en y ajoutant le contenu du keystore
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

			// Renvoi
			return sslContext;
		} catch (final NoSuchAlgorithmException | KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
			throw new CommunException(CommunException.ERREUR_GENERIQUE, e);
		}
	}

	/**
	 * Création d'un keystore avec le certificat fourni en paramètre.
	 *
	 * @param cheminFichier Chemin vers le certificat à lire et intégrer dans le keystore.
	 * @param motDePasse
	 * @return le keystore créé.
	 */
	public static KeyStore lireKeyStore(String cheminFichier, String motDePasse) {
		// Vérification présence fichier
		if (!new File(cheminFichier).exists()) {
			throw new CommunException(CommunException.ERREUR_GENERIQUE, new FileNotFoundException(cheminFichier));
		}

		try (final InputStream inputStream = new FileInputStream(cheminFichier)) {
			// Création du keystore
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(inputStream, motDePasse.toCharArray());

			// Logs
			LOGGER.info("Le keystore '{}' contient {} entrées", cheminFichier, keystore.size());
			for (final Enumeration<String> enu = keystore.aliases(); enu.hasMoreElements();) {
				String alias = enu.nextElement();
				Certificate c = keystore.getCertificate(alias);
				if (keystore.isKeyEntry(alias)) {
					LOGGER.debug("L'entrée '{}' est bien une clef privée avec pour Subject '{}'", alias,
							((X509Certificate) c).getSubjectX500Principal().getName());
				} else {
					LOGGER.warn("L'entrée '{}' n'est pas une clef privée", cheminFichier);
				}
			}

			return keystore;
		} catch (final Exception e) {
			throw new CommunException(CommunException.ERREUR_GENERIQUE, e);
		}
	}

	/**
	 * Constructeur privé pour bloquer l'instanciation.
	 */
	private SslContextUtils() {
		// Rien à faire
	}
}