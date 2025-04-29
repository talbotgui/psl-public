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
package com.github.talbotgui.psl.socle.dbdocument.apiclient.dto;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * Classe contenant les métadonnées et le contenu d'un document.
 */
public class MessageSauvegardeDocumentDto extends MessageMetadonneesDocumentDto {

	/** Checksum du document (MD5) */
	@Pattern(regexp = RegexUtils.CHECKSUM)
	private String checksumMd5;
	/** Clef unique de téléchargement. */
	@Pattern(regexp = RegexUtils.CLEF_UNIQUE_TELECHARGEMENT)
	private String clefUniqueTelechargement;
	/** Contenu du document en BASE64 */
	@Pattern(regexp = RegexUtils.BASE64)
	private String contenuDocument;
	/** Identifiant dans les logs de la création et/ou modification (ajout par concaténation séparée par un espace) de cet objet. */
	@Pattern(regexp = RegexUtils.ID_TRACE)
	private String idTrace;
	/** Date/heure limite du téléchargement généré avec Date.getTime() (10 secondes après la demande). */
	@Min(0)
	private Long tempsLimiteDeValiditeDeLaClefDeTelechargement;

	public MessageSauvegardeDocumentDto() {
		super();
	}

	public MessageSauvegardeDocumentDto(String codeDemarche, String numeroTeledossier, String codeDocument, String libelleDocument,
			String nomDocument, Boolean documentPresenteAuTelechargementEnFinDeDemarche, Integer ordrePresentation, String contentType,
			Integer taille, String contenuDocument, String checksumMd5) {
		super(codeDemarche, numeroTeledossier, codeDocument, libelleDocument, nomDocument, documentPresenteAuTelechargementEnFinDeDemarche,
				ordrePresentation, contentType);
		this.contenuDocument = contenuDocument;
		this.checksumMd5 = checksumMd5;
		super.setTaille(taille);
	}

	public String getChecksumMd5() {
		return this.checksumMd5;
	}

	public String getClefUniqueTelechargement() {
		return this.clefUniqueTelechargement;
	}

	public String getContenuDocument() {
		return this.contenuDocument;
	}

	public String getIdTrace() {
		return this.idTrace;
	}

	public Long getTempsLimiteDeValiditeDeLaClefDeTelechargement() {
		return this.tempsLimiteDeValiditeDeLaClefDeTelechargement;
	}

	public void setChecksumMd5(String checksumMd5) {
		this.checksumMd5 = checksumMd5;
	}

	public void setClefUniqueTelechargement(String clefUniqueTelechargement) {
		this.clefUniqueTelechargement = clefUniqueTelechargement;
	}

	public void setContenuDocument(String contenuDocument) {
		this.contenuDocument = contenuDocument;
	}

	public void setIdTrace(String idTrace) {
		this.idTrace = idTrace;
	}

	public void setTempsLimiteDeValiditeDeLaClefDeTelechargement(Long tempsLimiteDeValiditeDeLaClefDeTelechargement) {
		this.tempsLimiteDeValiditeDeLaClefDeTelechargement = tempsLimiteDeValiditeDeLaClefDeTelechargement;
	}

}
