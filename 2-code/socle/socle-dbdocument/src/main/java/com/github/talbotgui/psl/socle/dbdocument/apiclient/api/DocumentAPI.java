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

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.DbdocumentClient;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbdocument", contextId = "DocumentApi")
public interface DocumentAPI {

	/**
	 * Génération d'une clef de téléchargement pour disposer d'une URL unique donnant accès au document souhaité.
	 *
	 * @param numeroTeledossier Numéro de télé-dossier
	 * @param codeDocument      Code unique du document
	 * @return la clef de téléchargement générée
	 * @throws UnsupportedEncodingException
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbdocumentClient.URI_DOCUMENT_DISPONIBLE_DU_TELEDOSSIER)
	String demanderClefDeTelechargement(//
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") final String numeroTeledossier,
			@Pattern(regexp = RegexUtils.CODE_DOCUMENT) @PathVariable(name = "codeDocument") final String codeDocument)
			throws UnsupportedEncodingException;

	/**
	 * Lecture de la liste des documents disponibles à l'usager pour un télé-dossier donné.
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 * @return Configuration trouvée
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbdocumentClient.URI_DOCUMENTS_DISPONIBLES_DU_TELEDOSSIER)
	List<MessageMetadonneesDocumentDto> rechercherDocumentsVisiblesDuTeledossier(
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") String numeroTeledossier);

	/**
	 * API exposant le service de soumission.
	 *
	 * @param dto Données soumises.
	 * @return Numéro de télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbdocumentClient.URI_SAUVEGARDE_DOCUMENT_GENERE_DE_TELEDOSSIER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String sauvegarderUnDocumentGenereDeTeledossier(@Valid @RequestBody MessageSauvegardeDocumentDto dto);

	/**
	 * Suppression des documents générés d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbdocumentClient.URI_DOCUMENTS_DISPONIBLES_DU_TELEDOSSIER)
	void supprimerDocumentsDunTeledossier(
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") String numeroTeledossier);

	/**
	 * Téléchargement du document
	 *
	 * @param numeroTeledossier Numéro de télé-dossier
	 * @param codeDocument      Code unique du document
	 * @return
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbdocumentClient.URI_DOCUMENT_DISPONIBLE_DU_TELEDOSSIER)
	ResponseEntity<byte[]> telechargerDocumentVisibleDuTeledossier(//
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") final String numeroTeledossier,
			@Pattern(regexp = RegexUtils.CODE_DOCUMENT) @PathVariable(name = "codeDocument") final String codeDocument);

}