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
import { ContenuDeBloc, ContenuParagraphe, TypeContenuDeBloc } from '../../src/lib/model/configurationdemarchecontenubloc.model';
import { ConfigurationDemarche } from '../../src/lib/model/configurationdemarchegeneral.model';
import { LogUtils } from './log-utils';

/** Classe utilitaire regroupant des actions et validations */
export class ContenuAffichageTestUtils {

    /** Validation d'un contenu d'affichage riche.*/
    public static validerContenuAffichage(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, occurenceDeBlocDynamique: number | undefined, configuration: ConfigurationDemarche | undefined): void {

        // Validation d'un contenu 'utilisateurConnecte'
        if (TypeContenuDeBloc.utilisateurConnecte === contenu.type) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('img').should('exist');
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('p').should('have.length', 3);
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Contenu de type \'' + contenu.type + '\' validé (une image et 3 balises P)');
        }

        // Validation d'un contenu 'paragraphe'
        else if (TypeContenuDeBloc.paragraphe === contenu.type) {
            const p = cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('.paragraphe').find('p');
            p.should('exist');
            p.should('be.visible');

            // Si pas de valeur dynamique dans le paragraphe, on valide aussi le contenu
            const contenuParagraphe = (contenu as ContenuParagraphe);
            if (contenuParagraphe.texte?.includes('{{') || contenuParagraphe.texte?.includes('<')) {
                LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Contenu de type \'' + contenu.type + '\' validé (1 balise P mais pas de validation du contenu car il est dynamique ou HTML)');
            } else {
                p.should('have.text', contenuParagraphe.texte);
                LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Contenu de type \'' + contenu.type + '\' validé (1 balise P avec un contenu valide)');
            }
        }

        // Validation d'un contenu 'recapitulatif'
        else if (TypeContenuDeBloc.recapitulatif === contenu.type && configuration) {
            LogUtils.log('** TODO ** valider le contenu de type recapitulatif');
        }

        // Validation d'un contenu 'finDemarche'
        else if (TypeContenuDeBloc.finDemarche === contenu.type) {
            LogUtils.log('** TODO ** valider le contenu de type finDemarche');

        }

        // Validation d'un contenu 'documents'
        else if (TypeContenuDeBloc.documents === contenu.type) {
            LogUtils.log('** TODO ** valider le contenu de type documents');
        }

        // Sinon
        else {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, '**ERREUR** Contenu de type \'' + contenu.type + '\' non validé');
        }
    }
}
