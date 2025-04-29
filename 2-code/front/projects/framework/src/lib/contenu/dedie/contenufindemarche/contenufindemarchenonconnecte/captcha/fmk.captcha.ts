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
import { Component, ElementRef, NgZone, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { mergeMap, of, tap } from 'rxjs';
import { DonneesService, SoumissionService } from '../../../../../../public-api';

/** Ce composant est très particulier car il affiche des ressources (HTML, CSS, JS, ...) venant directement du système de captcha via les APIs du BACK. */
// template:'' pour indiquer que le template est vide et qu'aucun fichier n'est associé
@Component({
    selector: 'div[data-fmk-captcha]', template: '',
    standalone: true
})
export class FmkCaptchaComponent implements OnInit {

    /** Constructeur pour injection des dépendances */
    public constructor(private soumissionService: SoumissionService, private elementRef: ElementRef, private ngZone: NgZone, private logger: NGXLogger) { }

    /** Au chargement du composant */
    public ngOnInit(): void {

        // Appel au captcha
        this.soumissionService.genererCaptcha()
            .pipe(
                // Affichage du contenu HTML dans le navigateur
                // Attention, ceci constitue une faille de sécurité (chargement d'un script qui vient de l'extérieur) mais c'est le principe du captcha
                tap((contenuCaptchaHtml: string) => this.elementRef.nativeElement.innerHTML = contenuCaptchaHtml),
                // Chargement du script JS associé au captcha
                mergeMap(() => {
                    const baliseScript = this.elementRef.nativeElement.querySelector('script');
                    if (baliseScript && baliseScript.src) {
                        return this.soumissionService.chargerRessourceCaptcha(baliseScript.src);
                    } else {
                        return of('');
                    }
                }),
                // Exécution de ce script dans Angular
                tap((contenuScriptCaptcha: string) => {
                    if (contenuScriptCaptcha) {
                        const f = new Function(contenuScriptCaptcha);
                        this.ngZone.runOutsideAngular(() => f());
                    }
                })
            )
            .subscribe();
    }

    /** Pour lire l'ID du captcha depuis le composant parent */
    public obtenirIdCaptcha(): string | undefined {
        const inputIdCaptcha = this.elementRef.nativeElement.querySelector('input');
        if (inputIdCaptcha) {
            return inputIdCaptcha.value;

        } else {
            this.logger.warn('Aucun input présent avec l\'identifiant du captcha');
            return undefined;
        }
    }
}
