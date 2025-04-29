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
package com.github.talbotgui.psl.socle.referentiel.apiclient.dto;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

/**
 * UGLE = Unité de Gestion de Liste Electorale
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommuneUgleDto implements ITransformableEnDocumentLuceneDto {

	private String code;
	private String libelle;

	public CommuneUgleDto() {
		super();
	}

	public CommuneUgleDto(String code, String libelle) {
		super();
		this.code = code;
		this.libelle = libelle;
	}

	public String getCode() {
		return this.code;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	private String toJson() {
		return "{\"libelle\":\"" + this.libelle + "\", \"code\":\"" + this.code + "\"}";
	}

	@Override
	public Document transformerObjetEnDocument() {
		Document document = new Document();
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE, this.libelle, Field.Store.YES));
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_TYPE_JAVA, this.getClass().getSimpleName(), Field.Store.YES));
		document.add(new StoredField(IndexeurService.NOM_CHAMP_LUCENE_CONTENU_JSON, this.toJson()));
		return document;
	}

}