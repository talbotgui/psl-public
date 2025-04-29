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
package com.github.talbotgui.psl.socle.soumission.outil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.soumission.utils.ItextUtils;
import com.github.talbotgui.psl.socle.soumission.utils.TraitementDunChampDePdf;

/**
 * Outil de récupération des templates de documents d'un démarche PSL actuelle.
 * 
 * Etape 2 : copie des documents de l'ancienne démarche et modification des clefs à partir du fichier de mapping renseigné à la main précédemment.
 * 
 * Utilisation : cet outil ne peut être utilisé que depuis l'IDE (chemin relatif). Il utilise un fichier
 * src/main/resources/${codeDemarche}-mappingDesClefsDeTemplate.properties généré par le code de l'étape 1 et complété manuellement à partir des clefs
 * de la configruation publique de la nouvelle démarche.
 * 
 */
public class OutilRecuperationTemplateDeDocumentEtape2 {

	private static final String CHEMIN_RELATIF_ENTRE_RECAPPDF_ET_MESSAGEPROPERTIES = "../webapp/WEB-INF/messages.properties";
	private static final String CHEMIN_RELATIF_MESSAGEPROPERTIES_FRAMEWORK = "../psl-demarche-framework/psl-demarche-framework/src/main/resources/messages.properties";
	private static final String CLEF_LIBELLE_THYMELEAF_DEBUT = "#\\{";
	private static final String CLEF_LIBELLE_THYMELEAF_FIN = "\\}";
	private static final String CLEF_VELOCITY_DEBUT = "teledossier.datas.get\\(\"";
	private static final String CLEF_VELOCITY_FIN = "\"\\)";
	private static final Logger LOGGER = LoggerFactory.getLogger(OutilRecuperationTemplateDeDocumentEtape2.class);
	private static final String PREFIXE_FICHIER_GENERE = "auto-";

	/**
	 * Calcul du chemin du nouveau template dans les ressources du projet socle-dbconfiguration.
	 * 
	 * @param codeDemarche Code de la démarche.
	 * @param template     Template à dupliquer.
	 * @return Chemin du template dans les sources du projet socle-dbconfiguration.
	 * @throws IOException
	 */
	private static Path calculerNouveauCheminEtSupprimerSiExistant(String codeDemarche, Path template) throws IOException {
		// Calcul du nouveau nom de fichier
		String nomFichier = codeDemarche + "_" + template.toFile().getName().replaceAll("[\\-_]*", "");
		Path nouveauChemin = Paths.get(OutilRecuperationTemplateDeDocumentEtape1.REPERTOIRE_RESOURCES_DB, PREFIXE_FICHIER_GENERE + nomFichier);

		// Si le fichier existe, on le supprime
		Files.deleteIfExists(nouveauChemin);

		// Renvoi du résultat
		return nouveauChemin;
	}

	/**
	 * Copie du fichier original dans les ressources du projet socle-dbconfiguration en remplaçant les clefs originelles par les nouvelles clefs
	 * définies dans le fichier de mappnig.
	 * 
	 * @param codeDemarche       Code de la démarche.
	 * @param templatesExistants Templates à traiter.
	 * @param clef               Liste des clefs.
	 * @throws IOException
	 */
	private static void copierEtModifierLesTemplates(String codeDemarche, List<Path> templatesExistants, Map<String, String> clefs)
			throws IOException {
		LOGGER.info("Traitement des templates");
		for (Path template : templatesExistants) {
			LOGGER.info("  Analyse du template {}", template);

			// Calcul du nouveau chemin et suppression si existant
			Path nouveauChemin = calculerNouveauCheminEtSupprimerSiExistant(codeDemarche, template);

			// Traitement
			if (template.toFile().getName().toUpperCase().endsWith(OutilRecuperationTemplateDeDocumentEtape1.EXTENSION_PDF)) {
				copierEtModifierLeTemplatePDF(template, clefs, nouveauChemin);
			} else if (template.toFile().getName().toUpperCase().endsWith(OutilRecuperationTemplateDeDocumentEtape1.EXTENSION_HTML)) {
				copierEtModifierLeTemplateRecap(template, nouveauChemin);
			} else {
				copierEtModifierLeTemplateTextuel(template, clefs, nouveauChemin);
			}
		}
	}

