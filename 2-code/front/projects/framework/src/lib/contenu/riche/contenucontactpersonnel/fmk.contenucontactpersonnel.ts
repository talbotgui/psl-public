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
import { ContenuDeBloc, ContenuSaisie, TypeContenuDeBloc, Validation } from '../../../model/configurationdemarchecontenubloc.model';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuComplexeAbstraitComponent } from '../../abstrait/fmk.contenucomplexeabstrait';
import { FmkContenuSimpleComponent } from '../../simple/fmk.contenusimple';

@Component({
    selector: 'div[data-fmk-contenucontactpersonnel]', templateUrl: './fmk.contenucontactpersonnel.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [LibelleDirective, FmkContenuSimpleComponent]
})
export class FmkContenuContactPersonnelComponent extends FmkContenuComplexeAbstraitComponent {

    public obtenirLesChampsEtContenusPossibleDuComposant(): ContenuDeBloc[] {
        return FmkContenuContactPersonnelComponent.calculerLesChampsEtContenusPossibleDuComposant(this.contexte.langue);
    }


    /** Méthode de l'instance permettant de changer les libellés au changement de langue */
    protected auChangementDeLangue(langueCourante: string): void {
        FmkContenuContactPersonnelComponent.redefinirLibelles(this.contenuComplexe.sousContenus, langueCourante);
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
                const libelle = (i18n.contenu.contactPersonnel as { [clef: string]: any })[clefReduite] as { titre: LibelleI18n, aide: LibelleI18n };

                // Alimentation du titre à afficher à partir de la langue courante
                c.titreAafficher = libelle.titre.get(langueCourante);

                // Pour chaque langue disponible, alimentation des attributs 'titreXX' et 'aideXX'
                for (let l of i18n.contenu.contactPersonnel.email.titre.getListeLanguesSupportees()) {
                    const suffixe = (l === 'FR') ? '' : l;
                    (c as any)['titre' + suffixe] = libelle.titre.get(l);
                    (c as any)['aide' + suffixe] = libelle.aide.get(l);
                }
            }
        }
    }

    public static calculerLesChampsEtContenusPossibleDuComposant(langue: string): ContenuDeBloc[] {
        const liste: ContenuDeBloc[] = [];

        const email = new ContenuSaisie();
        email.clef = 'email';
        email.type = TypeContenuDeBloc.saisie;
        email.conditionDesactivation = undefined;
        email.validationsSimples = [Validation.email];
        liste.push(email);

        const numTeleGeneral = new ContenuSaisie();
        numTeleGeneral.clef = 'numTeleGeneral';
        numTeleGeneral.type = TypeContenuDeBloc.saisie;
        numTeleGeneral.conditionDesactivation = undefined;
        numTeleGeneral.validationsSimples = [Validation.telephoneFR];
        liste.push(numTeleGeneral);

        // Initialisation des libellés
        FmkContenuContactPersonnelComponent.redefinirLibelles(liste, langue);

        return liste;
    }

}
