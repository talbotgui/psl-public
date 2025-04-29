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
import { Component, Input, ViewEncapsulation } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { ContenuDeBloc, FormulaireService } from '../../../../../public-api';
import { FmkContenuComponent } from '../../../../contenu/fmk.contenu';
import { Bloc, SousBloc } from '../../../../model/configurationdemarchegeneral.model';
import { AbstractComponent } from '../../../../utilitaires/abstract.component';
import { LibelleDirective } from '../../../directives/libelle';


@Component({
    selector: 'div[data-fmk-bloc]', templateUrl: './fmk.bloc.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuComponent, LibelleDirective]
})
export class FmkBlocComponent extends AbstractComponent {

    /** Index du bloc dans la page. */
    @Input()
    public indexBloc = 0;

    /** Bloc à afficher */
    @Input()
    public bloc: Bloc | SousBloc | undefined;

    /** Formulaire */
    @Input()
    public formulaire: UntypedFormGroup | undefined;

    /** Liste des contenus de tous les blocs dynamiques.  */
    // (instance à ne jamais réinstancier car elle est utilisée dans les callBack "controle.valueChanges")
    @Input()
    public listeContenusDesBlocsDynamiquesPourRecalculDesConditionsAuOnChange: ContenuDeBloc[] = [];

    /** Flag d'affichage de l'aide du bloc. */
    public aideAffichee = false;

    /** Constructeur avec injection des dépendances */
    public constructor(private formulaireService: FormulaireService) { super(); }

    /** Récupère le controle présent dans le formulaire lié à un contenu */
    public recupererControleAssocieAuContenu(contenu: ContenuDeBloc | undefined): UntypedFormControl | UntypedFormGroup | undefined {
        if (this.formulaire && contenu) {
            return this.formulaireService.recupererControleAssocieAuContenu(contenu, this.formulaire);
        } else {
            return undefined;
        }
    }

    /** Retourne le nombre de contenus visible du bloc */
    public compterContenusVisibles(): number {
        if (this.bloc) {
            // Contenus visibles du bloc
            let nbContenusVisibles = this.bloc.contenus.filter(b => b.visibilite).length;

            // Ajout des contenus visibles du sous-bloc
            if (this.bloc instanceof Bloc && this.bloc.sousBlocs) {
                nbContenusVisibles += this.bloc.sousBlocs.map(sb => sb.contenus.filter(b => b.visibilite).length).reduce((a, b) => a + b, 0);
            }

            return nbContenusVisibles;
        } else {
            return 0;
        }
    }

    /** Retour l'information si le bloc est un sous-bloc. */
    public estUnSousBloc() {
        // Attention, un bloc est un sous-bloc
        return !(this.bloc instanceof Bloc);
    }

    /** Retourne la liste des sous-blocs s'il en existe */
    public extraireSousBlocSiPresent(): SousBloc[] {
        if (this.bloc instanceof Bloc) {
            return this.bloc.sousBlocs;
        } else {
            return [];
        }
    }
}
