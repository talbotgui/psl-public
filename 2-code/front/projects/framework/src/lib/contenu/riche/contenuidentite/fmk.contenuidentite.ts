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

import { Component, ViewEncapsulation } from '@angular/core';
import { LibelleI18n, i18n } from '../../../conf/i18n';
import { ContenuAutocompletion, ContenuDeBloc, ContenuRadio, ContenuSaisie, TypeContenuDeBloc } from '../../../model/configurationdemarchecontenubloc.model';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuComplexeAbstraitComponent } from '../../abstrait/fmk.contenucomplexeabstrait';
import { FmkContenuSimpleComponent } from '../../simple/fmk.contenusimple';

@Component({
    selector: 'div[data-fmk-contenuidentite]', templateUrl: './fmk.contenuidentite.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [LibelleDirective, FmkContenuSimpleComponent]
})
export class FmkContenuIdentiteComponent extends FmkContenuComplexeAbstraitComponent {


    public obtenirLesChampsEtContenusPossibleDuComposant(contenu: ContenuDeBloc): ContenuDeBloc[] {
        return FmkContenuIdentiteComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, this.contexte.langue);
    }

    /** Méthode de l'instance permettant de changer les libellés au changement de langue */
    protected auChangementDeLangue(langueCourante: string): void {
        FmkContenuIdentiteComponent.redefinirLibelles(this.contenuComplexe.sousContenus, langueCourante);
    }

    /** Méthode de définition des libellés. */
    private static redefinirLibelles(contenus: ContenuDeBloc[], langueCourante: string): void {

        // Pour chaque contenu ayant une clef
        for (let c of contenus) {
            if (c.clef) {
                // en cas de changement de langue, la clef du contenu est préfixée par l'id du composant
                let clefReduite = c.clef;
                if (clefReduite.includes('_')) {
                    clefReduite = clefReduite.substring(clefReduite.lastIndexOf('_') + 1);
                }

                // Recherche du libellé dans i18n
                const libelle = (i18n.contenu.identite as { [clef: string]: any })[clefReduite] as { titre: LibelleI18n, aide: LibelleI18n };

                // Alimentation du titre à afficher à partir de la langue courante
                c.titreAafficher = libelle.titre.get(langueCourante);
                c.aideAafficher = libelle.aide.get(langueCourante);

                // Pour chaque langue disponible, alimentation des attributs 'titreXX' et 'aideXX'
                for (let l of i18n.contenu.identite.civilite.titre.getListeLanguesSupportees()) {
                    const suffixe = (l === 'FR') ? '' : l;
                    (c as any)['titre' + suffixe] = libelle.titre.get(l);
                    (c as any)['aide' + suffixe] = libelle.aide.get(l);
                }
            }
        }

        // Gestion des valeurs possibles dans le radio
        const civilite = contenus[0] as ContenuRadio;
        civilite.valeurs[0].libelleAafficher = i18n.contenu.identite.monsieur.get(langueCourante);
        civilite.valeurs[1].libelleAafficher = i18n.contenu.identite.madame.get(langueCourante);
        for (let l of i18n.contenu.identite.civilite.titre.getListeLanguesSupportees()) {
            const suffixe = (l === 'FR') ? '' : l;
            (civilite.valeurs[0] as any)['libelle' + suffixe] = i18n.contenu.identite.monsieur.get(l);
            (civilite.valeurs[1] as any)['libelle' + suffixe] = i18n.contenu.identite.madame.get(l);
        }
    }

    public static calculerLesChampsEtContenusPossibleDuComposant(contenu: ContenuDeBloc, langueCourante: string): ContenuDeBloc[] {
        const liste: ContenuDeBloc[] = [];

        // ne pas utiliser d'artifice pour gagner en lignes de code. Il faut que la compilation garantisse les noms des attributs
        const civilite = new ContenuRadio();
        civilite.clef = 'civilite';
        civilite.type = TypeContenuDeBloc.radio;
        civilite.conditionDesactivation = undefined;
        civilite.conditionVisibilite = 'true';
        civilite.validationsSimples = [];
        civilite.valeurs = [
            { valeur: 'M', libelle: '', libelleAafficher: '', conditionVisibilite: undefined, visibilite: true },
            { valeur: 'MME', libelle: '', libelleAafficher: '', conditionVisibilite: undefined, visibilite: true }
        ];
        liste.push(civilite);

        const nomFamille = new ContenuSaisie();
        nomFamille.clef = 'nomFamille';
        nomFamille.type = TypeContenuDeBloc.saisie;
        nomFamille.conditionDesactivation = undefined;
        nomFamille.conditionVisibilite = 'true';
        nomFamille.validationsSimples = [];
        liste.push(nomFamille);

        const nomUsage = new ContenuSaisie();
        nomUsage.clef = 'nomUsage';
        nomUsage.type = TypeContenuDeBloc.saisie;
        nomUsage.conditionDesactivation = undefined;
        nomUsage.conditionVisibilite = 'true';
        nomUsage.validationsSimples = [];
        liste.push(nomUsage);

        const prenoms = new ContenuSaisie();
        prenoms.clef = 'prenoms';
        prenoms.type = TypeContenuDeBloc.saisie;
        prenoms.conditionDesactivation = undefined;
        prenoms.conditionVisibilite = 'true';
        prenoms.validationsSimples = [];
        liste.push(prenoms);

        const dateNaissance = new ContenuSaisie();
        dateNaissance.clef = 'dateNaissance';
        dateNaissance.type = TypeContenuDeBloc.date;
        dateNaissance.conditionDesactivation = undefined;
        dateNaissance.conditionVisibilite = 'true';
        dateNaissance.validationsSimples = [];
        liste.push(dateNaissance);

        const paysNaissance = new ContenuAutocompletion();
        paysNaissance.clef = 'paysNaissance';
        paysNaissance.type = TypeContenuDeBloc.autocompletion;
        paysNaissance.conditionDesactivation = undefined;
        paysNaissance.conditionVisibilite = 'true';
        paysNaissance.validationsSimples = [];
        paysNaissance.api = 'paysNaissance';
        paysNaissance.attributReponseApiPourLibelle = 'libelle';
        liste.push(paysNaissance);

        // Définition de la condition d'affichage de la bonne saisie de commune (France ou pas)
        const conditionVisibiliteCommuneFrance = '((' + contenu.clef + '_paysNaissance_libelle || \'\').toUpperCase() === \'FRANCE\')';

        // Spécificité de ce composant sur l'affichage de la bonne commune
        const communeNaissanceFR = new ContenuAutocompletion();
        communeNaissanceFR.clef = 'communeNaissanceFR';
        communeNaissanceFR.type = TypeContenuDeBloc.autocompletion;
        communeNaissanceFR.conditionDesactivation = undefined;
        communeNaissanceFR.conditionVisibilite = conditionVisibiliteCommuneFrance;
        communeNaissanceFR.validationsSimples = [];
        communeNaissanceFR.api = 'communeNaissance';
        communeNaissanceFR.attributReponseApiPourLibelle = 'libelleLong';
        liste.push(communeNaissanceFR);

        const communeNaissanceETR = new ContenuSaisie();
        communeNaissanceETR.clef = 'communeNaissanceETR';
        communeNaissanceETR.type = TypeContenuDeBloc.saisie;
        communeNaissanceETR.conditionDesactivation = undefined;
        communeNaissanceETR.conditionVisibilite = '!(' + conditionVisibiliteCommuneFrance + ')';
        communeNaissanceETR.validationsSimples = [];
        liste.push(communeNaissanceETR);

        const nationalite = new ContenuAutocompletion();
        nationalite.clef = 'nationalite';
        nationalite.type = TypeContenuDeBloc.autocompletion;
        nationalite.conditionDesactivation = undefined;
        nationalite.conditionVisibilite = 'true';
        nationalite.validationsSimples = [];
        nationalite.api = 'nationalitePaysActuel';
        nationalite.attributReponseApiPourLibelle = 'nationalite';
        liste.push(nationalite);

        // Initialisation des libellés
        FmkContenuIdentiteComponent.redefinirLibelles(liste, langueCourante);

        return liste;
    }

}
