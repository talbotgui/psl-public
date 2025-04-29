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
import { Directive, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { FmkContenuAbstraitComponent } from './fmk.contenuabstrait';

/**
 * Cette classe fournit les comportements de base de tous les contenus avec un unique champ de saisie.
 */
// @Directive pour pouvoir utilisé les annotations Angular dans cette classe
@Directive()
export class FmkContenuMonoSaisieAbstraitComponent extends FmkContenuAbstraitComponent {

    /**
     * Controle associé au champ de saisie.
     * Pour que le contrôle soit associé à un FormGroup, il doit être initialié au niveau de la page (cf. https://angular.io/guide/reactive-forms#grouping-form-controls).
     */
    @Input()
    public controle: UntypedFormControl | undefined;

    /** Calcul de la validation d'un champ */
    public get saisieValide(): boolean {
        return !this.controle || (!this.controle.invalid && (this.controle.dirty || this.controle.touched));
    }

    /** Calcul de la validation d'un champ */
    public get saisieInvalide(): boolean {
        return !this.controle || (this.controle.invalid && (this.controle.dirty || this.controle.touched));
    }

}
