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
import { UntypedFormGroup } from '@angular/forms';
import { ContenuDeBloc, TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';
import { FmkContenuAdresseFrOuRtrComponent } from './contenuadressefrouetr/fmk.contenuadressefrouetr';
import { FmkContenuContactPersonnelComponent } from './contenucontactpersonnel/fmk.contenucontactpersonnel';
import { FmkContenuIdentiteComponent } from './contenuidentite/fmk.contenuidentite';

/** Composant de sélection du composant à afficher selon le contenu. */
@Component({
    selector: 'div[data-fmk-contenuriche]', templateUrl: './fmk.contenuriche.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuAdresseFrOuRtrComponent, FmkContenuContactPersonnelComponent, FmkContenuIdentiteComponent]
})
export class FmkContenuRicheComponent {

    /** Paramètrage du contenu */
    @Input()
    public contenu: ContenuDeBloc | undefined;

    /** Controle associé au champ */
    @Input()
    public groupeControles: UntypedFormGroup | undefined;

    /** Liste des méthodes retournant TRUE ou FALSE pour déclencher l'affichage d'un composant en fonction d'un contenu. */
    public estDeTypeAdresseFrOuEtr(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.adresseFrOuEtr); }
    public estDeTypeIdentite(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.identite); }
    public estDeTypeContactPersonnel(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.contactPersonnel); }
}
