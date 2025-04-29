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
import { ContenuSaisie, ContenuSaisieComplexe, TypeContenuDeBloc, Validation } from '../../../framework/src/lib/model/configurationdemarchecontenubloc.model';
import { ConfigurationDemarche, Page } from '../../../framework/src/lib/model/configurationdemarchegeneral.model';
import { UtilitaireModel } from '../../../framework/src/lib/utilitaires/utilitaire.model';
import { LogUtils } from './log-utils';

/** Classe utilitaire regroupant des actions et validations */
export class NavigationTestUtils {

    /** Passage à la page suivante par le bouton SUIVANT ou le bouton SOUMETTRE */
    public static passerAlaPageSuivante(page: Page): void {
        // Si la page contient le composant de soumission, clic sur SOUMETTRE MON TELEDOSSIER
        if (NavigationTestUtils.testerPageContientUnContenuDeSoumission(page)) {
            cy.get('#captchaFormulaireExtInput').type('JPJAMJGAJP');
            cy.get('button').contains('Soumettre mon télé-dossier').click();
        }
        // Sinon clic sur SUIVANT
        else {
            cy.navigationObtenirBoutonValider().click();
        }
    }

    /** Vérifie la présence d'un contenu 'finDemarche' dans la page */
    public static testerPageContientUnContenuDeSoumission(page: Page): boolean {
        return (page.blocs || []).filter(b => b.contenus && b.contenus.filter(c => c.type === TypeContenuDeBloc.finDemarche).length > 0).length > 0;
    }

    /** Validation des boutons dans la navigation */
    public static validerBoutonsDeLaPage(configurationDemarche: ConfigurationDemarche, iPage: number): void {
        LogUtils.logPage(iPage, 'Validation des boutons dans la navigation');

        // Vérification de la présence/absence du bouton précédent (précédent ne s'affiche en première et dernière page)
        if (iPage > 0 && iPage !== configurationDemarche.pages.length - 1) {
            cy.navigationObtenirBoutonPrecedent().not('[disabled]').should('exist');
        } else {
            cy.navigationObtenirBoutonPrecedent().should('not.exist');
        }

        // Vérification de la présence/absence du bouton brouillon
        if (configurationDemarche.fonctionnalites && !configurationDemarche.fonctionnalites.brouillon) {
            // la fonctionnalité est désactivée
            cy.navigationObtenirBoutonBrouillon().should('not.exist');
        } else if (iPage === configurationDemarche.pages.length - 1) {
            // en dernière page
            cy.navigationObtenirBoutonBrouillon().should('not.exist');
        } else {
            cy.navigationObtenirBoutonBrouillon().not('[disabled]').should('exist');
        }

        // Calcul du nombre de contenu "monochamp" avec une validation REQUIRED
        const nbBlocsNonDynamiquesAvecAuMoinsUnContenuSimpleObligatoires = (configurationDemarche.pages[iPage].blocs || []).filter(b =>
            !b.dynamique && b.contenus && b.contenus.filter(c => UtilitaireModel.contenuEstUneSaisieMonoChamp(c) && (c as ContenuSaisie).validationsSimples && (c as ContenuSaisie).validationsSimples.indexOf(Validation.required) !== -1).length > 0
        ).length;
        // Ajout du nombre de champs avec une validation REQUIRED dans un composant complexe
        const nbBlocsAvecAuMoinsUnContenuComplexeObligatoires = (configurationDemarche.pages[iPage].blocs || []).filter(b =>
            b.contenus && b.contenus.filter(c => UtilitaireModel.contenuEstUneSaisieComplexe(c) && (c as ContenuSaisieComplexe).validationsComplexes && Object.keys((c as ContenuSaisieComplexe).validationsComplexes).filter(k => ((c as ContenuSaisieComplexe).validationsComplexes[k] as string[]).indexOf(Validation.required) !== -1).length > 0).length > 0
        ).length;

        // Vérification de la présence/désactivation/absence du bouton suivant
        if ((nbBlocsNonDynamiquesAvecAuMoinsUnContenuSimpleObligatoires + nbBlocsAvecAuMoinsUnContenuComplexeObligatoires) > 0) {
            LogUtils.logPage(iPage, nbBlocsNonDynamiquesAvecAuMoinsUnContenuSimpleObligatoires + ' bloc(s) contenant un contenu simple et ' + nbBlocsAvecAuMoinsUnContenuComplexeObligatoires + ' bloc(s) contenant un contenu complexe avec au moins un contenu obligatoire. Donc le bouton SUIVANT est désactivé');
            // si au moins un champ est REQUIRED
            cy.navigationObtenirBoutonValider().filter('[disabled]').should('exist');
        } else if (iPage === configurationDemarche.pages.length - 1) {
            LogUtils.logPage(iPage, 'Dernière page de la configuration');
            // à la dernière page
            cy.navigationObtenirBoutonValider().should('not.exist');
        } else if (NavigationTestUtils.testerPageContientUnContenuDeSoumission(configurationDemarche.pages[iPage])) {
            LogUtils.logPage(iPage, 'Page contenant le composant de soumission');
            // avec le composant de soumission
            cy.navigationObtenirBoutonValider().should('not.exist');
        } else {
            cy.navigationObtenirBoutonValider().should('exist');
        }
    }

    /** Démarrage de la navigation */
    public static demarrerNavigation(configurationDemarche: ConfigurationDemarche, urlScenarioAdemarrer: string): void {
        LogUtils.log('');
        LogUtils.log('*******');
        LogUtils.log('Démarrage de la navigation pour le scénario ' + urlScenarioAdemarrer);

        // Accès à la démarche 
        cy.visit('generique/?codeDemarche=' + configurationDemarche.codeDemarche + urlScenarioAdemarrer);
        
        // Injection du plugin d'accessibilité
        cy.injectAxe();
    }
}

