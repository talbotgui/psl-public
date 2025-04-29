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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Classe décrivant les données de configuration d'une démarche envoyée dans le navigateur de l'usager pour paramétrer l'application WEB et permettre
 * la saisie des données.
 *
 * @see configurationdemarchegeneral.model.ts
 */
@JsonIgnoreProperties(ignoreUnknown = true)
// Pour ne pas renvoyer tous les attributs NULL pour tous les types de contenu
@JsonInclude(Include.NON_NULL)
// Pas de record dans ce cas à cause des différents constructeurs
public class ContenuDto {

	/** Modèle de l'aide (peut contenir une {{variable}}). */
	private String aide;

	/** Liste des aides des champs @see ContenuSaisieComplexe dans le code FRONT */
	private Map<String, String> aides;

	/** Code de l'API après de laquelle se sourcer. @see ContenuAutocompletion dans le code FRONT */
	private String api;

	/**
	 * Nom de l'attribut des objets en sortie de l'API à utiliser pour afficher les options à l'usager @see ContenuAutocompletion dans le code FRONT
	 */
	private String attributReponseApiPourLibelle;

	/** Liste des champs à afficher (sans condition autre que celles portées par le composant) @see ContenuSaisieComplexe dans le code FRONT */
	private List<String> champsVisibles;

	/**
	 * Clef unique définissant la donnée saisie. Cette clef est utilisable dans les conditions et les autres champs. (cet attribut n'est pas utile
	 * dans tous les sous-types mais presque et il est très fortement utilisé)
	 */
	private String clef;

	/** Valeur paramétrée du complément à ajouter à l'appel de l'API (peut contenir des variables entre {{}} */
	private String complementAppelApi;

	/** Condition de désactivation du champs (cf. conditionVisibilite) */
	private String conditionDesactivation;

	/**
	 * Condition d'affichage du contenu. Cette attribut est optionnel et sa valeur par défaut est undefined. Cette condition est une expression JS
	 * évaluée avec l'équivalent de la méthode EVAL() ayant accès aux données de la démarche uniquement. Attention, toutes les données sont des
	 * chaines de caractères (même les boolean et les nombres).
	 */
	private String conditionVisibilite;

	/** Flag utilisé par les contenus de type PARAGRAPHE dans les blocs dynamiques. */
	private Boolean estIncluDansUnBlocDynamiquePourLectureSeule;

	/** Id du document stocké @see ContenuUploadDocument dans le code FRONT */
	private String idDocumentStocke;

	/** @see ContenuParagraphe dans le code FRONT */
	private String style;

	/** Liste de classes CSS séparée par un espace à appliquer à l'aide associée @see ContenuParagraphe dans le code FRONT. */
	private String styleAide;

	/** @see ContenuParagraphe dans le code FRONT */
	private String texte;

	/** Modèle du titre du contenu de bloc (peut contenir une {{variable}}). */
	private String titre;

	/** Liste des titres des champs @see ContenuSaisieComplexe dans le code FRONT */
	private Map<String, String> titres;

	/** Type du contenu */
	private String type;

	/** Liste des valeurs possibles affichées par le composant */
	private List<ValeurPossibleDto> valeurs;

	/** Liste des champs avec validations pour un contenu complexe */
	private Map<String, List<String>> validationsComplexes;

	/** Liste des validations pour un contenu simple */
	private List<String> validationsSimples;

	public ContenuDto() {
		super();
	}

	/**
	 * Clone du contenu en y remplaçant le @@ par l'occurence.
	 *
	 * @param contenuDtoAcloner ContenuDto à cloner.
	 * @param o                 Numéro de l'occurence du bloc dynamique.
	 */
	public ContenuDto(ContenuDto contenuDtoAcloner, String o) {
		this.aide = contenuDtoAcloner.aide;
		this.aides = contenuDtoAcloner.aides;
		this.api = contenuDtoAcloner.api;
		this.attributReponseApiPourLibelle = contenuDtoAcloner.attributReponseApiPourLibelle;
		this.champsVisibles = contenuDtoAcloner.champsVisibles;
		if (contenuDtoAcloner.clef != null) {
			this.clef = contenuDtoAcloner.clef.replace("@@", o);
		}
		this.complementAppelApi = contenuDtoAcloner.complementAppelApi;
		if (contenuDtoAcloner.conditionDesactivation != null) {
			this.conditionDesactivation = contenuDtoAcloner.conditionDesactivation.replace("@@", o);
		}
		if (contenuDtoAcloner.conditionVisibilite != null) {
			this.conditionVisibilite = contenuDtoAcloner.conditionVisibilite.replace("@@", o);
		}
		this.idDocumentStocke = contenuDtoAcloner.idDocumentStocke;
		this.style = contenuDtoAcloner.style;
		this.styleAide = contenuDtoAcloner.styleAide;
		if (contenuDtoAcloner.texte != null) {
			this.texte = contenuDtoAcloner.texte.replace("@@", o);
		}
		this.titre = contenuDtoAcloner.titre;
		this.titres = contenuDtoAcloner.titres;
		this.type = contenuDtoAcloner.type;
		this.valeurs = contenuDtoAcloner.valeurs;
		this.validationsComplexes = contenuDtoAcloner.validationsComplexes;
		this.validationsSimples = contenuDtoAcloner.validationsSimples;
	}

