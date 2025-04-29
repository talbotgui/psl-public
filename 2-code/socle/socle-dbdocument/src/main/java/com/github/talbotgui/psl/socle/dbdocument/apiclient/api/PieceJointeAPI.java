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
package com.github.talbotgui.psl.socle.dbdocument.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.DbdocumentClient;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbdocument", contextId = "PieceJointeApi")
public interface PieceJointeAPI {

	/**
	 * Association d'une pièce jointe à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 * @param idTeledossier ID du télé-dossier
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PutMapping(value = DbdocumentClient.URI_SUPPRESSION_OU_MAJ_PIECEJOINTE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	void associerPieceJointeAunTeledossier(//
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idPieceJointe") String idPieceJointe,
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @RequestBody String idTeledossier);

	/**
	 * Supression des pièces jointes d'un télé-dossier soumis.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbdocumentClient.URI_SUPPRESSION_PIECEJOINTE_DU_TELEDOSSIER, produces = MediaType.APPLICATION_JSON_VALUE)
	void desassocierPiecesJointesDuTeledossier(
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") String numeroTeledossier);

	/**
	 * API exposant le service de sauvegarde d'une pièce jointe.
	 *
	 * @param codeDemarche    Le code de la démarche (obligatoire).
	 * @param codePieceJointe le code de la pièce jointe associée à la démarche (obligatoire).
	 * @param fichier         la pièce jointe uploadée.
	 * @return l'ID de la pièce jointe sauvegardée.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbdocumentClient.URI_SAUVEGARDE_PIECEJOINTE, produces = MediaType.APPLICATION_JSON_VALUE)
	String sauvegarderPieceJointeEnUneUniqueRequete(//
			@Pattern(regexp = RegexUtils.CODE_DEMARCHE) @RequestPart(value = "codeDemarche", required = true) String codeDemarche,
			@Pattern(regexp = RegexUtils.CODE_DOCUMENT) @RequestPart(value = "codePieceJointe", required = true) String codePieceJointe,
			@RequestPart(value = "fichier", required = true) MultipartFile fichier);

	/**
	 * Supression d'une pièce jointe tant qu'elle n'est pas associée à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbdocumentClient.URI_SUPPRESSION_OU_MAJ_PIECEJOINTE, produces = MediaType.APPLICATION_JSON_VALUE)
	void supprimerPieceJointeNonSoumise(@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idPieceJointe") String idPieceJointe);

}