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
import { KeyValuePipe } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../service/admin.service';
import { ConfigurationService } from '../../service/configuration.service';
import { AbstractAppRouteDemarcheEtSoumissionComponent } from '../routesoumission/app.abstractroutedemarcheetsoumission';

@Component({
    selector: 'app-routedemarche', templateUrl: './app.routedemarche.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule, KeyValuePipe]
})
export class AppRouteDemarcheComponent extends AbstractAppRouteDemarcheEtSoumissionComponent {

    /** Constructeur avec injection des dépendances */
    public constructor(adminService: AdminService, configService: ConfigurationService, dialog: MatDialog) {
        super(adminService, configService, dialog, true);
    }

    /** A la sélection d'une version, on en affiche le contenu. */
    protected override callbackApresSelectionVersionConfiguration(): void {
        this.saisie = JSON.stringify(this.configurationEnCoursDeModification, null, 2);
    }

    /** Appel au service d'enregistrement. */
    protected realiserEnregistrement(codeDemarche: string, idVersion: string | undefined, confEnObjet: any) {
        // Enregistrement du commentaire dans l'objet
        confEnObjet['commentaireCreation'] = this.commentaire;
        const jsonAenvoyer = JSON.stringify(confEnObjet);

        // Préparation de l'appel
        let appel;
        if (idVersion) {
            appel = this.configService.modifierVersionDeConfiguration(true, codeDemarche, idVersion, jsonAenvoyer);
        } else {
            appel = this.configService.creerVersionDeConfiguration(true, codeDemarche, jsonAenvoyer);
        }

        // Appel au service
        const sub = appel.subscribe(() => {
            // Reset de l'écran comme si on sélectionnait une démarche
            this.selectionnerCodeDemarche(codeDemarche);
        });
        super.declarerSouscription(sub);
    }
}
