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
/**
 * Ce fichier contient toutes les classes MODEL liées à la configuration d'une démarche.
 * 
 * Règles de conception : 
 *  - tous les attributs sont public
 *  - pas de constructeur
 *  - pas de méthodes intelligentes (ce ne sont pas des BusinessObject)
 *  - toutes les collections sont initialisées à []
 *  - chaque classe doit être décrite et expliquée
 *  - chaque règle de gestion associée à un attribut doit être expliquée
 *  - tout attribut dont le type est différent de STRING doit faire l'objet d'un traitement dans la méthode DocumentService.chargerConfigurationDemarche
 * 
 * Toute modification ici doit être reportée dans schemajson.ts et dans app.editeurniveau4.html (projet EDITEUR)
 */

/** Liste des types de contenu de bloc disponibles (correspondant aux classes existantes) */
export enum TypeContenuDeBloc {
    adresseFrOuEtr = 'adresseFrOuEtr', // ContenuAdresseFrOuEtr
    autocompletion = 'autocompletion', // ContenuAutocompletion
    case = 'case', //ContenuCase
    contactPersonnel = 'contactPersonnel', //ContenuContactPersonnel
    date = 'date', // ContenuDate
    finDemarche = 'finDemarche',// ContenuFinDemarche
    identite = 'identite', // ContenuIdentite
    listeFinie = 'listeFinie', //ContenuListeFinie
    paragraphe = 'paragraphe', // ContenuParagraphe
    radio = 'radio', // ContenuRadio
    recapitulatif = 'recapitulatif', // ContenuRecapitulatif
    saisie = 'saisie', // ContenuSaisie
    saisieLongue = 'saisieLongue', // ContenuSaisieLongue
    documents = "documents", // ContenuDocuments
    uploadDocument = "uploadDocument", //ContenuUploadDocument
    utilisateurConnecte = "utilisateurConnecte" // ContenuUtilisateurConnecte
}

/** Liste des valideurs supportés */
export abstract class Validation {
    public static readonly required = 'required';
    public static readonly email = 'email';
    public static readonly url = 'url';
    public static readonly telephoneFR = 'telephoneFR';
    public static readonly secu = 'secu';
    public static readonly datePassee = 'datePassee';
    public static readonly dateFuture = 'dateFuture';
    public static readonly dateAvant = 'dateAvant';
    public static readonly dateApres = 'dateApres';
    public static readonly autocompletionPrecise = 'autocompletionPrecise';
    public static readonly regex = 'regex';
    public static readonly motDePasse = 'motDePasse';
}

/** Attributs et règles communes à tous les contenus de bloc */
export abstract class ContenuDeBloc {
    /** Cette variable statique publique contient la valeur de la configuration (ListeFonctionnalites.modeObligatoireParDefaut) */
    // Cette variable est statique pour être simple à initialiser depuis le chargement de la configuration et parcequ'il ne peut y avoir qu'une seule et unique valeur pour toute la démarche
    public static FLAG_FONCTIONNALITE_MODE_OBLIGATOIRE_PAR_DEFAUT: boolean;
    /** GETTER permettant d'accéder à la donnée statique proprement depuis le code HTML */
    public get modeObligatoireParDefaut() { return ContenuDeBloc.FLAG_FONCTIONNALITE_MODE_OBLIGATOIRE_PAR_DEFAUT; }

    /**
     * Clef unique définissant la donnée saisie.
     * Cette clef est utilisable dans les conditions et les autres champs.
     * (cet attribut n'est pas utile dans tous les sous-types mais presque et il est très fortement utilisé)
     */
    public clef: string | undefined;
    /**
     * Modèle du titre du contenu de bloc (peut contenir une {{variable}}).
     * Cet attribut n'est pas obligatoire. S'il est vide, aucun titre ne s'affiche.
     */
    public titre: string | undefined;
    /**
     * Titre du contenu de bloc à afficher (prise en compte des variables éventuellement présente).
     * Cet attribut n'est pas obligatoire. S'il est vide, aucun titre ne s'affiche.
     */
    public titreAafficher: string | undefined;
    /**
     * Modèle de l'aide (peut contenir une {{variable}}).
     * Cet attribut n'est pas obligatoire. S'il est vide, aucun titre ne s'affiche.
      */
    public aide: string | undefined;
    /** Aide à afficher à côté du titre si elle est renseignée (prise en compte des variables éventuellement présente). */
    public aideAafficher: string | undefined;
    /** Liste des clefs de variable auxquelles va réagir ce contenu (les clefs présentes dans le titre, l'aide ou les conditions). */
    public listeClefsDonneesPresentesDansConfiguration: string[] = [];
    /** Type du contenu */
    public type: TypeContenuDeBloc | undefined;
    /** Condition de désactivation du champs (cf. conditionVisibilite) */
    public conditionDesactivation: string | undefined;
    /** Désactivation calculée depuis la condition */
    public desactivation = false;
    /**
     * Condition d'affichage du contenu.
     * Cette attribut est optionnel et sa valeur par défaut est undefined.
     * Cette condition est une expression JS évaluée avec l'équivalent de la méthode EVAL() ayant accès aux données de la démarche uniquement.
     * Attention, toutes les données sont des chaines de caractères (même les boolean et les nombres).
     */
    public conditionVisibilite: string | undefined;
    /** Visibilité calculée depuis la condition */
    public visibilite = true;
}

