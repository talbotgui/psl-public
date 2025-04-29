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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminService } from '../../../service/admin.service';


@Component({
    selector: 'app-routeadmindemarches', templateUrl: './app.routeadmindemarches.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./app.routeadmindemarches.scss'],
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule]
})
export class AppRouteAdminDemarchesComponent implements OnInit {

    /** Liste des codes de démarche */
    public codesDemarche: string[] = [];

    /** Code de démarche à créer */
    public nouveauCodeDemarche: string = "";

    /** Constructeur avec injection des dépendances */
    public constructor(private adminService: AdminService) {
    }

    /** A l'initialisation du composant */
    public ngOnInit(): void {
        // Chargement de la liste des codes de démarche
        this.adminService.listerCodesDemarche().subscribe(liste => this.codesDemarche = liste);
    }

    /** Création d'une nouvelle démarche puis refresh de la liste. */
    public creerDemarche(): void {
        this.adminService.creerNouvelleDemarche(this.nouveauCodeDemarche).subscribe(() => {
            this.ngOnInit();
            this.nouveauCodeDemarche = "";
        });
    }
}
