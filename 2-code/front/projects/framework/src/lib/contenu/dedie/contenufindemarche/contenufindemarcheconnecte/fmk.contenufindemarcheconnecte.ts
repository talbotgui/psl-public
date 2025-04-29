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
import { ContexteService, DonneesService, FmkContenuAbstraitComponent, SoumissionService } from '../../../../../public-api';
import { i18nPipeDirective } from '../../../../morceaudepage/directives/i18nPipe';
import { FmkContenuFinDemarcheAbstraitComponent } from '../fmk.abstraitcontenufindemarche';

@Component({
    selector: 'div[data-fmk-contenufindemarcheconnecte]', templateUrl: './fmk.contenufindemarcheconnecte.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [i18nPipeDirective]
})
export class FmkContenuFinDemarcheConnecteComponent extends FmkContenuAbstraitComponent {

    /** Flag de désactivation du bouton de soumission durant la requête */
    public boutonActif = true;

    /** Constructeur pour injection de dépendance. */
    public constructor(protected soumissionService: SoumissionService, protected donneesService: DonneesService, protected contexte: ContexteService) { super(); }

    /** A la soumission d'un télé-dossier */
    public soumettreTeledossier(): void {

        // désactivation du bouton le temps de la requête
        this.boutonActif = false;

        // Soumission au socle puis on avance sur la page suivante
        const sub = this.soumissionService.soumettreTeledossier().subscribe(resultat => {
            // réactivation du bouton
            this.boutonActif = true;

            // Traitement de la réponse
            if (resultat) {
                this.contexte.avancerDepuisLaSoumissionDeDemarche();
            }
        });
        this.declarerSouscription(sub);
    }
}
