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

public class ProtectionDeCommuneDto {
	/** Identifiant unique du protecteur */
	private String id;
	/** Nom du protecteur */
	private String nomProtecteur;
	/** Type de protection (PN ou GN) */
	private TypeProtectionDeCommune typeProtection;

	public ProtectionDeCommuneDto() {
		super();
	}

	public ProtectionDeCommuneDto(TypeProtectionDeCommune typeProtection, String nomProtecteur, String id) {
		super();
		this.typeProtection = typeProtection;
		this.nomProtecteur = nomProtecteur;
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getNomProtecteur() {
		return this.nomProtecteur;
	}

	public TypeProtectionDeCommune getTypeProtection() {
		return this.typeProtection;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNomProtecteur(String nomProtecteur) {
		this.nomProtecteur = nomProtecteur;
	}

	public void setTypeProtection(TypeProtectionDeCommune typeProtection) {
		this.typeProtection = typeProtection;
	}

	@Override
	public String toString() {
		return "ProtectionDeCommuneDto [typeProtection=" + this.typeProtection + ", id=" + this.id + ", nomProtecteur=" + this.nomProtecteur + "]";
	}

}
