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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Classe décrivant les données de configuration d'une démarche envoyée dans le navigateur de l'usager pour paramétrer l'application WEB et permettre
 * la saisie des données.
 *
 * @see configurationdemarchegeneral.model.ts
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//Pour ne pas renvoyer tous les attributs NULL pour tous les types de contenu
@JsonInclude(Include.NON_NULL)
public class SousBlocDto {
	/** Contenu du cadre d'aide (si renseigné) */
	private String aide;
	/** Tooltip affiché sur le bouton à côté du titre (si renseigné). */
	private String aideTitre;
	/** Condition de visibilité du bloc. */
	private String conditionVisibilite;
	/** Liste ordonnée des éléments dans le bloc */
	private List<ContenuDto> contenus = new ArrayList<>();
	/** Activation du mode dynamique du bloc. */
	private Boolean dynamique;
	/** Nombre d'occurences maximales */
	private Integer maxOccurences;
	/** Titre du bloc. */
	private String titre;

	/** Constructeur vide */
	public SousBlocDto() {
	}

	/** Constructeur spécifique */
	public SousBlocDto(List<ContenuDto> contenus) {
		if (contenus != null) {
			this.contenus.addAll(contenus);
		}
	}

	/** Constructeur spécifique */
	public SousBlocDto(List<ContenuDto> contenus, String conditionVisibilite) {
		if (contenus != null) {
			this.contenus.addAll(contenus);
		}
		this.conditionVisibilite = conditionVisibilite;
	}

	/**
	 * Clone du bloc en y remplaçant le @@ par l'occurence (dans les données du bloc mais aussi dans les contenus eux aussi clonés).
	 *
	 * @param sousBlocAcloner Instance à cloner.
	 * @param o               Numéro de l'occurence du bloc dynamique.
	 */
	public SousBlocDto(SousBlocDto sousBlocAcloner, String o) {
		this(sousBlocAcloner.titre, sousBlocAcloner.aideTitre, sousBlocAcloner.aide, new ArrayList<>(), null, sousBlocAcloner.dynamique,
				sousBlocAcloner.maxOccurences);

		// Duplication des contenus
		if (sousBlocAcloner.contenus != null) {
			this.contenus.addAll(sousBlocAcloner.contenus.stream().map(c -> new ContenuDto(c, o)).toList());
		}

		// Traitement spécifique de cet attribut
		String nouvelleConditionVisibilite = sousBlocAcloner.conditionVisibilite;
		if (nouvelleConditionVisibilite != null) {
			nouvelleConditionVisibilite = sousBlocAcloner.conditionVisibilite.replace("@@", o);
		}
	}

	/**
	 * Constructeur contenant tous les attributs.
	 */
	public SousBlocDto(String titre, String aideTitre, String aide, List<ContenuDto> contenus, String conditionVisibilite, Boolean dynamique,
			Integer maxOccurences) {
		this.titre = titre;
		this.aideTitre = aideTitre;
		this.aide = aide;
		this.contenus = contenus;
		this.conditionVisibilite = conditionVisibilite;
		this.dynamique = dynamique;
		this.maxOccurences = maxOccurences;
	}

	public String getAide() {
		return this.aide;
	}

	public String getAideTitre() {
		return this.aideTitre;
	}

	public String getConditionVisibilite() {
		return this.conditionVisibilite;
	}

	public List<ContenuDto> getContenus() {
		return this.contenus;
	}

	public Boolean getDynamique() {
		return this.dynamique;
	}

	public Integer getMaxOccurences() {
		return this.maxOccurences;
	}

	public String getTitre() {
		return this.titre;
	}

	public void setAide(String aide) {
		this.aide = aide;
	}

	public void setAideTitre(String aideTitre) {
		this.aideTitre = aideTitre;
	}

	public void setConditionVisibilite(String conditionVisibilite) {
		this.conditionVisibilite = conditionVisibilite;
	}

	public void setContenus(List<ContenuDto> contenus) {
		this.contenus = contenus;
	}

	public void setDynamique(Boolean dynamique) {
		this.dynamique = dynamique;
	}

	public void setMaxOccurences(Integer maxOccurences) {
		this.maxOccurences = maxOccurences;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	@Override
	public String toString() {
		return "BlocDto [" + (this.contenus != null ? "contenus=" + this.contenus + ", " : "")
				+ (this.titre != null ? "titre=" + this.titre + ", " : "") + (this.aideTitre != null ? "aideTitre=" + this.aideTitre + ", " : "")
				+ (this.aide != null ? "aide=" + this.aide + ", " : "")
				+ (this.conditionVisibilite != null ? "conditionVisibilite=" + this.conditionVisibilite + ", " : "")
				+ (this.dynamique != null ? "dynamique=" + this.dynamique + ", " : "")
				+ (this.maxOccurences != null ? "maxOccurences=" + this.maxOccurences : "") + "]";
	}
}