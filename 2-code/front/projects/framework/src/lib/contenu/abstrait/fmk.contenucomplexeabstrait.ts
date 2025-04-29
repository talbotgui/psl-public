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
import { Directive, Input, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { ContenuDeBloc, ContenuSaisieComplexe } from '../../model/configurationdemarchecontenubloc.model';
import { ContexteService } from '../../services/stateful/contexte.service';
import { FormulaireService } from '../../services/stateless/formulaire.service';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';
import { FmkContenuAbstraitComponent } from './fmk.contenuabstrait';

/**
 * Cette classe fournit les comportements de base de tous les contenus avec du contenu riche.
 * 
 * @see cf. https://angular.io/guide/reactive-forms#grouping-form-controls
 */
// @Directive pour pouvoir utilisé les annotations Angular dans cette classe
@Directive()
export abstract class FmkContenuComplexeAbstraitComponent extends FmkContenuAbstraitComponent implements OnInit {

    /**
     * Controle associé au champ de saisie.
     * Pour que le contrôle soit associé à un FormGroup, il doit être initialié au niveau de la page (cf. https://angular.io/guide/reactive-forms#grouping-form-controls).
     */
    @Input()
    public groupeControles: UntypedFormGroup | undefined;

    /** Pour ne pas appeler systématiquement la méthode formulaireService.recupererControleAssocieAuContenu, on en initialise les résultats et on les garde */
    public referenceDesControlesParClef: { [index: string]: any } = {};

    /** GETTER pour ne pas caster */
    public get contenuComplexe(): ContenuSaisieComplexe { return this.contenu as ContenuSaisieComplexe; }

    /** Langue précédente (utilisée pour limiter les changements multiples) */
    private languePrecedente: string | undefined;

    /** Constructeur avec injection des dépendances */
    public constructor(protected contexte: ContexteService, private formulaireService: FormulaireService, private logger: NGXLogger) { super(); }

    /** A la création du composant */
    public ngOnInit(): void {
        // Appel aux composants pour modifier les libellés pour la langue sélectionnée.
        const sub = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => {
            if (langue && langue !== this.languePrecedente) {
                this.logger.info('Changement de langue pour le composant \'' + this.contenu?.clef + '\' (\'' + langue + '\')');
                this.auChangementDeLangue(langue);
                this.languePrecedente = langue;
            }
        });
        this.declarerSouscription(sub);
    }

    /** Méthode de l'instance permettant de changer les libellés au changement de langue */
    protected abstract auChangementDeLangue(langueCourante: string): void;

    /** A l'initialisation */
    protected override initialiserComposant(contenu: ContenuDeBloc): void {

        // Récupération de la liste champs possible du composant complexe
        this.contenuComplexe.sousContenus = this.obtenirLesChampsEtContenusPossibleDuComposant(contenu);

        // Gestion de la visibilité de ces champs
        // (/!\code dupliqué dans ContenuComplexeTestUtils)
        const champsVisibles = (contenu as ContenuSaisieComplexe).champsVisibles;
        this.contenuComplexe.sousContenus.forEach(cc => {
            if (!!cc.clef && champsVisibles.indexOf(cc.clef) === -1) {
                cc.conditionVisibilite = 'false';
            }
        });

        // Gestion des validations de ces champs (masque tout puis active)
        //  (/!\code dupliqué dans ContenuComplexeTestUtils)
        const validations = (contenu as ContenuSaisieComplexe).validationsComplexes;
        Object.keys(validations).forEach(champs => {
            const sousContenu = this.contenuComplexe.sousContenus.find(c => c.clef === champs) as ContenuDeBloc;
            if (!sousContenu) {
                this.logger.error('Erreur de configuration des validations : le champs \'' + champs + '\' n\'existe pas pour le composant complexe de type ' + contenu.type + ' avec la clef ' + contenu.clef + '.');
            }
            else if (validations[champs] && validations[champs].length) {
                this.logger.info('Surcharge de la validation de \'' + champs + '\' avec ' + validations[champs]);
                (sousContenu as any)['validationsSimples'] = validations[champs];
            }
        });

        // Ajout des libellés d'aide
        const aides = (contenu as ContenuSaisieComplexe).aides;
        if (aides) {
            Object.keys(aides).forEach(champs => {
                const sousContenu = this.contenuComplexe.sousContenus.find(c => c.clef === champs) as ContenuDeBloc;
                if (!sousContenu) {
                    this.logger.error('Erreur de configuration des aides : le champs \'' + champs + '\' n\'existe pas pour le composant complexe de type ' + contenu.type + ' avec la clef ' + contenu.clef + '.');
                }
                else if (aides[champs]) {
                    sousContenu.aide = aides[champs];
                }
            });
        }

        // Ajout des titres personnalisés
        const titres = (contenu as ContenuSaisieComplexe).titres;
        if (titres) {
            Object.keys(titres).forEach(champs => {
                const sousContenu = this.contenuComplexe.sousContenus.find(c => c.clef === champs) as ContenuDeBloc;
                if (!sousContenu) {
                    this.logger.error('Erreur de configuration des titres : le champs \'' + champs + '\' n\'existe pas pour le composant complexe de type ' + contenu.type + ' avec la clef ' + contenu.clef + '.');
                }
                else if (titres[champs]) {
                    sousContenu.titre = titres[champs];
                }
            });
        }

        this.contenuComplexe.sousContenus.forEach(c => {
            // Modification des clefs des contenus pour les prefixer avec celle du composant
            c.clef = ((this.contenu) ? this.contenu.clef : '') + '_' + c.clef;
            // Initialisation des données du contenu autour de la gestion des titres et des aides (pouvant contenir des variables)
            UtilitaireModel.initialiserLeOuLesTitresEtAides(c);
        });


        if (this.groupeControles) {
            // Pour chaque bloc, création des controles liés aux contenus
            const subs = this.formulaireService.creerLesControlesDunFormulaire(this.contenuComplexe.sousContenus, this.groupeControles as UntypedFormGroup);
            this.declarerSouscriptions(subs);

            // Calculer les conditions d'affichage
            this.formulaireService.calculerLesConditionsDeTousLesContenus(this.contenuComplexe.sousContenus, this.groupeControles as UntypedFormGroup, this.contexte.langue);

            // Recalcule les titres de touts les au changement de langue
            const sub2 = this.contexte.obtenirUnObservableDeChargementDeLaLangue().subscribe(langue => {
                if (this.groupeControles && langue && this.contenuComplexe.sousContenus) {
                    this.formulaireService.calculerLesConditionsDeTousLesContenus(this.contenuComplexe.sousContenus, this.groupeControles as UntypedFormGroup, langue);
                }
            });
            this.declarerSouscription(sub2);

            // Log
            this.logger.debug('Initialisation terminée du composant complexe \'' + contenu.clef + '\'', { contenusComposant: this.contenuComplexe.sousContenus, groupeControles: (this.groupeControles as UntypedFormGroup).controls });

            // Calcul du cache pour la méthode recupererControleAssocieAuContenu
            this.contenuComplexe.sousContenus.forEach(c => {
                if (c.clef) {
                    this.referenceDesControlesParClef[c.clef] = this.formulaireService.recupererControleAssocieAuContenu(c, this.groupeControles as UntypedFormGroup);
                }
            });
        }
    }

    /**
     * Méthode retournant la liste des champs possibles du composant
     * avec la condition de visibilité à 'true' ou avec une condition intelligente
     * avec la condition de désactivation à 'false'
     * et les validations à []. 
     */
    public abstract obtenirLesChampsEtContenusPossibleDuComposant(contenu: ContenuDeBloc): ContenuDeBloc[];


    /** Méthode permettant de récupérer un contrôle à partir du contenu */
    public recupererControleAssocieAuContenu(contenu: ContenuDeBloc): UntypedFormControl | undefined {
        if (contenu.clef) {
            return this.referenceDesControlesParClef[contenu.clef];
        } else {
            return undefined;
        }
    }

}
