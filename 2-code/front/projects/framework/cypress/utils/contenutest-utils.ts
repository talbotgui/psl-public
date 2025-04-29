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
import { BrouillonDemarche } from '../../../framework/src/lib/model/brouillonsoumissiondemarche.model';
import { ContenuDeBloc, ContenuSaisie, ContenuSaisieComplexe } from '../../../framework/src/lib/model/configurationdemarchecontenubloc.model';
import { UtilitaireModel } from '../../../framework/src/lib/utilitaires/utilitaire.model';
import { ConfigurationDemarche } from '../../src/lib/model/configurationdemarchegeneral.model';
import { ContenuAffichageTestUtils } from './contenuaffichagetest-utils';
import { ContenuComplexeTestUtils } from "./contenucomplexetest-utils";
import { ContenuMonoTestUtils } from "./contenumonotest-utils";
import { DonneesUtils } from './donnees-utils';
import { LogUtils } from './log-utils';

/** Classe utilitaire regroupant des actions et validations */
export class ContenuTestUtils {

    /** Validation de la présence du contenu */
    public static validerExistence(iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, occurenceDeBlocDynamique: number | undefined): void {
        cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).should('exist');
    }

    /** Saisie d'une valeur valide dans un contenu quelque soit son type. */
    public static validerBonFonctionnementContenu(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, configuration: ConfigurationDemarche | undefined, brouillonDemarche: BrouillonDemarche, donneesDeBase: { [index: string]: any }, donneesDeLaPage: { [index: string]: any }, occurenceDeBlocDynamique: number | undefined): void {

        // Vérification des validations pour les mono-champs
        if (UtilitaireModel.contenuEstUneSaisieMonoChamp(contenu)) {
            // Validation des libellés (titre et aide)
            ContenuMonoTestUtils.validerLeLibelleEtAide(iPage, iBloc, iSousBloc, iContenu, contenu as ContenuSaisie, configuration?.fonctionnalites?.modeObligatoireParDefaut || false, occurenceDeBlocDynamique);
            // Validation des validations du contenu
            ContenuMonoTestUtils.validerLesValidationDunContenuMonoChamp(iPage, iBloc, iSousBloc, iContenu, contenu as ContenuSaisie, brouillonDemarche, occurenceDeBlocDynamique);
            // Recherche d'une bonne valeur
            const valeur = ContenuMonoTestUtils.definirBonneValeur(contenu as ContenuSaisie, brouillonDemarche);
            // Saisie de la bonne valeur
            ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu as ContenuSaisie, valeur, occurenceDeBlocDynamique);
            // Sauvegarder la donnée saisie
            ContenuMonoTestUtils.enregistrerLaSaisieDansLesDonneesDeLaPage(iPage, iBloc, iSousBloc, iContenu, contenu, valeur, brouillonDemarche, donneesDeLaPage);
        }
        // Pour les complexes
        else if (UtilitaireModel.contenuEstUneSaisieComplexe(contenu)) {
            ContenuComplexeTestUtils.saisirValeurValideDunContenuComplexe(iPage, iBloc, iSousBloc, iContenu, contenu as ContenuSaisieComplexe, brouillonDemarche, donneesDeBase, donneesDeLaPage, occurenceDeBlocDynamique);
        }
        // Pour les composants riches d'affichage
        else if (UtilitaireModel.contenuNestPasUneSaisie(contenu)) {
            ContenuAffichageTestUtils.validerContenuAffichage(iPage, iBloc, iSousBloc, iContenu, contenu as ContenuDeBloc, occurenceDeBlocDynamique, configuration);
        }
        // Pour les autres
        else {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, '/!\\ attention le type de contenu \'' + contenu.type + '\' n\'est pas géré spécifiquement dans le code de test');
            // rien à faire
        }
    }

    /** Calcul les conditions d'un contenu pour vérifier que le contenu est actif. */
    public static verifierSiLeContenuEstActif(iPage: number, iBloc: number, iContenu: number, contenu: ContenuDeBloc, toutesLesDonnees: { [index: string]: string }): boolean {

        // Calcul de la condition d'affichage du contenu
        const visibilite = DonneesUtils.calculerCondition(contenu.conditionVisibilite, toutesLesDonnees, true);
        if (!visibilite) {
            LogUtils.logContenu(iPage, iBloc, undefined, iContenu, 'Le contenu n\'est pas visible');
        }

        // Calcul de la désactivation du contenu
        const desactive = DonneesUtils.calculerCondition(contenu.conditionDesactivation, toutesLesDonnees, false);
        if (desactive) {
            LogUtils.logContenu(iPage, iBloc, undefined, iContenu, 'Le contenu n\'est pas actif');
        }

        // TRUE si le contenu est actif
        return visibilite && !desactive;
    }
}

