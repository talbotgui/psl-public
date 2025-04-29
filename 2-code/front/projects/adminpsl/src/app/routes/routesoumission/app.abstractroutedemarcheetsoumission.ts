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
import { Directive, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { AbstractComponent } from '../../../../../framework/src/lib/utilitaires/abstract.component';
import { ConfirmationDialog } from '../../composants/confirmation/app.confirmation';
import { AdminService } from '../../service/admin.service';
import { ConfigurationService } from '../../service/configuration.service';

@Directive()
export abstract class AbstractAppRouteDemarcheEtSoumissionComponent extends AbstractComponent implements OnInit {

    /** Pour le champs de sélection : liste des démarches. */
    public listeDemarches: string[] = [];
    /** Pour le champs de sélection : liste des version de l'élément éditable. */
    public listeVersions: { [index: string]: any } = {};

    /** Edition : flag édition en cours. */
    public editionEnCours = false;
    /** Edition : contenu du document. */
    public saisie: string | undefined;
    /** Version de la configuraition au moment de son chargement */
    protected versionConfigurationInitialementChargee: string | undefined;
    /** Edition : contenu en cours de modification du document. */
    protected configurationEnCoursDeModification: any | undefined;
    /** Edition : commentaire de sauvegarde. */
    public commentaire: string | undefined;

    /** Constructeur avec injection des dépendances */
    public constructor(protected adminService: AdminService, protected configService: ConfigurationService, protected dialog: MatDialog, private confPublique: boolean) {
        super();
    }

    /** A l'initialisation du composant */
    public ngOnInit(): void {
        // Chargement de la liste des démarches
        const sub = this.adminService.listerCodesDemarche().subscribe(liste => this.listeDemarches = liste);
        super.declarerSouscription(sub);

        // Reset du champs de commentaire
        this.commentaire = undefined;
    }

    /** A la sélection d'un code de démarche. */
    public selectionnerCodeDemarche(codeDemarche: string | undefined): void {

        // Dans tous les cas, on reset les données en aval
        this.editionEnCours = false;
        this.configurationEnCoursDeModification = undefined;
        this.saisie = undefined;
        this.commentaire = undefined;
        this.listeVersions = [];

        // Si un code est sélectionné
        if (codeDemarche) {

            // Reset du champs de commentaire
            this.commentaire = undefined;

            // Chargement de la liste des configurations
            const sub = this.configService.listerLesVersionsDeConfiguration(this.confPublique, codeDemarche).subscribe(mapVersions => {
                this.listeVersions = mapVersions;
            });
            super.declarerSouscription(sub);
        }
    }

    /** A la sélection d'une version de configuration. */
    protected selectionnerVersionConfiguration(codeDemarche: string | undefined, idVersion: string | undefined): void {

        // Reset d'une partie du formulaire
        this.editionEnCours = false;
        this.configurationEnCoursDeModification = undefined;
        this.commentaire = undefined;
        this.saisie = undefined;

        // Si un code et une version sont sélectionnés
        if (codeDemarche && idVersion) {

            // Chargement de la configuration
            const sub = this.configService.chargerVersionDeConfiguration(this.confPublique, codeDemarche, idVersion)
                .subscribe(configurationChargee => {
                    this.configurationEnCoursDeModification = configurationChargee;
                    this.versionConfigurationInitialementChargee = configurationChargee['versionConfiguration'];
                    this.callbackApresSelectionVersionConfiguration();
                });
            super.declarerSouscription(sub);
        }
    }
    protected abstract callbackApresSelectionVersionConfiguration(): void;

    /** Enregistrement des modifications */
    public enregistrer(codeDemarche: string | undefined, idVersion: string | undefined): void {
        // Au cas où
        if (!this.saisie || !codeDemarche || !idVersion) {
            return;
        }

        // Si les versions sont différentes, on demande confirmation avant d'enregistrer
        const confEnObjet = JSON.parse(this.saisie);
        if (this.versionConfigurationInitialementChargee && confEnObjet['versionConfiguration'] != this.versionConfigurationInitialementChargee) {
            const sub = this.demanderConfirmation('Sûr ?').subscribe(confirmation => {
                if (confirmation) {
                    this.realiserEnregistrement(codeDemarche, undefined, confEnObjet);
                }
            });
            super.declarerSouscription(sub);
        }

        // Si les versions sont identiques, enregistrement tout simplement
        else {
            this.realiserEnregistrement(codeDemarche, idVersion, confEnObjet);
        }
    }

    /** Appel au service d'enregistrement. */
    protected abstract realiserEnregistrement(codeDemarche: string, idVersion: string | undefined, confEnObjet: any): void;

    /** Passage en mode édition */
    public demarrerEdition(): void {
        this.editionEnCours = true;
    }

    /** Annuler les modifications */
    public annuler(): void {
        this.editionEnCours = false;
        this.saisie = JSON.stringify(this.configurationEnCoursDeModification, null, 2);
    }

    /** Demande de confirmation */
    protected demanderConfirmation(message: string): Observable<boolean> {

        // Ouverture de la popup
        const dialogRef = this.dialog.open(ConfirmationDialog, { disableClose: false });
        dialogRef.componentInstance.message = message;

        // Renvoi de la callback
        return dialogRef.afterClosed();
    }
}
