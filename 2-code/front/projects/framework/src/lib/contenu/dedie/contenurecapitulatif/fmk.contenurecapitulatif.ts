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
import { ConfigurationService, ContenuDeBloc, ContexteService, DonneesService, FormulaireService, UtilitaireModel } from '../../../../public-api';
import { Bloc, ConfigurationDemarche, Page, SousBloc } from '../../../model/configurationdemarchegeneral.model';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { UtilitaireBlocDynamique } from '../../../utilitaires/utilitaire.blocdynamique';
import { FmkContenuAbstraitComponent } from '../../abstrait/fmk.contenuabstrait';
import { FmkListeContenusRecapitulatifComponent } from './listecontenusrecapitulatif/fmk.listecontenusrecapitulatif';


@Component({
    selector: 'div[data-fmk-contenurecapitulatif]', templateUrl: './fmk.contenurecapitulatif.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.contenurecapitulatif.scss'],
    standalone: true,
    imports: [FmkListeContenusRecapitulatifComponent, i18nPipeDirective]
})
export class FmkContenuRecapitulatifComponent extends FmkContenuAbstraitComponent {

    /** Configuration de la démarche permettant de construire le contenu du composant */
    public configuration: ConfigurationDemarche | undefined;

    /** Index de la page contenant le composant de recap */
    public indexPageCourante: number | undefined;

    /** Constructeur avec injection des dépendances */
    public constructor(private formulaireService: FormulaireService, private donneesService: DonneesService, private configurationService: ConfigurationService, private contexteService: ContexteService) { super(); }

    /** Initialisation du composant */
    protected override initialiserComposant(): void {
        // Récupération de la configuration depuis le contexte
        this.configuration = this.contexteService.configurationDemarche;
        const sub = this.contexteService.obtenirUnObservableDeChangementDePage().subscribe(i => this.indexPageCourante = i)
        this.declarerSouscription(sub);
    }

    /** Modification de la page courante */
    public changerDePage(numeroPage: number): void {
        this.contexteService.changerDePage(numeroPage);
    }

    /** Cette méthode permet de masquer les pages vides */
    public calculerSiPageContientUnContenuAffichable(page: Page): boolean {
        const nb = (page.blocs || []).filter(b => this.calculerSiBlocContientUnContenuAffichable(b)).length;
        return nb > 0;
    }

    /** Cette méthode permet de masquer les blocs vides */
    public calculerSiBlocContientUnContenuAffichable(bloc: SousBloc): boolean {
        // Accumulation de tous les contenus du bloc
        const tousLesContenus = [];
        tousLesContenus.push(...(bloc.contenus || []));
        if ((bloc as any)['sousBlocs']) {
            tousLesContenus.push(...((bloc as Bloc).sousBlocs || []).flatMap(sb => sb.contenus));
        }

        // Calcul du nombre de contenus visibles
        const nb = tousLesContenus.filter(c => {
            const contenuGenerantValeurTypeObjet = UtilitaireModel.contenuGereUneValeurDeTypeObjet(c);

            // Si le contenu est visible
            return c.visibilite
                && (
                    // qu'il est mono-champ et valorisé
                    (!contenuGenerantValeurTypeObjet && c.clef && !!this.donneesService.lire(c.clef))
                    ||
                    // qu'il est complexe et valorisé
                    (contenuGenerantValeurTypeObjet && c.clef && Object.keys(this.donneesService.lireUnObjet(c.clef) || {}).length > 0)
                );
        }
        ).length;
        return nb > 0;
    }

    /** Création de la liste des occurences en cours d'un bloc dynamique. */
    public creerListeOccurences(bloc: Bloc): Array<number> {
        const listeOccurences = [];
        // Pour chaque occurence possible (vis-à-vis du nombre max)
        if (bloc && bloc.maxOccurences) {
            for (let i = 0; i < bloc.maxOccurences; i++) {
                // Recherche d'une valeur pour un des champs (en remettant la clef originale)
                const contenusTrouve = bloc.contenus.find(c => {
                    const clefOriginale = c.clef;
                    c.clef = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(c.clef, i);
                    const valeurTrouvee = !!this.formulaireService.determinerValeurParDefaut(c);
                    c.clef = clefOriginale;
                    return !!valeurTrouvee;
                });

                // Si trouvé, on ajoute l'occurence
                if (contenusTrouve) {
                    listeOccurences.push(i);
                }
            }
        }
        return listeOccurences;
    }

    /** Création des contenus pour une occurence de bloc dynamique */
    public creerContenusDeBlocDynamique(bloc: Bloc, occurence: number): ContenuDeBloc[] {
        return this.configurationService.clonerContenusPourUneOccurenceDeBlocDynamique(bloc, occurence);
    }
}
