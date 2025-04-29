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
import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ConfigurationDemarche } from '../../../../../framework/src/public-api';


@Component({
    selector: 'app-editeurjson', templateUrl: './app.editeurjson.html', encapsulation: ViewEncapsulation.None, styleUrls: [],
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule]
})
export class AppEditeurJsonComponent implements OnInit {

    /** String JSON contenant la configuration de la démarche */
    public json: string | undefined;

    /** Message d'erreur au formatage */
    public messageFormatage: string | undefined;

    /** Configuration à éditer */
    @Input()
    public configurationEnCoursDedition: ConfigurationDemarche | undefined;

    /** Evenement sortant pour lancer le test de la configuration saisie */
    @Output()
    public onDemandeDeTest = new EventEmitter<ConfigurationDemarche>();

    /** A l'initialisation */
    public ngOnInit(): void {
        if (this.configurationEnCoursDedition) {
            let jsonEpure = JSON.stringify(this.configurationEnCoursDedition, null, 2);
            jsonEpure = jsonEpure.replace(/[ ]*"(desactivation|visibilite)": (false|true)[,]?[\n]?/g, '');
            jsonEpure = jsonEpure.replace(/[ ]*"(titreAafficher|texteAafficher|aideAafficher)": "[^"]*"[,]?[\n]?/g, '');
            jsonEpure = jsonEpure.replace(/[ ]*"(listeClefsDonneesPresentesDansConfiguration|validations)": \[[^\]]*\][,]?[\n]?/g, '');
            jsonEpure = jsonEpure.replace(/[ ]*"exclueDuFilDariane": false[,]?[\n]?/g, '');
            jsonEpure = jsonEpure.replace(/[ ]*"[^"]*": ""[,]?[\n]?/g, '');
            jsonEpure = jsonEpure.replace(/,[ \n]*}/g, '}');
            this.json = jsonEpure;
        }
    }

    /** Formatage du JSON */
    public formaterJson(): void {
        // réinitialisation du message d'erreur
        this.messageFormatage = undefined;

        // tentative de formattage
        if (this.json) {
            try {
                this.json = JSON.stringify(JSON.parse(this.json), null, 2);
            } catch (e) {
                this.messageFormatage = 'Erreur inconue';
                if (e instanceof Error) this.messageFormatage = e.message;
            }
        }
    }

    /** Test de la configuration */
    public testerJson(): void {

        // Avant le test, on valide le contenu
        this.formaterJson();

        // Test de la configuration
        if (this.json && !this.messageFormatage) {
            this.onDemandeDeTest.emit(JSON.parse(this.json));
        }
    }
}
