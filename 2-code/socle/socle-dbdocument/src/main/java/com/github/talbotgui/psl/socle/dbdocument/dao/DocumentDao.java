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
package com.github.talbotgui.psl.socle.dbdocument.dao;

import java.util.List;

import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

public interface DocumentDao {

	/**
	 * Méthode listant les documents visibles de l'usager pour un télé-dossier précis (sans ID, contenu et MD5).
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @return Metadonnées du document.
	 */
	List<MessageMetadonneesDocumentDto> rechercherDocumentsVisibleDuTeledossierSansContenuOuDonneesDeTelechargement(String numeroTeledossier);

	/**
	 * Téléchargement d'un document.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code du document.
	 * @return Le document et ses méta-data.
	 */
	MessageSauvegardeDocumentDto rechercherDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument);

	/**
	 * Sauvegarde de la clef et du temps dans les méta-données du document.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code du document.
	 * @param clef              Clef à sauvegarder
	 * @param temps             Temps à sauvegarder
	 */
	void sauvegarderClefDansMessageMetadonneesDocumentDto(String numeroTeledossier, String codeDocument, String clef, Long temps);

	/**
	 * Sauvegarde d'un document généré de télé-dossier.
	 *
	 * @param message Contenu et metadonnées d'un document généré de télé-dossier
	 * @return Identifiant du document
	 */
	String sauvegarderDocumentGenereDeTeledossier(MessageSauvegardeDocumentDto message);

	/**
	 * Suppression des méta-données dans un document.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code du document.
	 */
	void supprimerClefDansMessageMetadonneesDocumentDto(String numeroTeledossier, String codeDocument);

	/**
	 * Suppression des documents générés d'un télé-dossier (en totalité).
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	void supprimerDocumentsDuTeledossierAvecLeDocumentDeTransfert(String numeroTeledossier);

	/**
	 * Suppression des documents générés d'un télé-dossier (sans supprimer le document de transfert).
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	void supprimerDocumentsDuTeledossierSansLeDocumentDeTransfert(String numeroTeledossier);
}