	/**
	 * Traitement d'un fichier PDF.
	 * 
	 * @param template      Template à dupliquer.
	 * @param clefs         Contenu du fichier de mapping.
	 * @param nouveauChemin Chemin du nouveau fichier.
	 * @throws IOException
	 */
	public static void copierEtModifierLeTemplatePDF(Path template, Map<String, String> clefs, Path nouveauChemin) throws IOException {

		// Chargement du fichier original
		byte[] contenuTemplate = Files.readAllBytes(template);

		// Définition du traitement
		TraitementDunChampDePdf traitement = champ -> {

			// Recherche de la modification à faire
			String ancienneClef = champ.getFullyQualifiedName();
			String nouvelleClef = clefs.get(ancienneClef);

			// Application de la modification
			LOGGER.info("  Remplacement de la clef '{}' par '{}'", ancienneClef, nouvelleClef);
			champ.setPartialName(nouvelleClef);
		};

		try {
			// Appel au traitement
			byte[] contenuFichier = ItextUtils.traiterUnPdf(contenuTemplate, traitement);

			// Sauvegarde du nouveau template
			Files.write(nouveauChemin, contenuFichier);
			LOGGER.info("  Fichier {} créé", nouveauChemin);
		}
		// Gestion des erreurs
		catch (IOException e) {
			LOGGER.error("  Erreur de génération du document {}", template, e);
		}

	}

	/**
	 * Copie uniquement d'un fichier.
	 * 
	 * @param template      Template à dupliquer.
	 * @param nouveauChemin Chemin du nouveau fichier.
	 * @throws IOException
	 */
	private static void copierEtModifierLeTemplateRecap(Path template, Path nouveauChemin) throws IOException {

		// Définition du chemin des fichiers de properties de la démarche et du framework
		Path cheminFichiersLibelleDemarche = template.getParent().resolve(CHEMIN_RELATIF_ENTRE_RECAPPDF_ET_MESSAGEPROPERTIES);
		if (!cheminFichiersLibelleDemarche.toFile().exists()) {
			throw new FileNotFoundException("Fichier des libellés introuvable '" + cheminFichiersLibelleDemarche + "'");
		}
		Path cheminFichiersLibelleFramework = Paths.get(OutilRecuperationTemplateDeDocumentEtape1.REPERTOIRE_ABSOLU_DEMARCHES,
				CHEMIN_RELATIF_MESSAGEPROPERTIES_FRAMEWORK);
		if (!cheminFichiersLibelleFramework.toFile().exists()) {
			throw new FileNotFoundException("Fichier des libellés introuvable '" + cheminFichiersLibelleFramework + "'");
		}

		// Chargement des fichiers de libellés
		List<String> lignesFichiersLibelle = Files.readAllLines(cheminFichiersLibelleDemarche);
		lignesFichiersLibelle.addAll(Files.readAllLines(cheminFichiersLibelleFramework));

		// Transformation en Map
		Map<String, String> libelles = lignesFichiersLibelle.stream()
				// sans les lignes de commentaire et attention aux lignes terminant par un "="
				.filter(ligne -> !ligne.trim().startsWith("#") && ligne.contains("="))//
				.collect(Collectors.toMap(
						// mapping de la clef
						ligne -> ligne.split("=")[0],
						// mapping de la valeur
						ligne -> ligne.endsWith("=") ? "" : ligne.split("=")[1],
						// traitement des doublons (les premières lignes sont celles de la démarche et donc prioritaires)
						(valeurExistante, nouvelleValeur) -> valeurExistante//
				));

		// Chargement du fichier original
		String contenuFichier = Files.readString(template);

		// Remplacement des libellés dans le template HTML
		for (Map.Entry<String, String> libelle : libelles.entrySet()) {
			contenuFichier = contenuFichier.replaceAll(CLEF_LIBELLE_THYMELEAF_DEBUT + libelle.getKey() + CLEF_LIBELLE_THYMELEAF_FIN,
					libelle.getValue());
		}

		// Sauvegarde du nouveau template
		Files.write(nouveauChemin, contenuFichier.getBytes(StandardCharsets.UTF_8));
		LOGGER.info("  Fichier {} créé", nouveauChemin);
	}

