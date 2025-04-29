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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatExpansionPanel, MatExpansionPanelActionRow, MatExpansionPanelDescription, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { Bloc, Page } from '../../../../../framework/src/public-api';
import { AppEditeurNiveau3Component } from '../editeurniveau3/app.editeurniveau3';


@Component({
    selector: 'app-editeurniveau2', templateUrl: './app.editeurniveau2.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./app.editeurniveau2.scss'],
    standalone: true,
    imports: [MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanelDescription, MatIconButton, MatFormField, MatLabel, MatInput, ReactiveFormsModule, FormsModule, MatRadioGroup, MatRadioButton, MatExpansionPanelActionRow, MatButton, AppEditeurNiveau3Component]
})
export class AppEditeurNiveau2Component implements OnInit {

    @Input()
    public pages: Page[] = [];

    /** Statut des mat-expansion. */
    public statuts: boolean[] = [];

    /** A l'initialisation */
    public ngOnInit(): void {
        this.statuts = this.pages.map(() => false);
    }

    /** Supprimer un élément */
    public supprimerElement(i: number): void {
        this.pages.splice(i, 1);
    }

    /** Ajouter un élément */
    public ajouterElementEnfant(i: number): void {
        this.pages[i].blocs.push(new Bloc());
    }

    /** Monter un élément dans la liste */
    public monterElement(i: number): void {
        if (i > 0) {
            this.pages.splice(i - 1, 0, this.pages.splice(i, 1)[0]);
        }
    }

    /** Descendre un élément dans la liste */
    public descendreElement(i: number): void {
        if (i < this.pages.length - 1) {
            this.pages.splice(i + 1, 0, this.pages.splice(i, 1)[0]);
        }
    }
}
