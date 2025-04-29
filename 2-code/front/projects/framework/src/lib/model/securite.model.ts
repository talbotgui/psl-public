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
 * Ce fichier contient toutes les classes MODEL liées à la sécurité.
 */

/** Structure de données renvoyée par l'API de sécurité du socle */
export class ReponseConnexion {
    /** Token de sécurité JWT. */
    public token: string | undefined;
}

/** Structure de données obtenue depuis le fournisseur d'identité */
export class DonneesUsager {
    /** Type de compte */
    public accountType: string | undefined;
    /** Civilite */
    public civilite: string | undefined;
    /** Code insee de la commune de naissance */
    public codeInseeDeNaissance: string | undefined;
    /** Code insee du pays de naissance */
    public codeInseePaysDeNaissance: string | undefined;
    /** Commune de naissance */
    public communeDeNaissance: string | undefined;
    /** Département de naissance */
    public departementDeNaissance: string | undefined;
    /** Email de contact */
    public email: string | undefined;
    /** Email du compte de connexion */
    public emailTechnique: string | undefined;
    /** Type de connexion */
    public franceConnect: string | undefined;
    /** Nom de naissance */
    public nomDeNaissance: string | undefined;
    /** Pays de naissance */
    public paysDeNaissance: string | undefined;
    /** Situation de famille */
    public situationFamiliale: string | undefined;
    /** Date de naissance */
    public dateDeNaissance: string | undefined;
    /** Nom usuel */
    public nom: string | undefined;
    /** Prénoms */
    public prenoms: string | undefined;
    /** 1 pour masculin et 2 pour féminin. */
    public sexe: string | undefined;
}

/** Structure des données ajoutées dans les données soumises */
export const CLEF_UTILISATEUR = 'utilisateur';

/** Etats de la connexion OIDC */
export enum STATUT_CONNEXION_OIDC {
    // Pas de connexion donc mode anonyme
    modeAnonyme,
    // Connexion OIDC en cours (redirection)
    connexionEnCours,
    // Connexion OIDC réussie
    connexionReussie,
    // Connexion OIDC échouée
    erreurConnexion,
    // URL incohérente avec le paramétrage de la démarche
    erreurURL
}