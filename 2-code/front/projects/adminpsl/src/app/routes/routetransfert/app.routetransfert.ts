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
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTooltip } from '@angular/material/tooltip';
import { NGXLogger } from 'ngx-logger';
import { AbstractComponent } from '../../../../../framework/src/lib/utilitaires/abstract.component';
import { TransfertDansRechercheMulticriteres } from '../../model/transfert.model';
import { AdminService } from '../../service/admin.service';

@Component({
    selector: 'app-routetransfert', templateUrl: './app.routetransfert.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./app.routetransfert.scss'],
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule, MatTooltip, DatePipe]
})
export class AppRouteTransfertComponent extends AbstractComponent implements OnInit {

    /** Pour le champs de sélection : liste des démarches. */
    public listeDemarches: string[] = [];

    /** Champs de recherche par dates */
    public dateDebut: Date | undefined;
    public dateFin: Date | undefined;
    /** Champs de recherche par codes de démarche */
    public codesDemarche: string[] = [];
    /** Champs de recherche par numéro de télé-dossier */
    public numeroTeledossier: string | undefined;

    /** Pagination - nombres possibles d'éléments par page */
    public nombresElementsParPage = [10, 50, 100, 500];
    /** Pagination - nombre d'éléments par page */
    public nombreElementsParPage = 50;
    /** Pagination - index de la page */
    public numeroPage = 0;
    /** Pagination - index maximal possible de la page */
    public numeroPageMax = 0;
    /** Pagination - nb total déléments.  */
    public nombreTotalResultats = 0;


    /** Donnees - liste des éléments. */
    public transferts: TransfertDansRechercheMulticriteres[] = [];
    /** Donnees - recherche en cours. */
    public rechercheEnCours = false;

    /** Constructeur pour injection des dépendances */
    public constructor(private adminService: AdminService, private logger: NGXLogger) {
        super();
    }

    /** Au chargement du composant. */
    public ngOnInit(): void {

        // Chargement de la liste des démarches
        const sub = this.adminService.listerCodesDemarche().subscribe(liste => this.listeDemarches = liste);
        super.declarerSouscription(sub);

        // Initialisation des champs par défaut (pas de fin et début à hier)
        this.dateDebut = new Date((new Date()).getTime() - (24 * 3600 * 1000));
        this.dateFin = undefined;

        // Recherche par défaut
        this.rechercher();
    }

    /** Sélection d'une page et recherche des données */
    public selectionnerPageEtRechercher(noPage: number): void {
        // Sélection de la page
        this.numeroPage = noPage;
        // Recherche
        this.rechercher();
    }

    /** Recherche des données */
    public rechercher(): void {

        // Traitement des paramètres
        const params = { dateDebut: this.dateDebut, dateFin: this.dateFin, codesDemarche: this.codesDemarche, numeroTeledossier: this.numeroTeledossier, numeroPage: this.numeroPage, nombreElementsParPage: this.nombreElementsParPage }
        if (this.codesDemarche && this.codesDemarche.length === 1 && this.codesDemarche[0] === '') {
            params.codesDemarche = [];
        }

        // Appel
        this.rechercheEnCours = true;
        this.adminService.rechercherTransferts(params).subscribe(pageResultats => {
            this.nombreTotalResultats = pageResultats.nombreTotalResultats;
            this.transferts = pageResultats.resultats;
            this.numeroPageMax = Math.trunc((pageResultats.nombreTotalResultats - 1) / this.nombreElementsParPage);

            this.rechercheEnCours = false;
        })
    }

    /** Transformation d'une chaine de caractère contenant une date ISO en date locale. */
    public traiterLocalisationDate(val: string): Date {
        return new Date(val);
    }
}
