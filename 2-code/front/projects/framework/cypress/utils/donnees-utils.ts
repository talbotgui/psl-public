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
// Depuis les tests CYPRESS, les imports doivent se faire directement sur le fichier et non sur le 'public-api'

import { ES6Parser, ES6StaticEval } from "espression";

/** Classe utilitaire de manipulation des données */
export class DonneesUtils {

    /** Analyseur d'expression conditionnelle */
    private static parser = new ES6Parser();


    /** Evaluateur d'expression conditionnelle */
    private static staticEval = new ES6StaticEval();

    /** Calcul d'une condition d'affichage à partir des données stockées dans le service plus les données fournies en plus */
    public static calculerCondition(conditionVisibilite: string | undefined, toutesLesDonnees: { [index: string]: string }, valeurParDefaut: boolean): boolean {

        if (conditionVisibilite === undefined || conditionVisibilite === '' || conditionVisibilite === null) {
            return valeurParDefaut;
        } else if (conditionVisibilite === 'true') {
            return true;
        } else if (conditionVisibilite === 'false') {
            return false;
        } else {
            try {
                // Parse de l'expression
                const expression = this.parser.parse(conditionVisibilite);

                // Calcul
                return this.staticEval.evaluate(expression, toutesLesDonnees);
            } catch (erreur) {
                return valeurParDefaut;
            }
        }
    }

    /** Intégration des données de la page dans les données de base */
    public static integrerDonnees(donneesDeBase: { [index: string]: any }, donneesDeLaPage: { [index: string]: any }): { [index: string]: any } {
        const toutesLesDonnees = { ...donneesDeBase };
        Object.keys(donneesDeLaPage).forEach(clef => {
            if (donneesDeLaPage[clef] === undefined || donneesDeLaPage[clef] === null) {
                // rien à faire
            }
            else if (typeof donneesDeLaPage[clef] === 'string' || donneesDeLaPage[clef] instanceof Date) {
                toutesLesDonnees[clef] = donneesDeLaPage[clef];
            } else {
                Object.keys(donneesDeLaPage[clef]).forEach(k => toutesLesDonnees[k] = donneesDeLaPage[clef][k]);
            }
        });

        return toutesLesDonnees;
    }
}
