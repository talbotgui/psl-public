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
 * Ce fichier contient toutes les classes MODEL liées aux brouillons d'une démarche.
 * 
 * Règles de conception : @see configurationdemarchecontenubloc.model.ts
 */
export class DonneesDeSoumission {

    /** Code de la démarche */
    public codeDemarche: string | undefined;

    /** Version de la configuration */
    public versionConfiguration: string | undefined;

    /** ID du brouillon chargé par l'usager */
    public idBrouillon: string | undefined;

    /** Langue de l'usager au moment de la soumission */
    public langue: string | undefined;

    /**
     * Les données sous forme de clef valeur avec uniquement des chaines de caractères.
     */
    public donnees: { [index: string]: any } = {};
}

export class BrouillonDemarche extends DonneesDeSoumission {

    /** Identifiant unique du brouillon utilisé à la sauvegarde quand l'usager veut mettre son brouillon à jour. */
    public id: string | undefined;

    /** Index de la page de la sauvegarde de l'usager */
    public indexPage: number | undefined;
}

export class DocumentsPostSoumission {
    /** Code unique du document au sein du télé-dossier. */
    public codeDocument: string | undefined;
    /** Description du type de document à destination de l'usager. */
    public libelleDocument: string | undefined;
    /** Nom du document. */
    public nomDocument: string | undefined;
    /** Ordre de présentation. */
    public ordrePresentation: number | undefined;
    /** Type de document. */
    public contentType: string | undefined;
    /** Taille en octer. */
    public taille: number | undefined;
    /** Attribut spécifique au code dans Anguglar : l'URL de téléchargement */
    public urlTelechargement: string | undefined;
}