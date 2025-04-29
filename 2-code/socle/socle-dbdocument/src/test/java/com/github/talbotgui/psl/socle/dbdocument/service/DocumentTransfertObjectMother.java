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
package com.github.talbotgui.psl.socle.dbdocument.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe;

/**
 * Classe responsable de créer des jeux de données pour les tests.
 */
public class DocumentTransfertObjectMother {

	/**
	 * Création d'un document.
	 *
	 * @param codeDocument Code du document.
	 * @return
	 */
	public static MessageSauvegardeDocumentDto creerDocument(String codeDocument) {
		long numeroUnique = new Date().getTime();
		return new MessageSauvegardeDocumentDto("codeDemarche", "teledossier-" + numeroUnique, codeDocument, "lib", "nom", null, null,
				"application/pdf", null, "contenuDocument", "md5");
	}

	/**
	 * Création d'un document
	 *
	 * @param codeDocument      Code du document.
	 * @param numeroTeledossier Numéro de télé-dossier à utiliser
	 * @return
	 */
	public static MessageSauvegardeDocumentDto creerDocument(String codeDocument, String numeroTeledossier) {
		MessageSauvegardeDocumentDto document = creerDocument(codeDocument);
		document.setNumeroTeledossier(numeroTeledossier);
		return document;
	}

	/**
	 * Création d'une pièce jointe
	 *
	 * @return
	 */
	public static PieceJointe creerPieceJointe() {
		return new PieceJointe("codeDemarche", "codePieceJointe", "originalFilename", 123L, "application/pdf",
				"contenuDocument".getBytes(StandardCharsets.UTF_8), "md5");
	}

	private DocumentTransfertObjectMother() {
		// Pas d'instanciation possible
	}
}
