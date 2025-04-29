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
import { UntypedFormControl } from '@angular/forms';
import { ContenuDeBloc, TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';
import { FmkContenuAutocompletionComponent } from './contenuautocompletion/fmk.contenuautocompletion';
import { FmkContenuCaseComponent } from './contenucase/fmk.contenucase';
import { FmkContenuDateComponent } from './contenudate/fmk.contenudate';
import { FmkContenuListeFinieComponent } from './contenulistefinie/fmk.contenulistefinie';
import { FmkContenuParagrapheComponent } from './contenuparagraphe/fmk.contenuparagraphe';
import { FmkContenuRadioComponent } from './contenuradio/fmk.contenuradio';
import { FmkContenuSaisieComponent } from './contenusaisie/fmk.contenusaisie';
import { FmkContenuSaisieLongueComponent } from './contenusaisielongue/fmk.contenusaisielongue';
import { FmkContenuUploadDocumentComponent } from './contenuuploaddocument/fmk.contenuuploaddocument';

/** Composant de sélection du composant à afficher selon le contenu. */
@Component({
    selector: 'div[data-fmk-contenusimple]', templateUrl: './fmk.contenusimple.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuAutocompletionComponent, FmkContenuCaseComponent, FmkContenuDateComponent, FmkContenuListeFinieComponent, FmkContenuParagrapheComponent, FmkContenuRadioComponent, FmkContenuSaisieComponent, FmkContenuSaisieLongueComponent, FmkContenuUploadDocumentComponent]
})
export class FmkContenuSimpleComponent {

    /** Paramètrage du contenu */
    @Input()
    public contenu: ContenuDeBloc | undefined;

    /** Controle associé au champ */
    @Input()
    public controle: UntypedFormControl | undefined;

    /** Liste des méthodes retournant TRUE ou FALSE pour déclencher l'affichage d'un composant en fonction d'un contenu. */
    public estDeTypeCase(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.case); }
    public estDeTypeAutocompletion(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.autocompletion); }
    public estDeTypeDate(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.date); }
    public estDeTypeListeFinie(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.listeFinie); }
    public estDeTypeParagraphe(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.paragraphe); }
    public estDeTypeRadio(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.radio); }
    public estDeTypeSaisie(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.saisie); }
    public estDeTypeSaisieLongue(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.saisieLongue); }
    public estDeTypeUploadDocument(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.uploadDocument); }

}
