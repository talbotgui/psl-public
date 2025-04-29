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
import { Directive } from '@angular/core';
import { ContexteService, DonneesService, SoumissionService } from '../../../../public-api';
import { FmkContenuAbstraitComponent } from '../../abstrait/fmk.contenuabstrait';

/**
 * Cette classe fournit les comportements de base de tous les contenus de soumission.
 */
// @Directive pour pouvoir utilisé les annotations Angular dans cette classe
@Directive()
export abstract class FmkContenuFinDemarcheAbstraitComponent extends FmkContenuAbstraitComponent {

    /** Flag de désactivation du bouton de soumission durant la requête */
    public boutonActif = true;

    /** Constructeur pour injection de dépendance. */
    public constructor(protected soumissionService: SoumissionService, protected donneesService: DonneesService, protected contexte: ContexteService) { super(); }

    /** Méthode permettant un traitement spécifique dans une classe fille avant la soumission */
    protected abstract traiterUnCasParticulierAvantLaSoumission(): void;

    /** Méthode permettant un traitement spécifique dans une classe fille après la soumission */
    protected abstract traiterUnCasParticulierApresLaSoumission(resultat: string | undefined): void;

    /** A la soumission d'un télé-dossier */
    public soumettreTeledossier(): void {

        this.traiterUnCasParticulierAvantLaSoumission();

        // désactivation du bouton le temps de la requête
        this.boutonActif = false;

        // Soumission au socle puis on avance sur la page suivante
        const sub = this.soumissionService.soumettreTeledossier().subscribe(resultat => {
            // réactivation du bouton
            this.boutonActif = true;

            // Traitement spécifique de la réponse
            this.traiterUnCasParticulierApresLaSoumission(resultat);

            // Traitement de la réponse
            if (resultat) {
                this.contexte.avancerDepuisLaSoumissionDeDemarche();
            }
        });
        this.declarerSouscription(sub);
    }
}
