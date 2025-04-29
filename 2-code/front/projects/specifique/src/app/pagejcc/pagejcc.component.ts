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
import { Component, Input, OnInit, ViewEncapsulation } from "@angular/core";
import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from "@angular/forms";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { FmkMessagesValidationComponent } from "../../../../framework/src/lib/contenu/messagesvalidation/fmk.messagesvalidation";
import { AbstractComponent } from "../../../../framework/src/lib/utilitaires/abstract.component";
import { DonneesService, Page, Validation, ValidationService } from "../../../../framework/src/public-api";


/**
 * Contenu de la page spécifique de JCC
 */
@Component({
    selector: 'app-pagejcc', templateUrl: './pagejcc.component.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./pagejcc.component.scss'],
    standalone: true,
    imports: [ReactiveFormsModule, FmkMessagesValidationComponent]
})
export class PageJccComponent extends AbstractComponent implements OnInit {

    /** Liste des destinataires actifs */
    public destinatairesActifs: { [index: string]: any; } | undefined = {};

    /** Etat des destinataires sélectionnés au chargement de la page */
    public destinatairesSelectionnees: { [index: string]: any; } | undefined = {};

    /** Formulaire */
    @Input()
    public formulaire: UntypedFormGroup | undefined;

    /** La configuration de la page */
    @Input()
    public page: Page | undefined;

    /** Flag de gestion de l'affichage de l'aide de chacun des champs */
    public flagAfficherAide: { [index: string]: any; } = {};

    /** Flag conditionnant l'affichage du champs de numéro de sécurité social */
    public flagMasquerNumeroSecu = true;

    /** Calcul de la validation du champ de numéro de sécurité sociale pour ne pas le coder plusieurs fois */
    public get saisieNumeroSecuValide(): boolean {
        const ctrl = this.formulaire?.get('numeroSecu');
        return !ctrl || (!ctrl.invalid && (ctrl.dirty || ctrl.touched));
    }

    /** Calcul de la validation du champ de numéro de sécurité sociale pour ne pas le coder plusieurs fois */
    public get saisieNumeroSecuInvalide(): boolean {
        const ctrl = this.formulaire?.get('numeroSecu');
        return !ctrl || (ctrl.invalid && (ctrl.dirty || ctrl.touched));
    }

    /** Constructeur pour injection des dépendances */
    public constructor(private donneesService: DonneesService, private validationService: ValidationService) { super(); }

    /** Au chargement du composant */
    public ngOnInit(): void {
        this.initialiserFormulaireAvecLesDestinataires();
        this.completerFormulaireAvecChampNumeroSecu();
    }

    /** Initialisation du formulaire avec la liste des destinataires */
    private initialiserFormulaireAvecLesDestinataires(): void {

        // Récupération de la liste des destinataires actifs depuis la configuration
        this.destinatairesActifs = this.donneesService.lireUnObjet('destinatairesActifs');

        // Définition des valeurs par défaut (le ternaire de la seconde ligne est pour le premier accès à la page uniquement)
        this.destinatairesSelectionnees = this.donneesService.lireUnObjet('destinatairesSelectionnees');
        this.destinatairesSelectionnees = this.destinatairesSelectionnees ? this.destinatairesSelectionnees : {};

        // Création du formulaire
        for (const clef in this.destinatairesActifs) {

            // Initialisation du flag d'aide de chaque destinataire
            this.flagAfficherAide[clef] = false;

            // Ajout du champ dans le formulaire (valeur par défaut inutile)
            const valeurParDefaut = this.destinatairesSelectionnees[clef] ? this.destinatairesSelectionnees[clef] : '';
            if (this.formulaire) {
                this.formulaire.addControl(clef.replace('destinatairesActifs', 'destinatairesSelectionnees'), new UntypedFormControl(valeurParDefaut, { updateOn: 'blur', validators: [] }));
            }
        }

    }

    /** Ajout du mini-formulaire affiché si un destinataire du cadre "santé et retraite" est coché */
    private completerFormulaireAvecChampNumeroSecu(): void {
        if (!this.formulaire) {
            return;
        }

        // Initialisation du flag d'affichage de l'aide
        const clefNumeroSecu = 'numeroSecu';
        this.flagAfficherAide[clefNumeroSecu] = false;

        // Ajout du champs numéro de sécu
        const validationsSecu = this.validationService.creerValidationsSynchrones([Validation.required, Validation.secu]);
        const valeurParDefaut = this.donneesService.lire(clefNumeroSecu);
        this.formulaire.addControl(clefNumeroSecu, new UntypedFormControl({ value: valeurParDefaut, disabled: true }, { updateOn: 'blur', validators: validationsSecu }));

        // Définition de la liste des champs associés à l'affichage du numéro de sécu
        const listeControleDuNumeroSecu = [this.formulaire.get('destinatairesSelectionnees_cnmss'), this.formulaire.get('destinatairesSelectionnees_agirc'),
        this.formulaire.get('destinatairesSelectionnees_cpam'), this.formulaire.get('destinatairesSelectionnees_cdc'),
        this.formulaire.get('destinatairesSelectionnees_cnav'), this.formulaire.get('destinatairesSelectionnees_msa'),
        this.formulaire.get('destinatairesSelectionnees_camieg'), this.formulaire.get('destinatairesSelectionnees_enim')];

        // Fonction appelée à chaque changement de la valeur de l'un des champs ci-dessus
        const controleNumeroSecu = this.formulaire.get(clefNumeroSecu);
        const afficherMasquerNumeroSecu = () => {
            // Si un des champs est coché
            this.flagMasquerNumeroSecu = !listeControleDuNumeroSecu.map(ctrl => !!ctrl && ctrl.value === 'true').reduce((cumule, courant) => cumule || courant);
            // Activation/désactivation du controle pour activer/désactiver les validators associés et donc valider/invalider le formulaire
            if (controleNumeroSecu) {
                if (controleNumeroSecu.enabled && this.flagMasquerNumeroSecu) {
                    controleNumeroSecu.disable({ emitEvent: false });
                    controleNumeroSecu.setValue('');
                } else if (controleNumeroSecu.disabled && !this.flagMasquerNumeroSecu) {
                    controleNumeroSecu.enable({ emitEvent: false });
                }
            }
        };

        // Ajout de la surveillance des champs conditionnant l'affichage du numéro de sécurité sociale
        // debounce est nécessaire pour attendre que le changement de valeur soit effectif (@see https://github.com/angular/angular/issues/24818)
        listeControleDuNumeroSecu.forEach(controle => {
            if (controle) {
                const sub = controle.valueChanges.pipe(debounceTime(0), distinctUntilChanged()).subscribe(afficherMasquerNumeroSecu);
                this.declarerSouscription(sub);
            }
        });

        // A l'initialisation, appel une première fois à la méthode
        afficherMasquerNumeroSecu();
    }

    /** Affiche ou masque l'aide */
    public afficherMasquerAide(clef: string): void {
        this.flagAfficherAide[clef] = !this.flagAfficherAide[clef];
    }

    /**
     * @see FmkContenuCaseComponent
     */
    public changerLaValeur(e: Event, clef: string): void {
        if (this.formulaire) {
            const controle = this.formulaire.get(clef);
            // Toute donnée de l'application est une chaine de caractère et non un boolean
            // (seul les paramètres comme les destinataires actifs sont des boolean)
            if (controle) {
                controle.setValue((e.target as HTMLInputElement)?.checked + '');
                controle.markAsDirty();
            }
        }
    }
}
