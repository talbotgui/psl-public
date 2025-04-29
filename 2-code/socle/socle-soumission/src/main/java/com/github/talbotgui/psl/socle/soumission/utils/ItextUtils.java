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
package com.github.talbotgui.psl.socle.soumission.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

import com.github.talbotgui.psl.socle.soumission.exception.SoumissionException;

/** Classe de manipulation de la librairie IText. */
public class ItextUtils {

	/**
	 * Méthode récursive pour traiter tout l'arbre des champs.
	 *
	 * @param champ      Un champ à traiter (qui peut en contenir d'autre).
	 * @param executable Le code à exécuter pour les champs 'feuille' de l'arbre.
	 */
	private static void traiterLeChampOuSesEnfants(PDField champ, TraitementDunChampDePdf executable) {
		if (champ instanceof PDNonTerminalField pdntf) {
			for (PDField sousChamp : pdntf.getChildren()) {
				traiterLeChampOuSesEnfants(sousChamp, executable);
			}
		} else {
			executable.traiterLeChampDuPdf(champ);
		}
	}

	public static byte[] traiterUnPdf(byte[] contenuTemplate, TraitementDunChampDePdf executable) throws IOException {
		// Initialisation du résultat
		byte[] resultat = null;

		// Ouverture du fichier
		try (PDDocument doc = Loader.loadPDF(contenuTemplate)) {

			// Lecture du formulaire
			PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();

			// Execution du code à réaliser
			for (PDField champ : acroForm.getFields()) {
				traiterLeChampOuSesEnfants(champ, executable);
			}

			// Initialisation du résultat
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			doc.save(bos);
			resultat = bos.toByteArray();

		} catch (IOException e) {
			throw new SoumissionException(SoumissionException.ERREUR_SERVEUR_AVEC_CODE, e, SoumissionException.CODE_ERREUR_EDGDD);
		}

		// Renvoi du contenu
		return resultat;
	}

	/** Constructeur bloquant l'instanciation de cette classe utilitaire. */
	private ItextUtils() {
		// rien à faire
	}
}
