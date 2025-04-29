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
import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { ConfigurationService, ContexteService, FormulaireService } from '../../../../../public-api';
import { FmkContenuComponent } from '../../../../contenu/fmk.contenu';
import { ContenuDeBloc, ContenuParagraphe, TypeContenuDeBloc } from '../../../../model/configurationdemarchecontenubloc.model';
import { Bloc, Page } from '../../../../model/configurationdemarchegeneral.model';
import { AbstractComponent } from '../../../../utilitaires/abstract.component';
import { UtilitaireBlocDynamique } from '../../../../utilitaires/utilitaire.blocdynamique';
import { UtilitaireModel } from '../../../../utilitaires/utilitaire.model';


@Component({
    selector: 'div[data-fmk-blocdynamique]', templateUrl: './fmk.blocdynamique.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkContenuComponent]
})
export class FmkBlocDynamiqueComponent extends AbstractComponent implements OnInit {

    /** Index de l'occurence en cours d'édition */
    public occurenceEnCoursDedition: number | undefined;

    /** Page en cours */
    @Input()
    public page: Page | undefined;

    /** Bloc dynamique à traiter */
    @Input()
    public bloc: Bloc | undefined;

    /** Formulaire */
    @Input()
    public formulaire: UntypedFormGroup | undefined;

    /** Evenement de création de nouveaux contenus dans une nouvelle occurence */
    @Output()
    public onCreationContenus = new EventEmitter<ContenuDeBloc[]>();

    /** Evenement de suppression de nouveaux contenus dans une nouvelle occurence */
    @Output()
    public onDestructionContenus = new EventEmitter<ContenuDeBloc[]>();

    /** Liste des contenus de chaque occurence de chaque bloc */
    public contenusDupliques: Map<number, ContenuDeBloc[]> = new Map();

    /** Liste des occurences existantes pour chaque bloc dynamique */
    public listeDesOccurences: number[] = [];

    public contenuAffichageBlocDynamique: ContenuParagraphe | undefined;

    /** Constructeur pour injection des dépendances. */
    public constructor(private contexte: ContexteService, private formulaireService: FormulaireService, private configurationService: ConfigurationService, private logger: NGXLogger) { super(); }

    /** Au chargement du composant */
    public ngOnInit(): void {
        // Si une condition d'affichage est présente
        if (this.page && this.formulaire && this.bloc) {

            // Copie de la condition d'affichage du bloc dynamique dans le contenu de gestion de l'affichage (le texte permet d'identifier le paragraphe dans les logs)
            this.contenuAffichageBlocDynamique = new ContenuParagraphe();
            this.contenuAffichageBlocDynamique.type = TypeContenuDeBloc.paragraphe;
            this.contenuAffichageBlocDynamique.texte = 'contenuAffichageBlocDynamique';
            this.contenuAffichageBlocDynamique.conditionVisibilite = (this.bloc.conditionVisibilite) ? this.bloc.conditionVisibilite : 'true';
            UtilitaireModel.initialiserLeOuLesTitresEtAides(this.contenuAffichageBlocDynamique);

            // Création du controle lié au contenu
            const subs = this.formulaireService.creerLesControlesDunFormulaire([this.contenuAffichageBlocDynamique], this.formulaire);
            this.declarerSouscriptions(subs);

            // recalcul de la condition du contenu
            this.formulaireService.calculerLesConditionsDeTousLesContenus([this.contenuAffichageBlocDynamique], this.formulaire, this.contexte.langue);

            // Recalcule les titres de touts les au changement de langue
            const sub = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => {
                if (this.formulaire && langue && this.contenuAffichageBlocDynamique) {
                    this.formulaireService.calculerLesConditionsDeTousLesContenus([this.contenuAffichageBlocDynamique], this.formulaire, langue);
                }
            });
            this.declarerSouscription(sub);

            // On prévient la page de la création des nouveaux contenus
            this.onCreationContenus.next([this.contenuAffichageBlocDynamique]);
        }

