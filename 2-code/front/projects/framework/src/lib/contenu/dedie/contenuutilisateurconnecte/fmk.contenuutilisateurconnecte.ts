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
import { DatePipe } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { CLEF_UTILISATEUR } from '../../../model/securite.model';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { ContexteService } from '../../../services/stateful/contexte.service';
import { DonneesService } from '../../../services/stateful/donnees.service';
import { FmkContenuAbstraitComponent } from '../../abstrait/fmk.contenuabstrait';

@Component({
    selector: 'div[data-fmk-contenuutilisateurconnecte]', templateUrl: './fmk.contenuutilisateurconnecte.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.contenuutilisateurconnecte.scss'],
    standalone: true,
    imports: [DatePipe, i18nPipeDirective]
})
export class FmkContenuUtilisateurConnecteComponent extends FmkContenuAbstraitComponent {

    /** Structure de données d'un utilisateur connecté */
    public utilisateur: any | undefined;

    /** Constructeur avec injection des dépendances */
    public constructor(private donneesService: DonneesService, private contexte: ContexteService) {
        super();
    }

    /** Implémentation de l'initialisation */
    protected override initialiserComposant(): void {
        // Si le composant est sur la première page, la configuration est chargée rapidement et le composant s'initialise
        // Mais la connexion OIDC et la récupération du token peuvent arriver après.
        // Donc on tente de lire les données à l'initialistion du composant
        this.utilisateur = this.donneesService.lireUnObjet(CLEF_UTILISATEUR) as any;

        // mais on écoute aussi le contexte pour récupérer les données quand elles sont disponibles
        const sub = this.contexte.obtenirUnObservableDesInformationsDeLutilisateurConnecte().subscribe(
            () => this.utilisateur = this.donneesService.lireUnObjet(CLEF_UTILISATEUR) as any
        );

        // Sans oublier de déclarer la souscription pour la détruire à la destruction du composant
        super.declarerSouscription(sub);
    }
}
