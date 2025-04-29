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
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';

import { DonneesService } from '../../../public-api';
import { ConfigurationDemarche } from '../../model/configurationdemarchegeneral.model';
import { ContexteService } from '../../services/stateful/contexte.service';
import { AbstractComponent } from '../../utilitaires/abstract.component';
import { FmkMessageGlobalComponent } from '../entete/messageglobal/fmk.messageglobal';
import { FmkFilDarianeComponent } from './fildariane/fmk.fildariane';
import { FmkPageComponent } from './page/fmk.page';


@Component({
    selector: 'div[data-fmk-conteneurpages]', templateUrl: './fmk.conteneurpages.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkFilDarianeComponent, FmkMessageGlobalComponent, FmkPageComponent]
})
export class FmkConteneurPagesComponent extends AbstractComponent implements OnInit {

    /** Configuration de la démarche */
    @Input()
    public configurationDemarche: ConfigurationDemarche | undefined;

    /** Index de la page courante */
    public indexPageCourante: number | undefined;

    /** Constructeur pour injection des dépendances. */
    public constructor(private contexte: ContexteService, private donnees: DonneesService) { super(); }

    /** A l'initialisation du composant */
    public ngOnInit(): void {
        // Pour être prévenu d'un changement de page
        const sub = this.contexte.obtenirUnObservableDeChangementDePage().subscribe(i => {
            // MaJ index de la page
            this.indexPageCourante = i
            // MaJ du titre de la fenêtre
            this.recalculerTitreFenetre('');
        });
        this.declarerSouscription(sub);

        // Recalcule les titres de touts les au changement de langue
        const sub2 = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => {

            // récupération de la configuration
            const conf = this.contexte.configurationDemarche
            if (conf && langue) {

                // Calcul du suffixe à utiliser
                const suffixe = (langue === 'FR') ? '' : langue;

                // MaJ du titre de la fenêtre
                this.recalculerTitreFenetre(suffixe);

                // Recalcul de tous les libellés de la structure des pages
                if (conf) {
                    conf.titreAafficher = (conf as any)['titre' + suffixe] || conf.titre;

                    (conf.pages || []).forEach(p => {
                        p.titreAafficher = (p as any)['titre' + suffixe] || p.titre;
                        p.titreArianeAafficher = (p as any)['titreAriane' + suffixe] || p.titreAriane;

                        (p.blocs || []).forEach(b => {
                            b.titreAafficher = (b as any)['titre' + suffixe] || b.titre;
                            b.aideAafficher = (b as any)['aide' + suffixe] || b.aide;
                            b.aideTitreAafficher = (b as any)['aideTitre' + suffixe] || b.aideTitre || b.aideAafficher;

                            (b.sousBlocs || []).forEach(sb => {
                                sb.titreAafficher = (sb as any)['titre' + suffixe] || sb.titre;
                                sb.aideAafficher = (sb as any)['aide' + suffixe] || sb.aide;
                                sb.aideTitreAafficher = (sb as any)['aideTitre' + suffixe] || sb.aideTitre || sb.aideAafficher;
                            });
                        });
                    });
                }
            }
        });
        this.declarerSouscription(sub2);
    }

    /**
     * Méthode recalculant le titre de la fenêtre sur le modèle 'demarche.titre :: page.titre' :
     * Par défaut, le titre de la page est utilisé. 
     * A défaut de titre, si la page n'est pas exclue du fil d'Ariane, le titre du fil d'Ariane est pris. 
     * Sinon, on ne touche pas au titre de la fenêtre.
     */
    private recalculerTitreFenetre(suffixe: string): void {
        if (this.configurationDemarche && typeof this.indexPageCourante !== 'undefined') {
            // Recherche de la page
            const page = this.configurationDemarche.pages[this.indexPageCourante];
            if (page) {

                // Recherche du titre à utiliser
                const titreDemarche = (this.configurationDemarche as any)['titre' + suffixe] || this.configurationDemarche.titre;
                const titreAutiliser = (page as any)['titre' + suffixe] || (page as any)['titreAriane' + suffixe] || page.titre || page.titreAriane || undefined;

                // Si un titre est disponible
                if (titreAutiliser) {
                    // Résolution des variables dans le titre à utiliser
                    const toutesLesDonnees = this.donnees.lireToutEtIntegrerLesDonneesFournies({});
                    const titreAutiliserResolu = this.donnees.integrerDesVariablesDansUnLibelle(titreAutiliser, toutesLesDonnees);
                    // MaJ du titre
                    const titreFenetre = titreDemarche + ' :: ' + titreAutiliserResolu;
                    window.name = titreFenetre;
                    window.document.title = titreFenetre;
                }
            }
        }
    }
}
