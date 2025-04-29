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

import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

public class PaysActuelDto implements ITransformableEnDocumentLuceneDto {

	/** Nom de la capitale (cf. AutreClient). */
	private String capitale;

	/** Code internationnal sur 2 caractères. */
	private String code2caracteres;

	/** Code INSEE. */
	private String codeInsee;

	/** Libellé */
	private String libelle;

	/** Nationalité (cf. AutreClient). */
	private String nationalite;

	public PaysActuelDto() {
		super();
	}

	public PaysActuelDto(String codeInsee, String libelle, String code2caracteres) {
		super();
		this.codeInsee = codeInsee;
		this.libelle = libelle;
		this.code2caracteres = code2caracteres;
	}

	public String getCapitale() {
		return this.capitale;
	}

	public String getCode2caracteres() {
		return this.code2caracteres;
	}

	public String getCodeInsee() {
		return this.codeInsee;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public String getNationalite() {
		return this.nationalite;
	}

	public void setCapitale(String capitale) {
		this.capitale = capitale;
	}

	public void setCode2caracteres(String code2caracteres) {
		this.code2caracteres = code2caracteres;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setNationalite(String nationnalite) {
		this.nationalite = nationnalite;
	}

	private String toJson() {
		return "{\"libelle\":\"" + this.libelle + "\", \"codeInsee\":\"" + this.codeInsee + "\", \"capitale\":\"" + this.capitale
				+ "\", \"nationalite\":\"" + this.nationalite + "\"}";
	}

	@Override
	public Document transformerObjetEnDocument() {
		Document document = new Document();
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE, this.libelle, Field.Store.YES));
		if (this.nationalite != null) {
			document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE2, this.nationalite, Field.Store.YES));
		}
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_TYPE_JAVA, this.getClass().getSimpleName(), Field.Store.YES));
		document.add(new StoredField(IndexeurService.NOM_CHAMP_LUCENE_CONTENU_JSON, this.toJson()));
		return document;
	}

}
