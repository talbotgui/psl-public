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
package com.github.talbotgui.psl.socle.securite.client.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class InformationSpMembreFamilleDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date dateDeNaissance;
	private String nom;
	private String prenoms;
	/** 1 pour masculin et 2 pour féminin. */
	private Integer sexe;

	public InformationSpMembreFamilleDto() {
		super();
	}

	public InformationSpMembreFamilleDto(String nom, String prenoms, Integer sexe, Date dateDeNaissance) {
		super();
		this.nom = nom;
		this.prenoms = prenoms;
		this.sexe = sexe;
		this.dateDeNaissance = dateDeNaissance;
	}

	public Date getDateDeNaissance() {
		return this.dateDeNaissance;
	}

	public String getNom() {
		return this.nom;
	}

	public String getPrenoms() {
		return this.prenoms;
	}

	public Integer getSexe() {
		return this.sexe;
	}

	public void setDateDeNaissance(Date dateDeNaissance) {
		this.dateDeNaissance = dateDeNaissance;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenoms(String prenoms) {
		this.prenoms = prenoms;
	}

	public void setSexe(Integer sexe) {
		this.sexe = sexe;
	}

}
