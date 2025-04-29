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

import java.util.List;

import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

public interface DocumentService {

	/**
	 * Création d'une clef unique valable 10 secondes et sauvegardée dans les méta-données du document.
	 *
	 * @param tokenJwt          TOken JWT
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code unique du document dans le télé-dossier.
	 * @return La clef générée et sauvegardée.
	 */
	String genererEtEnregistrerClefUniqueDeTelechargement(String tokenJwt, String numeroTeledossier, String codeDocument);

	/**
	 * Méthode listant les documents visibles de l'usager pour un télé-dossier précis (sans ID, contenu et MD5).
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @return Metadonnées du document.
	 */
	List<MessageMetadonneesDocumentDto> rechercherDocumentsVisibleDuTeledossier(String numeroTeledossier);

	/**
	 * Téléchargement d'un document.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code du document.
	 * @return Le document et ses méta-data.
	 */
	MessageSauvegardeDocumentDto rechercherDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument);

	/**
	 * Sauvegarde d'un document généré de télé-dossier.
	 *
	 * @param message Contenu et metadonnées d'un document généré de télé-dossier
	 * @return Identifiant du document
	 */
	String sauvegarderDocumentGenereDeTeledossier(MessageSauvegardeDocumentDto message);

	/**
	 * Suppression des documents générés d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	void supprimerDocumentsDuTeledossier(String numeroTeledossier);

}