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
import { NGXLogger } from 'ngx-logger';
import { ContenuDeBloc, ContenuSaisie } from '../../../model/configurationdemarchecontenubloc.model';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';
import { FmkMessagesValidationComponent } from '../../messagesvalidation/fmk.messagesvalidation';

@Component({
    selector: 'div[data-fmk-contenusaisielongue]', templateUrl: './fmk.contenusaisielongue.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.contenusaisielongue.scss'],
    standalone: true,
    imports: [ReactiveFormsModule, FmkMessagesValidationComponent, LibelleDirective, i18nPipeDirective]
})
export class FmkContenuSaisieLongueComponent extends FmkContenuMonoSaisieAbstraitComponent {
    /* Flag activant la zone de décompte des caractères. */
    public limiteDeCaracteresActive = false;

    /** Nombre limite de caractères */
    private nombreDeCaracteresMax = 0;

    /** Calcul du nombre de caractères restant */
    public get nombreCaracteresRestants(): number {
        if (this.controle && this.controle.value) {
            return this.nombreDeCaracteresMax - (this.controle.value as string).length;
        } else {
            return this.nombreDeCaracteresMax;
        }
    }

    /** Constructeur avec injection des dépendances */
    public constructor(private logger: NGXLogger) {
        super();
    }


    /** Méthode à surcharger pour initialiser un composant complexe (et pas ngOnInit @see FmkContenuAbstraitComponent.ngOnChanges) */
    protected override initialiserComposant(contenu: ContenuDeBloc): void {

        // Lecture du nombre limite de caractères
        const nbCaracteresMax = FmkContenuSaisieLongueComponent.extraireLongueurDeSaisieMaximale(contenu as ContenuSaisie);
        if (nbCaracteresMax) {
            this.nombreDeCaracteresMax = nbCaracteresMax;
            this.limiteDeCaracteresActive = (this.nombreDeCaracteresMax > 0);
        }

        // Ajout du bloquage du nombre de caractères
        if (this.controle) {
            const sub = this.controle.valueChanges.subscribe(v => {
                if (this.controle && this.nombreDeCaracteresMax > 0 && v && (v as string).length > this.nombreDeCaracteresMax) {
                    this.controle.setValue((v as string).substring(0, this.nombreDeCaracteresMax));
                }
            });
            this.declarerSouscription(sub);
        }
    }

    /**
     * Méthode extrayant le nombre maximal de caractères (décompte et bloquage dans le composant)
     * 
     * Cette méthode est publique et statique pour être utilisée dans les tests E2E.
     */
    public static extraireLongueurDeSaisieMaximale(contenu: ContenuSaisie): number | undefined {
        if (contenu.validationsSimples) {
            const DEBUT_VALIDATION_REGEX_DE_LONGUEUR_SIMPLE = 'REGEX-';
            const regex = contenu.validationsSimples.find(v => v.toUpperCase().startsWith(DEBUT_VALIDATION_REGEX_DE_LONGUEUR_SIMPLE));
            if (regex) {
                const debutLongueur = regex.indexOf('{0,');
                const finLongueur = regex.indexOf('}', debutLongueur);
                if (debutLongueur !== -1 && finLongueur !== -1) {
                    const limite = regex.substring(debutLongueur + 3, finLongueur);
                    if (!isNaN(parseInt(limite))) {
                        return parseInt(limite);
                    }
                }
            }
        }
        return undefined;
    }
}