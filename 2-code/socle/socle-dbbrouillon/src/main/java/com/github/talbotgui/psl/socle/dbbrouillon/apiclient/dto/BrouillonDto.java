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
package com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * Classe décrivant les données d'un brouillon.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrouillonDto {
	/** Code de la démarche */
	@Pattern(regexp = RegexUtils.CODE_DEMARCHE)
	private String codeDemarche;
	/** Données saisies et envoyées par l'application WEB */
	private Map<@Pattern(regexp = RegexUtils.DONNEE_SOUMISE_CLEF) String, @Pattern(regexp = RegexUtils.DONNEE_SOUMISE_VALEUR) String> donnees = new HashMap<>();
	/** Id du document une fois inséré */
	@Pattern(regexp = RegexUtils.ID)
	private String id;
	/** Utilisateur */
	@Pattern(regexp = RegexUtils.EMAIL)
	private String idUtilisateur;
	/** Index de la page */
	@Min(0)
	private Integer indexPage;
	/** Version de la configuration */
	@Pattern(regexp = RegexUtils.VERSION_CONFIGURATION)
	private String versionConfiguration;

	public BrouillonDto() {
		super();
	}

	/**
	 * Constructeur.
	 *
	 * @param codeDemarche         Code de la démarche
	 * @param versionConfiguration Version de la configuration
	 * @param indexPage            Page Index de la page
	 * @param donnees              Données saisies et envoyées par l'application WEB
	 */
	public BrouillonDto(String codeDemarche, String versionConfiguration, Integer indexPage, Map<String, String> donnees) {
		super();
		this.codeDemarche = codeDemarche;
		this.versionConfiguration = versionConfiguration;
		this.indexPage = indexPage;
		this.donnees = donnees;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public Map<String, String> getDonnees() {
		return this.donnees;
	}

	public String getId() {
		return this.id;
	}

	public String getIdUtilisateur() {
		return this.idUtilisateur;
	}

	public Integer getIndexPage() {
		return this.indexPage;
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

	public void setId(String id) {
		this.id = id;
	}

	public void setIdUtilisateur(String idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}

	public void setIndexPage(Integer indexPage) {
		this.indexPage = indexPage;
	}

	public void setVersionConfiguration(String versionConfiguration) {
		this.versionConfiguration = versionConfiguration;
	}

}
