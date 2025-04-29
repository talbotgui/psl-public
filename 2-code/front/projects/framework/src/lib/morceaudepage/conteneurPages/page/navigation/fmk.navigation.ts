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
import { UntypedFormGroup } from '@angular/forms';
import { TypeContenuDeBloc } from '../../../../model/configurationdemarchecontenubloc.model';
import { Page } from '../../../../model/configurationdemarchegeneral.model';
import { ContexteService } from '../../../../services/stateful/contexte.service';
import { BrouillonService } from '../../../../services/stateless/brouillon.service';
import { AbstractComponent } from '../../../../utilitaires/abstract.component';
import { UtilitaireModel } from '../../../../utilitaires/utilitaire.model';
import { i18nPipeDirective } from '../../../directives/i18nPipe';


@Component({
    selector: 'div[data-fmk-navigation]', templateUrl: './fmk.navigation.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [i18nPipeDirective]
})
export class FmkNavigationComponent extends AbstractComponent implements OnInit {

    /** Page à afficher */
    @Input()
    public page: Page | undefined;

    /** Formulaire */
    @Input()
    public formulaire: UntypedFormGroup | undefined;

    /** Indicateur sur la fonctionnalité brouillon */
    public estBrouillonActif = false;

    /** Indicateur si la page est la première */
    public estLaPremierePage = false;

    /** Indicateur si la page est la dernière */
    public estLaDernierePage = false;

    /** Indicateur si la page est la page de soumission */
    public estLaPageDeSoumission = false;

    /** Indicateur si la page est après la soumission */
    public estApresLaPageDeSoumission = false;

    /** Constructeur pour injection des dépendances. */
    public constructor(private contexte: ContexteService, private brouillonService: BrouillonService) { super(); }

    /** Au chargement du composant */
    public ngOnInit(): void {
        this.estLaPremierePage = !!this.page && this.contexte.calculerSiPageEstPremiereAffichable(this.page);
        this.estLaDernierePage = !!this.page && this.contexte.calculerSiPageEstDerniereAffichable(this.page);
        this.estApresLaPageDeSoumission = this.contexte.soumissionRealiseeBloquantLeRetourArriere;
        this.estLaPageDeSoumission = !!this.page && !!UtilitaireModel.rechercherComposantDansLaPage(this.page, TypeContenuDeBloc.finDemarche);

        // undefined ou true est valable
        if (this.contexte.configurationDemarche && this.contexte.configurationDemarche.fonctionnalites) {
            this.estBrouillonActif = (this.contexte.configurationDemarche.fonctionnalites.brouillon === true);
        }
    }

    public sauvegarderBrouillon(): void {
        if (this.estApresLaPageDeSoumission || !this.page) {
            return;
        }
        const indexPageCourante = this.contexte.rechercherIndexPage(this.page);
        // typeOf et non (indexPageCourante) car indexPageCourante peut avoir la valeur 0 (https://developer.mozilla.org/fr/docs/Glossary/Falsy)
        if (typeof indexPageCourante !== 'undefined') {
            const sub = this.brouillonService.sauvegarderBrouillon(indexPageCourante).subscribe();
            this.declarerSouscription(sub);
        }
    }

    /** Pour revenir en arrière d'une page */
    public reculer(): void {
        // le controle des flags est fait le service de contexte
        if (this.page && this.formulaire) {
            this.contexte.reculer(this.page, this.formulaire);
        }
    }

    /** Pour valider les données et avancer d'une page */
    public avancer(): void {
        // le controle des flags est fait le service de contexte
        if (this.page && this.formulaire) {
            this.contexte.avancer(this.page, this.formulaire);
        }
    }

}
