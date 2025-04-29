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

// pour le "libelleLong" indexé et donc renvoyé par le client d'API
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommuneDelegueeActuelleDto implements ITransformableEnDocumentLuceneDto {

	/** Code INSEE. */
	private String codeInsee;

	/** Libellé de la commune. */
	private String libelle;

	public CommuneDelegueeActuelleDto() {
		super();
	}

	public CommuneDelegueeActuelleDto(String codeInsee, String libelle) {
		super();
		this.codeInsee = codeInsee;
		this.libelle = libelle;
	}

	public String getCodeInsee() {
		return this.codeInsee;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String toJson() {
		return "{\"libelle\":\"" + this.libelle + "\", \"codeInsee\":\"" + this.codeInsee + "\"}";
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