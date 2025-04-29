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
import { AbstractComponent } from '../../../../framework/src/lib/utilitaires/abstract.component';
import { ConfigurationDemarche, ConfigurationService, ContexteService } from '../../../../framework/src/public-api';
import { AppEditeurJsonComponent } from './editeurjson/app.editeurjson';
import { AppEditeurNiveau1Component } from './editeurniveau1/app.editeurniveau1';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';

@Component({
    selector: 'app-editeur', templateUrl: './app.editeur.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./app.editeur.scss'],
    standalone: true,
    imports: [MatRadioGroup, ReactiveFormsModule, FormsModule, MatRadioButton, AppEditeurJsonComponent, AppEditeurNiveau1Component]
})
export class AppEditeurComponent extends AbstractComponent {

    @Output()
    public onDemandeDeTest = new EventEmitter<void>();

    public modeEdition: string | undefined = 'accordeon';

    private configurationInitiale: ConfigurationDemarche | undefined;

    public configurationEnCoursDedition: ConfigurationDemarche | undefined;

    /** Constructeur avec injection des dépendances */
    public constructor(private contexte: ContexteService, private configurationService: ConfigurationService) { super(); }

    /** Implémentation de l'initialisation */
    public ngOnInit(): void {

        // Au chargement du composant,
        const sub = this.contexte.obtenirUnObservableDeChargementDeLaConfiguration().subscribe(conf => {
            if (!this.configurationInitiale) {
                // on récupère la configuration initialement chargée (avec un clone)
                this.configurationInitiale = JSON.parse(JSON.stringify(conf));
                // on lit la configuration éditée à la configuration chargée
                this.configurationEnCoursDedition = conf;
            }
        });
        this.declarerSouscription(sub);
    }

    /**Application de la configuration fournie en paramètre. */
    public testerConfiguration(conf: ConfigurationDemarche): void {
        // Sauvegarde de la configuration pour que tous les éditeurs soient synchronisé
        this.configurationEnCoursDedition = conf;
        // Appel au service
        this.configurationService.appliquerConfigurationDemarche(conf);
        // MaJ du composant parent
        this.onDemandeDeTest.emit();
    }
}
