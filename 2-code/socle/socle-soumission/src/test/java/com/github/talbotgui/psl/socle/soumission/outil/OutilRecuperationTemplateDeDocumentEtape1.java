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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.soumission.utils.ItextUtils;
import com.github.talbotgui.psl.socle.soumission.utils.TraitementDunChampDePdf;

/**
 * Outil de récupération des templates de documents d'un démarche PSL actuelle.
 *
 * Etape 1 : extraire les clefs utilisées depuis les templates vers un fichier de properties.
 *
 * Limitations : cet outil ne peut être utilisé que depuis l'IDE (chemin relatif). Il génère un fichier
 * src/main/resources/${codeDemarche}-mappingDesClefsDeTemplate.properties.
 *
 * Utilisation : modifier le code de démarche dans la méthode 'main' avant de démarrer le code.
 *
 */
public class OutilRecuperationTemplateDeDocumentEtape1 {

	public static final String CHEMIN_RELATIF_DES_TEMPLATES_DANS_DEMARCHE_EXISTANTE = "src/main/resources/documentAGenerer";
	public static final String CHEMIN_RELATIF_DES_TEMPLATES_RECAP_DANS_DEMARCHE_EXISTANTE = "src/main/resources";
	private static final String DEBUT_NOM_FICHIER_RECAP = "RECAP";
	public static final String EXTENSION_HTML = "HTML";
	public static final String EXTENSION_PDF = "PDF";
	private static final List<String> EXTENSIONS_A_NE_PAS_TRAITER = java.util.Arrays.asList("PNG");
	private static final String FIN_NOM_FICHIER_RECAP = ".HTML";
	private static final Logger LOGGER = LoggerFactory.getLogger(OutilRecuperationTemplateDeDocumentEtape1.class);
	private static final String PATTERN_CLEF_VELOCITY = "\\$teledossier.datas.get\\(\"([a-zA-Z0-9\\.\\-]*)\"\\)";
	public static final String REPERTOIRE_ABSOLU_DEMARCHES = "D:/xxx";
	public static final String REPERTOIRE_RESOURCES_DB = "../socle-dbconfiguration/src/main/resources/db";
	public static final String SUFFIXE_FICHIER_MAPPING = "-mappingDesClefsDeTemplate.properties";

	/**
	 * Méthode ajoutant un nom de fichier dans la liste associée à une clef. Création de l'entrée dans la Map si nécessaire.
	 *
	 * @param clefs      La Map.
	 * @param clef       La clef.
	 * @param nomFichier Le nom du fichier dans lequel est utilisée la clef.
	 */
	private static void ajouterUneClef(Map<String, Set<String>> clefs, String clef, String nomFichier) {

		// Recherche d'une liste pour la clef
		Set<String> valeursExistantes = clefs.get(clef);

		// Si aucune liste, on la crée
		if (valeursExistantes == null) {
			valeursExistantes = new TreeSet<>();
			clefs.put(clef, valeursExistantes);
		}

		// On ajoute le fichier dans la liste
		valeursExistantes.add(nomFichier);
	}

	/** Méthode calculant le chemin vers le fichier de mapping */
	public static Path calculerPathFichierDeMapping(String codeDemarche) {
		return Paths.get(REPERTOIRE_RESOURCES_DB, codeDemarche + SUFFIXE_FICHIER_MAPPING);
	}

