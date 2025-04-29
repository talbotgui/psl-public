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
import { AbstractAppRouteDemarcheEtSoumissionComponent } from './app.abstractroutedemarcheetsoumission';

@Component({
    selector: 'app-routesoumission', templateUrl: './app.routesoumission.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule, KeyValuePipe]
})
export class AppRouteSoumissionComponent extends AbstractAppRouteDemarcheEtSoumissionComponent {

    /** Pour le champs de sélection : liste des éléments éditables. */
    public listeElementsAediter: { [index: string]: string } = {};

    /** Flag indiquant si un template a été modifié et que l'ensemble de la configuration doit être sauvegardée */
    public templateIntegre = false;

    /** Constructeur avec injection des dépendances */
    public constructor(adminService: AdminService, configService: ConfigurationService, dialog: MatDialog) {
        super(adminService, configService, dialog, false);
    }

    /** A la sélection d'un code de démarche. */
    public override selectionnerCodeDemarche(codeDemarche: string | undefined): void {
        // Reset d'une partie du formulaire
        this.listeElementsAediter = {};
        this.templateIntegre = false;

        // Appel au code commun
        super.selectionnerCodeDemarche(codeDemarche);
    }

    /** A la sélection d'une version de configuration. */
    public override selectionnerVersionConfiguration(codeDemarche: string | undefined, idVersion: string | undefined): void {
        // Reset d'une partie du formulaire
        this.listeElementsAediter = {};
        this.templateIntegre = false;

        // Appel au code commun
        super.selectionnerVersionConfiguration(codeDemarche, idVersion);
    }

    /** Extraction de la liste des templates de document d'une configuration interne. */
    protected override callbackApresSelectionVersionConfiguration(): void {
        // Si une configuration est chargée
        if (this.configurationEnCoursDeModification) {

            // ajout du document lui-même
            this.listeElementsAediter['configuration'] = 'configuration';

            // si elle contient des documents à générer
            if (this.configurationEnCoursDeModification['documentsAgenerer']) {
                //ajout de ses templates
                (this.configurationEnCoursDeModification['documentsAgenerer'] as { code: string, template: string }[])//
                    .forEach(doc => this.listeElementsAediter[doc.code] = doc.code);
            }
        }
        // Reset de la liste
        else {
            this.listeElementsAediter = {};
        }
    }

    /** A la sélection d'un élément à éditer */
    public selectionnerElementAediter(elementAediter: string): void {

        // Reset d'une partie du formulaire (pas de reset du commentaire car il est unique à l'ensemble de l'édition de la configuration)
        this.editionEnCours = false;
        this.saisie = undefined;

        // Si une configuration est chargée 
        if (this.configurationEnCoursDeModification) {

            // Si l'élément demandé est la conf elle-même
            if (elementAediter === 'configuration') {
                // clone profond
                const confSansTemplate = JSON.parse(JSON.stringify(this.configurationEnCoursDeModification));
                // Suppression des contenus de template si présents
                if (confSansTemplate['documentsAgenerer']) {
                    (confSansTemplate['documentsAgenerer'] as { template: string }[]).forEach(doc => doc.template = 'A MODIFIER INDEPENDAMMENT');
                }
                this.saisie = JSON.stringify(confSansTemplate, null, 2);
            }

            // Sinon si la conf contient des documents à générer
            else if (this.configurationEnCoursDeModification['documentsAgenerer']) {
                // Mise en saisie de l'élément demandé
                const docs: { code: string, template: string }[] = this.configurationEnCoursDeModification['documentsAgenerer'];
                const doc = docs.find(doc => doc.code === elementAediter);
                if (doc) {
                    // Décodage depuis la base64
                    // (pas de Buffer car elle apporte une dépendance à ne pas utiliser : "depends on 'buffer'. 
                    //   CommonJS or AMD dependencies can cause optimization bailouts. For more info see: 
                    // https://angular.io/guide/build#configuring-commonjs-dependencies")
                    this.saisie = atob(doc.template);
                }
            }
        }
    }

    /** Ré-intégration du template modifié dans la configuration en cours d'édition */
    public integrerElementModifie(elementAediter: string): void {
        // Si une saisie est bien présente et que des templates existent
        if (this.saisie && this.configurationEnCoursDeModification['documentsAgenerer']) {
            // Recherche du template à réintégrer
            const docs: { code: string, template: string }[] = this.configurationEnCoursDeModification['documentsAgenerer'];
            const doc = docs.find(doc => doc.code === elementAediter);
            if (doc) {
                // Encodage en base64 puis intégration 
                // (pas de Buffer car elle apporte une dépendance à ne pas utiliser : "depends on 'buffer'. 
                //   CommonJS or AMD dependencies can cause optimization bailouts. For more info see: 
                // https://angular.io/guide/build#configuring-commonjs-dependencies")
                doc.template = btoa(this.saisie);
                // Ajout d'une étoile si elle n'est pas déjà présente
                if (!this.listeElementsAediter[elementAediter].endsWith('*')) {
                    this.listeElementsAediter[elementAediter] = elementAediter + ' *';
                }
            }
            // Reset de la saisie
            this.saisie = undefined;
            this.editionEnCours = false;
            // Sauvegarde de l'information qu'un template a été intégré
            this.templateIntegre = true;
        }
    }

    /** Appel au service d'enregistrement. */
    protected realiserEnregistrement(codeDemarche: string, idVersion: string | undefined, confEnObjet: any) {
        // Enregistrement du commentaire dans l'objet
        confEnObjet['commentaireCreation'] = this.commentaire;

        // Ré-intégration des fichiers
        const docsConfEnCoursDeModif: { code: string, template: string }[] = this.configurationEnCoursDeModification['documentsAgenerer'];
        docsConfEnCoursDeModif.forEach(docConfEnCoursDeModif => {
            const docCorrespondant = (confEnObjet['documentsAgenerer'] as { code: string, template: string }[]).find(doc => doc.code === docConfEnCoursDeModif.code);
            if (docCorrespondant) {
                docCorrespondant.template = docConfEnCoursDeModif.template;
            }
        })

        // Passage en JSON
        const jsonAenvoyer = JSON.stringify(confEnObjet);

        // Préparation de l'appel
        let appel;
        if (idVersion) {
            appel = this.configService.modifierVersionDeConfiguration(false, codeDemarche, idVersion, jsonAenvoyer);
        } else {
            appel = this.configService.creerVersionDeConfiguration(false, codeDemarche, jsonAenvoyer);
        }

        // Appel au service
        const sub = appel.subscribe(() => {
            // Reset de l'écran comme si on sélectionnait une démarche
            this.selectionnerCodeDemarche(codeDemarche);
        });
        super.declarerSouscription(sub);
    }
}
