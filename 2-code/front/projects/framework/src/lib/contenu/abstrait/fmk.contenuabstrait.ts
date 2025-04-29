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
import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ContenuDeBloc, Validation } from '../../model/configurationdemarchecontenubloc.model';
import { AbstractComponent } from '../../utilitaires/abstract.component';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';

/**
 * Cette classe fournit les comportements de base de tous les contenus.
 * 
 * Dans l'application, tous les formulaires sont des REACTIVEs FORMs pour les raisons de validation et de performance (cf. https://angular.io/guide/forms-overview & https://angular.io/guide/reactive-forms).
 * 
 */
// @Directive pour pouvoir utilisé les annotations Angular dans cette classe
@Directive()
export abstract class FmkContenuAbstraitComponent extends AbstractComponent implements OnChanges {

    /** Compteur static permettant de créer manuellement des ID différents pour chaque champ */
    private static PROCHAINE_VALEUR_DU_NUMERO_UNIQUE = 0;

    /** Numéro unique de ce champ de saisie */
    public numeroUnique: number;

    /** Configuration du bloc */
    @Input()
    public contenu: ContenuDeBloc | undefined;

    /** Pour ne pas appeler l'initialisation plusieurs fois */
    private initialisationDejaAppelee = false;

    /** Constructeur pour injection des dépendances. */
    public constructor() {
        super();
        this.numeroUnique = FmkContenuAbstraitComponent.PROCHAINE_VALEUR_DU_NUMERO_UNIQUE++;
    }

    /**
     * Pour fournir une méthode d'initialisation de tous les composants dans laquelle
     * l'attribut this.contenu est défini (il ne l'est pas encore dans ngOnInit)
     */
    public ngOnChanges(changes: SimpleChanges): void {
        if (!this.initialisationDejaAppelee && changes['contenu'] && changes['contenu'].currentValue) {
            this.initialiserComposant(changes['contenu'].currentValue);
            this.initialisationDejaAppelee = true;
        }
    }

    /** Méthode à surcharger pour initialiser un composant complexe (et pas ngOnInit @see FmkContenuAbstraitComponent.ngOnChanges) */
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    protected initialiserComposant(contenu: ContenuDeBloc): void {
        // rien à faire mais la méthode et son paramètre doivent rester
    }

    /** Test si le bloc est marqué REQUIRED */
    public get validationObligatoirePresente(): boolean {
        return !!this.contenu && UtilitaireModel.contenuContientCetteValidation(this.contenu, Validation.required);
    }
}
