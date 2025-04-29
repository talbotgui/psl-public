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
import { Bloc, SousBloc } from "../../../framework/src/lib/model/configurationdemarchegeneral.model";
import { DonneesUtils } from "./donnees-utils";
import { LogUtils } from "./log-utils";

/** Classe utilitaire regroupant des actions et validations */
export class BlocTestUtils {

    /** Vérification que le bloc est actif/visible */
    public static verifierSiLeBlocEstVisible(iPage: number, iBloc: number, iSousBloc: number | undefined, bloc: SousBloc, toutesLesDonnees: { [index: string]: string; }) {

        // Calcul de la condition d'affichage du bloc
        const visibilite = DonneesUtils.calculerCondition(bloc.conditionVisibilite, toutesLesDonnees, true);
        if (!visibilite) {
            LogUtils.logBloc(iPage, iBloc, 'Le bloc n\'est pas visible de par sa condition');
            return false;
        }

        // Un bloc avec au moins un contenu sans condition de visibilité est donc visible
        let nbContenusSansConditionVisibilite = bloc.contenus.filter(contenu => !contenu.conditionVisibilite).length;
        if (iSousBloc === undefined) {
            ((bloc as Bloc).sousBlocs || []).forEach(sb => nbContenusSansConditionVisibilite += sb.contenus.filter(contenu => !contenu.conditionVisibilite).length);
        }
        if (nbContenusSansConditionVisibilite > 0) {
            LogUtils.logBloc(iPage, iBloc, 'Le bloc est visible');
            return true;
        }
        // sans contenu visible, le bloc n'est lui-même pas visible
        else {
            LogUtils.logBloc(iPage, iBloc, 'Calcul de conditions avec la valeur par défaut "true" et les données suivantes :' + JSON.stringify(toutesLesDonnees));
            let nbContenuVisible = bloc.contenus
                .filter(contenu => {
                    const resultat = DonneesUtils.calculerCondition(contenu.conditionVisibilite, toutesLesDonnees, true);
                    LogUtils.logBloc(iPage, iBloc, 'Condition="' + contenu.conditionVisibilite + '" et résultat="' + resultat + '"');
                    return resultat;
                })
                .length;
            if (iSousBloc === undefined) {
                ((bloc as Bloc).sousBlocs || []).forEach(sb => nbContenuVisible += sb.contenus
                    .filter(contenu => DonneesUtils.calculerCondition(contenu.conditionVisibilite, toutesLesDonnees, true))
                    .length);
            }
            LogUtils.logBloc(iPage, iBloc, 'Le bloc contient ' + nbContenuVisible + ' contenus visibles (bloc invisible si 0 contenu visible)');
            return nbContenuVisible;
        }
    }

    /** Validation d'un bloc */
    public static validerBloc(iPage: number, iBloc: number, iSousBloc: number | undefined, bloc: SousBloc): void {

        // Vérification du titre
        if (bloc.titre) {
            cy.blocObtenirTitre(iPage, iBloc, iSousBloc).should('have.text', bloc.titre);
        }
    }
}
