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

import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

public interface DocumentTransfertService {

	/**
	 * Changement de l'état du transfert en ERREUR.
	 * 
	 * @param idDocumentTransfert Identifiant du tranfert à traiter
	 */
	void notifierEchecDuTraitementDunDocumentDeTransfert(String idDocumentTransfert);

	/**
	 * Suppression des documents générés d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	void purgerUnTeledossierApresTransfert(String numeroTeledossier);

	/**
	 * Recherche d'un document généré ou d'une pièce jointe par son ID.
	 * 
	 * @param idDocument ID du document.
	 */
	MessageSauvegardeDocumentDto rechercherDocumentOuPieceJointeDuTeledossier(String idDocument);

	/**
	 * Recherche d'un document décrivant un transfert à réaliser et pose d'un verrou
	 * <ul>
	 * <li>au statut "A_FAIRE" sans contrainte de date</li>
	 * <li>au statut "EN_ERREUR" avec une date de traitement de plus de 24h</li>
	 * </ul>
	 * 
	 * @return L'identifiant du premier document trouvé et verrouillé
	 */
	String rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();

}