	/**
	 * Traitement d'un fichier textuel.
	 * 
	 * @param template      Template à dupliquer.
	 * @param clefs         Contenu du fichier de mapping.
	 * @param nouveauChemin Chemin du nouveau fichier.
	 * @throws IOException
	 */
	private static void copierEtModifierLeTemplateTextuel(Path template, Map<String, String> clefs, Path nouveauChemin) throws IOException {

		// Chargement du fichier original
		String contenuFichier = Files.readString(template);

		// Remplacement des clefs
		for (Map.Entry<String, String> entry : clefs.entrySet()) {
			contenuFichier = contenuFichier.replaceAll(CLEF_VELOCITY_DEBUT + entry.getKey() + CLEF_VELOCITY_FIN, entry.getValue());
		}

		// Sauvegarde du nouveau template
		Files.write(nouveauChemin, contenuFichier.getBytes(StandardCharsets.UTF_8));
		LOGGER.info("  Fichier {} créé", nouveauChemin);
	}

	/**
	 * Chargement du contenu du fichier de mapping
	 * 
	 * @param codeDemarche Code de la démarche traitée.
	 * @throws IOException
	 */
	private static Map<String, String> lireFichierMapping(String codeDemarche) throws IOException {
		LOGGER.info("Lecture du fichier de mapping");

		// Récupération des répertoires
		Path repertoireRessources = Paths.get(OutilRecuperationTemplateDeDocumentEtape1.REPERTOIRE_RESOURCES_DB);
		Path fichierMapping = OutilRecuperationTemplateDeDocumentEtape1.calculerPathFichierDeMapping(codeDemarche);

		// Si le répertoire n'existe pas
		if (!repertoireRessources.toFile().exists()) {
			throw new FileNotFoundException(
					"Répertoire des ressources du projet socle-dbconfiguration non trouvé. Cet outil est-il exécuté depuis l'IDE ?");
		}

		// Création du fichier
		Map<String, String> clefs = Files.readAllLines(fichierMapping).stream()
				// On ignore les commentaires
				.filter(l -> l != null && !l.startsWith("#") && l.contains("=")).collect(Collectors.toMap(//
						l -> l.split("=")[0], //
						l -> l.indexOf("=") == l.length() - 1 ? "" : l.split("=")[1]//
				));

		LOGGER.info("  fichier de mapping lu et contenant {} entrées", clefs.size());
		return clefs;
	}

	/**
	 * Méthode principale ne traitant aucun argument
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Paramètre à définir pour chaque exécution
		String codeDemarche = "jechangedecoordonnees";

		// Lecture du fichier de mapping (format .properties)
		Map<String, String> clefs = lireFichierMapping(codeDemarche);

		// Recherche des templates existants dans le code de la démarche actuelle
		List<Path> templatesExistants = OutilRecuperationTemplateDeDocumentEtape1.listerTemplatesExistantsDeLaDemarche(codeDemarche);
		templatesExistants.addAll(OutilRecuperationTemplateDeDocumentEtape1.listerTemplatesRecapExistantsDeLaDemarche(codeDemarche));

		// Pour chaque fichier, extraction des clefs
		copierEtModifierLesTemplates(codeDemarche, templatesExistants, clefs);

		// Log d'aide pour la suite
		LOGGER.info("Reste maintenant à ajouter les références aux templates dans le fichier {}-ConfigurationInterneDemarche-1.0.0", codeDemarche);
		LOGGER.info("Et traiter manuellement le(s) template(s) RECAP");
	}
}
