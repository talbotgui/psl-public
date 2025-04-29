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
 * Règles de conception : @see configurationdemarchecontenubloc.model.ts
 * 
 * Toute modification ici doit être reportée dans le code Java du projet "socle-configuration"
 */
import { ContenuDeBloc } from './configurationdemarchecontenubloc.model';

export class ConfigurationDemarche {

    /** Code de la démarche */
    public codeDemarche: string | undefined;

    /** Version de la configuration */
    public versionConfiguration: string | undefined;

    /** Titre de la démarche */
    public titre: string | undefined;
    /** Titre à afficher */
    public titreAafficher: string | undefined;

    /** 
     * Les points d'entrée possibles.
     * Si cette liste est vide, l'accès à la démarche est simple (sans condition particulière dès la première page) et anonyme.
     * Si un élément est présent, l'usager ne peut accéder à la démarche sans répondre aux critères de l'un des points d'entrée.
     */
    public pointsEntree: PointEntree[] = [];

    /** Les pièces jointes associées à cette démarche. */
    public piecesJointesAssociees: PieceJointeAssociee[] = [];

    /** Valeurs définies dès l'arrivée dans la démarche. */
    public valeursInitiales: { [index: string]: any } | undefined;

    /**
     * La liste ordonnées des pages que la démarche affiche.
     * Cette liste ne peut pas être vide.
     */
    public pages: Page[] = [];

    /** Liste des fonctionnalités spécifiques actives/inactives (si undefined, la fonctionnalité est active par défaut) */
    public fonctionnalites: ListeFonctionnalites | undefined;
}

export enum AuthentificationPossibles {
    FranceConnect = 'FranceConnect',
    RepriseBrouillonAnonyme = 'RepriseBrouillonAnonyme'
}


/**
 * Pour des paramètres (nom et valeur), une authentification est paramétrée.
 */
export class PointEntree {
    /** Paramètres d'URL à récupérer pour ce point d'entrée */
    public parametres: ParametrePointEntree[] | undefined;
    /** Type d'authentification associée */
    public authentification: AuthentificationPossibles | undefined;
}

/**
 * Description d'un paramètre et une valeur de paramètre.
 */
export class ParametrePointEntree {
    /** Paramètre d'URL à récupérer pour ce point d'entrée */
    public parametre: string | undefined;
    /** Valeurs possibles du paramètre */
    public valeurs: string[] = [];
}


/** Les contraintes associées à une pièce jointe de la démarche */
export class PieceJointeAssociee {
    /** Le code de la pièce jointe */
    public codePieceJointe: string | undefined;
    /** La taille maximale du document (en Mo) */
    public tailleMaximaleAutorisee: number | undefined;
    /** Les types MIME autorisés */
    public typesDeContenuAutorises: string[] = [];
}

export class Page {
    /** GETTER permettant d'accéder à la donnée statique proprement depuis le code HTML */
    public get modeObligatoireParDefaut() { return ContenuDeBloc.FLAG_FONCTIONNALITE_MODE_OBLIGATOIRE_PAR_DEFAUT; }

    /** Le titre de la page est affiché en haut de la page quand elle est affichée */
    public titre: string | undefined;
    /** Titre à afficher */
    public titreAafficher: string | undefined;

    /**
     * Le titre du fil d'Ariane peut être différent de celui de la page.
     * Ce titre peut être vide. L'attribut "titre" est alors utilisé.
     */
    public titreAriane: string | undefined;
    /** Titre à afficher */
    public titreArianeAafficher: string | undefined;