	/**
	 * Suppression du fichier ${codeDemarche}-mappingDesClefsDeTemplate.properties si existant et recréation avec la liste des clefs. Les noms des
	 * templates associés à chaque clef sont mis en commentaire pour aider à l'analyse et la complétion du fichier.
	 *
	 * @param codeDemarche Code de la démarche traitée.
	 * @param clefs        Liste des clefs trouvées.
	 * @throws IOException
	 */
	private static void creerFichierMapping(String codeDemarche, Map<String, Set<String>> clefs) throws IOException {
		LOGGER.info("Création du fichier de mapping");

		// Récupération des répertoires
		Path repertoireRessources = Paths.get(REPERTOIRE_RESOURCES_DB);
		Path fichierMapping = calculerPathFichierDeMapping(codeDemarche);

		// Si le répertoire n'existe pas
		if (!repertoireRessources.toFile().exists()) {
			throw new FileNotFoundException(
					"Répertoire des ressources du projet socle-dbconfiguration non trouvé. Cet outil est-il exécuté depuis l'IDE ?");
		}

		// s'il existe, le fichier est supprimé
		Files.deleteIfExists(fichierMapping);

		// Création du fichier
		try (BufferedWriter writer = Files.newBufferedWriter(fichierMapping)) {
			writer.append("#\n#Fichier de mapping pour " + codeDemarche + "\n#\n");

			// Pour chaque clef
			for (final Map.Entry<String, Set<String>> entry : clefs.entrySet()) {
				writer.append("# Dans " + entry.getValue() + "\n");
				writer.append(entry.getKey() + "=\n");
			}
		}

		LOGGER.info("  fichier de mapping '{}' créé", fichierMapping);
	}

	/**
	 * Extraction des clefs du template.
	 *
	 * @param templatesExistants Templates à traiter
	 * @param clef               Liste des clefs
	 * @throws IOException
	 */
	private static void extraireLesClefsDesTemplates(List<Path> templatesExistants, Map<String, Set<String>> clefs) throws IOException {
		LOGGER.info("Recherche les templates");
		for (final Path template : templatesExistants) {
			LOGGER.info("  Analyse du template {}", template);

			// Analyse selon le type de document
			if (template.toFile().getName().toUpperCase().endsWith(EXTENSION_PDF)) {
				extraireLesClefsDunTemplatePdf(template, clefs);
			} else {
				extraireLesClefsDunTemplateTextuel(template, clefs);
			}
		}
		LOGGER.info("  {} clefs trouvées", clefs.size());
	}

	/**
	 * Extraction des clefs utilisées dans un PDF.
	 *
	 * @param template Template PDF à analyser.
	 * @param clefs    Liste des clefs déjà trouvées.
	 * @throws IOException
	 */
	public static void extraireLesClefsDunTemplatePdf(Path template, Map<String, Set<String>> clefs) throws IOException {

		// lecture du PDF (avec chargement complet en mémoire)
		String nomFichier = template.toFile().getName();

		// Déclaration du traitement
		TraitementDunChampDePdf traitement = champ -> {
			if (PDCheckBox.class.isInstance(champ)) {
				LOGGER.info("La clef '{}' est un radio avec les valeurs possibles {}", champ.getFullyQualifiedName(),
						((PDCheckBox) champ).getOnValues());
			}
			ajouterUneClef(clefs, champ.getFullyQualifiedName(), nomFichier);

		};

		// Lancement du traitement
		ItextUtils.traiterUnPdf(Files.readAllBytes(template), traitement);

	}

	/**
	 * Extraction des clefs utilisées dans un template textuel Velocity.
	 *
	 * @param template Template Velocity à analyser.
	 * @param clefs    Liste des clefs déjà trouvées.
	 * @throws IOException
	 */
	private static void extraireLesClefsDunTemplateTextuel(Path template, Map<String, Set<String>> clefs) throws IOException {
		// Lecture du contenu du fichier
		String nomFichier = template.toFile().getName();
		String contenuTemplate = Files.readString(template);

		// Recherche des clefs
		Matcher matcher = Pattern.compile(PATTERN_CLEF_VELOCITY).matcher(contenuTemplate);

		// Pour chaque clef trouvée
		while (matcher.find()) {
			String clef = matcher.group(1);
			ajouterUneClef(clefs, clef, nomFichier);
		}
	}

