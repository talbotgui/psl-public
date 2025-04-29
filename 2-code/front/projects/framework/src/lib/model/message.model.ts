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
 * Ce fichier contient toutes les classes MODEL liées à l'affichage de message en entête de page.
 */

/** Liste des types de message affichable */
export enum TypeMessageAafficher {
    Erreur, Avertissement, Information
}

/** Structure d'un message à afficher */
export class MessageAafficher {
    /** Constructeur */
    public constructor(
        /** ID de l'emetteur (pour supprimer le message ensuite) */
        public codeEmetteurDuMessage: string = '',
        /** Type de message */
        public type: TypeMessageAafficher,
        /** Contenu du message */
        public message: string) {
    }

    /** Vérification du niveau du message : Information */
    public get isInformation(): boolean { return this.type === TypeMessageAafficher.Information; }
    /** Vérification du niveau du message : Avertissement */
    public get isAvertissement(): boolean { return this.type === TypeMessageAafficher.Avertissement; }
    /** Vérification du niveau du message : Erreur */
    public get isErreur(): boolean { return this.type === TypeMessageAafficher.Erreur; }
}

