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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { DonneesDeSoumission } from '../../model/brouillonsoumissiondemarche.model';
import { ContexteService } from '../../services/stateful/contexte.service';
import { DonneesService } from '../../services/stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

@Injectable({ providedIn: 'root' })
export class SoumissionService extends ClientApiService {

    /** URI du service de soumission de télé-dossier */
    private static readonly URI_SOUMISSION = '/socle/soumission/soumettre';

    /** URI du captcha */
    private static readonly URI_CAPTCHA = '/socle/soumission/captcha';

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, contexte: ContexteService, private donnees: DonneesService, logger: NGXLogger, private bouchon: BouchonService, private environnementService: EnvironnementService) {
        super(http, logger, contexte);
    }

    /** Création de l'URL du captcha. */
    public genererCaptcha(): Observable<string> {
        // Si les APIs du micro-service de soumission sont bouchonnées, renvoi d'une constante contenant du code HTML
        if (this.bouchon.verifierSiSoumissionEstBouchonnee()) {
            return this.bouchon.simulerAppelGenerationCaptcha();
        }

        this.logger.info('Appel au captcha');

        const url = this.environnementService.urls.socle.gateway + SoumissionService.URI_CAPTCHA;
        const options = this.creerOptionsTexteSimpleAvecAuthentification();
        const valeurParDefaut = '';
        return this.get<string>('genererCaptcha', url, options, valeurParDefaut);
    }

    /** Chargement d'une ressource du captcha (le JS en théorie) */
    public chargerRessourceCaptcha(url: string): Observable<string> {
        // Si les APIs du micro-service de soumission sont bouchonnées, renvoi d'une constante contenant du code HTML
        if (this.bouchon.verifierSiSoumissionEstBouchonnee()) {
            return of('');
        }

        this.logger.info('Appel au JS du captcha');

        const options = this.creerOptionsTexteSimpleAvecAuthentification();
        const valeurParDefaut = '';
        return this.get<string>('chargerRessourceCaptcha', url, options, valeurParDefaut);
    }

    /** Soumission d'un télé-dossier. */
    public soumettreTeledossier(): Observable<string | undefined> {

        // Création des données
        const soumission = new DonneesDeSoumission();
        if (this.contexte.configurationDemarche) {
            soumission.codeDemarche = this.contexte.configurationDemarche.codeDemarche;
            soumission.versionConfiguration = this.contexte.configurationDemarche.versionConfiguration;
            soumission.idBrouillon = this.contexte.idBrouillon;
            soumission.langue = this.contexte.langue;
            soumission.donnees = this.donnees.lireTout();
        }

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiSoumissionEstBouchonnee()) {
            return this.bouchon.appelerSoumissionBouchonnee(soumission);
        }

        // log
        this.logger.info('La soumission se fait avec les données suivantes : ', soumission);

        // appel
        const url = this.environnementService.urls.socle.gateway + SoumissionService.URI_SOUMISSION;
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = undefined;
        return this.post<string | undefined>('soumettreTeledossier', url, soumission, options, valeurParDefaut)
            .pipe(map(numeroTeledossier => {
                this.logger.info('Soumission terminée avec succès : ' + numeroTeledossier);
                this.donnees.sauvegarder("numeroTeledossier", numeroTeledossier);
                return numeroTeledossier;
            }));
    }
}