	/** Constructeur pour champ de type simple (input, checkbox ou autocompletion) */
	public ContenuDto(String clef, String type, String conditionVisibilite, String conditionDesactivation, List<String> validationsSimples) {
		super();
		this.clef = clef;
		this.type = type;
		this.conditionVisibilite = conditionVisibilite;
		this.conditionDesactivation = conditionDesactivation;
		this.validationsSimples = validationsSimples;
	}

	/** Constructeur pour liste déroulante ou radio. */
	public ContenuDto(String clef, String type, String conditionVisibilite, String conditionDesactivation, List<String> validationsSimples,
			List<ValeurPossibleDto> valeurs) {
		super();
		this.clef = clef;
		this.type = type;
		this.conditionVisibilite = conditionVisibilite;
		this.conditionDesactivation = conditionDesactivation;
		this.validationsSimples = validationsSimples;
		this.valeurs = valeurs;
	}

	/**
	 * Constructeur utilisé par Jackson pour instancier les ContenuDTO. Les données "validation" sont insérées sous forme de String et traitées dans
	 * le constructeur.
	 *
	 * @param clef
	 * @param type
	 * @param conditionVisibilite
	 * @param conditionDesactivation
	 * @param champsVisibles
	 * @param valeurs
	 * @param validations
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	@JsonCreator
	public ContenuDto(@JsonProperty("clef") String clef, @JsonProperty("type") String type,
			@JsonProperty("conditionVisibilite") String conditionVisibilite, @JsonProperty("conditionDesactivation") String conditionDesactivation,
			@JsonProperty("champsVisibles") List<String> champsVisibles, @JsonProperty("valeurs") List<ValeurPossibleDto> valeurs,
			@JsonProperty("validations") Object validations) {
		super();
		this.clef = clef;
		this.type = type;
		this.conditionVisibilite = conditionVisibilite;
		this.conditionDesactivation = conditionDesactivation;
		this.champsVisibles = champsVisibles;
		this.valeurs = valeurs;
		if (validations != null) {
			if (validations instanceof List) {
				this.validationsSimples = (List<String>) validations;
			} else {
				this.validationsComplexes = (Map<String, List<String>>) validations;
			}
		}
	}

	/** Constructeur pour champ complexe */
	public ContenuDto(String clef, String type, String conditionVisibilite, String conditionDesactivation, List<String> champsVisibles,
			Map<String, List<String>> validationsComplexes) {
		super();
		this.clef = clef;
		this.type = type;
		this.conditionVisibilite = conditionVisibilite;
		this.conditionDesactivation = conditionDesactivation;
		this.champsVisibles = champsVisibles;
		this.validationsComplexes = validationsComplexes;
	}

	public String getAide() {
		return this.aide;
	}

	public Map<String, String> getAides() {
		return this.aides;
	}

	public String getApi() {
		return this.api;
	}

	public String getAttributReponseApiPourLibelle() {
		return this.attributReponseApiPourLibelle;
	}

	public List<String> getChampsVisibles() {
		return this.champsVisibles;
	}

	public String getClef() {
		return this.clef;
	}

	public String getComplementAppelApi() {
		return this.complementAppelApi;
	}

	public String getConditionDesactivation() {
		return this.conditionDesactivation;
	}

	public String getConditionVisibilite() {
		return this.conditionVisibilite;
	}

	public Boolean getEstIncluDansUnBlocDynamiquePourLectureSeule() {
		return this.estIncluDansUnBlocDynamiquePourLectureSeule;
	}

