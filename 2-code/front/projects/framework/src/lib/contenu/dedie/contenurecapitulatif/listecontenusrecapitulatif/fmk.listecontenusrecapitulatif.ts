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
import { NGXLogger } from 'ngx-logger';
import { ContexteService, DonneesService, FmkContenuAdresseFrOuRtrComponent, FmkContenuContactPersonnelComponent, FmkContenuIdentiteComponent, UtilitaireModel } from '../../../../../public-api';
import { ContenuAutocompletion, ContenuDeBloc, ContenuListeFinie, TypeContenuDeBloc } from '../../../../model/configurationdemarchecontenubloc.model';
import { AbstractComponent } from '../../../../utilitaires/abstract.component';

@Component({
    selector: 'div[data-fmk-listecontenusrecapitulatif]', templateUrl: './fmk.listecontenusrecapitulatif.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.listecontenusrecapitulatif.scss'],
    standalone: true,
    imports: []
})
export class FmkListeContenusRecapitulatifComponent extends AbstractComponent implements OnInit {

    /** Les contenus à afficher */
    @Input()
    public contenus: ContenuDeBloc[] = [];

    /** Liste des contenus dans les contenus complexes (avec la clef en ID) */
    public contenuDesContenu: { [index: string]: any } = {};

    /** Constructeur avec injection des dépendances */
    public constructor(private contexte: ContexteService, private donneesService: DonneesService, private logger: NGXLogger) { super(); }

    /** A l'initialisation du composant */
    public ngOnInit(): void {

        //Recalcul des libellés du récap 
        this.calculerLibellesDuRecapitulatif(this.contexte.langue);

        // Recalcule les titres de touts les au changement de langue
        const sub = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => this.calculerLibellesDuRecapitulatif(langue));
        this.declarerSouscription(sub);
    }

    /**
     * Recalcul des libellés du récap
     * @param langue Langue courante
     */
    private calculerLibellesDuRecapitulatif(langue: string | undefined): void {
        // Lecture de la langue et calcul du suffixe d'un libellé
        const suffixe = langue === 'FR' ? '' : langue;

        // Si on recharge un brouillon, il se peut que le 'titreAafficher' d'un contenu n'ait pas été initialisé (réalisé à la création du formulaire de la page correspondante)
        // Donc, systématiquement, les titres sont recalculés
        const toutesLesDonnees = this.donneesService.lireTout();
        this.contenus.forEach(c => {
            const titre = (c as any)['titre' + suffixe] || c.titre;
            c.titreAafficher = this.donneesService.integrerDesVariablesDansUnLibelle(titre, toutesLesDonnees)
        });

        // Pour chaque contenu complexe
        this.contenus.filter(c => this.definirSiContenuEstDeTypeComplexe(c)).forEach(c => {
            if (c.clef) {
                // On récupère la liste des sous-contenus
                const liste = this.recupererListeDesContenusDunContenuComplexe(c);
                // On initalise le 'titreAafficher' (cf. plus haut dans cette méthode)
                liste.forEach(c => {
                    const titre = (c as any)['titre' + suffixe] || c.titre;
                    c.titreAafficher = this.donneesService.integrerDesVariablesDansUnLibelle(titre, toutesLesDonnees)
                });
                // On conserve la liste
                (this.contenuDesContenu as any)[c.clef] = liste;
            }
        });
    }

    /** Cette méthode s'exécute en boucle depuis l'écran mais est très très simplement implémentée dans DonneesService donc pas de pb de performance à prévoir */
    public lireDonneeSimple(contenu: ContenuDeBloc): string | undefined {

        // La clef à utiliser
        let clef = contenu.clef;
        if (!clef) {
            return undefined;
        }

        // Pour les objets
        if (UtilitaireModel.contenuGereUneValeurDeTypeObjet(contenu)) {
            if (contenu.type === TypeContenuDeBloc.autocompletion) {
                clef = clef + '_' + (contenu as ContenuAutocompletion).attributReponseApiPourLibelle;
            } else if (contenu.type === TypeContenuDeBloc.uploadDocument) {
                clef = clef + '_nom';
            } else {
                this.logger.error('Erreur dans le code car le type \'' + contenu.type + '\' est marqué \'contenuGereUneValeurDeTypeObjet\' mais n\'est pas géré dans le récapitulatif.');
            }
        }

        // La valeur brute
        let valeur = this.donneesService.lire(clef);

        // Pour les cases à cocher et autres saisies OUI/NON (radio ou listeFinie)
        if (UtilitaireModel.contenuGereUneValeurDeTypeBoolean(contenu)) {
            valeur = (valeur === 'true') ? 'Oui' : 'Non';
        }

        // Pour les listes finies ou radio
        else if (contenu.type === TypeContenuDeBloc.listeFinie || contenu.type === TypeContenuDeBloc.radio) {
            const optionSelectionnee = (contenu as ContenuListeFinie).valeurs.filter(option => option.valeur === valeur);
            valeur = optionSelectionnee.length === 1 ? optionSelectionnee[0].libelle : '';
        }

        return valeur;
    }

    /** Si la valeur a été saisie */
    public definirSiDonneeSaisie(contenu: ContenuDeBloc): boolean {
        return !!contenu.clef && (!!this.donneesService.lire(contenu.clef) || !!this.donneesService.lireUnObjet(contenu.clef));
    }

    /** Si le contenu est une unique valeur à afficher */
    public definirSiContenuEstUneSaisieMonoChamp(contenu: ContenuDeBloc): boolean {
        return UtilitaireModel.contenuEstUneSaisieMonoChamp(contenu);
    }

    /** Si le contenu est une unique valeur à afficher */
    public definirSiContenuEstDeTypeComplexe(contenu: ContenuDeBloc): boolean {
        return UtilitaireModel.contenuEstUneSaisieComplexe(contenu);
    }

    /** Obtient la liste des contenus simples contenu dans le composant complexe */
    private recupererListeDesContenusDunContenuComplexe(contenu: ContenuDeBloc): ContenuDeBloc[] {

        // Récupération de la liste des composants simples du composant complexe
        let liste: ContenuDeBloc[] = [];
        if (contenu.type === TypeContenuDeBloc.identite) {
            liste = FmkContenuIdentiteComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, this.contexte.langue);
        } else if (contenu.type === TypeContenuDeBloc.adresseFrOuEtr) {
            liste = FmkContenuAdresseFrOuRtrComponent.calculerLesChampsEtContenusPossibleDuComposant(contenu, this.contexte.langue);
        } else if (contenu.type === TypeContenuDeBloc.contactPersonnel) {
            liste = FmkContenuContactPersonnelComponent.calculerLesChampsEtContenusPossibleDuComposant(this.contexte.langue);
        } else {
            this.logger.error('Erreur de programmation, le composant de type \'' + contenu.type + '\' n\'est pas géré dans le récapitulatif.');
        }

        // récupération des données en cours
        const toutesLesDonnees = this.donneesService.lireToutEtIntegrerLesDonneesFournies({});

        liste.forEach(c => {
            // Modification des clefs des contenus pour les prefixer avec celle du composant
            c.clef = contenu.clef + '_' + c.clef;
            // Calcul de la condition de visibilité (la seule utile ici)
            c.visibilite = this.donneesService.calculerCondition(c.conditionVisibilite, toutesLesDonnees, true);
        });

        return liste;
    }
}
