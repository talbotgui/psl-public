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
import { Component, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { i18nPipeDirective } from '../../../../morceaudepage/directives/i18nPipe';
import { FmkCaptchaComponent } from './captcha/fmk.captcha';
import { ContexteService, DonneesService, FmkContenuAbstraitComponent, SoumissionService } from '../../../../../public-api';

@Component({
    selector: 'div[data-fmk-contenufindemarchenonconnecte]', templateUrl: './fmk.contenufindemarchenonconnecte.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkCaptchaComponent, ReactiveFormsModule, FormsModule, i18nPipeDirective]
})
export class FmkContenuFinDemarcheNonConnecteComponent extends FmkContenuAbstraitComponent {

    /** Valeur saisie */
    public valeurCaptcha: string | undefined;

    /** Référence au composant enfant présent dans la page (référence par type) */
    @ViewChild(FmkCaptchaComponent)
    private composantCaptcha: FmkCaptchaComponent | undefined;

    /** Flag de désactivation du bouton de soumission durant la requête */
    public boutonActif = true;

    /** Constructeur pour injection de dépendance. */
    public constructor(protected soumissionService: SoumissionService, protected donneesService: DonneesService, protected contexte: ContexteService) { super(); }

    /** A la soumission d'un télé-dossier */
    public soumettreTeledossier(): void {

        this.traiterUnCasParticulierAvantLaSoumission();

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

    /** Méthode appelée avant la soumission */
    private traiterUnCasParticulierAvantLaSoumission(): void {

        // L'id du captcha est lu au moment de la lecture de la valeur car le JS du captcha permet
        // de changer l'ID sans repasser par le code TS
        const idCaptcha = this.composantCaptcha?.obtenirIdCaptcha();
        if (idCaptcha) {
            this.donneesService.sauvegarderIdentifiantCaptcha(idCaptcha);
        } else {
            // rien à faire car la soumission va hurler que le captcha est manquant
        }

        if (this.valeurCaptcha) {
            this.donneesService.sauvegarderSaisieCaptcha(this.valeurCaptcha);
        } else {
            // rien à faire car la soumission va hurler que le captcha est manquant
        }
    }
}
