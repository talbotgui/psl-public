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
package com.github.talbotgui.psl.socle.referentiel.service.indexeur;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ITransformableEnDocumentLuceneDto;

@Service
public class IndexeurServiceImpl implements IndexeurService {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexeurServiceImpl.class);

	/** Flag indiquant que les données indexées doivent être écrites dans un fichier. */
	@Value("${cheminRepertoireDesFichiersPourAnalyse:}")
	private String cheminRepertoireDesFichiersPourAnalyse;

	/** Initialisation de l'index sans @Postconstruct car inutile pour une unique ligne */
	private Map<Class<? extends ITransformableEnDocumentLuceneDto>, Directory> indexesEnMemoire = new HashMap<>();

	/**
	 * Méthode créant systématiquement une nouvelle configuration. Sinon "java.lang.IllegalStateException: do not share IndexWriterConfig instances
	 * across IndexWriters"
	 *
	 * @return
	 */
	private IndexWriterConfig creerConfigurationAccesEcriture() {
		// Si modification de l'analiseur, il faut revoir le toLowerCase à la recherche
		return new IndexWriterConfig(new StandardAnalyzer());
	}

	/**
	 * Ecriture du JSON des données indexées dans le fichier d'analyse.
	 *
	 * @param nomClasseDonneeIndexee Type du contenu indexé.
	 * @param texte                  JSONs des contenus indexés.
	 */
	private void creerFichierDanalyse(String nomClasseDonneeIndexee, String texte) {
		// Si la fonctionnalité est active
		if (StringUtils.hasLength(this.cheminRepertoireDesFichiersPourAnalyse)) {

			// Création du répertoire si inexistant
			Path repertoireFichiersAnalyseP = Paths.get(this.cheminRepertoireDesFichiersPourAnalyse);
			File repertoireFichiersAnalyse = repertoireFichiersAnalyseP.toFile();
			if (!repertoireFichiersAnalyse.exists() && !repertoireFichiersAnalyse.mkdirs()) {
				LOGGER.warn("Impossible de créer le répertoire des fichiers d'analyse {}", repertoireFichiersAnalyse.getAbsoluteFile());
			}

			// Log création du fichier
			Path cheminFichierAnalyse = repertoireFichiersAnalyseP.resolve(nomClasseDonneeIndexee);
			LOGGER.info("  Création du fichier d'analyse {}", cheminFichierAnalyse.toFile().getAbsoluteFile());

			try {
				Files.write(cheminFichierAnalyse, texte.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.CREATE);
			} catch (IOException e) {
				LOGGER.warn("Impossible de créer le fichier d'analyse {}", cheminFichierAnalyse.toFile().getAbsoluteFile(), e);
				// Pas d'exception car ceci ne doit pas bloquer la fonctionnalité
			}
		}
	}

	/**
	 * Récupération d'un index en mémoire (ou création s'il n'existe pas encore)
	 *
	 * @param clazz Type de données à y stocker.
	 */
	// Les indexes en mémoire sont détruits à l'arrêt du service. Donc on peut ignorer le warning de fuite mémoire
	@SuppressWarnings("resource")
	private Directory creerOuRecupererIndex(Class<? extends ITransformableEnDocumentLuceneDto> clazz) {
		// Ajout de la valeur si elle est absente
		this.indexesEnMemoire.computeIfAbsent(clazz, k -> new ByteBuffersDirectory());
		// Renvoi de la valeur
		return this.indexesEnMemoire.get(clazz);
	}

	@Override
	public void initialiserIndex(Collection<? extends ITransformableEnDocumentLuceneDto> objetsAindexer) {
		Class<? extends ITransformableEnDocumentLuceneDto> clazz = objetsAindexer.iterator().next().getClass();
		LOGGER.info("  Indexation de {} objet(s) de type {}", objetsAindexer.size(), clazz.getSimpleName());

		// Récupération ou création de l'index s'il n'existe pas
		Directory indexEnMemoire = this.creerOuRecupererIndex(clazz);

		// Création du writer
		try (IndexWriter accesEcriture = new IndexWriter(indexEnMemoire, this.creerConfigurationAccesEcriture())) {

			// Purge avant insertion
			accesEcriture.deleteAll();

			// Transformation et ajout de chaque objet
			List<String> erreursDeTraitement = new ArrayList<>();
			List<String> listeJson = new ArrayList<>();
			for (ITransformableEnDocumentLuceneDto t : objetsAindexer) {
				try {
					Document doc = t.transformerObjetEnDocument();
					accesEcriture.addDocument(doc);
					listeJson.add(doc.get(IndexeurService.NOM_CHAMP_LUCENE_CONTENU_JSON));
				} catch (IOException e) {
					// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant (donc concaténation de string obligatoire)
					LOGGER.error("Erreur à l'indexation des données de type {}", clazz.getSimpleName(), e);
					erreursDeTraitement.add(e.getMessage());
				}
			}

			// Création du fichier d'analyse (pas d'écriture de chaque JSON en ligne à ligne car c'est trop long)
			this.creerFichierDanalyse(clazz.getSimpleName(), listeJson.toString());

			// Si des erreurs sont détectées
			if (!erreursDeTraitement.isEmpty()) {

				// Rollback
				try {
					accesEcriture.rollback();
				} catch (IOException e) {
					erreursDeTraitement.add(e.getMessage());
				}

				// Renvoi des erreurs
				throw new IndexationException(IndexationException.ERREUR_INDEXATION_DOCUMENT, erreursDeTraitement.size(), objetsAindexer.size(),
						String.join("\n", erreursDeTraitement));
			}

			// Sinon
			else {
				// commit des modifications (le close sera appelé automatiquement avec le TRY)
				accesEcriture.commit();

				// log du nombre d'éléments dans l'indexe
				try (IndexReader lecteur = DirectoryReader.open(indexEnMemoire)) {
					LOGGER.info("  L'index contient {} éléments pour le type {}", lecteur.numDocs(), clazz.getSimpleName());
				}
			}
		} catch (IOException e) {
			throw new IndexationException(IndexationException.ERREUR_INDEXATION, e);
		}
	}

	@Override
	public List<?> rechercher(Class<? extends ITransformableEnDocumentLuceneDto> type, String recherche, int limiteDeResultats,
			boolean rechercheSurSecondChampDeRecherche) {

		// Récupération ou création de l'index s'il n'existe pas
		Directory indexEnMemoire = this.creerOuRecupererIndex(type);

		// Définition de la clef de recherche
		String champDeRecherche = IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE;
		if (rechercheSurSecondChampDeRecherche) {
			champDeRecherche = IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE2;
		}

		try {
			// Création de l'objet de recherche
			IndexSearcher chercheur = new IndexSearcher(DirectoryReader.open(indexEnMemoire));

			// Création de la requéte
			// Le toLowerCase vient de l'usage du StandardAnalyser dans la méthode creerConfigurationAccesEcriture qui passe tout en minuscule
			BooleanQuery.Builder constructeurRequete = new BooleanQuery.Builder();
			for (String mot : recherche.split(" ")) {
				Query requetePourLeMot = new WildcardQuery(new Term(champDeRecherche, "*" + mot.toLowerCase() + "*"));
				constructeurRequete.add(requetePourLeMot, Occur.MUST);
			}

			// Exécution de la requéte
			TopDocs idDocumentsCorrespondant = chercheur.search(constructeurRequete.build(), limiteDeResultats);

			// Extraction du JSON de chaque élément trouvé
			List<Object> resultats = new ArrayList<>();
			ObjectMapper om = new ObjectMapper();
			om.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
			for (ScoreDoc scoreDoc : idDocumentsCorrespondant.scoreDocs) {
				String json = chercheur.storedFields().document(scoreDoc.doc).get(NOM_CHAMP_LUCENE_CONTENU_JSON);
				resultats.add(om.readValue(json, type));
			}

			return resultats;
		} catch (IOException e) {
			throw new IndexationException(IndexationException.ERREUR_INDEXATION_DOCUMENT, e, type.getSimpleName(), champDeRecherche, recherche);
		}
	}

	@Override
	public void viderLesIndexes() {
		for (Directory index : this.indexesEnMemoire.values()) {
			// Le commit est fait à l'appel de la méthode close()
			try (IndexWriter accesEcriture = new IndexWriter(index, this.creerConfigurationAccesEcriture())) {
				accesEcriture.deleteAll();
			} catch (IOException e) {
				throw new IndexationException(IndexationException.ERREUR_VIDANGE, e);
			}
		}
	}
}