        // Pré-initialisation des occurences si des données existent déjà (cas du retour arrière)
        this.preinitialiserLesOccurences();
    }

    /** Détection des occurences existantes dans les données déjà présentes */
    private preinitialiserLesOccurences(): void {
        // Pour chaque occurence possible (en commençant par la fin)
        if (this.bloc && this.bloc.maxOccurences) {
            for (let i = 0; i < this.bloc?.maxOccurences; i++) {
                // Recherche d'une valeur pour un des champs (en remettant la clef originale)
                const contenusValorises = this.bloc?.contenus.filter(c => {
                    const clefOriginale = c.clef;
                    c.clef = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(c.clef, i);
                    const valeurTrouvee = !!this.formulaireService.determinerValeurParDefaut(c);
                    c.clef = clefOriginale;
                    return valeurTrouvee;
                });
                if (contenusValorises.length > 0) {
                    // log
                    this.logger.info(contenusValorises.length + ' contenus avec une valeur dans l\'occurence ' + i);
                    // ajout de l'occurence précise
                    this.ajouterUneOccurencePrecise(i);
                }
            }
        }
    }

    /** Ajout d'une occurence de ce bloc dynamique dans le formulaire */
    public ajouterUneOccurence(): void {

        // Recherche du premier numéro d'occurence disponible
        let occurence = 0;
        while (this.listeDesOccurences.includes(occurence)) {
            occurence++;
        }

        // ajout de l'occurence précise
        this.ajouterUneOccurencePrecise(occurence);

        // A la création, la nouvelle occurence est tout de suite en édition
        this.occurenceEnCoursDedition = occurence;
    }

    /** Ajout d'une occurence à partir d'un numéro. */
    private ajouterUneOccurencePrecise(occurence: number): void {
        // Au cas où
        if (!this.formulaire || !this.page || !this.bloc) {
            return;
        }

        // Ajout de l'occurence à la liste
        this.listeDesOccurences.push(occurence);

        // Clone des contenus 
        const nouveauxContenus = this.configurationService.clonerContenusPourUneOccurenceDeBlocDynamique(this.bloc, occurence);

        // Sauvegarde des contenus de chaque occurence
        this.contenusDupliques.set(occurence, nouveauxContenus);

        // Création de la liste des contenus de la page (hors blocs dynamiques)
        const contenusDeLaPage: ContenuDeBloc[] = [];
        this.page.blocs.filter(b => !b.dynamique).forEach(b => contenusDeLaPage.push(...b.contenus));

        // Création des controles liés aux contenus
        const subs = this.formulaireService.creerLesControlesDunFormulaire(nouveauxContenus, this.formulaire, contenusDeLaPage);
        this.declarerSouscriptions(subs);

        // recalcul des conditions des contenus
        this.formulaireService.calculerLesConditionsDeTousLesContenus(nouveauxContenus, this.formulaire, this.contexte.langue);

        // On prévient la page de la création des nouveaux contenus
        this.onCreationContenus.next(nouveauxContenus);

        // log
        this.logger.trace('Fin de l\'ajout d\'une occurence de bloc dynamique ', { bloc: this.bloc, nouveauxContenus: nouveauxContenus, formulaire: this.formulaire.controls });
    }

    /** Suppression d'une occurence */
    public supprimerOccurence(occurence: number): void {
        // Retrait de l'occurence de la liste
        const indexDeLoccurrence = this.listeDesOccurences.indexOf(occurence);
        if (indexDeLoccurrence !== -1) {
            this.listeDesOccurences.splice(indexDeLoccurrence, 1);
        }

        // Si l'occurence en cours d'édition est celle supprimée, on reset l'occurence en cours d'édition
        if (occurence === this.occurenceEnCoursDedition) {
            this.occurenceEnCoursDedition = undefined;
        }

        // Récupération des contenus dupliqués pour cette occurence
        const contenusAsupprimer = this.contenusDupliques.get(occurence);

        if (contenusAsupprimer) {
            // Destruction des contrôles associés aux contenus à retirer
            if (this.formulaire) {
                this.logger.info('Desctruction des contrôles de l\'occurence ' + occurence);
                this.formulaireService.detruireLesControlesDunFormulaire(this.formulaire, contenusAsupprimer);
            }

            // Prévenir la page
            this.onDestructionContenus.next(contenusAsupprimer);
        }
    }

    /** Recherche d'un contenu précis pour une occurence */
    public recupererContenuDunBlocDynamique(occurence: number, indexContenuOriginal: number): ContenuDeBloc | undefined {
        const contenusOccurence = this.contenusDupliques.get(occurence);
        return contenusOccurence ? contenusOccurence[indexContenuOriginal] : undefined;
    }

    /** Recherche du controle dans le formulaire correspondant à un contenu de bloc dynamique*/
    public recupererControleAssocieAuContenu(occurence: number, indexContenuOriginal: number): UntypedFormControl | UntypedFormGroup | undefined {
        const contenu = this.recupererContenuDunBlocDynamique(occurence, indexContenuOriginal);
        if (this.formulaire && contenu) {
            return this.formulaireService.recupererControleAssocieAuContenu(contenu, this.formulaire);
        } else {
            return undefined;
        }
    }

    /** Vérification du nombre d'occurences déjà présentes VS le nombre maximal autorisé. */
    public calculerSiNouvelleOccurenceEstAutorisee(): boolean {
        return !!this.bloc && !!this.bloc.maxOccurences && this.listeDesOccurences.length < this.bloc.maxOccurences;
    }

    /** Gestion de l'édition */
    public quitterModeEdition(): void {
        this.occurenceEnCoursDedition = undefined;
    }

    /** Gestion de l'édition */
    public passerEnModeEdition(occurence: number): void {
        this.occurenceEnCoursDedition = occurence;
    }
}
