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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Structure de données retournée par l'API "particuliers/informations" du fournisseur d'identité OIDC. (en date du 23/04/2022)
 */
//Les seuls membres/attributs présents dans cette classe sont ceux utiles
@JsonIgnoreProperties(ignoreUnknown = true)
public class InformationSpUsagerDto extends InformationSpMembreFamilleDto {
	/** accountType est une données copiées depuis les données techniques du compte. */
	private TypeDeCompteSP accountType;
	private InformationSpAdresseDto adresse;
	private String civilite;
	private String codeInseeDeNaissance;
	private String codeInseePaysDeNaissance;
	private String communeDeNaissance;
	private String departementDeNaissance;
	private String email;
	/** emailTechnique est une données copiées depuis les données techniques du compte (email venant de l'API /userinfo). */
	private String emailTechnique;
	/** franceConnect est une données copiées depuis les données techniques du compte (venant de l'API /userinfo). */
	private Boolean franceConnect;
	private String nomDeNaissance;
	private String paysDeNaissance;
	private String situationFamiliale;
	private InformationSpTelephoneDto telephoneFixe;
	private InformationSpTelephoneDto telephoneMobile;
	/** uuidSp est une données copiées depuis les données techniques du compte (sub venant de l'API /userinfo). */
	private String uuidSp;

	public InformationSpUsagerDto() {
		super();
	}

	public InformationSpUsagerDto(String nom, String prenoms, Integer sexe, Date dateDeNaissance, InformationSpAdresseDto adresse, String civilite,
			String codeInseeDeNaissance, String codeInseePaysDeNaissance, String communeDeNaissance, String departementDeNaissance, String email,
			String nomDeNaissance, String paysDeNaissance, String situationFamiliale, InformationSpTelephoneDto telephoneFixe,
			InformationSpTelephoneDto telephoneMobile) {
		super(nom, prenoms, sexe, dateDeNaissance);
		this.adresse = adresse;
		this.civilite = civilite;
		this.codeInseeDeNaissance = codeInseeDeNaissance;
		this.codeInseePaysDeNaissance = codeInseePaysDeNaissance;
		this.communeDeNaissance = communeDeNaissance;
		this.departementDeNaissance = departementDeNaissance;
		this.email = email;
		this.nomDeNaissance = nomDeNaissance;
		this.paysDeNaissance = paysDeNaissance;
		this.situationFamiliale = situationFamiliale;
		this.telephoneFixe = telephoneFixe;
		this.telephoneMobile = telephoneMobile;
	}

	public InformationSpUsagerDto(String email, String nom, String prenoms) {
		super();
		this.email = email;
		super.setNom(nom);
		super.setPrenoms(prenoms);
	}

	public TypeDeCompteSP getAccountType() {
		return this.accountType;
	}

	public InformationSpAdresseDto getAdresse() {
		return this.adresse;
	}

	public String getCivilite() {
		return this.civilite;
	}

	public String getCodeInseeDeNaissance() {
		return this.codeInseeDeNaissance;
	}

	public String getCodeInseePaysDeNaissance() {
		return this.codeInseePaysDeNaissance;
	}

	public String getCommuneDeNaissance() {
		return this.communeDeNaissance;
	}

	public String getDepartementDeNaissance() {
		return this.departementDeNaissance;
	}

	public String getEmail() {
		return this.email;
	}

	public String getEmailTechnique() {
		return this.emailTechnique;
	}

	public Boolean getFranceConnect() {
		return this.franceConnect;
	}

	public String getNomDeNaissance() {
		return this.nomDeNaissance;
	}

	public String getPaysDeNaissance() {
		return this.paysDeNaissance;
	}

	public String getSituationFamiliale() {
		return this.situationFamiliale;
	}

	public InformationSpTelephoneDto getTelephoneFixe() {
		return this.telephoneFixe;
	}

	public InformationSpTelephoneDto getTelephoneMobile() {
		return this.telephoneMobile;
	}

	public String getUuidSp() {
		return this.uuidSp;
	}

	public void setAccountType(TypeDeCompteSP accountType) {
		this.accountType = accountType;
	}

	public void setAdresse(InformationSpAdresseDto adresse) {
		this.adresse = adresse;
	}

	public void setCivilite(String civilite) {
		this.civilite = civilite;
	}

	public void setCodeInseeDeNaissance(String codeInseeDeNaissance) {
		this.codeInseeDeNaissance = codeInseeDeNaissance;
	}

	public void setCodeInseePaysDeNaissance(String codeInseePaysDeNaissance) {
		this.codeInseePaysDeNaissance = codeInseePaysDeNaissance;
	}

	public void setCommuneDeNaissance(String communeDeNaissance) {
		this.communeDeNaissance = communeDeNaissance;
	}

	public void setDepartementDeNaissance(String departementDeNaissance) {
		this.departementDeNaissance = departementDeNaissance;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmailTechnique(String emailTechnique) {
		this.emailTechnique = emailTechnique;
	}

	public void setFranceConnect(Boolean franceConnect) {
		this.franceConnect = franceConnect;
	}

	public void setNomDeNaissance(String nomDeNaissance) {
		this.nomDeNaissance = nomDeNaissance;
	}

	public void setPaysDeNaissance(String paysDeNaissance) {
		this.paysDeNaissance = paysDeNaissance;
	}

	public void setSituationFamiliale(String situationFamiliale) {
		this.situationFamiliale = situationFamiliale;
	}

	public void setTelephoneFixe(InformationSpTelephoneDto telephoneFixe) {
		this.telephoneFixe = telephoneFixe;
	}

	public void setTelephoneMobile(InformationSpTelephoneDto telephoneMobile) {
		this.telephoneMobile = telephoneMobile;
	}

	public void setUuidSp(String uuidSp) {
		this.uuidSp = uuidSp;
	}

	@Override
	public String toString() {
		return "InformationSpUsagerDto [uuidSp=" + this.uuidSp + ", accountType=" + this.accountType + ", franceConnect=" + this.franceConnect
				+ ", emailTechnique=" + this.emailTechnique + ", email=" + this.email + ", civilite=" + this.civilite + ", sexe=" + super.getSexe()
				+ ", nom=" + super.getNom() + ", prenom=" + super.getPrenoms() + ", dateNaissance=" + super.getDateDeNaissance() + ", nomDeNaissance="
				+ this.nomDeNaissance + ", adresse=" + this.adresse + ", communeDeNaissance=" + this.communeDeNaissance + ", codeInseeDeNaissance="
				+ this.codeInseeDeNaissance + ", departementDeNaissance=" + this.departementDeNaissance + ", paysDeNaissance=" + this.paysDeNaissance
				+ ", codeInseePaysDeNaissance=" + this.codeInseePaysDeNaissance + ", situationFamiliale=" + this.situationFamiliale
				+ ", telephoneFixe=" + this.telephoneFixe + ", telephoneMobile=" + this.telephoneMobile + "]";
	}
}