	/**
	 * Recherche des templates existants dans le code de la démarche actuelle.
	 *
	 * @param codeDemarche Code de la démarche à analyser.
	 * @return La liste des templates de document.
	 * @throws IOException
	 */
	public static List<Path> listerTemplatesExistantsDeLaDemarche(String codeDemarche) throws IOException {
		LOGGER.info("Recherche des templates existants de la démarche '{}'", codeDemarche);

		// calcul des chemins
		Path repertoireTemplates = Paths.get(REPERTOIRE_ABSOLU_DEMARCHES, codeDemarche,
				CHEMIN_RELATIF_DES_TEMPLATES_DANS_DEMARCHE_EXISTANTE);

		// vérification de l'existence des répertoires
		if (!repertoireTemplates.toFile().exists()) {
			throw new FileNotFoundException("Le répertoire '" + repertoireTemplates + "' n'existe pas.");
		}

		// Filtrage des fichiers selon l'extension
		List<Path> templates = Files.list(repertoireTemplates)//
				.filter(p -> {
					String nomFichier = p.toFile().getName();
					String extension = nomFichier.toUpperCase().substring(nomFichier.lastIndexOf(".") + 1);
					return !EXTENSIONS_A_NE_PAS_TRAITER.contains(extension);
				}) //
				.toList();

		// Renvoi de la liste
		LOGGER.info("  {} template(s) trouvé(s) dans {}", templates.size(), repertoireTemplates);
		return templates;
	}

	/**
	 * Recherche des templates RECAP existants dans le code de la démarche actuelle.
	 *
	 * @param codeDemarche Code de la démarche à analyser.
	 * @return La liste des templates de document.
	 * @throws IOException
	 */
	public static List<Path> listerTemplatesRecapExistantsDeLaDemarche(String codeDemarche) throws IOException {
		LOGGER.info("Recherche des templates RECAP existants de la démarche '{}'", codeDemarche);

		// calcul des chemins
		Path repertoireTemplatesRecap = Paths.get(REPERTOIRE_ABSOLU_DEMARCHES, codeDemarche,
				CHEMIN_RELATIF_DES_TEMPLATES_RECAP_DANS_DEMARCHE_EXISTANTE);

		// vérification de l'existence des répertoires
		if (!repertoireTemplatesRecap.toFile().exists()) {
			throw new FileNotFoundException("Le répertoire '" + repertoireTemplatesRecap + "' n'existe pas.");
		}

		// Recherche, dans le répertoire, du/des récapitulatifs PDF
		List<Path> recaps = Files.list(repertoireTemplatesRecap)//
				.filter(p -> {
					String nomFichier = p.toFile().getName().toUpperCase();
					return nomFichier.startsWith(DEBUT_NOM_FICHIER_RECAP) && nomFichier.endsWith(FIN_NOM_FICHIER_RECAP);
				}) //
				.toList();

		// Renvoi de la liste
		LOGGER.info("  {} template(s) RECAP trouvé(s) dans {}", recaps.size(), repertoireTemplatesRecap);
		return recaps;
	}

	/**
	 * Méthode principale ne traitant aucun argument
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Paramètre à définir pour chaque exécution
		String codeDemarche = "jechangedecoordonnees";

		// Recherche des templates existants dans le code de la démarche actuelle
		List<Path> templatesExistants = listerTemplatesExistantsDeLaDemarche(codeDemarche);

		// Initialisation de la map des clefs clefUtilisee->List<nomDuTemplateUtilisantLaClef>
		Map<String, Set<String>> clefs = new TreeMap<>();

		// Pour chaque fichier, extraction des clefs
		extraireLesClefsDesTemplates(templatesExistants, clefs);

		// Création du fichier de mapping (format .properties)
		creerFichierMapping(codeDemarche, clefs);

		// Message concernant les recap
		List<Path> templatesRecapExistants = listerTemplatesRecapExistantsDeLaDemarche(codeDemarche);
		LOGGER.info("  /!\\ Seront à traiter à la main le(s) {} template(s) RECAP trouvé(s) dans {}", templatesRecapExistants.size());
	}

}
