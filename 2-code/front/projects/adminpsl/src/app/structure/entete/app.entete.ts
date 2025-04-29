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
import { AbstractComponent } from '../../../../../framework/src/lib/utilitaires/abstract.component';
import { UtilisateurConnecte } from '../../model/securite.model';
import { AdminContexteService } from '../../service/adminContexte.service';


@Component({
    selector: 'app-entete', templateUrl: './app.entete.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: []
})
export class AppEnteteComponent extends AbstractComponent implements OnInit {

    /** Flag si l'utilisateur est connecté */
    public utilisateur: UtilisateurConnecte | undefined;

    /** Constructeur pour injection de dépendance. */
    public constructor(private adminContexte: AdminContexteService) { super(); }

    /** A l'initialisation */
    public ngOnInit(): void {

        // Abonnement aux évènements de connexion pour afficher/masquer le menu
        const sub = this.adminContexte.obtenirUnObservableDesInformationsDeLutilisateurConnecte().subscribe(u => this.utilisateur = u);
        super.declarerSouscription(sub);
    }
}
