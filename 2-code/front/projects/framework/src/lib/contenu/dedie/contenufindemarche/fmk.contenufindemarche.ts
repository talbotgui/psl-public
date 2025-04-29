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
import { ContexteService } from '../../../../public-api';
import { FmkContenuFinDemarcheConnecteComponent } from './contenufindemarcheconnecte/fmk.contenufindemarcheconnecte';
import { FmkContenuFinDemarcheNonConnecteComponent } from './contenufindemarchenonconnecte/fmk.contenufindemarchenonconnecte';


@Component({
    selector: 'div[data-fmk-contenufindemarche]', templateUrl: './fmk.contenufindemarche.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuFinDemarcheConnecteComponent, FmkContenuFinDemarcheNonConnecteComponent]
})
export class FmkContenuFinDemarcheComponent {

    /** Constructeur pour injection des dépendances */
    public constructor(private contexteService: ContexteService) { }

    /** Flag permettant de savoir si l'utilisateur connecté est connecté en anonyme ou OIDC */
    public estUsagerConnecte(): boolean {
        return this.contexteService.utilisateurConnecteOidc;
    }
}
