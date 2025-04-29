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
import { ContenuDeBloc, ContenuParagraphe } from '../../../model/configurationdemarchecontenubloc.model';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuAbstraitComponent } from '../../abstrait/fmk.contenuabstrait';

@Component({
    selector: 'div[data-fmk-contenuparagraphe]', templateUrl: './fmk.contenuparagraphe.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [LibelleDirective]
})
export class FmkContenuParagrapheComponent extends FmkContenuAbstraitComponent {

    public get contenuParagraphe(): ContenuParagraphe { return this.contenu as ContenuParagraphe; }

    /** Le paragraphe commence par un P. */
    public texteCommenceParUnP = false;

    /** L'aide commence par un P. */
    public aideCommenceParUnP = false;

    /** Constructeur avec injection des dépendances */
    public constructor() {
        super();
    }

    /** Implémentation de l'initialisation */
    protected override initialiserComposant(contenu: ContenuDeBloc): void {
        const contPara = (contenu as ContenuParagraphe);

        // Valeur par défaut au style si besoin
        if (!contPara.style) {
            contPara.style = '';
        }

        // Pour gérer le cas des <ul>, <ol>, ... interdits dans un P
        // @see https://html.spec.whatwg.org/multipage/grouping-content.html#the-p-element
        this.texteCommenceParUnP = !!this.contenuParagraphe && !!this.contenuParagraphe.texte && this.contenuParagraphe.texte.toUpperCase().startsWith('<P');
        this.aideCommenceParUnP = !!this.contenuParagraphe && !!this.contenuParagraphe.aide && this.contenuParagraphe.aide.toUpperCase().startsWith('<P');
    }
}
