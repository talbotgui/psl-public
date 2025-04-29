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
package com.github.talbotgui.psl.socle.soumission.apiclient.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import jakarta.validation.constraints.Pattern;

/** Object image des données envoyées par une démarche */
// Cette classe ne peut être un record à cause de la méthode ajouterChaqueDonnee
public class DonneesDeSoumissionDto {

	/** Code de langue FR. */
	public static final String LANGUE_FR = "FR";

	/** Code de la démarche fourni dans l'appel REST */
	@Pattern(regexp = RegexUtils.CODE_DEMARCHE)
	private String codeDemarche;

	/** Données saisies et envoyées par l'application WEB */
	private Map<@Pattern(regexp = RegexUtils.DONNEE_SOUMISE_CLEF) String, @Pattern(regexp = RegexUtils.DONNEE_SOUMISE_VALEUR) String> donnees = new HashMap<>();

	/** ID du brouillon chargé par l'usager. */
	@Pattern(regexp = RegexUtils.ID)
	private String idBrouillon;

	/** Langue de l'usager au moment de la soumission */
	@Pattern(regexp = RegexUtils.LANGUE)
	private String langue;

	/** Version de la configuration chargée par l'usager dans son navigateur et ayant servi à la création/constitution des données */
	@Pattern(regexp = RegexUtils.VERSION_CONFIGURATION)
	private String versionConfiguration;

	public DonneesDeSoumissionDto() {
		super();
	}

	public DonneesDeSoumissionDto(String codeDemarche, String versionConfiguration, String langue, Map<String, String> donnees) {
		super();
		this.codeDemarche = codeDemarche;
		this.versionConfiguration = versionConfiguration;
		this.langue = langue;
		this.donnees = donnees;
	}

	@JsonAnySetter
	public void ajouterChaqueDonnee(String clef, String valeur) {
		this.donnees.put(clef, valeur);
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	@JsonAnyGetter
	public Map<String, String> getDonnees() {
		return this.donnees;
	}

	public String getIdBrouillon() {
		return this.idBrouillon;
	}

	public String getLangue() {
		return this.langue;
	}

	public String getVersionConfiguration() {
		return this.versionConfiguration;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setDonnees(Map<String, String> donnees) {
		this.donnees = donnees;
	}

	public void setIdBrouillon(String idBrouillon) {
		this.idBrouillon = idBrouillon;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}

	public void setVersionConfiguration(String versionConfiguration) {
		this.versionConfiguration = versionConfiguration;
	}

	@Override
	public String toString() {
		return "DonneesDeSoumissionDto [codeDemarche=" + this.codeDemarche + ", versionConfiguration=" + this.versionConfiguration + ", donnees="
				+ this.donnees + "]";
	}

}
