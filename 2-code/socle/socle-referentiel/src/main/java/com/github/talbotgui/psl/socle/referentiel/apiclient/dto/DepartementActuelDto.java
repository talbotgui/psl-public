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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

// Jackson est utilisé pour stocker les données dans le cache et pour renvoyer un objet en sortie de l'API.
// Pour éviter que Jackson ne boucle entre commune->departement->region->commune, @JsonIdentityInfo est utilisée 
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "codeInsee")
public class DepartementActuelDto implements ITransformableEnDocumentLuceneDto {

	private String codeInsee;
	private String codeInseeCommuneChefLieu;
	private String codeInseeRegion;
	private CommuneActuelleDto communeChefLieu;
	private String libelle;
	private RegionActuelleDto region;

	public DepartementActuelDto() {
		super();
	}

	public DepartementActuelDto(String codeInsee, String libelle, String codeInseeRegion, String codeInseeCommuneChefLieu) {
		super();
		this.codeInsee = codeInsee;
		this.codeInseeRegion = codeInseeRegion;
		this.codeInseeCommuneChefLieu = codeInseeCommuneChefLieu;
		this.libelle = libelle;
	}

	public String getCodeInsee() {
		return this.codeInsee;
	}

	public String getCodeInseeCommuneChefLieu() {
		return this.codeInseeCommuneChefLieu;
	}

	public String getCodeInseeRegion() {
		return this.codeInseeRegion;
	}

	public CommuneActuelleDto getCommuneChefLieu() {
		return this.communeChefLieu;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public RegionActuelleDto getRegion() {
		return this.region;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public void setCodeInseeCommuneChefLieu(String codeInseeCommuneChefLieu) {
		this.codeInseeCommuneChefLieu = codeInseeCommuneChefLieu;
	}

	public void setCodeInseeRegion(String codeInseeRegion) {
		this.codeInseeRegion = codeInseeRegion;
	}

	public void setCommuneChefLieu(CommuneActuelleDto communeChefLieu) {
		this.communeChefLieu = communeChefLieu;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setRegion(RegionActuelleDto region) {
		this.region = region;
	}

	public String toJson() {
		return "{\"libelle\":\"" + this.libelle + "\", \"codeInsee\":\"" + this.codeInsee + "\", \"region\":" + this.region.toJson()
				+ ", \"communeChefLieu\":" + this.communeChefLieu.toJsonSimple() + "}";
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
