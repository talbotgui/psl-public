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
import { Component, EventEmitter, Output, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { ContenuOption } from '../../../../../../framework/src/public-api';

@Component({
    selector: 'app-saisievaleurdelistefinie', templateUrl: './app.saisievaleurdelistefinie.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./app.saisievaleurdelistefinie.scss'],
    standalone: true,
    imports: [MatFormField, MatLabel, MatInput, ReactiveFormsModule, FormsModule, MatIconButton]
})
export class AppSaisieValeurDeListeFinieComponent {

    /** Valeur à saisir */
    public valeur = '';

    /** Libelle à saisir */
    public libelle = '';

    /** Evenement de notification au parent */
    @Output()
    public onValidation = new EventEmitter<ContenuOption>();

    /** Validation du formulaire */
    public valider(): void {
        if (this.valeur !== '' && this.libelle !== '') {
            const option = new ContenuOption();
            option.libelle = this.libelle;
            option.valeur = this.valeur;
            this.onValidation.next(option);
        }
    }
}

