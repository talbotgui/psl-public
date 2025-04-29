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

import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe;

public interface PieceJointeDao {

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
	 * Recherche d'une pièce jointe avec le bon code de démarche et le bon code de document.
	 *
	 * @param codeDemarche    Code de démarche.
	 * @param codePieceJointe Code du document.
	 * @return PieceJointe trouvé.
	 */
	PieceJointe rechercherPieceJointe(String codeDemarche, String codePieceJointe);

	/**
	 * Recherche plusieurs (ou aucune) pièces jointes avec le bon code de démarche et le bon code de document.
	 *
	 * @param codeDemarche    Code de démarche.
	 * @param codePieceJointe Code du document.
	 * @return PieceJointe trouvées.
	 */
	List<PieceJointe> rechercherPiecesJointes(String codeDemarche, String codePieceJointe);

	/**
	 * Sauvegarde d'une pièce jointe.
	 *
	 * @param dto Données de la pièce jointe.
	 * @return Identifiant du document sauvegardé
	 */
	String sauvegarderPieceJointe(PieceJointe dto);

	/**
	 * Supression d'une pièce jointe tant qu'elle n'est pas associée à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 */
	void supprimerPieceJointeNonSoumise(String idPieceJointe);

	/**
	 * Suppression de toutes les pièces jointes d'un télé-dossier.
	 * 
	 * @param numeroTeledossier Numéro de télé-dossier
	 */
	void supprimerPiecesJointesDuTeledossier(String numeroTeledossier);
}