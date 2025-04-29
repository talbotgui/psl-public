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
public class BlocDto extends SousBlocDto {

	/** Liste des sous-blocs. */
	private List<SousBlocDto> sousBlocs = new ArrayList<>();

	/** Constructeur vide. */
	public BlocDto() {
		super();
	}

	/**
	 * Clone du bloc en y remplaçant le @@ par l'occurence (dans les données du bloc mais aussi dans les contenus eux aussi clonés).
	 *
	 * @param blocAcloner Instance à cloner.
	 * @param o           Numéro de l'occurence du bloc dynamique.
	 */
	public BlocDto(BlocDto blocAcloner, String o) {
		super(blocAcloner, o);

		// Traitement des sous-blocs
		this.sousBlocs = new ArrayList<>();
		if (blocAcloner.sousBlocs != null) {
			this.sousBlocs.addAll(blocAcloner.sousBlocs.stream().map(sb -> new SousBlocDto(sb, o)).toList());
		}
	}

	public BlocDto(List<ContenuDto> contenus) {
		super(contenus);
	}

	public BlocDto(List<ContenuDto> contenus, String conditionVisibilite) {
		super(contenus, conditionVisibilite);
	}

	public List<SousBlocDto> getSousBlocs() {
		return this.sousBlocs;
	}

	/**
	 * Méthode récupérant tous les contenus d'un bloc et de ses sous-blocs.
	 *
	 * @return Tous les contenus.
	 */
	public List<ContenuDto> obtenirTousLesContenusDuBlocEtDeSesSousBlocs() {
		List<ContenuDto> resultats = new ArrayList<>();
		if (this.getContenus() != null) {
			resultats.addAll(this.getContenus());
		}
		if (this.sousBlocs != null) {
			for (SousBlocDto sb : this.sousBlocs) {
				if (sb.getContenus() != null) {
					resultats.addAll(sb.getContenus());
				}
			}
		}
		return resultats;
	}

	public void setSousBlocs(List<SousBlocDto> sousBlocs) {
		this.sousBlocs = sousBlocs;
	}
}