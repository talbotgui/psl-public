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
import { NavigationTestUtils } from '../../../framework/cypress/utils/navigationtest-utils';
import { BrouillonDemarche } from '../../../framework/src/lib/model/brouillonsoumissiondemarche.model';
import { ConfigurationDemarche, Page } from '../../../framework/src/lib/model/configurationdemarchegeneral.model';

/** Classe utilitaire regroupant des actions et validations pour la page personnalisée de JCC*/
export class PageJccTestUtils {

    /** Validation de la présence des champs en fonction de la configuration de la démarche */
    public static validerPresenceDesChamps(confDemarche: ConfigurationDemarche): void {
        Object.keys(confDemarche.valeursInitiales as any)
            .filter(clef => clef.startsWith('destinatairesActifs_') && confDemarche.valeursInitiales && confDemarche.valeursInitiales[clef] === true)
            .forEach(clef => {
                const codeDestinataire = clef.substring(20);
                cy.get('.checkbox.' + codeDestinataire).should('exist');
            });
    }

    /** Saisie du minimum vital dans la page */
    public static saisirPage(brouillonDemarche: BrouillonDemarche, page: Page): void {

        // On coche les cases cochées dans le brouillon
        Object.keys(brouillonDemarche.donnees)
            .filter(clef => clef.startsWith('destinatairesSelectionnees_') && brouillonDemarche.donnees[clef] === 'true')
            .forEach(clef => {
                const codeDestinataire = clef.substring(27);
                cy.get('.checkbox.' + codeDestinataire).find('input').check({ force: true }).blur();
            })

        // On saisie le numéro de sécurité social associé s'il est dans le brouillon
        const numeroSecu = brouillonDemarche.donnees['numeroSecu'];
        if (numeroSecu) {
            cy.get('.numeroSecu').find('input').clear().type(numeroSecu).blur();
        }

        // Cliquer sur SUIVANT
        NavigationTestUtils.passerAlaPageSuivante(page);
    }
}
