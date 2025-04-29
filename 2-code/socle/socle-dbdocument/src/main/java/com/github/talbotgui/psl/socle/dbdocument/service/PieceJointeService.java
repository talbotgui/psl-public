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

import org.springframework.web.multipart.MultipartFile;

public interface PieceJointeService {

	/**
	 * Association d'une pièce jointe à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 * @param idTeledossier ID du télé-dossier
	 */
	void associerPieceJointeAunTeledossier(String idPieceJointe, String idTeledossier);

	/**
	 * Supression des pièces jointes d'un télé-dossier soumis.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 */
	void desassocierPiecesJointesDuTeledossier(String numeroTeledossier);

	/**
	 * Sauvegarde d'une pièce jointe.
	 *
	 * @param codeDemarche    Code de la démarche.
	 * @param codePieceJointe Code de la pièce jointe.
	 * @param fichier         Le fichier
	 * @return Identifiant du document
	 */
	String sauvegarderPieceJointe(String codeDemarche, String codePieceJointe, MultipartFile fichier);

	/**
	 * Supression d'une pièce jointe tant qu'elle n'est pas associée à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 */
	void supprimerPieceJointeNonSoumise(String idPieceJointe);

}