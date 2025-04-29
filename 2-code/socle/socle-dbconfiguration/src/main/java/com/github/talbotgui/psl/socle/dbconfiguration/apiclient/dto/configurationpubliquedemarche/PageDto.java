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

import org.springframework.data.annotation.PersistenceCreator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe décrivant les données de configuration d'une démarche envoyée dans le navigateur de l'usager pour paramétrer l'application WEB et permettre
 * la saisie des données.
 *
 * @see configurationdemarchegeneral.model.ts
 */
//Pour ne pas renvoyer tous les attributs NULL pour tous les types de contenu
@JsonInclude(Include.NON_NULL)
//Les seuls membres/attributs présents dans cette classe sont ceux utiles
@JsonIgnoreProperties(ignoreUnknown = true)
public record PageDto(

		/**
		 * Liste ordonnée des bloc à afficher dans la page. Cette liste ne peut être vide.
		 */
		List<BlocDto> blocs,

		/**
		 * Condition d'affichage de la page. Cette attribut est optionnel et sa valeur par défaut est undefined. Cette condition est une expression JS
		 * évaluée avec l'équivalent de la méthode EVAL() ayant accès aux données de la démarche uniquement. Attention, toutes les données sont des
		 * chaines de caractères (même les boolean et les nombres). Toutes les pages affichables (selon la condition d'affichage) sont présentes dans
		 * le fil d'Ariane sauf les pages marquées "exclueDuFilDariane:true".
		 */
		String conditionVisibilite,

		/** Le titre de la page est affiché en haut de la page quand elle est affichée */
		String titre,

		/**
		 * Le titre du fil d'Ariane peut être différent de celui de la page. Ce titre peut être vide. L'attribut "titre" est alors utilisé.
		 */
		String titreAriane,

		/**
		 * Flag définissant si la page doit être visible dans le fil d'Ariane. Si deux pages consécutives ont le même titreAriane, l'une des deux
		 * devrait être exclueDuFilDariane:true pour éviter un doublon dans le fil d'Ariane.
		 */
		Boolean exclueDuFilDariane,

		/** Flag indiquant que la page ne doit pas être générée par le framework. */
		Boolean specifiqueAlaDemarche) {

	/** Constructeur vide */
	public PageDto() {
		this(new ArrayList<>(), null, null, null, false, false);
	}

	/** Constructeur par défaut */
	public PageDto(List<BlocDto> blocs, String conditionVisibilite) {
		this(blocs, conditionVisibilite, null, null, false, false);
	}

	/**
	 * Constructeur contenant tous les attributs mais avec le premier en dernier pour fournit à SpringDataMongo un constructeur à utitiliser (les
	 * setter n'existe pas dans un record). De plus, pour les mêmes raisons mais avec Jackson, il faut ajouter un @JsonCreator sur le constructeur et
	 * un @JsonProperty sur chaque paramètre.
	 */
	@PersistenceCreator
	@JsonCreator
	public PageDto(@JsonProperty("conditionVisibilite") String conditionVisibilite, @JsonProperty("titre") String titre,
			@JsonProperty("titreAriane") String titreAriane, @JsonProperty("exclueDuFilDariane") Boolean exclueDuFilDariane,
			@JsonProperty("specifiqueAlaDemarche") Boolean specifiqueAlaDemarche, @JsonProperty("blocs") List<BlocDto> blocs) {
		this(blocs, conditionVisibilite, titre, titreAriane, exclueDuFilDariane, specifiqueAlaDemarche);
	}

}
