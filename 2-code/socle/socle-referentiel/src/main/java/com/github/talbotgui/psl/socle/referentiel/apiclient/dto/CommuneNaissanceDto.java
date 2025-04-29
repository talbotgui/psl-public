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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

// pour le "libelleLong" indexé et donc renvoyé par le client d'API
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommuneNaissanceDto implements ITransformableEnDocumentLuceneDto {

	private String code;
	/** Premier des codes postaux (utile quand on ne veut pas traiter le cas des multiples codes postaux). */
	private String codePostal;
	private List<String> codesPostaux = new ArrayList<>();

	private String libelle;

	public CommuneNaissanceDto() {
		super();
	}

	public CommuneNaissanceDto(String code, List<String> codesPostaux, String libelle) {
		super();
		this.code = code;
		this.setCodesPostaux(codesPostaux);
		this.libelle = libelle;
	}

	public CommuneNaissanceDto(String code, String libelle) {
		super();
		this.code = code;
		this.libelle = libelle;
	}

	public String getCode() {
		return this.code;
	}

	public String getCodePostal() {
		return this.codePostal;
	}

	public List<String> getCodesPostaux() {
		return this.codesPostaux;
	}

	public String getLibelle() {
		return this.libelle;
	}

	/** Pour disposer d'un libellé long affichable */
	public String getLibelleLong() {
		String informationComplementaire = "";
		if (!this.codesPostaux.isEmpty()) {
			informationComplementaire = " (" + String.join(", ", this.codesPostaux) + ")";
		}
		return this.libelle + informationComplementaire;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public void setCodesPostaux(List<String> codesPostaux) {
		this.codesPostaux = codesPostaux;
		if (this.codesPostaux == null) {
			this.codesPostaux = new ArrayList<>();
		} else if (!codesPostaux.isEmpty()) {
			this.codePostal = this.codesPostaux.get(0);
		}
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	private String toJson() {
		StringBuilder json = new StringBuilder();

		json.append("{\"libelle\":\"");
		json.append(this.libelle);
		json.append("\"");

		json.append(",\"code\":\"");
		json.append(this.code);
		json.append("\"");

		json.append(",\"codePostal\":\"");
		json.append(this.codePostal);
		json.append("\"");

		json.append(",\"codesPostaux\":");
		json.append(("[\"" + String.join("\",\"", this.codesPostaux) + "\"]").replaceFirst("\"\"", ""));
		json.append("}");

		return json.toString();
	}

	@Override
	public Document transformerObjetEnDocument() {
		Document document = new Document();
		document.add(
				new TextField(IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE, String.join(" ", this.codesPostaux) + " " + this.libelle, Field.Store.YES));
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_TYPE_JAVA, this.getClass().getSimpleName(), Field.Store.YES));
		document.add(new StoredField(IndexeurService.NOM_CHAMP_LUCENE_CONTENU_JSON, this.toJson()));
		return document;
	}

}