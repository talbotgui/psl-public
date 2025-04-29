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
import { MatButton } from '@angular/material/button';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { TypeContenuDeBloc } from '../../../../../../framework/src/public-api';

import { CdkScrollable } from '@angular/cdk/scrolling';
import { MatFormField, MatLabel } from '@angular/material/form-field';

@Component({
    selector: 'app-selecteurtypecontenu', templateUrl: './app.selecteurtypecontenu.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [MatDialogTitle, CdkScrollable, MatDialogContent, MatFormField, MatLabel, MatButton, MatDialogClose, MatDialogActions]
})
export class AppSelecteurTypeContenuComponent {

    /** Liste des types de contenu */
    public listeTypesContenu = Object.keys(TypeContenuDeBloc).slice();

    /** Type sélectionné */
    public typeSelectionne: string | undefined;

    /** Constructeur avec injection de dépendance */
    constructor(public dialogRef: MatDialogRef<AppSelecteurTypeContenuComponent>) { }

    /** Fermer sans rien faire de plus */
    public annuler(): void {
        this.dialogRef.close();
    }
}
