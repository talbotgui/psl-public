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
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { EnvironnementService } from '../../../../framework/src/lib/services/stateful/environnement.service';

/**
 * Cette classe contient les méthodes de bouchonnage des APIs.
 * 
 * Les bouchons sont impérativement utilisés dans les tests E2E.
 * Ils peuvent aussi l'être durant les développements.
 * 
 * Comment bouchonner un simple appel GET :
 * - mettre un fichier JSON (ou autre) dans le répertoire bouchonapi
 * - modifier l'URL dans le fichier this.environnementService.ts (et ses variantes) pour pointer sur le fichier du répertoire bouchonapi
 * Comment bouchonner un GET complexe ou un PUT/POST/DELETE
 * - ajouter une variable 'xxxxxBouchonne' dans la configuration
 * - vérifier cette variable dans le service réel
 * - si la variable existe et est 'true', faire appel à une méthode statique de cette classe
 */
@Injectable({ providedIn: 'root' })
export class AdminBouchonService {

    /** La liste des jeux de données à utiliser dans le bouchon. */
    private static readonly MINI_JDD = {
        '/socle/adminpsl/demarche': ['arnaqueInternet', 'etatCivil', 'biliotheque', 'operationtranquillitevacances', 'daua', 'DICPE', 'EICPE', 'DIOTA', 'DAENV'],
        '/socle/adminpsl/config/{codeDemarche}/xxx': { 'id1': 'libelleVersion1', 'id2': 'libelleVersion2' },
        '/socle/adminpsl/config/{codeDemarche}/xxx/{idConfiguration}': { 'codeDemarche': 'codeDemarche', 'version': 'v1', 'documentsAgenerer': [{ 'code': 'doc1', 'template': 'dGVtcGxhdGVEb2Mx' }, { 'code': 'doc2', 'template': 'dGVtcGxhdGVEb2My' }] },
        '/socle/adminpsl/transfert': { resultats: [{ 'codeDemarche': 'arnaqueInternet', 'numeroTeledossier': 'SP2-AI-0000000000', 'dateCreation': new Date() }], nombreTotalResultats: 1 }
    };

    /** Constructeur pour injection de dépendance. */
    public constructor(private logger: NGXLogger, private environnementService: EnvironnementService) {

        // Construction dynamique du jeu de données
        const page = AdminBouchonService.MINI_JDD['/socle/adminpsl/transfert'];
        page.resultats = [];
        const nbPages = 21;
        for (let i = 0; i < nbPages; i++) {
            const index = ((i < 10) ? '0' : '') + i;
            page.resultats.push({ 'codeDemarche': 'arnaqueInternet', 'numeroTeledossier': 'SP2-AI-00000000' + index, 'dateCreation': new Date() });
        }
        page.nombreTotalResultats = page.resultats.length;
    }

    /** Pour vérifier si la configuration demande à utiliser le bouchon. */
    public verifierSiApiEstBouchonne(): boolean {
        return this.environnementService.urls.socle.adminpslBouchonnee && this.environnementService.urls.socle.adminpslBouchonnee === true;
    }

    /** Pour remplacer un appel d'API */
    public appelerApiBouchonne(api: string, valeur: string): Observable<any> {
        this.logger.trace('Appel bouchonné à l\'API \'' + api + '\' avec le paramètre \'' + valeur + '\'');
        return of((AdminBouchonService.MINI_JDD as any)[api]);
    }
}
