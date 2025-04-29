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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outil de modification d'un template de document PDF.
 * 
 * Utilisation : cet outil ne peut être utilisé que depuis l'IDE (chemin relatif).
 * 
 */
public class OutilModificationTemplateDeDocumentPdf {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutilModificationTemplateDeDocumentPdf.class);

	/**
	 * Méthode principale ne traitant aucun argument.
	 * 
	 * Le chemin du fichier à modifier et du fichier généré sont les deux premières variables de la méthode.
	 * 
	 * Pour lister les clefs uniquement, vider (mettre en commentaire) le contenu de la Map. Pour générer le nouveau fichier avec l'ancien template
	 * modifié, initialiser des données dans la Map.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Chemin du fichier à modifier
		Path cheminFichierAmodifier = Paths.get("src/test/resources", "templates/template.pdf");
		Path cheminFichierModifie = Paths.get("src/test/resources", "templates/template.pdf");

		// Liste des clefs à modifier (vide pour lister les clefs du document)
		Map<String, String> modificationsArealiser = Map.of(//
//				"topmostSubform[0].Page2[0].N2P-3[0]", "clef02", //
//				"topmostSubform[0].Page2[0].clef01-oui", "clef01-MME"//
		);

		// Log des clefs
		if (modificationsArealiser.isEmpty()) {
			Map<String, Set<String>> clefs = new HashMap<>();
			OutilRecuperationTemplateDeDocumentEtape1.extraireLesClefsDunTemplatePdf(cheminFichierAmodifier, clefs);
			LOGGER.info("Les clefs disponibles dans le document sont :");
			for (String clef : clefs.keySet()) {
				LOGGER.info("  {}", clef);
			}
		}

		// Modification du PDF
		else {
			OutilRecuperationTemplateDeDocumentEtape2.copierEtModifierLeTemplatePDF(cheminFichierAmodifier, modificationsArealiser,
					cheminFichierModifie);
		}
	}
}
