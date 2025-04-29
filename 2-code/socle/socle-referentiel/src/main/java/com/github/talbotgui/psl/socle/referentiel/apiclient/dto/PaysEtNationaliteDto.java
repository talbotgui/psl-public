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

/**
 * Structure de données venant de https://www.business-plan-excel.fr/liste-pays-du-monde-excel-capitale-continent/
 */
public class PaysEtNationaliteDto {

	private String capitale;
	private String code2caracteres;
	private String code3caracteres;
	private String continent;
	private String nationalite;
	private String nom;

	public PaysEtNationaliteDto(String code2caracteres, String code3caracteres, String nom, String capitale, String continent, String nationalite) {
		super();
		this.code2caracteres = code2caracteres;
		this.code3caracteres = code3caracteres;
		this.nom = nom;
		this.capitale = capitale;
		this.continent = continent;
		this.nationalite = nationalite;
	}

	public String getCapitale() {
		return this.capitale;
	}

	public String getCode2caracteres() {
		return this.code2caracteres;
	}

	public String getCode3caracteres() {
		return this.code3caracteres;
	}

	public String getContinent() {
		return this.continent;
	}

	public String getNationalite() {
		return this.nationalite;
	}

	public String getNom() {
		return this.nom;
	}

	public void setCapitale(String capitale) {
		this.capitale = capitale;
	}

	public void setCode2caracteres(String code2caracteres) {
		this.code2caracteres = code2caracteres;
	}

	public void setCode3caracteres(String code3caracteres) {
		this.code3caracteres = code3caracteres;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setNationalite(String nationnalite) {
		this.nationalite = nationnalite;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
}
