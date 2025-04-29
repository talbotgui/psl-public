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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ContexteService } from '../../services/stateful/contexte.service';
import { EnvironnementService } from '../../services/stateful/environnement.service';
import { OIDCService } from '../../services/stateless/oidc.service';
import { i18nPipeDirective } from '../directives/i18nPipe';
import { FmkMessageGlobalComponent } from './messageglobal/fmk.messageglobal';


/** Composant d'entête. */
@Component({
    selector: 'div[data-fmk-entete]', templateUrl: './fmk.entete.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.entete.scss'],
    standalone: true,
    imports: [FmkMessageGlobalComponent, i18nPipeDirective]
})
export class FmkEnteteComponent implements OnInit {

    /** Libellé de chaque langue */
    private static readonly LIBELLES_LANGUES: { [index: string]: string } = { 'FR': 'Français', 'EN': 'English', 'DE': 'Deutsch', 'ES': 'Español' }

    /** Donnees de connexion */
    public donneesDeConnexion: any | undefined;

    /** Liste des langues disponibles */
    public languesDisponibles = ['FR'];

    /** Langue active */
    public langueCourante = 'FR';

    /** Founi le libellé d'une langue */
    public getLibelleLangue(langue: string): string {
        return FmkEnteteComponent.LIBELLES_LANGUES[langue];
    }

    /** Constructeur avec injection de dépendances */
    public constructor(private environnementService: EnvironnementService, private contexte: ContexteService, private oidcService: OIDCService) { }

    /** Au chargement du composant,  */
    public ngOnInit(): void {

        // on écoute les statuts de connexion et informations de la connexion OIDC
        // Souscription non détruite car ce composant vie autant que l'application
        this.contexte.obtenirUnObservableDesInformationsDeLutilisateurConnecte()
            .subscribe(donneesDeConnexion => this.donneesDeConnexion = donneesDeConnexion);

        // on écoute le chargement de la conf 
        // Souscription non détruite car ce composant vie autant que l'application
        this.contexte.obtenirUnObservableDeChargementDeLaConfiguration()
            .subscribe(configuration => {
                //pour extraire les langues disponibles
                if (configuration && ((configuration as any)['titreEN'])) {
                    this.languesDisponibles.push('EN');
                }
                if (configuration && ((configuration as any)['titreDE'])) {
                    this.languesDisponibles.push('DE');
                }
                if (configuration && ((configuration as any)['titreES'])) {
                    this.languesDisponibles.push('ES');
                }
                // pour initialiser la liste déroulante car la valeur a déjà été définie (dans FmkApplicationAbstraitComponent)
                this.langueCourante = this.contexte.langue;
                // pour gérer les préférences ordonnées de langue versus les langues dans la configuration
                for (let l of navigator.languages) {
                    const codeL = l.substring(0, 2).toUpperCase();
                    // si la langue est disponible, on la prend
                    if (this.languesDisponibles.includes(codeL)) {
                        // Si ce n'est pas déjà la bonne, on la définit
                        if (this.langueCourante !== codeL) {
                            this.contexte.changerLangue(codeL);
                            this.langueCourante = codeL;
                        }
                        // C'est fini
                        break;
                    }
                }
            });
    }

    /** Sélection d'une langue */
    public selectionnerLangue(langue: string): void {

        // Valeur sélectionnée sauvegardée dans le composant
        this.langueCourante = langue;

        // Modification de tous les libellés à afficher
        this.contexte.changerLangue(langue)

    }

    /** Déconnexion */
    public seDeconnecter(): void {
        this.oidcService.seDeconnecter();
    }

    /** Récupération de la base de l'URL de ServicePublic pour les liens dans l'entête. */
    public get lienServicePublic(): string {
        return this.environnementService.urls.liensServicePublic;
    }

    /** Récupération de la base de l'URL de la page de connexion ServicePublic pour les liens dans l'entête. */
    public get lienConnexionServicePublic(): string {
        return this.environnementService.urls.lienConnexionServicePublic;
    }
}
