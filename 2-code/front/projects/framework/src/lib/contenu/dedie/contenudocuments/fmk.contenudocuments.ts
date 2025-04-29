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
import { DocumentsPostSoumission } from '../../../model/brouillonsoumissiondemarche.model';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { DocumentService } from '../../../services/stateless/document.service';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';

@Component({
    selector: 'div[data-fmk-contenudocuments]', templateUrl: './fmk.contenudocuments.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [i18nPipeDirective]
})
export class FmkContenuDocumentsComponent extends FmkContenuMonoSaisieAbstraitComponent {

    /** Liste des documents à afficher */
    public documents: DocumentsPostSoumission[] = [];

    /** Constructeur avec injection des dépendances */
    public constructor(private documentService: DocumentService) {
        super();
    }

    /** Implémentation de l'initialisation */
    protected override initialiserComposant(): void {
        const sub = this.documentService.chargerListeDocumentsPostSoumission().subscribe(documents => this.documents = documents);
        this.declarerSouscription(sub);
    }

    /** Téléchargement du document */
    public telechargerDocument(e: Event, document: DocumentsPostSoumission): void {
        // Pour éviter que le clic ne se propage à la DIV parente et ne déclenche l'ouverture de la sélection de fichier
        e.preventDefault();
        e.stopPropagation();

        const sub = this.documentService.telechargerDocument(document).subscribe();
        this.declarerSouscription(sub);
    }

    public formaterTailleAffichable(taille: number | undefined): string {
        if (taille === undefined) {
            return '';
        }
        if (taille < 1024) {
            return taille + 'o'
        }
        const ko = taille / 1024;
        if (ko < 1024) {
            return Math.round(ko) + 'ko'
        }
        const mo = ko / 1024;
        if (mo < 1024) {
            return Math.round(mo) + 'Mo'
        }
        const go = mo / 1024;
        if (go < 1024) {
            return Math.round(go) + 'Go'
        }
        return taille + '0';
    }
}
