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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatExpansionPanel, MatExpansionPanelActionRow, MatExpansionPanelDescription, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { Validation } from '../../../../../framework/src/public-api';
import { AppSaisieValeurDeListeFinieComponent } from './saisievaleurdelistefinie/app.saisievaleurdelistefinie';


@Component({
    selector: 'app-editeurniveau4', templateUrl: './app.editeurniveau4.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanelDescription, MatIconButton, MatFormField, MatLabel, MatInput, ReactiveFormsModule, FormsModule, MatSelect, MatOption, AppSaisieValeurDeListeFinieComponent, MatExpansionPanelActionRow, MatButton]
})
export class AppEditeurNiveau4Component {

    /** Liste des validations */
    public listeValidations = Object.keys(Validation).slice().map(k => k.toString());

    /** Liste des champs visibles par type de contenu */
    public listeChampsVisibles: { [index: string]: any } = {
        adresseFrOuEtr: ['estFrance', 'etage', 'immeuble', 'voie', 'boitePostale', 'commune', 'adresseETR', 'paysETR'],
        identite: ['civilite', 'nomFamille', 'nomUsage', 'prenoms', 'dateNaissance', 'paysNaissance', 'communeNaissanceFR', 'communeNaissanceETR', 'nationalite']
    };

    /** Contenus à éditer */
    @Input()
    public contenus: any[] = [];

    /** Supprimer un élément */
    public supprimerElement(i: number): void {
        this.contenus.splice(i, 1);
    }

    /** Monter un élément dans la liste */
    public monterElement(i: number): void {
        if (i > 0) {
            this.contenus.splice(i - 1, 0, this.contenus.splice(i, 1)[0]);
        }
    }

    /** Descendre un élément dans la liste */
    public descendreElement(i: number): void {
        if (i < this.contenus.length - 1) {
            this.contenus.splice(i + 1, 0, this.contenus.splice(i, 1)[0]);
        }
    }
}
