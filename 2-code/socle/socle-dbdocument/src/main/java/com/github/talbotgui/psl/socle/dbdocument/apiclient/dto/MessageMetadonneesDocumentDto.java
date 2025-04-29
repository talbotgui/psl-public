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

import java.util.Date;

/**
 * Classe contenant les métadonnées d'un document.
 */
public class MessageMetadonneesDocumentDto {

	/** Code de la démarche. */
	private String codeDemarche;
	/** Code unique du document pour ce télé-dossier */
	private String codeDocument;
	/** Type de contenu (MimeType) */
	private String contentType;
	/** Date création du document */
	private Date dateCreation;
	/** Flag indiquant si le fichier doit être proposé au téléchargement à l'usager. */
	private Boolean documentPresenteAuTelechargementEnFinDeDemarche;
	/** Id du document une fois inséré */
	private String id;
	/** Libellé explicatif du document à destination de l'usager */
	private String libelleDocument;
	/** Nom du document */
	private String nomDocument;
	/** Numéro du télé-dossier */
	private String numeroTeledossier;
	/** Ordre de présentation du document */
	private Integer ordrePresentation;
	/** Taille en octet du document. */
	private Integer taille;

	public MessageMetadonneesDocumentDto() {
		super();
	}

	public MessageMetadonneesDocumentDto(String codeDemarche, String numeroTeledossier, String codeDocument, String libelleDocument,
			String nomDocument, Boolean documentPresenteAuTelechargementEnFinDeDemarche, Integer ordrePresentation, String contentType) {
		super();
		this.codeDemarche = codeDemarche;
		this.numeroTeledossier = numeroTeledossier;
		this.codeDocument = codeDocument;
		this.nomDocument = nomDocument;
		this.libelleDocument = libelleDocument;
		this.documentPresenteAuTelechargementEnFinDeDemarche = documentPresenteAuTelechargementEnFinDeDemarche;
		this.ordrePresentation = ordrePresentation;
		this.contentType = contentType;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public String getCodeDocument() {
		return this.codeDocument;
	}

	public String getContentType() {
		return this.contentType;
	}

	public Date getDateCreation() {
		return this.dateCreation;
	}

	public Boolean getDocumentPresenteAuTelechargementEnFinDeDemarche() {
		return this.documentPresenteAuTelechargementEnFinDeDemarche;
	}

	public String getId() {
		return this.id;
	}

	public String getLibelleDocument() {
		return this.libelleDocument;
	}

	public String getNomDocument() {
		return this.nomDocument;
	}

	public String getNumeroTeledossier() {
		return this.numeroTeledossier;
	}

	public Integer getOrdrePresentation() {
		return this.ordrePresentation;
	}

	public Integer getTaille() {
		return this.taille;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setCodeDocument(String codeDocument) {
		this.codeDocument = codeDocument;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public void setDocumentPresenteAuTelechargementEnFinDeDemarche(Boolean documentPresenteAuTelechargementEnFinDeDemarche) {
		this.documentPresenteAuTelechargementEnFinDeDemarche = documentPresenteAuTelechargementEnFinDeDemarche;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLibelleDocument(String libelleDocument) {
		this.libelleDocument = libelleDocument;
	}

	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}

	public void setNumeroTeledossier(String numeroTeledossier) {
		this.numeroTeledossier = numeroTeledossier;
	}

	public void setOrdrePresentation(Integer ordrePresentation) {
		this.ordrePresentation = ordrePresentation;
	}

	public void setTaille(Integer taille) {
		this.taille = taille;
	}

}
