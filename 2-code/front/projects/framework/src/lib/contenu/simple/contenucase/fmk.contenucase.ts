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

import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';
import { FmkMessagesValidationComponent } from '../../messagesvalidation/fmk.messagesvalidation';

@Component({
    selector: 'div[data-fmk-contenucase]', templateUrl: './fmk.contenucase.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkMessagesValidationComponent, LibelleDirective, i18nPipeDirective]
})
export class FmkContenuCaseComponent extends FmkContenuMonoSaisieAbstraitComponent implements OnInit {

    /** Valeur initiale de la case (plus utilisée par la suite). */
    public valeurInitialeDeLattributChecked = false;

    /** A l'initialisation */
    public ngOnInit(): void {
        // Récupération de la valeur du contrôle pour forcer la coche de la case
        if (this.controle) {
            this.valeurInitialeDeLattributChecked = this.controle.value === 'true';
            this.controle.setValue(this.valeurInitialeDeLattributChecked + '');
            this.controle.markAsDirty();
        }
    }

    /**
     * Au clic sur la checkbox, on en change la valeur.
     * Ainsi les listeners sur les changements de valeur des champs fonctionnent même sur les cases à cocher.
     * Et transformation du boolean en string car toute valeur est une chaine de caractères dans la démarche.
     * Sans oublier le dirty pour que la case soit prise en compte à la soumission du formulaire.
     */
    public changerLaValeur(e: Event): void {
        if (this.controle) {
            this.controle.setValue((e.target as HTMLInputElement)?.checked + '');
            this.controle.markAsDirty();
        }
    }
}
