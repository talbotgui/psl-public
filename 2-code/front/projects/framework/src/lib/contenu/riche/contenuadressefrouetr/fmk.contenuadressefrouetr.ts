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
    selector: 'div[data-fmk-contenuadressefrouetr]', templateUrl: './fmk.contenuadressefrouetr.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [LibelleDirective, FmkContenuSimpleComponent]
})
export class FmkContenuAdresseFrOuRtrComponent extends FmkContenuComplexeAbstraitComponent {

    public obtenirLesChampsEtContenusPossibleDuComposant(contenu: ContenuDeBloc): ContenuDeBloc[] {
        return FmkContenuAdresseFrOuRtrComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, this.contexte.langue);
    }

    /** Méthode de l'instance permettant de changer les libellés au changement de langue */
    protected auChangementDeLangue(langueCourante: string): void {
        FmkContenuAdresseFrOuRtrComponent.redefinirLibelles(this.contenuComplexe.sousContenus, langueCourante);
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
                const libelle = (i18n.contenu.adresseFrOuEtr as { [clef: string]: any })[clefReduite] as { titre: LibelleI18n, aide: LibelleI18n };

                if (libelle) {
                    // Alimentatino du titre à afficher à partir de la langue courante
                    c.titreAafficher = libelle.titre.get(langueCourante);

                    // Pour chaque langue disponible, alimentation des attributs 'titreXX' et 'aideXX'
                    for (let l of i18n.contenu.adresseFrOuEtr.estFrance.titre.getListeLanguesSupportees()) {
                        const suffixe = (l === 'FR') ? '' : l;
                        (c as any)['titre' + suffixe] = libelle.titre.get(l);
                        (c as any)['aide' + suffixe] = libelle.aide.get(l);
                    }
                }
            }
        }

        // Gestion des valeurs possibles dans le radio
        const estFrance = contenus[0] as ContenuRadio;
        estFrance.valeurs[0].libelleAafficher = i18n.commun.oui.get(langueCourante);
        estFrance.valeurs[1].libelleAafficher = i18n.commun.non.get(langueCourante);
        for (let l of i18n.contenu.identite.civilite.titre.getListeLanguesSupportees()) {
            const suffixe = (l === 'FR') ? '' : l;
            (estFrance.valeurs[0] as any)['libelle' + suffixe] = i18n.commun.oui.get(l);
            (estFrance.valeurs[1] as any)['libelle' + suffixe] = i18n.commun.non.get(l);
        }
    }

    public static calculerLesChampsEtContenusPossibleDuComposant(contenu: ContenuDeBloc, langueCourante: string): ContenuDeBloc[] {
        const liste: ContenuDeBloc[] = [];

        // ne pas utiliser d'artifice pour gagner en lignes de code. Il faut que la compilation garantisse les noms des attributs
        const estFrance = new ContenuRadio();
        estFrance.clef = 'estFrance';
        estFrance.type = TypeContenuDeBloc.radio;
        estFrance.conditionDesactivation = undefined;
        estFrance.conditionVisibilite = 'true';
        estFrance.validationsSimples = [];
        estFrance.valeurs = [
            { valeur: 'true', libelle: '', libelleAafficher: '', conditionVisibilite: undefined, visibilite: true },
            { valeur: 'false', libelle: '', libelleAafficher: '', conditionVisibilite: undefined, visibilite: true }
        ];
        liste.push(estFrance);

        // Condition pour afficher/masquer les champs
        // Ce n'est pas l'opposé l'un de l'autre pour masquer tout tant qu'aucune valeur n'est sélectionnée
        const conditionAffichageFrance = contenu.clef + '_estFrance===\'true\'';
        const conditionAffichageETR = contenu.clef + '_estFrance===\'false\'';

        const etage = new ContenuSaisie();
        etage.clef = 'etage';
        etage.type = TypeContenuDeBloc.saisie;
        etage.conditionDesactivation = undefined;
        etage.conditionVisibilite = conditionAffichageFrance;
        etage.validationsSimples = [];
        liste.push(etage);

        const immeuble = new ContenuSaisie();
        immeuble.clef = 'immeuble';
        immeuble.type = TypeContenuDeBloc.saisie;
        immeuble.conditionDesactivation = undefined;
        immeuble.conditionVisibilite = conditionAffichageFrance;
        immeuble.validationsSimples = [];
        liste.push(immeuble);

        const voie = new ContenuSaisie();
        voie.clef = 'voie';
        voie.type = TypeContenuDeBloc.saisie;
        voie.conditionDesactivation = undefined;
        voie.conditionVisibilite = conditionAffichageFrance;
        voie.validationsSimples = [];
        liste.push(voie);

        const boitePostale = new ContenuSaisie();
        boitePostale.clef = 'boitePostale';
        boitePostale.type = TypeContenuDeBloc.saisie;
        boitePostale.conditionDesactivation = undefined;
        boitePostale.conditionVisibilite = conditionAffichageFrance;
        boitePostale.validationsSimples = [];
        liste.push(boitePostale);

        const communeNaissance = new ContenuAutocompletion();
        communeNaissance.clef = 'communeNaissance';
        communeNaissance.type = TypeContenuDeBloc.autocompletion;
        communeNaissance.conditionDesactivation = undefined;
        communeNaissance.conditionVisibilite = conditionAffichageFrance;
        communeNaissance.validationsSimples = [];
        communeNaissance.api = 'communeNaissance';
        communeNaissance.attributReponseApiPourLibelle = 'libelleLong';
        liste.push(communeNaissance);

        const communeActuelle = new ContenuAutocompletion();
        communeActuelle.clef = 'communeActuelle';
        communeActuelle.type = TypeContenuDeBloc.autocompletion;
        communeActuelle.conditionDesactivation = undefined;
        communeActuelle.conditionVisibilite = conditionAffichageFrance;
        communeActuelle.validationsSimples = [];
        communeActuelle.api = 'communeActuelle';
        communeActuelle.attributReponseApiPourLibelle = 'libelleLong';
        liste.push(communeActuelle);

        const adresseETR = new ContenuSaisie();
        adresseETR.clef = 'adresseETR';
        adresseETR.type = TypeContenuDeBloc.saisie;
        adresseETR.conditionDesactivation = undefined;
        adresseETR.conditionVisibilite = conditionAffichageETR;
        adresseETR.validationsSimples = [];
        liste.push(adresseETR);

        const paysETR = new ContenuAutocompletion();
        paysETR.clef = 'paysETR';
        paysETR.type = TypeContenuDeBloc.autocompletion;
        paysETR.conditionDesactivation = undefined;
        paysETR.conditionVisibilite = conditionAffichageETR;
        paysETR.validationsSimples = [];
        paysETR.api = 'paysNaissance';
        paysETR.attributReponseApiPourLibelle = 'libelle';
        liste.push(paysETR);

        // Initialisation des libellés
        FmkContenuAdresseFrOuRtrComponent.redefinirLibelles(liste, langueCourante);

        return liste;
    }

}
