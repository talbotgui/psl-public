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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.DbdocumentClient;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbdocument", contextId = "TransfertApi")
public interface DocumentTransfertAPI {

	/** Code du document des données soumises */
	static final String CODE_DOCUMENT_DONNEES_SOUMISES = "DocumentDonneesSoumises";

	/** Code du document des données de routage */
	static final String CODE_DOCUMENT_TRANSFERT = "TRANSFERT";

	/**
	 * Modification du statut du transfert en ERROR.
	 *
	 * @param idDocumentTransfert ID du document de transfert
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PutMapping(value = DbdocumentClient.URI_INTERNE_TRANSFERT_PRECIS)
	void notifierEchecDuTraitementDunDocumentDeTransfert(
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idDocumentTransfert") String idDocumentTransfert);

	/**
	 * Suppression des documents d'un télé-dossier (tous sauf le document de transfert).
	 *
	 * @param numeroTeledossier Numéro de télé-dossier.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbdocumentClient.URI_PURGE_TELEDOSSIER)
	void purgerUnTeledossierApresTransfert(
			@Pattern(regexp = RegexUtils.NUMERO_TELEDOSSIER) @PathVariable(name = "numeroTeledossier") String numeroTeledossier);

	/**
	 * Recherche d'un document décrivant un transfert à réaliser et pose d'un verrou
	 * <ul>
	 * <li>au statut "A_FAIRE" sans contrainte de date</li>
	 * <li>au statut "EN_ERREUR" avec une date de traitement de plus de 24h</li>
	 * </ul>
	 *
	 * @return L'identifiant du premier document trouvé et verrouillé
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbdocumentClient.URI_INTERNE_TRANSFERT_RECHERCHE)
	String rechercherEtVerrouillerUnTransfertAtraiter();

	/**
	 * Téléchargement du document.
	 *
	 * @param idDocument ID du document
	 * @return
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbdocumentClient.URI_INTERNE_DOCUMENT)
	ResponseEntity<byte[]> telechargerDocumentDuTeledossier(
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idDocument") final String idDocument);
}