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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.service.DocumentService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class DocumentControleur implements DocumentAPI {

	@Autowired
	private DocumentService service;

	@Override
	public String demanderClefDeTelechargement(String numeroTeledossier, String codeDocument) throws UnsupportedEncodingException {
		// Lecture des entête
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();
		// Encodage de la clef pour que, au moment de son utilisation dans une URL, elle soit bien utilisée
		return "\"" + URLEncoder.encode(this.service.genererEtEnregistrerClefUniqueDeTelechargement(tokenJwt, numeroTeledossier, codeDocument),
				StandardCharsets.UTF_8) + "\"";
	}

	@Override
	public List<MessageMetadonneesDocumentDto> rechercherDocumentsVisiblesDuTeledossier(final String numeroTeledossier) {
		return this.service.rechercherDocumentsVisibleDuTeledossier(numeroTeledossier);
	}

	@Override
	public String sauvegarderUnDocumentGenereDeTeledossier(MessageSauvegardeDocumentDto dto) {
		// Encadrement de la chaine de caractères retournée par des " pour en faire du JSON valide
		return "\"" + this.service.sauvegarderDocumentGenereDeTeledossier(dto) + "\"";
	}

	@Override
	public void supprimerDocumentsDunTeledossier(final String numeroTeledossier) {
		this.service.supprimerDocumentsDuTeledossier(numeroTeledossier);
	}

	@Override
	public ResponseEntity<byte[]> telechargerDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument) {
		// Chargement du contenu depuis la base
		MessageSauvegardeDocumentDto doc = this.service.rechercherDocumentVisibleDuTeledossier(numeroTeledossier, codeDocument);

		// Renvoi en sortie
		return ResponseEntity.ok()//
				.contentType(MediaType.parseMediaType(doc.getContentType()))//
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomDocument() + "\"")//
				.body(Base64.getDecoder().decode(doc.getContenuDocument()));
	}
}