    /**
     * Flag définissant si la page doit être visible dans le fil d'Ariane.
     * Si deux pages consécutives ont le même titreAriane, l'une des deux devrait être exclueDuFilDariane:true pour éviter un doublon dans le fil d'Ariane.
     */
    public exclueDuFilDariane: boolean | undefined;
    /**
     * Flag indiquant que la page ne doit pas être générée par le framework.
     * Le code de la démarche se charge de fournir cette page (cf. fmk.conteneurpages.html)
     * et de gérer son affichage en surveillant l'index de la page courante
     * (disponible à travers ContexteService.obtenirUnObservableDeChangementDePage).
     * 
     * Néanmoins, même si l'affichage et toutes les mécaniques sont spécifiques à la démarche, il faut paramétrer, dans la configuration de la page,
     * une liste de contenus correspondants aux données saissables. Cette liste est utilisée par le composant de récapitulatif ainsi que par la validation des données dans le socle (ordre des contenus, clef et type)
     */
    public specifiqueAlaDemarche = false;
    /**
     * Condition d'affichage de la page.
     * Cette attribut est optionnel et sa valeur par défaut est undefined.
     * Cette condition est une expression JS évaluée avec l'équivalent de la méthode EVAL() ayant accès aux données de la démarche uniquement.
     * Attention, toutes les données sont des chaines de caractères (même les boolean et les nombres).
     * Toutes les pages affichables (selon la condition d'affichage) sont présentes dans le fil d'Ariane sauf les pages marquées "exclueDuFilDariane:true".
     */
    public conditionVisibilite: string | undefined;
    /** Visibilité calculée depuis la condition */
    public visibilite = true;
    /**
     * Liste ordonnée des bloc à afficher dans la page.
     * Cette liste ne peut être vide.
     */
    public blocs: Bloc[] = [];
}

/** Un bloc est un ensemble de contenu (champs, textes) contenant un titre et séparés par un trait horizontal */
export class SousBloc {
    /**
     * Titre du bloc.
     * Cet attribut n'est pas obligatoire. S'il est vide, aucun titre ne s'affiche.
     */
    public titre: string | undefined;
    /** Titre à afficher */
    public titreAafficher: string | undefined;
    /** Tooltip affiché sur le bouton à côté du titre (si renseigné). */
    public aideTitre: string | undefined;
    /** Tooltip à afficher */
    public aideTitreAafficher: string | undefined;
    /** Contenu du cadre d'aide (si renseigné) */
    public aide: string | undefined;
    /** Contenu du cadre d'aide (si renseigné) */
    public aideAafficher: string | undefined;
    /**
     * Condition d'affichage du bloc.
     * Cette condition s'ajoute automatiquement à tous les contenus du bloc et s'applique alors la règle "un bloc sans contenu visible n'est pas affiché".
     */
    public conditionVisibilite: string | undefined;
    /** Liste ordonnée des éléments dans le bloc */
    public contenus: ContenuDeBloc[] = [];
    /** BLOC DYNAMIQUE : activation */
    public dynamique: boolean | undefined = false;
    /** BLOC DYNAMIQUE : nombre maximum d'itération (valeur par défaut à maintenir en cohérence avec BlocDto.java) */
    public maxOccurences: number | undefined = 10;
}

/** 
 * Un bloc est un ensemble de contenu (champs, textes) contenant un titre et séparés par un trait horizontal.
 * 
 * Un bloc de premier niveau peut contenir des sous-blocs (BlocSecondNiveau). Les sous-blocs sont affichés sous les contenus.
 * 
 * Un bloc dynamique ne peut pas contenir de sous-blocs.
 */
export class Bloc extends SousBloc {
    /**
     * Liste ordonnée des sousbloc à afficher dans le bloc (pas plus d'un niveau).
     * Cette liste peut être vide.
     */
    public sousBlocs: SousBloc[] = [];
}

/**
 *  La liste des fonctionnalités activables/désactivables ou paramétrables d'une démarche.
 *  /!\ Attention à bien penser à modifier le code Java en conséquence aussi.
 */
export class ListeFonctionnalites {
    /** Activation ou non du captcha en fin de démarche. */
    public captcha = true;
    /** Activation ou non de la sauvegarde d'un brouillon */
    public brouillon = true;
    /** Mode d'affichage des astérisques rouges ou des mentions "optionnel" */
    public modeObligatoireParDefaut = false;
    /** Mode "deuil nationnal" */
    public deuil = false;
}