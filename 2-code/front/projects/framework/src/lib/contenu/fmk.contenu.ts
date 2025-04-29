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
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ContenuDeBloc, TypeContenuDeBloc } from '../model/configurationdemarchecontenubloc.model';
import { UtilitaireModel } from '../utilitaires/utilitaire.model';
import { FmkContenuDocumentsComponent } from './dedie/contenudocuments/fmk.contenudocuments';
import { FmkContenuFinDemarcheComponent } from './dedie/contenufindemarche/fmk.contenufindemarche';
import { FmkContenuRecapitulatifComponent } from './dedie/contenurecapitulatif/fmk.contenurecapitulatif';
import { FmkContenuUtilisateurConnecteComponent } from './dedie/contenuutilisateurconnecte/fmk.contenuutilisateurconnecte';
import { FmkContenuRicheComponent } from './riche/fmk.contenuriche';
import { FmkContenuSimpleComponent } from './simple/fmk.contenusimple';

/** Composant de sélection du composant à afficher selon le contenu. */
@Component({
    selector: 'div[data-fmk-contenu]', templateUrl: './fmk.contenu.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuRicheComponent, FmkContenuSimpleComponent, FmkContenuDocumentsComponent, FmkContenuUtilisateurConnecteComponent, FmkContenuFinDemarcheComponent, FmkContenuRecapitulatifComponent]
})
export class FmkContenuComponent {

    /** Paramètrage du contenu */
    @Input()
    public contenu: ContenuDeBloc | undefined;

    /** Controle associé au champ */
    @Input()
    public controle: UntypedFormControl | UntypedFormGroup | undefined;
    public get controleAsFormGroup(): UntypedFormGroup | undefined { return this.controle as UntypedFormGroup; }
    public get controleAsFormControl(): UntypedFormControl | undefined { return this.controle as UntypedFormControl; }

    /** Liste des méthodes retournant TRUE ou FALSE pour déclencher l'affichage d'un composant en fonction d'un contenu. */
    public estDeTypeSimple(): boolean { return !!this.contenu && !UtilitaireModel.contenuEstUneSaisieComplexe(this.contenu); }
    public estDeTypeRiche(): boolean { return !!this.contenu && UtilitaireModel.contenuEstUneSaisieComplexe(this.contenu); }

    public estDeTypeFinDemarche(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.finDemarche); }
    public estDeTypeRecapitulatif(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.recapitulatif); }
    public estDeTypeUtilisateurConnecte(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.utilisateurConnecte); }
    public estDeTypeDocuments(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeType(this.contenu, TypeContenuDeBloc.documents); }

    public estDeTypeInconnu(): boolean { return !!this.contenu && UtilitaireModel.contenuEstDeTypeInconnu(this.contenu); }
}
