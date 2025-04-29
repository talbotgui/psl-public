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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Structure de données retournée par l'API userinfo du fournisseur d'identité OIDC.
 */
//Les seuls membres/attributs présents dans cette classe sont ceux utiles
@JsonIgnoreProperties(ignoreUnknown = true)
public class InformationSpCompteDto {

	private TypeDeCompteSP accountType;
	private String email;
	private Boolean franceConnect;
	private String sub;

	public InformationSpCompteDto() {
		super();
	}

	public InformationSpCompteDto(String email, String sub, TypeDeCompteSP accountType, Boolean franceConnect) {
		super();
		this.email = email;
		this.sub = sub;
		this.accountType = accountType;
		this.franceConnect = franceConnect;
	}

	public TypeDeCompteSP getAccountType() {
		return this.accountType;
	}

	public String getEmail() {
		return this.email;
	}

	public Boolean getFranceConnect() {
		return this.franceConnect;
	}

	public String getSub() {
		return this.sub;
	}

	public void setAccountType(TypeDeCompteSP accountType) {
		this.accountType = accountType;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFranceConnect(Boolean franceConnect) {
		this.franceConnect = franceConnect;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	@Override
	public String toString() {
		return "InformationSpCompteDto [accountType=" + this.accountType + ", franceConnect=" + this.franceConnect + ", sub=" + this.sub + ", email="
				+ this.email + "]";
	}
}
