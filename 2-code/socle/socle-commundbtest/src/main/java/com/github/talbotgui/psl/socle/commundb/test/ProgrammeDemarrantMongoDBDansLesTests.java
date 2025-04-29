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
package com.github.talbotgui.psl.socle.commundb.test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.utils.HttpClientUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.mongo.types.DatabaseDir;
import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.io.directories.PersistentDir;
import de.flapdoodle.embed.process.io.directories.TempDir;
import de.flapdoodle.embed.process.io.progress.ProgressListener;
import de.flapdoodle.embed.process.io.progress.Slf4jProgressListener;
import de.flapdoodle.embed.process.transitions.DownloadPackage;
import de.flapdoodle.embed.process.transitions.ImmutableDownloadPackage;
import de.flapdoodle.embed.process.transitions.ImmutableInitTempDirectory;
import de.flapdoodle.embed.process.transitions.InitTempDirectory;
import de.flapdoodle.embed.process.types.ProcessWorkingDir;
import de.flapdoodle.reverse.Transition;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;

/**
 * Cette classe permet de démarrer une base de données MongoDB.
 */
public class ProgrammeDemarrantMongoDBDansLesTests {

	/** Chemin vers le fichier de configuration Spring */
	static final String CHEMIN_APPLICATION_PROPERTIES = "/config/mongodb.properties";

	/** Clef de configuration Spring liée à MongoDB - host */
	static final String CLEF_SPRING_CONTENANT_HOST_MONGODB = "spring.data.mongodb.host";

	/** Clef de configuration Spring liée à MongoDB - port */
	static final String CLEF_SPRING_CONTENANT_PORT_MONGODB = "spring.data.mongodb.port";

	/** Logger propre à la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgrammeDemarrantMongoDBDansLesTests.class);

	/** Logger dédié aux logs de MongoDB */
	private static final Logger LOGGER_MONGODB = LoggerFactory.getLogger(ProgrammeDemarrantMongoDBDansLesTests.class.getName() + ".mongodb");

	/** Référence au processus. */
	private static TransitionWalker.ReachedState<RunningMongodProcess> mongoProcessus;

	/** Nom du répertoire de téléchargement et exécution de MongoDB. */
	private static final String NOM_REPERTOIRE_BINAIRE_MONGODB = ".embedmongo";

	/** Chemin du fichier PID. */
	private static final String PATH_PID = "./pid_service-nosql-1.pid";

	/** Arrêt du processus */
	public static void arreterMongoDB() {
		if ((mongoProcessus != null) && (mongoProcessus.current() != null) && mongoProcessus.current().isAlive()) {
			mongoProcessus.current().stop();
		}
	}

	/**
	 * Méthode attendant la suppression du fichier PID pour arrêter la base de
	 * données MONGO et arrêter le programme.
	 *
	 * @param cheminPidEtLogs Chemin absolu du répertoire des PID et des LOGS
	 */
	public static void attendreSignalArret(String cheminPidEtLogs) {

		Path cheminPid = Paths.get(cheminPidEtLogs, PATH_PID);
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
		LOGGER.info("Arrêt de la base de données");
		arreterMongoDB();
	}

	/**
	 * Retourne le chemin du répertoire de téléchargement et exécution de MongoDB.
	 */
	private static String definirCheminRacineDuSocle() {
		// Si le programme démarre dans le répertoire du socle
		if (Paths.get("services-cloud").toFile().exists()) {
			return Paths.get(NOM_REPERTOIRE_BINAIRE_MONGODB).toFile().getAbsolutePath();
		} else if (Paths.get("../services-cloud").toFile().exists()) {
			return Paths.get("..", NOM_REPERTOIRE_BINAIRE_MONGODB).toFile().getAbsolutePath();
		} else {
			return Paths.get("../..", NOM_REPERTOIRE_BINAIRE_MONGODB).toFile().getAbsolutePath();
		}
	}

	/**
	 * Création des paramètres de connexion à la base MongoDB
	 *
	 * @param chaineDeConnexion
	 * @return
	 */
	private static MongoClientSettings definirParametreConnexionMongoDb(String chaineDeConnexion) {
		return MongoClientSettings.builder().applyToSocketSettings(builder -> {
			builder.connectTimeout(3, TimeUnit.SECONDS);
			builder.readTimeout(3, TimeUnit.SECONDS);
		}).applyToClusterSettings(builder -> builder.serverSelectionTimeout(3, TimeUnit.SECONDS))
				.applyConnectionString(new ConnectionString(chaineDeConnexion))

				.build();
	}

