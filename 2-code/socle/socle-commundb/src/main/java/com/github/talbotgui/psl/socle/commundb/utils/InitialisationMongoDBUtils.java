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
package com.github.talbotgui.psl.socle.commundb.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * Classe fournissant des méthodes utilitaires pour initialiser une base de données MongoDB.
 */
public class InitialisationMongoDBUtils {

	/** Chemin, dans un projet Front, vers le répertoire des bouchons d'API */
	public static final String CHEMIN_REPERTOIRE_BOUCHONAPI = "src/assets/bouchonapi/";

	/** Chemin vers les projets Front */
	public static final String CHEMIN_REPERTOIRE_PROJETS_FRONT = "../front/projects";

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(InitialisationMongoDBUtils.class);

	/**
	 * Initialisation d'une collection dans la base.
	 *
	 * @param clientMongoDB   Client MongoDB déjà connecté à l'instance précédemment démarrée.
	 * @param nomBdd          Nom de la base de données.
	 * @param nomCollection   Nom de la collecion à créer.
	 * @param indexCollection Index de la collection (peut être null).
	 * @return Le nombre de documents dans le collection.
	 */
	public static long initialiserCollection(MongoClient clientMongoDB, String nomBdd, String nomCollection, Bson indexCollection) {
		// Récupérer la base (création si besoin)
		MongoDatabase base = clientMongoDB.getDatabase(nomBdd);

		// Création de la collection
		MongoCollection<Document> collection = base.getCollection(nomCollection);
		if (indexCollection != null) {
			collection.createIndex(indexCollection, new IndexOptions().unique(true));
		}

		// Comptage des documents et log
		long nbElements = collection.countDocuments();
		LOGGER.info("La collection \"{}\" de la base de données \"{}\" contient {} élément(s)", nomCollection, nomBdd, nbElements);

		// Renvoi
		return nbElements;
	}

	/**
	 * Recherche des fichiers présents dans les répertoires de bouchon des projets FRONT Angular.
	 *
	 * @param clientMongoDB   Le client MongoDB.
	 * @param nomBdd          Nom de la base de données.
	 * @param nomCollection   Le nom de la collection.
	 * @param debutNomFichier Le préfix des noms de fichiers à insérer.
	 * @throws IOException
	 */
	public static void insererDocumentsDepuisLesFichiersDeBouchonDesProjetsFront(MongoClient clientMongoDB, String nomBdd, String nomCollection,
			String debutNomFichier) throws IOException {

		// Récupération des documents
		List<Path> fichiers = recupererCheminDesRepertoiresDeConfiguration(debutNomFichier);

		// Récupération de la collection
		MongoCollection<Document> collection = clientMongoDB.getDatabase(nomBdd).getCollection(nomCollection);

		// Pour chaque fichier trouvé
		for (Path fichierConfigurationInterne : fichiers) {
			LOGGER.info("Fichier \"{}\" inséré dans la collection.", fichierConfigurationInterne);

			// Parse du fichier
			Document doc = Document.parse(Files.readString(fichierConfigurationInterne, StandardCharsets.UTF_8));

			// Si le document ne contient pas encore d'identifiant, on en crée un
			if (!doc.containsKey("_id") && doc.containsKey("codeDemarche")) {
				String version = "123";
				if (doc.containsKey("versionConfiguration")) {
					version = doc.get("versionConfiguration").toString();
				}
				doc.append("_id", doc.get("codeDemarche") + "-" + version);
			}

			// Insertion
			collection.insertOne(doc);
		}
	}

	/**
	 * Méthode lisant le fichier depuis un JAR (donc pas de java.nio)
	 *
	 * @param fichier Ressource à lire.
	 * @return Contenu du fichier
	 * @throws IOException
	 */
	public static String lireUnFichierDepuisLeJar(Resource fichier) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fichier.getInputStream(), StandardCharsets.UTF_8))) {
			for (String line; (line = reader.readLine()) != null;) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	/**
	 * Recherche de tous les documents correspondant au pattern fourni dans le classpath
	 */
	public static Map<Resource, String> rechercherDocumentsDansLeClasspath(String cheminDesDocumentsDansLeClasspath) throws IOException {

		// Recherche des fichiers de configuration
		Resource[] fichiers = ResourcePatternUtils.getResourcePatternResolver(null).getResources(cheminDesDocumentsDansLeClasspath);
		LOGGER.info("{} configurations internes trouvées dans {}", fichiers.length, cheminDesDocumentsDansLeClasspath);

		// Pour chaque fichier
		Map<Resource, String> resultats = new HashMap<>();
		for (Resource fichier : fichiers) {

			// Lecture du fichier JSON
			String json = lireUnFichierDepuisLeJar(fichier);
			resultats.put(fichier, json);
		}

		return resultats;
	}

	/**
	 * Méthode récupérant le chemin du répertoire contenant les fichiers de configuration des démarches utilisés en tant que bouchon dans
	 * l'application WEB Angular.
	 *
	 * @param debutNomFichier le début de nom de fichier à rechercher
	 * @return les fichiers trouvés
	 */
	private static List<Path> recupererCheminDesRepertoiresDeConfiguration(String debutNomFichier) {

		// Si le chemin courant est celui d'un projet socle-dbxxx, on remonte d'un répertoire
		String prefix = "./";
		if (Files.exists(Paths.get("src"))) {
			prefix = "../";
		}

		// Accès au répertoire des projets du front
		Path repertoireProjetsFront = Paths.get(prefix, CHEMIN_REPERTOIRE_PROJETS_FRONT);
		if (!repertoireProjetsFront.toFile().exists()) {
			LOGGER.error("Aucun répertoire répertoire '{}' n'existe", repertoireProjetsFront.toAbsolutePath());
			return Collections.emptyList();
		}
		LOGGER.debug("Recherche de fichiers dans le répertoire '{}'", repertoireProjetsFront.toAbsolutePath());

		// Recherche des répertoires assets
		List<Path> resultat = new ArrayList<>();
		for (File repertoireProjet : repertoireProjetsFront.toFile().listFiles()) {
			// Aucune configuration n'est présente dans le projet 'framework'
			if ("framework".equals(repertoireProjet.getName())) {
				continue;
			}
			// Si le répertoire bouchonapi n'existe pas
			Path bouchonapi = Paths.get(repertoireProjet.getAbsolutePath(), CHEMIN_REPERTOIRE_BOUCHONAPI);
			if (!bouchonapi.toFile().exists()) {
				LOGGER.warn("Le répertoire '{}' n'existe pas", bouchonapi.toAbsolutePath());
			}
			// Sinon recherche du fichier commençant par debutNomFichier
			else {
				List<Path> resultatDansCeRepertoire = new ArrayList<>();
				for (File fichier : bouchonapi.toFile().listFiles()) {
					if (fichier.exists() && fichier.isFile() && fichier.getName().startsWith(debutNomFichier)) {
						resultatDansCeRepertoire.add(Paths.get(fichier.getAbsolutePath()));
					}
				}
				if (resultatDansCeRepertoire.isEmpty()) {
					LOGGER.error("Aucun fichier de configuration n'a été trouvé dans le répertoire '{}' avec le début '{}'",
							repertoireProjet.getAbsoluteFile(), debutNomFichier);
				} else {
					resultat.addAll(resultatDansCeRepertoire);
				}
			}
		}
		return resultat;
	}

	/**
	 * Constructeur privé pour ne pas instancier cette classe
	 */
	private InitialisationMongoDBUtils() {
		// rien à faire
	}
}
