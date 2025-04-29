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
import { ContenuDeBloc, ContenuSaisie, ContenuSaisieComplexe, TypeContenuDeBloc } from '../../../framework/src/lib/model/configurationdemarchecontenubloc.model';
import { UtilitaireModel } from "../../../framework/src/lib/utilitaires/utilitaire.model";
import { FmkContenuAdresseFrOuRtrComponent } from "../../src/lib/contenu/riche/contenuadressefrouetr/fmk.contenuadressefrouetr";
import { FmkContenuContactPersonnelComponent } from "../../src/lib/contenu/riche/contenucontactpersonnel/fmk.contenucontactpersonnel";
import { FmkContenuIdentiteComponent } from "../../src/lib/contenu/riche/contenuidentite/fmk.contenuidentite";
import { ContenuMonoTestUtils } from "./contenumonotest-utils";
import { ContenuTestUtils } from "./contenutest-utils";
import { DonneesUtils } from "./donnees-utils";
import { LogUtils } from "./log-utils";

/** Classe utilitaire regroupant des actions et validations */
export class ContenuComplexeTestUtils {

    /** Méthode saisissant des données valides pour tous les champs dans un composant complexe */
    public static saisirValeurValideDunContenuComplexe(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuSaisieComplexe, brouillonDemarche: BrouillonDemarche, donneesDeBase: { [index: string]: any }, donneesDeLaPage: { [index: string]: any }, occurenceDeBlocDynamique: number | undefined): void {

        // Récupération de la liste des sous-contenus
        const listeDesContenus = ContenuComplexeTestUtils.extraireSousContenuDunContenuComplexe(contenu);
        LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Validation&saisie d\'une valeur valide du contenu complexe de type \'' + contenu.type + '\' avec les ' + listeDesContenus.length + ' sous-contenus.');

        // Parcours des sous-contenus de type 'MonoChamp'
        listeDesContenus.filter(c => UtilitaireModel.contenuEstUneSaisieMonoChamp(c)).forEach((c) => {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Validation&saisie d\'une valeur valide du sous-contenu de type \'' + c.type + '\' avec la clef ' + c.clef);

            // Calcul de la visibilité du contenu
            if (!ContenuTestUtils.verifierSiLeContenuEstActif(iPage, iBloc, iContenu, c, DonneesUtils.integrerDonnees(donneesDeBase, donneesDeLaPage))) {
                return;
            }

            // Ajout, à la clef du sous-contenu, de la clef du contenu complexe parent
            c.clef = contenu.clef + '_' + c.clef;

            // Validation du sous-contenu
            ContenuMonoTestUtils.validerLesValidationDunContenuMonoChamp(iPage, iBloc, iSousBloc, iContenu, c as ContenuSaisie, brouillonDemarche, occurenceDeBlocDynamique);

            // Recherche d'une bonne valeur
            const valeur = ContenuMonoTestUtils.definirBonneValeur(c as ContenuSaisie, brouillonDemarche);

            // Saisie de la bonne valeur
            ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, c, valeur, occurenceDeBlocDynamique);

            // Sauvegarde de la donnée saisie dans les données de la page en cours (nécessaire au calcul des conditions d'affichage et d'inactivité)
            ContenuMonoTestUtils.enregistrerLaSaisieDansLesDonneesDeLaPage(iPage, iBloc, iSousBloc, iContenu, c, valeur, brouillonDemarche, donneesDeLaPage);
        });
    }

    /** Récupère la liste des sous-contenus d'un contenu complexe */
    public static extraireSousContenuDunContenuComplexe(contenu: ContenuSaisieComplexe): ContenuDeBloc[] {
        let sousContenus: ContenuDeBloc[] = [];
        if (contenu.type === TypeContenuDeBloc.identite) {
            sousContenus = FmkContenuIdentiteComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, 'FR');
        } else if (contenu.type === TypeContenuDeBloc.adresseFrOuEtr) {
            sousContenus = FmkContenuAdresseFrOuRtrComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, 'FR');
        } else if (contenu.type === TypeContenuDeBloc.contactPersonnel) {
            sousContenus = FmkContenuContactPersonnelComponent.calculerLesChampsEtContenusPossibleDuComposant('FR');
        } else {
            LogUtils.log('** ERREUR ** Type de composant \'' + contenu.type + '\' non traité dans les tests');
        }

        // Gestion de la visibilité de ces champs
        // (/!\code dupliqué dans FmkContenuComplexeAbstraitComponent)
        const champsVisibles = (contenu as ContenuSaisieComplexe).champsVisibles;
        sousContenus.forEach(cc => {
            if (!!cc.clef && champsVisibles && champsVisibles.indexOf(cc.clef) === -1) {
                cc.conditionVisibilite = 'false';
            }
        });

        // Gestion des validations de ces champs (masque tout puis active)
        //  (/!\code dupliqué dans FmkContenuComplexeAbstraitComponent)
        const validations = (contenu as ContenuSaisieComplexe).validationsComplexes;
        if (validations) {
            Object.keys(validations).forEach(champs => {
                const contenu = sousContenus.find(c => c.clef === champs) as ContenuDeBloc;
                if (contenu) {
                    (contenu as any)['validationsSimples'] = validations[champs];
                }
            });
        }

        // Renvoi de la liste
        return sousContenus;
    }
}
