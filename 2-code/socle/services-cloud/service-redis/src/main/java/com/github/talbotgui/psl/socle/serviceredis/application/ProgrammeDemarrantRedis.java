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
package com.github.talbotgui.psl.socle.serviceredis.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.OS;

public class ProgrammeDemarrantRedis {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgrammeDemarrantRedis.class);

	/** Chemin du fichier PID. */
	private static final String PATH_PID = "./pid_service-redis-1.pid";

	/** Instance de serveur. */
	private static RedisServer serveurRedis;

	/**
	 * Attente de la suppression du PID pour arrêter le serveur proprement.
	 *
	 * @param parametres Paramètres de démarrage.
	 */
	private static void attendreSignalArret(ParametresDemarrageDto parametres) {
		Path cheminPid = Paths.get(parametres.getRepertoirePidEtLog(), PATH_PID);
		if (Files.exists(cheminPid)) {
			LOGGER.info("Pour arrêter la base de donnée, supprimer le fichier PID");
		} else {
			LOGGER.error("Le fichier '{}' n'existe pas.", cheminPid);
		}

		// Boucle d'attente de la suppression du fichier PID
		while (Files.exists(cheminPid)) {
			// tant que le fichier de PID existe, le processus continue de tourner
		}

		// Arrêt de la bddet suppression des documents (inclu dans le stop)
		LOGGER.info("Arrêt du serveur REDIS");
		serveurRedis.stop();

	}

	/**
	 * Démarrage.
	 *
	 * @param parametres Paramètres de démarrage.
	 * @throws IOException
	 */
	private static void demarrerRedis(ParametresDemarrageDto parametres) throws IOException {
		LOGGER.info("Démarrage du serveur REDIS sur le port {}", parametres.getPort());

		// Démarrage
		serveurRedis = RedisServer.builder()//
				.port(parametres.getPort())//
				.redisExecProvider(RedisExecProvider.defaultProvider().override(OS.WINDOWS, parametres.getCheminExecutable()))
				// pour éviter les popups de Windows
				.setting("bind 127.0.0.1")//
				.setting("daemonize no")//
				.setting("maxmemory 128M")//
				.build();
		serveurRedis.start();

		// Création du PID
		Path cheminPid = Paths.get(parametres.getRepertoirePidEtLog(), PATH_PID);
		LOGGER.info("Création du fichier de PID {}", cheminPid.toAbsolutePath());
		if (!Files.exists(cheminPid.getParent())) {
			Files.createDirectories(cheminPid.getParent());
		}
		Files.writeString(cheminPid, "1");
	}

	/**
	 * Méthode de démarrage
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Parse des paramètres
		ParametresDemarrageDto parametres = new ParametresDemarrageDto();
		JCommander.newBuilder().addObject(parametres).build().parse(args);

		// Démarrage
		demarrerRedis(parametres);

		// Attente
		attendreSignalArret(parametres);
	}
}
