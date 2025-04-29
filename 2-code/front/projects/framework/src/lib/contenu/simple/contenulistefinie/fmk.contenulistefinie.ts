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
import { ReactiveFormsModule } from '@angular/forms';
import { ContenuListeFinie } from '../../../model/configurationdemarchecontenubloc.model';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';
import { FmkMessagesValidationComponent } from '../../messagesvalidation/fmk.messagesvalidation';

@Component({
    selector: 'div[data-fmk-contenulistefinie]', templateUrl: './fmk.contenulistefinie.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FmkMessagesValidationComponent, LibelleDirective]
})
export class FmkContenuListeFinieComponent extends FmkContenuMonoSaisieAbstraitComponent {

    /** Ce getter permet de simplifier le code HTML et de ne pas caster pour récupérer les options du contenu */
    public get contenuListe(): ContenuListeFinie | undefined {
        return this.contenu as ContenuListeFinie;
    }
}