	public String getIdDocumentStocke() {
		return this.idDocumentStocke;
	}

	public String getStyle() {
		return this.style;
	}

	public String getStyleAide() {
		return this.styleAide;
	}

	public String getTexte() {
		return this.texte;
	}

	public String getTitre() {
		return this.titre;
	}

	public Map<String, String> getTitres() {
		return this.titres;
	}

	public String getType() {
		return this.type;
	}

	public List<ValeurPossibleDto> getValeurs() {
		return this.valeurs;
	}

	public Map<String, List<String>> getValidationsComplexes() {
		return this.validationsComplexes;
	}

	public List<String> getValidationsSimples() {
		return this.validationsSimples;
	}

	public void setAide(String aide) {
		this.aide = aide;
	}

	public void setAides(Map<String, String> aides) {
		this.aides = aides;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public void setAttributReponseApiPourLibelle(String attributReponseApiPourLibelle) {
		this.attributReponseApiPourLibelle = attributReponseApiPourLibelle;
	}

	public void setChampsVisibles(List<String> champsVisibles) {
		this.champsVisibles = champsVisibles;
	}

	public void setClef(String clef) {
		this.clef = clef;
	}

	public void setComplementAppelApi(String complementAppelApi) {
		this.complementAppelApi = complementAppelApi;
	}

	public void setConditionDesactivation(String conditionDesactivation) {
		this.conditionDesactivation = conditionDesactivation;
	}

	public void setConditionVisibilite(String conditionVisibilite) {
		this.conditionVisibilite = conditionVisibilite;
	}

	public void setEstIncluDansUnBlocDynamiquePourLectureSeule(Boolean estIncluDansUnBlocDynamiquePourLectureSeule) {
		this.estIncluDansUnBlocDynamiquePourLectureSeule = estIncluDansUnBlocDynamiquePourLectureSeule;
	}

	public void setIdDocumentStocke(String idDocumentStocke) {
		this.idDocumentStocke = idDocumentStocke;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleAide(String styleAide) {
		this.styleAide = styleAide;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setTitres(Map<String, String> titres) {
		this.titres = titres;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValeurs(List<ValeurPossibleDto> valeurs) {
		this.valeurs = valeurs;
	}

	public void setValidationsComplexes(Map<String, List<String>> validationsComplexes) {
		this.validationsComplexes = validationsComplexes;
	}

	public void setValidationsSimples(List<String> validationsSimples) {
		this.validationsSimples = validationsSimples;
	}

	@Override
	public String toString() {
		return "ContenuDto [" + (this.aide != null ? "aide=" + this.aide + ", " : "") + (this.aides != null ? "aides=" + this.aides + ", " : "")
				+ (this.api != null ? "api=" + this.api + ", " : "")
				+ (this.attributReponseApiPourLibelle != null ? "attributReponseApiPourLibelle=" + this.attributReponseApiPourLibelle + ", " : "")
				+ (this.champsVisibles != null ? "champsVisibles=" + this.champsVisibles + ", " : "")
				+ (this.clef != null ? "clef=" + this.clef + ", " : "")
				+ (this.complementAppelApi != null ? "complementAppelApi=" + this.complementAppelApi + ", " : "")
				+ (this.conditionDesactivation != null ? "conditionDesactivation=" + this.conditionDesactivation + ", " : "")
				+ (this.conditionVisibilite != null ? "conditionVisibilite=" + this.conditionVisibilite + ", " : "")
				+ (this.idDocumentStocke != null ? "idDocumentStocke=" + this.idDocumentStocke + ", " : "")
				+ (this.style != null ? "style=" + this.style + ", " : "") + (this.styleAide != null ? "styleAide=" + this.styleAide + ", " : "")
				+ (this.texte != null ? "texte=" + this.texte + ", " : "") + (this.titre != null ? "titre=" + this.titre + ", " : "")
				+ (this.titres != null ? "titres=" + this.titres + ", " : "") + (this.type != null ? "type=" + this.type + ", " : "")
				+ (this.valeurs != null ? "valeurs=" + this.valeurs + ", " : "")
				+ (this.validationsComplexes != null ? "validationsComplexes=" + this.validationsComplexes + ", " : "")
				+ (this.validationsSimples != null ? "validationsSimples=" + this.validationsSimples : "") + "]";
	}
}
