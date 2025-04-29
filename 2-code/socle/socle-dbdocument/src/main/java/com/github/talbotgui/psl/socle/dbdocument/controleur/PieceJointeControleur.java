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
package com.github.talbotgui.psl.socle.dbdocument.controleur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.talbotgui.psl.socle.commun.utils.LogUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.PieceJointeAPI;
import com.github.talbotgui.psl.socle.dbdocument.service.PieceJointeService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class PieceJointeControleur implements PieceJointeAPI {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PieceJointeControleur.class);

	@Autowired
	private PieceJointeService pieceJointeService;

	@Override
	public void associerPieceJointeAunTeledossier(String idPieceJointe, String idTeledossier) {
		this.pieceJointeService.associerPieceJointeAunTeledossier(idPieceJointe, idTeledossier);
	}

	@Override
	public void desassocierPiecesJointesDuTeledossier(String numeroTeledossier) {
		this.pieceJointeService.desassocierPiecesJointesDuTeledossier(numeroTeledossier);
	}

	@Override
	public String sauvegarderPieceJointeEnUneUniqueRequete(String codeDemarche, String codePieceJointe, MultipartFile fichier) {
		LOGGER.info("Demande de sauvegarde de la pièce jointe {}/{}/{}", LogUtils.nettoyerDonneesAvantDeLogguer(codeDemarche),
				LogUtils.nettoyerDonneesAvantDeLogguer(codePieceJointe), LogUtils.nettoyerDonneesAvantDeLogguer(fichier.getOriginalFilename()));

		// Sauvegarde de la pièce jointe dans MongoDB
		String id = this.pieceJointeService.sauvegarderPieceJointe(codeDemarche, codePieceJointe, fichier);
		return "\"" + id + "\"";
	}

	@Override
	public void supprimerPieceJointeNonSoumise(String idPieceJointe) {
		this.pieceJointeService.supprimerPieceJointeNonSoumise(idPieceJointe);
	}

}