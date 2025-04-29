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
import { UntypedFormGroup } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { Observable, combineLatest } from 'rxjs';
import { FmkConteneurPagesComponent } from '../../../framework/src/lib/morceaudepage/conteneurPages/fmk.conteneurpages';
import { FmkPiedDePageComponent } from '../../../framework/src/lib/morceaudepage/pieddepage/fmk.pieddepage';
import { BrouillonService, ConfigurationService, ContexteService, DonneesService, FmkApplicationAbstraitComponent, FormulaireService, OIDCService, Page, SecuriteService } from '../../../framework/src/public-api';

import { FmkEnteteComponent } from '../../../framework/src/lib/morceaudepage/entete/fmk.entete';
import { EnvironnementService } from '../../../framework/src/lib/services/stateful/environnement.service';

/**
 * Les comportements de base de la classe AppComponent sont dans la classe FmkApplicationAbstraitComponent
 */
@Component({
    selector: 'app-root', templateUrl: './app.component.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [FmkEnteteComponent, FmkConteneurPagesComponent, FmkPiedDePageComponent]
})
export class AppComponent extends FmkApplicationAbstraitComponent {

    /** Observable de l'index de page courant */
    public indexPageCouranteObservable: Observable<number> | undefined;
    public page: Page | undefined;
    public formulaire: UntypedFormGroup | undefined;

    /** Constructeur pour injection des dépendances */
    public constructor(
        // Dépendances nécessaires pour la classe parente
        oidcService: OIDCService, contexteService: ContexteService, configurationService: ConfigurationService, donneesService: DonneesService, brouillonService: BrouillonService, securiteService: SecuriteService, logger: NGXLogger, environnementService: EnvironnementService,
        // Dépendances pour cette classe
        private formulaireService: FormulaireService
    ) {
        super(oidcService, contexteService, configurationService, donneesService, brouillonService, securiteService, logger, environnementService);
    }

    /** Au chargement du composant */
    public override ngOnInit(): void {
        // Appel à la méthode parente
        super.ngOnInit();

        this.formulaire = this.formulaireService.creerUnFormulaireVide();

        // Récupération de l'observable de l'index courant de la page
        this.indexPageCouranteObservable = this.contexteService.obtenirUnObservableDeChangementDePage();

        // Récupération de la page associée (à partir de l'index et de la configuration)
        // La souscription n'est pas à détruire car le composant APP reste actif jusqu'à la fin de vie de l'application
        combineLatest([
            this.contexteService.obtenirUnObservableDeChargementDeLaConfiguration(),
            this.contexteService.obtenirUnObservableDeChangementDePage()
        ]).subscribe(([configuration, indexPage]) => this.page = (configuration) ? configuration.pages[indexPage] : undefined);
    }
}
