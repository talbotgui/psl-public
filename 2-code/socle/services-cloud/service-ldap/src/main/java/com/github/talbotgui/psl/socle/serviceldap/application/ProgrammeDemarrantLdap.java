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
package com.github.talbotgui.psl.socle.serviceldap.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

/**
 * Classe de démarrage du LDAP.
 *
 * Il est possible de se connecter à ce LDAP avec le compte Manager (cf. paramètres adminUtilisateur et adminMdp) ou avec l'UID complet.
 */
public class ProgrammeDemarrantLdap {

	/** Logger propre à la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgrammeDemarrantLdap.class);

	/** Chemin du fichier PID. */
	private static final String PATH_PID = "./pid_service-ldap-1.pid";

	/**
	 * Méthode attendant la suppression du fichier PID pour arrêter le LDAP.
	 */
	public static void attendreSignalArret(InMemoryDirectoryServer server, String cheminPidEtLogs) {

		Path cheminPid = Paths.get(cheminPidEtLogs, PATH_PID);
		if (Files.exists(cheminPid)) {
			LOGGER.info("Pour arrêter le LDAP, supprimer le fichier PID");
		} else {
			LOGGER.error("Le fichier '{}' n'existe pas.", cheminPid);
		}

		// Boucle d'attente de la suppression du fichier PID
		while (Files.exists(cheminPid)) {
			// tant que le fichier de PID existe, le processus continue de tourner
		}

		// Arrêt de la bddet suppression des documents (inclu dans le stop)
		LOGGER.info("Arrêt du LDAP");
		server.shutDown(true);
	}

	/** Création de la configuration à partir des paramètres. */
	private static InMemoryDirectoryServerConfig creerConfiguration(ParametresDemarrageDto parametresDemarrage) throws LDAPException {
		// Configuration de l'interface LDAP
		InMemoryListenerConfig interfaceLdap = InMemoryListenerConfig.createLDAPConfig("LDAP", null, parametresDemarrage.getLdapPort(), null);

		// Configuration serveur
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(parametresDemarrage.getBaseDn());
		config.addAdditionalBindCredentials(parametresDemarrage.getAdminUtilisateur(), parametresDemarrage.getAdminMdp());
		config.setListenerConfigs(interfaceLdap);
		return config;
	}

	/**
	 * Création du PID.
	 *
	 * @param repertoirePidEtLog Répertoire du PID.
	 * @throws IOException En cas d'erreur
	 */
	private static void creerFichierPid(String repertoirePidEtLog) throws IOException {
		Path cheminPid = Paths.get(repertoirePidEtLog, PATH_PID);
		LOGGER.info("Création du fichier de PID {}", cheminPid);
		if (!Files.exists(cheminPid.getParent())) {
			Files.createDirectories(cheminPid.getParent());
		}
		Files.writeString(cheminPid, "-1", StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Import des données d'un LDIF pour initialiser le LDAP.
	 *
	 * @param cheminLdif Chemin du LDIF.
	 * @param server     Instance du serveur.
	 * @throws LDAPException En cas d'erreur.
	 */
	private static void importerDonnees(String cheminLdif, InMemoryDirectoryServer server) throws LDAPException {
		// Pas de StringUtils dans le classpath et on ne va pas ajouter une énorme dépendance pour si peu
		if (cheminLdif != null && !"".equals(cheminLdif)) {
			// Import de données
			int nbEntreesLdif = server.importFromLDIF(true, cheminLdif);
			LOGGER.info("{} entrées importées du fichier '{}'", nbEntreesLdif, cheminLdif);
		} else {
			LOGGER.info("Aucun fichier LDIF pour initialiser le LDAP");
		}
	}

	/**
	 * Démarrage du programme.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Parse des paramètres
		ParametresDemarrageDto parametres = new ParametresDemarrageDto();
		JCommander.newBuilder().addObject(parametres).build().parse(args);

		// Logs
		LOGGER.info("Paramètres au démarrage : {}", parametres);

		// Vérification de la présence du PID
		if (verifierPresenceFichierPid(parametres.getRepertoirePidEtLog())) {
			LOGGER.error("Le fichier PID existe déjà dans le répertoire '{}'", parametres.getRepertoirePidEtLog());
			return;
		}

		// Création de la configuration LDAP
		InMemoryDirectoryServerConfig config;
		try {
			config = creerConfiguration(parametres);
		} catch (LDAPException e) {
			LOGGER.error("Démarrage du LDAP impossible", e);
			return;
		}

		// Création du serveur (pas de try-with-resources car le serveur doit rester actif à la fin de la méthode)
		try (InMemoryDirectoryServer server = new InMemoryDirectoryServer(config)) {

			// Création du PID
			importerDonnees(parametres.getCheminLdif(), server);

			// Démarrage
			server.startListening();

			// Création du fichier PID surveillé pour déclencher l'arrêt de la base et du programme
			creerFichierPid(parametres.getRepertoirePidEtLog());

			// Attente puis arrêt
			attendreSignalArret(server, parametres.getRepertoirePidEtLog());

		} catch (LDAPException | IOException e) {
			LOGGER.error("Démarrage du LDAP impossible", e);
		}
	}

	/** Vérifie que le fichier PID existe (ou pas) */
	private static boolean verifierPresenceFichierPid(String repertoirePidEtLog) {
		Path cheminPid = Paths.get(repertoirePidEtLog, PATH_PID);
		return Files.exists(cheminPid);
	}
}