	/**
	 * Installation et démarrage d'une base de données MongoDB.
	 *
	 * L'installation peut mal se passée si le téléchargement est bloqué/géré par un
	 * antivirus. Dans ce cas, il faut télécharger le fichier
	 * http://downloads.mongodb.org/win32/mongodb-win32-x86_64-x.x.x.zip (cf. logs)
	 * dans le répertoire ./2-code/socle/.embedmongo/windows.
	 *
	 * @param cheminPidEtLogs Chemin absolu du répertoire des PID et des LOGS
	 * @return TRUE si la base a été démarrée
	 */
	public static boolean demarrerUneBaseDeDonneesMongoDB(String cheminPidEtLogs)
			throws IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException {
		return demarrerUneBaseDeDonneesMongoDB(cheminPidEtLogs, null);
	}

	/**
	 * Installation et démarrage d'une base de données MongoDB.
	 *
	 * L'installation peut mal se passée si le téléchargement est bloqué/géré par un
	 * antivirus. Dans ce cas, il faut télécharger le fichier
	 * http://downloads.mongodb.org/win32/mongodb-win32-x86_64-x.x.x.zip (cf. logs)
	 * dans le répertoire ./2-code/socle/.embedmongo/windows.
	 *
	 * @param cheminPidEtLogs            Chemin absolu du répertoire des PID et des
	 *                                   LOGS
	 * @param cheminFichierConfiguration Chemin vers le répertoire de configuration
	 * @return TRUE si la base a été démarrée
	 */
	public static boolean demarrerUneBaseDeDonneesMongoDB(String cheminPidEtLogs, String cheminFichierConfiguration)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {
		// log
		LOGGER.info("Démarrage du programme de lancement d'une base de données embarquée MongoDB");

		// Lecture des données du fichier de configuration Spring
		Resource ressource;
		if (!StringUtils.hasLength(cheminFichierConfiguration)) {
			ressource = new ClassPathResource(CHEMIN_APPLICATION_PROPERTIES);
		} else {
			ressource = new FileUrlResource(cheminFichierConfiguration);
		}
		Properties proprietes = PropertiesLoaderUtils.loadProperties(ressource);
		String ip = proprietes.getProperty(CLEF_SPRING_CONTENANT_HOST_MONGODB);
		int port = Integer.parseInt(proprietes.getProperty(CLEF_SPRING_CONTENANT_PORT_MONGODB));
		String chaineConnexion = "mongodb://" + ip + ":" + port;

		// Tentative de connexion
		LOGGER.info("Vérification de l'existance d'une instance déjà démarrée");

		// Création du client
		try (MongoClient client = MongoClients.create(definirParametreConnexionMongoDb(chaineConnexion))) {
			// Vérification qu'on est connecté
			client.listDatabases().first();
			// Log
			LOGGER.info("Instance déjà démarrée !");
			// Renvoi du client en cas de succès
			return false;
		} catch (final Exception e) {
			// une simple erreur et on passe à la suite
			LOGGER.info("Aucune instance en cours d'exécution. Donc démarrage d'une nouvelle instance");
		}

		// Désactivation des contrôles SSL
		desactiverLesControlesSSL();

		// Définition des répertoires
		String cheminMongo = definirCheminRacineDuSocle();
		Path cheminBdd = Paths.get(cheminMongo, "bdd");
		Path cheminTemp = Paths.get(cheminMongo, "temp");
		Path cheminTravail = Paths.get(cheminMongo, "travail");
		Path cheminStockage = Paths.get(cheminMongo, "stockage");

		// Vérification de la présence de tous les répertoires
		for (final Path p : Arrays.asList(cheminBdd, cheminTemp, cheminTravail, cheminStockage)) {
			if (!Files.exists(p)) {
				Files.createDirectories(p);
			}
		}

		LOGGER.info("Stockage des données de la base MongoDB dans le répertoire '{}'", cheminStockage);

		// Configuration du starter
		Transition<DatabaseDir> confRepertoireBdd = Start.to(DatabaseDir.class).initializedWith(DatabaseDir.of(cheminBdd));
		InitTempDirectory confRepertoireTemp = ImmutableInitTempDirectory.builder().tempDir(TempDir.of(cheminTemp)).build();
		Transition<ProcessWorkingDir> confRepertoireTravail = Start.to(ProcessWorkingDir.class).initializedWith(ProcessWorkingDir.of(cheminTravail));
		Transition<PersistentDir> confRepertoireStockage = Start.to(PersistentDir.class).initializedWith(PersistentDir.of(cheminStockage));
		Transition<Net> confHostEtPort = Start.to(Net.class).initializedWith(Net.of(ip, port, false));
		Transition<ProgressListener> confProgression = Start.to(ProgressListener.class).initializedWith(new Slf4jProgressListener(LOGGER_MONGODB));
		Transition<ProcessOutput> confLogs = Start.to(ProcessOutput.class).initializedWith(//
				ProcessOutput.builder()//
						.output(Processors.logTo(LOGGER_MONGODB, Slf4jLevel.INFO))//
						.error(Processors.logTo(LOGGER_MONGODB, Slf4jLevel.ERROR))//
						.commands(Processors.logTo(LOGGER_MONGODB, Slf4jLevel.INFO))//
						.build()//
		);

		// Redéfinition du téléchargement car la classe par défaut télécharge dans le
		// répertoire temporaire de Windows puis déplace le fichier avec un
		// AtomicMove (cf. URLConnections.downloadTo)
		ImmutableDownloadPackage telechargeur = DownloadPackage.withDefaults()
				.withDownloadToPath((url, destination, proxy, userAgent, timeoutConfig, copyListener) -> {
					try {

						// Définition d'un fichier temporaire
						Path fichierTemporaireAcoteDeLaDestination = Paths.get(destination.toString() + ".temp");
						LOGGER.info("Téléchargement dans le répertoire '{}' depuis l'URL '{}'", fichierTemporaireAcoteDeLaDestination, url);

						// Téléchargement dans le fichier temporaire
						HttpClient clientHttp = HttpClientUtils.creerLeBuilderDuClientHttp().build();
						HttpRequest requeteHTTP = HttpClientUtils.creerLeBuilderDeLaRequeteDeBase(Long.valueOf(timeoutConfig.getConnectionTimeout()))//
								.uri(URI.create(url.toString()))//
								.GET()//
								.build();
						clientHttp.send(requeteHTTP, HttpResponse.BodyHandlers.ofFile(fichierTemporaireAcoteDeLaDestination));

						// Déplacement atomic dans le bon chemin
						LOGGER.info("Suite au téléchargement, copie du fichier '{}' dans '{}'", fichierTemporaireAcoteDeLaDestination, destination);
						Files.move(fichierTemporaireAcoteDeLaDestination, destination, StandardCopyOption.ATOMIC_MOVE);
						LOGGER.info("Copie faite");

					} catch (final InterruptedException e) {
						throw new IOException(e);
					}
				});

		// Création instance à démarrer
		// @see
		// https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/blob/main/docs/Howto.md
		// @see
		// https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/blob/main/docs/UseCases.md
		Mongod mongodExe = Mongod.builder()//
				.net(confHostEtPort)//
				.progressListener(confProgression).processOutput(confLogs)//
				.databaseDir(confRepertoireBdd).initTempDirectory(confRepertoireTemp)//
				.persistentBaseDir(confRepertoireStockage).processWorkingDir(confRepertoireTravail)//
				.downloadPackage(telechargeur)//
				.build();

		// Démarrage du processus MongoDB
		LOGGER.info("Lancement de la base de données MongoDB");
		mongoProcessus = mongodExe.start(Version.Main.V6_0);

		// Création du fichier PID surveillé pour déclencher l'arrêt de la base et du
		// programme
		Path cheminPid = Paths.get(cheminPidEtLogs, PATH_PID);
		LOGGER.info("Création du fichier de PID {}", cheminPid.toAbsolutePath());
		if (!Files.exists(cheminPid.getParent())) {
			Files.createDirectories(cheminPid.getParent());
		}
		Files.writeString(cheminPid, "PID inconnu", StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		// Création d'un client
		MongoClients.create(definirParametreConnexionMongoDb(chaineConnexion));

		// Une instance est bien démarrée
		return true;
	}

	/**
	 * Désactivation statique du contexte SSL pour permettre le téléchargement du
	 * binaire.
	 */
	private static void desactiverLesControlesSSL() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustManagers = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// rien à faire
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// rien à faire
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} };
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustManagers, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}
}
