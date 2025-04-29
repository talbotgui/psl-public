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
import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ContenuRadio } from '../../../model/configurationdemarchecontenubloc.model';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';
import { FmkMessagesValidationComponent } from '../../messagesvalidation/fmk.messagesvalidation';

@Component({
    selector: 'div[data-fmk-contenuradio]', templateUrl: './fmk.contenuradio.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [ReactiveFormsModule, FmkMessagesValidationComponent, LibelleDirective]
})
export class FmkContenuRadioComponent extends FmkContenuMonoSaisieAbstraitComponent {

    /** Ce getter permet de simplifier le code HTML et de ne pas caster pour récupérer les options du contenu RADIO */
    public get contenuRadio(): ContenuRadio | undefined {
        return this.contenu as ContenuRadio;
    }

    /** Ce getter permet de simplifier le code HTML et de ne pas traiter le cas impossible d'un contenu CASE sans clef */
    public get clef(): string {
        return this.contenu?.clef || '';
    }

    /** Ce getter permet de simplifier le code HTML et de ne pas traiter le cas impossible d'un controle sans formulaire */
    public get formulaire(): UntypedFormGroup {
        return (this.controle as UntypedFormControl).parent as UntypedFormGroup;
    }

}
