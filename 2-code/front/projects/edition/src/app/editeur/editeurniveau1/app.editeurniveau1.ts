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
import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelActionRow, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { ConfigurationDemarche, Page } from '../../../../../framework/src/public-api';
import { AppEditeurNiveau2Component } from '../editeurniveau2/app.editeurniveau2';


@Component({
    selector: 'app-editeurniveau1', templateUrl: './app.editeurniveau1.html', encapsulation: ViewEncapsulation.None, styleUrls: [],
    standalone: true,
    imports: [MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatFormField, MatLabel, MatInput, ReactiveFormsModule, FormsModule, MatExpansionPanelActionRow, MatButton, AppEditeurNiveau2Component]
})
export class AppEditeurNiveau1Component {

    /** Configuration à éditer */
    @Input()
    public configurationEnCoursDedition: ConfigurationDemarche | undefined;

    /** Evenement sortant pour lancer le test de la configuration saisie */
    @Output()
    public onDemandeDeTest = new EventEmitter<ConfigurationDemarche>();

    /** Ajouter un élément */
    public ajouterElementEnfant(): void {
        if (this.configurationEnCoursDedition) {
            this.configurationEnCoursDedition.pages.push(new Page());
        }
    }

    /** Test de la configuration */
    public testerJson(): void {
        this.onDemandeDeTest.emit(this.configurationEnCoursDedition);
    }
}
