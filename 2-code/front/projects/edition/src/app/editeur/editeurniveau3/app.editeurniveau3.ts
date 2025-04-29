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
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel, MatExpansionPanelActionRow, MatExpansionPanelDescription, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { NGXLogger } from 'ngx-logger';
import { AbstractComponent } from '../../../../../framework/src/lib/utilitaires/abstract.component';
import { Bloc, ConfigurationService } from '../../../../../framework/src/public-api';
import { AppEditeurNiveau4Component } from '../editeurniveau4/app.editeurniveau4';
import { AppSelecteurTypeContenuComponent } from './selecteurtypecontenu/app.selecteurtypecontenu';


@Component({
    selector: 'app-editeurniveau3', templateUrl: './app.editeurniveau3.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanelDescription, MatIconButton, MatFormField, MatLabel, MatInput, ReactiveFormsModule, FormsModule, MatSelect, MatOption, MatExpansionPanelActionRow, MatButton, AppEditeurNiveau4Component]
})
export class AppEditeurNiveau3Component extends AbstractComponent {

    @Input()
    public blocs: Bloc[] = [];

    /** Constructeur avec injection de dépendances */
    constructor(private configurationService: ConfigurationService, private logger: NGXLogger, public dialog: MatDialog) { super(); }


    /** Supprimer un élément */
    public supprimerElement(i: number): void {
        this.blocs.splice(i, 1);
    }

    /** Ajouter un élément */
    public ajouterElementEnfant(i: number): void {
        // ouverture de la popup
        const refPopup = this.dialog.open(AppSelecteurTypeContenuComponent, { width: '450px' });

        // A la fermeture de la popup, si un type est présent, on ajoute un contenu
        const sub = refPopup.afterClosed().subscribe({
            next: (type: string) => {
                if (type) {
                    this.logger.info('Création d\'un contenu de type ', type);
                    const contenu = this.configurationService.transtyperContenuDeBloc({ type }, 0, 0, 0);
                    if (contenu) {
                        this.blocs[i].contenus.push(contenu);
                    }
                }
            }
        });
        this.declarerSouscription(sub);
    }

    /** Monter un élément dans la liste */
    public monterElement(i: number): void {
        if (i > 0) {
            this.blocs.splice(i - 1, 0, this.blocs.splice(i, 1)[0]);
        }
    }

    /** Descendre un élément dans la liste */
    public descendreElement(i: number): void {
        if (i < this.blocs.length - 1) {
            this.blocs.splice(i + 1, 0, this.blocs.splice(i, 1)[0]);
        }
    }
}
