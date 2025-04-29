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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.FonctionnalitesDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PageDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PieceJointeAssocieeDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PointEntreeDto;

/**
 * Classe décrivant les données de configuration d'une démarche envoyée dans le navigateur de l'usager pour paramétrer l'application WEB et permettre
 * la saisie des données.
 *
 * @see configurationdemarchegeneral.model.ts
 */
// Les seuls membres/attributs présents dans cette classe sont ceux utiles
@JsonIgnoreProperties(ignoreUnknown = true)
// Pas de record ici sinon Spring n'arrive pas à initialiser les attributs
public class ConfigurationPubliqueDemarcheDto {

	/** Code de la démarche */
	private String codeDemarche;

	/** Description des fonctionnalités actives (ou non) dans cette démarche. */
	private FonctionnalitesDto fonctionnalites;

	/**
	 * La liste ordonnées des pages que la démarche affiche. Cette liste ne peut pas être vide.
	 */
	private List<PageDto> pages;

	/** Liste des pièces jointes de la démarche */
	private List<PieceJointeAssocieeDto> piecesJointesAssociees = new ArrayList<>();

	/**
	 * Les points d'entrée possibles. Si cette liste est vide, l'accès à la démarche est simple (sans condition particulière dès la première page) et
	 * anonyme. Si un élément est présent, l'usager ne peut accéder à la démarche sans répondre aux critères de l'un des points d'entrée.
	 */
	private List<PointEntreeDto> pointsEntree = new ArrayList<>();

	/** Titre de la démarche */
	private String titre;

	/** Données initiales envoyées à l'application WEB */
	private Map<String, String> valeursInitiales = new HashMap<>();

	/** Version de la configuration */
	private String versionConfiguration;

	public ConfigurationPubliqueDemarcheDto() {
		super();
	}

	public ConfigurationPubliqueDemarcheDto(String codeDemarche, String versionConfiguration) {
		super();
		this.codeDemarche = codeDemarche;
		this.versionConfiguration = versionConfiguration;
		this.pages = new ArrayList<>();
		this.pointsEntree = new ArrayList<>();
	}

	public ConfigurationPubliqueDemarcheDto(String codeDemarche, String versionConfiguration, FonctionnalitesDto fonctionnalites) {
		this(codeDemarche, versionConfiguration);
		this.fonctionnalites = fonctionnalites;
	}

	public ConfigurationPubliqueDemarcheDto(String codeDemarche, String versionConfiguration, FonctionnalitesDto fonctionnalites,
			List<PointEntreeDto> pointsEntree) {
		this(codeDemarche, versionConfiguration);
		this.fonctionnalites = fonctionnalites;
		this.pointsEntree = pointsEntree;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public FonctionnalitesDto getFonctionnalites() {
		return this.fonctionnalites;
	}

	public List<PageDto> getPages() {
		return this.pages;
	}

	public List<PieceJointeAssocieeDto> getPiecesJointesAssociees() {
		return this.piecesJointesAssociees;
	}

	public List<PointEntreeDto> getPointsEntree() {
		return this.pointsEntree;
	}

	public String getTitre() {
		return this.titre;
	}

	public Map<String, String> getValeursInitiales() {
		return this.valeursInitiales;
	}

	public String getVersionConfiguration() {
		return this.versionConfiguration;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setFonctionnalites(FonctionnalitesDto fonctionnalites) {
		this.fonctionnalites = fonctionnalites;
	}

	public void setPages(List<PageDto> pages) {
		this.pages = pages;
	}

	public void setPiecesJointesAssociees(List<PieceJointeAssocieeDto> piecesJointesAssociees) {
		this.piecesJointesAssociees = piecesJointesAssociees;
	}

	public void setPointsEntree(List<PointEntreeDto> pointsEntree) {
		this.pointsEntree = pointsEntree;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setValeursInitiales(Map<String, String> valeursInitiales) {
		this.valeursInitiales = valeursInitiales;
	}

	public void setVersionConfiguration(String versionConfiguration) {
		this.versionConfiguration = versionConfiguration;
	}

	@Override
	public String toString() {
		return "ConfigurationPubliqueDemarcheDto [codeDemarche=" + this.codeDemarche + ", versionConfiguration=" + this.versionConfiguration
				+ ", titre=" + this.titre + ", fonctionnalites=" + this.fonctionnalites + ", pointsEntree=" + this.pointsEntree
				+ ", valeursInitiales=" + this.valeursInitiales + ", piecesJointesAssociees=" + this.piecesJointesAssociees + ", pages=" + this.pages
				+ "]";
	}

}