/** Contenu de bloc ne contenant qu'un paragraphe de texte sur fond coloré selon le style sélectionné. */
export class ContenuParagraphe extends ContenuDeBloc {
    /**
     * Modèle de texte correspond au contenu d'un unique paragraphe (balise <p>).
     * Il peut contenir des balises HTML.
     * Il peut demander l'affichage d'une ou plusieurs des données précédemment saisie avec les caractères {{}}
     */
    public texte: string | undefined;
    /** Le texte à afficher avec les variables résolues */
    public texteAafficher: string | undefined;
    /** Liste de classes CSS séparée par un espace à appliquer au paragraphe <p> */
    public style: string | undefined;
    /** Liste de classes CSS séparée par un espace à appliquer à l'aide associée */
    public styleAide: string | undefined;
    /** Informe que le texte est inclu dans un bloc dynamique et ne s'affiche que quand ce dernier est en mode lecture seule. */
    public estIncluDansUnBlocDynamiquePourLectureSeule: boolean | undefined;
}

/** Contenu de bloc affichant les données d'un utilisateur connecté. */
export class ContenuUtilisateurConnecte extends ContenuDeBloc { }

/** Contenu de bloc affichant les documents générés à l'issue de la soumission. */
export class ContenuDocuments extends ContenuDeBloc { }

/** Attributs et règles communes à tous les contenus de bloc permettant de la saisie */
export class ContenuSaisie extends ContenuDeBloc {
    /** Liste des validations à appliquer au champ à la saisie et une fois les données envoyées au socle */
    public validationsSimples: string[] = [];
}

/** Attributs et rèles communes à tous les contenus de bloc permettant de la saisie */
export abstract class ContenuSaisieComplexe extends ContenuDeBloc {
    /** Liste des champs à afficher (sans condition autre que celles portées par le composant) */
    public champsVisibles: string[] = [];
    /** Liste des champs obligatoires */
    public validationsComplexes: { [index: string]: any } = {};
    /** Liste des aides des champs */
    public aides: { [index: string]: any } = {};
    /** Liste des titre des champs */
    public titres: { [index: string]: any } = {};
    /** Sous-contenus générés au chargement*/
    public sousContenus: ContenuDeBloc[] = [];
}

/** Case à cocher */
export class ContenuCase extends ContenuSaisie { }

/** Liste déroulante au contenu fixe et connu à l'avance */
export class ContenuOption {
    /** Libellé paramétré */
    public libelle: string | undefined;
    /** Libellé affiché */
    public libelleAafficher: string | undefined;
    /** Valeur conservée dans les données soumises */
    public valeur: string | number | boolean | undefined;
    /** Condition d'affichage du champs (cf. ContenuDeBlocconditionVisibilite) */
    public conditionVisibilite: string | undefined;
    /** Visibilité calculée depuis la condition */
    public visibilite = true;
}
export class ContenuListeFinie extends ContenuSaisie {
    /** Liste des valeurs possibles affichées par le composant */
    public valeurs: ContenuOption[] = [];
}

/** Groupe de radio bouton */
export class ContenuRadio extends ContenuListeFinie { }

/** Saisie de date */
export class ContenuDate extends ContenuSaisie { }

/** Saisie par autocompletion de donnée venant d'une API */
export class ContenuAutocompletion extends ContenuSaisie {
    /** Code de l'API après de laquelle se sourcer. */
    public api: string | undefined;
    /** Nom de l'attribut des objets en sortie de l'API à utiliser pour afficher les options à l'usager */
    public attributReponseApiPourLibelle: string | undefined;
    /** Valeur paramétrée du complément à ajouter à l'appel de l'API (peut contenir des variables entre {{}} */
    public complementAppelApi: string | undefined;
    /** Valeur calculée du complément à ajouter à l'appel de l'API (peut contenir des variables entre {{}} */
    public complementAppelApiCalcule: string | undefined;
}

/** Saisie de l'identite d'une personne physique ou morale */
export class ContenuIdentite extends ContenuSaisieComplexe { }

/** Saisie d'une adresse française ou étrangère */
export class ContenuAdresseFrOuEtr extends ContenuSaisieComplexe { }

/** Saisie de coordonnées de contact */
export class ContenuContactPersonnel extends ContenuSaisieComplexe { }

/** Récapitulatif en fin de démarche */
export class ContenuRecapitulatif extends ContenuDeBloc { }

/** Composant proposant la mise à jour des données personnelles et du porte document */
export class ContenuFinDemarche extends ContenuDeBloc { }

/** Composant proposant l'upload d'une pièce jointe */
export class ContenuUploadDocument extends ContenuSaisie {
    /** Id du document stocké */
    public idDocumentStocke: string | undefined;
}