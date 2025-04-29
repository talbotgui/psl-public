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
import { ContexteService } from '../stateful/contexte.service';
import { DonneesService } from '../stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

@Injectable({ providedIn: 'root' })
export class ReferentielService extends ClientApiService {

    /** URI de base du service (l'URL est construite dans le code) */
    private static readonly URI_REFERENTIEL_EXTERNE = '/socle/referentielexterne/';

    /** URI de base du service (l'URL est construite dans le code) */
    private static readonly URI_REFERENTIEL = '/socle/referentiel/';

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, logger: NGXLogger, contexte: ContexteService, private donneesService: DonneesService, private bouchon: BouchonService, private environnementService: EnvironnementService) {
        super(http, logger, contexte);
    }

    /**
     * Méthode appelant un référentiel du socle (interne ou externe)
     * @param api API à appeler.
     * @param valeur Valeur à rechercher.
     * @param complementAppelApi Potentiel complément à ajouter dans l'URL de l'API
     * @returns La liste des résultats remontés par l'API
     */
    public appelerReferentiel(api: string, valeur: string, complementAppelApi?: string): Observable<any[]> {

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiReferentielEstBouchonne()) {
            return this.bouchon.appelerReferentielBouchonne(api, valeur);
        }

        this.logger.trace('Appel à l\'API \'' + api + '\' avec le paramètre \'' + valeur + '\' et le complément \'' + complementAppelApi + '\'');

        // Vérification que la longueur de données saisie suffit
        if ((api === 'communeban' || api === 'adresseban') && valeur && valeur.length < 3) {
            return of([]);
        }

        // Définition de l'URL
        let url;
        if (api === 'communeban' || api === 'adresseban') {
            url = this.environnementService.urls.socle.gateway + ReferentielService.URI_REFERENTIEL_EXTERNE;
        } else {
            url = this.environnementService.urls.socle.gateway + ReferentielService.URI_REFERENTIEL;
        }
        url += api + '?recherche=' + valeur;
        if (complementAppelApi) {
            url += '&' + complementAppelApi;
        }

        // Appel
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut: [] = [];
        return this.get<any[]>('referentiel' + api, url, options, valeurParDefaut);
    }
}
