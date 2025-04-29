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
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { ConfigurationDemarche } from '../../../model/configurationdemarchegeneral.model';
import { ContexteService } from '../../../services/stateful/contexte.service';
import { AbstractComponent } from '../../../utilitaires/abstract.component';
import { i18nPipeDirective } from '../../directives/i18nPipe';


@Component({
    selector: 'div[data-fmk-fildariane]', templateUrl: './fmk.fildariane.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [i18nPipeDirective]
})
export class FmkFilDarianeComponent extends AbstractComponent implements OnInit {

    /** Index de la page courante utilisé dans la page HTML */
    public indexPageCourante = 0;

    /**
     * Liste des indexes visibles des pages.
     * Si 4 pages existent mais 1 n'est pas visible dans le fil d'Ariane, on obtient [1, 2, 2, 3]
     */
    public indexVisibleDesPages: (number)[] = [];

    /** Plus grand index affiché. */
    public indexVisibleMaximal = 0;

    /** Configuration de la démarche fournie en entrée */
    @Input()
    public configurationDemarche: ConfigurationDemarche | undefined;

    /** Indicateur si la page est après la soumission */
    private estApresLaPageDeSoumission = false;

    /** Constructeur pour injection des dépendances. */
    public constructor(private contexte: ContexteService) { super(); }

    /** Au chargement du fil d'Ariane */
    public ngOnInit(): void {
        // A chaque changement de page
        const sub1 = this.contexte.obtenirUnObservableDeChangementDePage().subscribe(i => {
            // Pour être prévenu d'un changement de page
            this.indexPageCourante = i;
            // Pour remettre l'utilisateur en haut de la page
            window.scrollTo(0, 100);
            // Pour savoir si la soumission est faite
            this.estApresLaPageDeSoumission = this.contexte.soumissionRealiseeBloquantLeRetourArriere;
        });
        this.declarerSouscription(sub1);

        // Recalcule de l'index visible des pages au changement de configuration
        const sub2 = this.contexte.obtenirUnObservableDeChargementDeLaConfiguration().subscribe(() => {
            if (this.contexte.configurationDemarche) {
                this.indexVisibleDesPages = this.contexte.configurationDemarche.pages.map((p, i) => this.calculerIndexVisibleDeLaPage(i));
                this.indexVisibleMaximal = this.indexVisibleDesPages[this.indexVisibleDesPages.length - 1];
            }
        });
        this.declarerSouscription(sub2);
    }

    /** Modification de la page courante */
    public changerDePage(numeroPage: number): void {
        // Pas de changement de page autre que vers l'arrière
        // et pas après la soumission
        if (this.indexPageCourante != undefined && numeroPage < this.indexPageCourante && !this.estApresLaPageDeSoumission) {
            this.contexte.changerDePage(numeroPage);
        }
    }

    /**
     * Méthode calculant l'index d'une page à afficher dans le fil d'Ariane.
     */
    private calculerIndexVisibleDeLaPage(indexPage: number): number {
        // IndexVisible = indexPage + 1 (car TS compte à partir de 0) - nbPageNonVisibleAvantCetIndex
        if (this.configurationDemarche) {
            const nbPageNonVisibleAvantCetIndex = this.configurationDemarche.pages.filter((p, i) => (p.exclueDuFilDariane || !p.visibilite) && i <= indexPage).length;
            return indexPage + 1 - nbPageNonVisibleAvantCetIndex;
        } else {
            return -1;
        }
    }
}
