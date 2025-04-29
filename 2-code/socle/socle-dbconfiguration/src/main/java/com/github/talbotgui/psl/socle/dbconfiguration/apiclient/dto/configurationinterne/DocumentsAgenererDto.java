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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne;

// Cette classe ne peut pas être un record car des setter sont appelés dans le code de socle-dbsoumission.
public class DocumentsAgenererDto {
	/** Code unique du document au sein de la configuration */
	private String code;
	/** Condition à remplir pour générer le document (vide est concidéré comme TRUE) */
	private String conditionGeneration;
	/** Type de contenu (MimeType) */
	private String contentType;
	/** Flag indiquant si le document doit être présenté en fin de démarche */
	private Boolean documentPresenteAuTelechargementEnFinDeDemarche;
	/** Document permettant de valider le document généré (en Base64) */
	private String documentValidation;
	/** Libelle du document pour l'usager */
	private String libelle;
	/** Nom final du fichier généré (peut contenir des variables avec ${} */
	private String nomFinalDuDocument;
	/** Contenu du template de document en Base64 */
	private String template;
	/** Type de génération de document */
	private TypeGenerationDocumentEnum typeDeGeneration;

	public DocumentsAgenererDto() {
		super();
	}

	public DocumentsAgenererDto(String code, String libelle, TypeGenerationDocumentEnum typeDeGeneration, String conditionGeneration,
			String nomFinalDuDocument, Boolean documentPresenteAuTelechargementEnFinDeDemarche, String contentType, String template) {
		super();
		this.code = code;
		this.libelle = libelle;
		this.typeDeGeneration = typeDeGeneration;
		this.conditionGeneration = conditionGeneration;
		this.nomFinalDuDocument = nomFinalDuDocument;
		this.documentPresenteAuTelechargementEnFinDeDemarche = documentPresenteAuTelechargementEnFinDeDemarche;
		this.contentType = contentType;
		this.template = template;
	}

	public String getCode() {
		return this.code;
	}

	public String getConditionGeneration() {
		return this.conditionGeneration;
	}

	public String getContentType() {
		return this.contentType;
	}

	public Boolean getDocumentPresenteAuTelechargementEnFinDeDemarche() {
		return this.documentPresenteAuTelechargementEnFinDeDemarche;
	}

	public String getDocumentValidation() {
		return this.documentValidation;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public String getNomFinalDuDocument() {
		return this.nomFinalDuDocument;
	}

	public String getTemplate() {
		return this.template;
	}

	public TypeGenerationDocumentEnum getTypeDeGeneration() {
		return this.typeDeGeneration;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setConditionGeneration(String conditionGeneration) {
		this.conditionGeneration = conditionGeneration;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDocumentPresenteAuTelechargementEnFinDeDemarche(Boolean documentPresenteAuTelechargementEnFinDeDemarche) {
		this.documentPresenteAuTelechargementEnFinDeDemarche = documentPresenteAuTelechargementEnFinDeDemarche;
	}

	public void setDocumentValidation(String documentValidation) {
		this.documentValidation = documentValidation;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setNomFinalDuDocument(String nomFinalDuDocument) {
		this.nomFinalDuDocument = nomFinalDuDocument;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setTypeDeGeneration(TypeGenerationDocumentEnum typeDeGeneration) {
		this.typeDeGeneration = typeDeGeneration;
	}

}
