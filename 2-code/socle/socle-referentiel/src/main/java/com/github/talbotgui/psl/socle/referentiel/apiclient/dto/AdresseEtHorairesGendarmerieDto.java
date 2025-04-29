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

public class AdresseEtHorairesGendarmerieDto {
	/** Adresse */
	private String adresse;
	/** Horaires */
	private String horaires;
	/** Identifiant unique du protecteur */
	private String id;
	/** Nom */
	private String nom;
	/** Telephone */
	private String telephone;
	/** URL de la gendarmerie sur le portail SP */
	private String urlSP;

	public AdresseEtHorairesGendarmerieDto() {
		super();
	}

	public AdresseEtHorairesGendarmerieDto(String id, String nom, String adresse, String telephone, String horaires, String urlSP) {
		super();
		this.id = id;
		this.nom = nom;
		this.adresse = adresse;
		this.telephone = telephone;
		this.horaires = horaires;
		this.urlSP = urlSP;
	}

	public String getAdresse() {
		return this.adresse;
	}

	public String getHoraires() {
		return this.horaires;
	}

	public String getId() {
		return this.id;
	}

	public String getNom() {
		return this.nom;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public String getUrlSP() {
		return this.urlSP;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public void setHoraires(String horaires) {
		this.horaires = horaires;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setUrlSP(String urlSP) {
		this.urlSP = urlSP;
	}

	public String toJson() {
		// L'ID n'est pas nécessaire en sortie de l'API
		return "{\"nom\":\"" + this.nom + "\",\"telephone\":\"" + this.telephone + "\",\"urlSP\":\"" + this.urlSP + "\",\"adresse\":\"" + this.adresse
				+ "\",\"horaires\":\"" + this.horaires + "\"}";
	}
}
