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
import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AbstractComponent } from '../../../../../framework/src/lib/utilitaires/abstract.component';
import { AdminService } from '../../service/admin.service';

@Component({
    selector: 'app-routeconnexion', templateUrl: './app.routeconnexion.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FormsModule]
})
export class AppRouteConnexionComponent extends AbstractComponent {

    /** Constructeur pour injection des dépendances */
    constructor(private adminService: AdminService, private routeur: Router) {
        super();
    }

    /** Donnée du formulaire : nomUtilisateur */
    public nomUtilisateur: string | undefined;

    /** Donnée du formulaire : motDePasse */
    public motDePasse: string | undefined;

    /** Méthode de connexion */
    public tenterConnexion(): void {
        if (this.nomUtilisateur && this.motDePasse) {
            // Tentative de connexion
            const sub = this.adminService.tenterConnexion(this.nomUtilisateur, this.motDePasse).subscribe(
                resultat => {
                    // Si succès, redirection vers la page par défaut
                    if (resultat) {
                        this.routeur.navigate(['/']);
                    }
                });
            super.declarerSouscription(sub);
        }
    }
}
