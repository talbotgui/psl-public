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
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { ContenuDeBloc } from '../../../model/configurationdemarchecontenubloc.model';
import { Page } from '../../../model/configurationdemarchegeneral.model';
import { ContexteService } from '../../../services/stateful/contexte.service';
import { FormulaireService } from '../../../services/stateless/formulaire.service';
import { AbstractComponent } from '../../../utilitaires/abstract.component';
import { i18nPipeDirective } from '../../directives/i18nPipe';
import { FmkBlocComponent } from './bloc/fmk.bloc';
import { FmkBlocDynamiqueComponent } from './blocdynamique/fmk.blocdynamique';
import { FmkNavigationComponent } from './navigation/fmk.navigation';


@Component({
    selector: 'div[data-fmk-page]', templateUrl: './fmk.page.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FmkBlocComponent, FmkBlocDynamiqueComponent, FmkNavigationComponent, i18nPipeDirective]
})
export class FmkPageComponent extends AbstractComponent implements OnInit {

    /** Page à afficher */
    @Input()
    public page: Page | undefined;

    /** Formulaire */
    public formulaire: UntypedFormGroup | undefined;

    /** Liste des contenus de tous les blocs dynamiques.  */
    // (instance à ne jamais réinstancier car elle est utilisée dans les callBack "controle.valueChanges")
    public listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange: ContenuDeBloc[] = [];

    /** Constructeur avec injection des dépendances */
    public constructor(private contexte: ContexteService, private formulaireService: FormulaireService, private logger: NGXLogger) { super(); }

    /** A l'initialisation */
    ngOnInit(): void {
        // Création du formulaire
        this.formulaire = this.formulaireService.creerUnFormulaireVide();

        const contenusDeLaPage: ContenuDeBloc[] = [];
        if (this.page) {
            // Création de la liste complète des contenus présents dans des blocs non-dynamiques
            const listeBlocsNonDynamiques = this.page.blocs.filter(b => !b.dynamique);
            listeBlocsNonDynamiques.forEach(b => {
                contenusDeLaPage.push(...b.contenus);
                (b.sousBlocs || []).forEach(sb => contenusDeLaPage.push(...sb.contenus));
            });
        }

        // Pour chaque bloc, création des controles liés aux contenus
        const subs = this.formulaireService.creerLesControlesDunFormulaire(contenusDeLaPage, this.formulaire, this.listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange);
        this.declarerSouscriptions(subs);

        // calcul initial des visibilités des contenus
        this.formulaireService.calculerLesConditionsDeTousLesContenus(contenusDeLaPage, this.formulaire, this.contexte.langue);

        // Recalcule les titres de touts les au changement de langue
        const sub2 = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => {
            if (this.formulaire && langue) {
                this.formulaireService.calculerLesConditionsDeTousLesContenus(contenusDeLaPage, this.formulaire, langue);
            }
        });
        this.declarerSouscription(sub2);

        // log
        this.logger.trace('Fin de l\'initialisation de la page ', { page: this.page, formulaire: this.formulaire.controls });
    }

    /**
     *  Evenement généré depuis un bloc dynamique. Ce dernier envoi la liste des nouveaux contenus créés pour une nouvelle occurence dans le bloc dynamique.
     *  La liste listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange est maintenue à jour avec tous les contenus pour que tous soient recalculés à chaque évènement (visibilité, désactivation, titre, aide, ...)
     */
    public onCreationContenus(nouveauxContenus: ContenuDeBloc[]): void {
        this.listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange.push(...nouveauxContenus);
    }

    /**
     *  Evenement généré depuis un bloc dynamique. Ce dernier envoi la liste des contenus détruits à la destruction d'une cccurence du bloc dynamique.
     *  La liste listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange est maintenue à jour avec tous les contenus pour que tous soient recalculés à chaque évènement (visibilité, désactivation, titre, aide, ...)
     */
    public onDestructionContenus(contenusDetruits: ContenuDeBloc[]): void {
        // /!\ ne pas réinstancier listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange /!\
        contenusDetruits.forEach(c => {
            const index = this.listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange.indexOf(c);
            this.listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange.splice(index, 1);
        })
    }
}
