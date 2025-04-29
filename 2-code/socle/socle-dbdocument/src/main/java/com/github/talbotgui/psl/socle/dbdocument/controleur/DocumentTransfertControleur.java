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

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.service.DocumentTransfertService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class DocumentTransfertControleur implements DocumentTransfertAPI {

	@Autowired
	private DocumentTransfertService service;

	@Override
	public void notifierEchecDuTraitementDunDocumentDeTransfert(String idDocumentTransfert) {
		this.service.notifierEchecDuTraitementDunDocumentDeTransfert(idDocumentTransfert);
	}

	@Override
	public void purgerUnTeledossierApresTransfert(String numeroTeledossier) {
		this.service.purgerUnTeledossierApresTransfert(numeroTeledossier);
	}

	@Override
	public String rechercherEtVerrouillerUnTransfertAtraiter() {
		// Encadrement de la chaine de caractères retournée par des " pour en faire du JSON valide
		return this.service.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
	}

	@Override
	public ResponseEntity<byte[]> telechargerDocumentDuTeledossier(String idDocument) {
		// Chargement du contenu depuis la base
		MessageSauvegardeDocumentDto doc = this.service.rechercherDocumentOuPieceJointeDuTeledossier(idDocument);

		// Renvoi en sortie
		return ResponseEntity.ok()//
				.contentType(MediaType.parseMediaType(doc.getContentType()))//
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomDocument() + "\"")//
				.body(Base64.getDecoder().decode(doc.getContenuDocument()));
	}
}