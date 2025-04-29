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
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { FmkMessagesValidationComponent } from '../../contenu/messagesvalidation/fmk.messagesvalidation';
import { TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { i18nPipeDirective } from '../../morceaudepage/directives/i18nPipe';
import { ContexteService } from '../../services/stateful/contexte.service';
import { ValidationService } from '../../services/stateless/validation.service';

import { CdkScrollable } from '@angular/cdk/scrolling';

@Component({
    selector: 'div[data-fmk-connexionreprisebrouillon]', templateUrl: './fmk.connexionbrouillon.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [CdkScrollable, MatDialogContent, ReactiveFormsModule, FmkMessagesValidationComponent, MatDialogActions, i18nPipeDirective]
})
export class ConnexionBrouillonDialog implements OnInit {

    /** Flag indiquant si la fenêtre modale est utilisée pour la sauvegarde ou la reprise du brouillon */
    public flagReprise = false;

    /** Contrôle associé au champ 'nomUtilisateur' (valeur par défaut vide) */
    public controleNomUtilisateur = new FormControl('', { updateOn: 'blur' });

    /** Contrôle associé au champ 'motDePasse' (valeur par défaut vide) */
    public controleMotDePasse = new FormControl('', { updateOn: 'blur' });

    /** Flag venant de la configuration de la démarche */
    public modeObligatoireParDefaut = false;

    /** Calcul de la validation du champ 'nomUtilisateur' */
    public get saisieNomUtilisateurValide(): boolean {
        return !this.controleNomUtilisateur || (!this.controleNomUtilisateur.invalid && (this.controleNomUtilisateur.dirty || this.controleNomUtilisateur.touched));
    }

    /** Calcul de la validation du champ 'nomUtilisateur' */
    public get saisieNomUtilisateurInvalide(): boolean {
        return !this.controleNomUtilisateur || (this.controleNomUtilisateur.invalid && (this.controleNomUtilisateur.dirty || this.controleNomUtilisateur.touched));
    }
    /** Calcul de la validation du champ 'motDePasse' */
    public get saisieMotDePasseValide(): boolean {
        return !this.controleMotDePasse || (!this.controleMotDePasse.invalid && (this.controleMotDePasse.dirty || this.controleMotDePasse.touched));
    }

    /** Calcul de la validation du champ 'motDePasse' */
    public get saisieMotDePasseInvalide(): boolean {
        return !this.controleMotDePasse || (this.controleMotDePasse.invalid && (this.controleMotDePasse.dirty || this.controleMotDePasse.touched));
    }

    /** Constructeur permettant l'injection de dépendances */
    constructor(private validationService: ValidationService, private contexteService: ContexteService, private dialogRef: MatDialogRef<ConnexionBrouillonDialog>) { }

    /**Initialisation du composant */
    public ngOnInit(): void {

        // Initialisation du champ 'nomUtilisateur'
        this.controleNomUtilisateur.addValidators(this.validationService.creerValidationsSynchrones(['required', 'email'], TypeContenuDeBloc.saisie));
        this.controleNomUtilisateur.addAsyncValidators(this.validationService.creerValidationsAsynchrones());

        // Initialisation du champ 'motDePasse'
        this.controleMotDePasse.addValidators(this.validationService.creerValidationsSynchrones(['required', 'motDePasse'], TypeContenuDeBloc.saisie));
        this.controleMotDePasse.addAsyncValidators(this.validationService.creerValidationsAsynchrones());

        // Récupération du flag 'modeObligatoireParDefaut' depuis la configuration de la démarche
        this.modeObligatoireParDefaut = !!this.contexteService.configurationDemarche?.fonctionnalites?.modeObligatoireParDefaut;
    }

    /** Si l'utilisateur annule sa saisie, la modale renvoie le nom de l'utilisateur et le mot de passe saisi. */
    public confirmer(): void {
        this.dialogRef.close({ nomUtilisateur: this.controleNomUtilisateur?.value, motDePasse: this.controleMotDePasse?.value });
    }

    /** Si l'utilisateur annule sa saisie, la modale ne renvoie rien. */
    public annuler(): void {
        this.dialogRef.close(undefined);
    }
}