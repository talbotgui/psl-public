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
import { Page } from '../../../framework/src/lib/model/configurationdemarchegeneral.model';
import { LogUtils } from './log-utils';

/** Classe utilitaire regroupant des actions et validations */
export class FilArianeTestUtils {

    /** Validation du fil d'Ariane en page iPage */
    public static validerFilDunePage(page: Page, iPage: number, nbPagesInvisiblesDansAriane: number, modeObligatoireParDefaut: boolean): number {
        LogUtils.logPage(iPage, 'Validation du fil d\'Ariane et de la mention facultative/obligatoire');

        // Si la page est exclue du fil d'Ariane, on incrémente le compteur (renvoyé en sortie de la méthode)
        if (page.exclueDuFilDariane) {
            nbPagesInvisiblesDansAriane++
        }

        // Vérification du numéro vis-à-vis de l'index de la page et du nombre de pages non visibles dans le fil d'Ariane
        // si la page n'est pas la première avec l'exclusion du fil d'Ariane
        if (!(iPage === 0 && page.exclueDuFilDariane)) {
            cy.filarianeObtenirNumero().should('contain.text', 'Étape ' + (1 + iPage - nbPagesInvisiblesDansAriane) + ' sur ');
        }

        // Vérification du titre de la page
        cy.pageObtenirTitre().should('have.text', page.titre);

        if (modeObligatoireParDefaut) {
            cy.get('.main').find('p.mentionObligatoireParDefaut').should('be.visible');
        } else {
            cy.get('.main').find('p.mentionPasObligatoireParDefaut').should('be.visible');
        }

        // Renvoi du nombre de pages non visibles
        return nbPagesInvisiblesDansAriane;
    }
}
