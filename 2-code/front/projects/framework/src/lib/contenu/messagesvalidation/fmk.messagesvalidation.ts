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
import { Component, Input, ViewEncapsulation } from '@angular/core';
import { AbstractControl, UntypedFormControl } from '@angular/forms';
import { ContenuDeBloc, TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { i18nPipeDirective } from '../../morceaudepage/directives/i18nPipe';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';


/** Composant affichant les messages d'erreur de validation. */
// Le sélecteur est [data-fmk-messagesvalidation]. Il ne précise pas de P ou DIV car les deux usages existent
@Component({
    selector: '[data-fmk-messagesvalidation]', templateUrl: './fmk.messagesvalidation.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [i18nPipeDirective]
})
export class FmkMessagesValidationComponent {

    /** Le FormControle contenant les erreurs à afficher */
    @Input()
    public controle: UntypedFormControl | AbstractControl | undefined | null;

    /** Le contenu lié à ce controle */
    @Input()
    public contenu: ContenuDeBloc | undefined;

    /** Permet d'identifier un type particulier de contenu */
    public estDeTypeDate(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.date); }
}
