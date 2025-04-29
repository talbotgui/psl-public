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
import { EnvironnementService } from '../../../../framework/src/lib/services/stateful/environnement.service';
import { AdminContexteService } from './adminContexte.service';
import { AdminBouchonService } from './adminbouchon.service';
import { ClientAdminApiService } from './clientadminapi.service';

@Injectable({ providedIn: 'root' })
export class ConfigurationService extends ClientAdminApiService {

    /** URI de la liste des versions de configuration publique et de la création. */
    private static readonly URI_CONFIG_CREATION_ET_LISTE = '/socle/adminpsl/config/{codeDemarche}/xxx';

    /** URI de la lecture et modification de configuration publique. */
    private static readonly URI_CONFIG_MODIFICATION_ET_LECTURE = '/socle/adminpsl/config/{codeDemarche}/xxx/{idConfiguration}';

    /** Constructeur pour injection de dépendance. */
    public constructor(httpClient: HttpClient, adminContexte: AdminContexteService, logger: NGXLogger, private bouchon: AdminBouchonService, private environnementService: EnvironnementService) {
        super(httpClient, logger, adminContexte);
    }

    /**
     * Chargement d'une configuration.
     * @param publique Configuration publique (true) ou interne (false).
     * @param codeDemarche Code de la démarche.
     * @param idConfiguration ID de la configuration.
     * @returns Configuration chargée.
     */
    public chargerVersionDeConfiguration(publique: boolean, codeDemarche: string, idConfiguration: string): Observable<any> {
        this.logger.info('Chargement d\'une configuration pour ' + codeDemarche + ' - ' + idConfiguration);

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Chargement via le bouchon');
            return this.bouchon.appelerApiBouchonne(ConfigurationService.URI_CONFIG_MODIFICATION_ET_LECTURE, '');
        }

        // Appel réel
        const url = this.construireUrl(ConfigurationService.URI_CONFIG_MODIFICATION_ET_LECTURE, publique, codeDemarche, idConfiguration);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut: any = {};
        return this.get<any>('chargerVersionDeConfiguration', url, options, valeurParDefaut);
    }

    /** Construction de l'URL à appeler en remplaçant xxx par interne/publique et remplaçant les paramètres. */
    private construireUrl(url: string, publique: boolean, codeDemarche: string, idConfiguration: string | undefined): string {
        return this.environnementService.urls.socle.adminpsl + url//
            .replace('xxx', publique ? 'publique' : 'interne')//
            .replace('{codeDemarche}', codeDemarche)//
            .replace('{idConfiguration}', idConfiguration ? idConfiguration : '');
    }

    /**
     * Création d'une nouvelle version de démarche.
     * @param publique Configuration publique (true) ou interne (false).
     * @param codeDemarche Code de la démarche.
     * @param configuration Configuration à sauvegarder.
     * @returns ID de l'objet trouvé.
     */
    public creerVersionDeConfiguration(publique: boolean, codeDemarche: string, configuration: string): Observable<string> {
        this.logger.info('Création d\'une version de configuration pour ' + codeDemarche);

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Chargement via le bouchon');
            return of('nouvelleVersion');
        }

        // Préparation de l'appel
        const url = this.construireUrl(ConfigurationService.URI_CONFIG_CREATION_ET_LISTE, publique, codeDemarche, undefined);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = '';

        // Appel
        return this.post<string>('creerVersionDeConfiguration', url, configuration, options, valeurParDefaut);
    }

    /**
     * Recherche des versions de configuration.
     * @param publique Configuration publique (true) ou interne (false).
     * @param codeDemarche Code de la démarche.
     * @returns Map de versions (ID/libellé)
     */
    public listerLesVersionsDeConfiguration(publique: boolean, codeDemarche: string): Observable<{ [index: string]: any }> {
        this.logger.info('Chargement des versions de configuration pour ' + codeDemarche);

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Chargement via le bouchon');
            return this.bouchon.appelerApiBouchonne(ConfigurationService.URI_CONFIG_CREATION_ET_LISTE, '');
        }

        // Appel réel
        const url = this.construireUrl(ConfigurationService.URI_CONFIG_CREATION_ET_LISTE, publique, codeDemarche, undefined);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut: { [index: string]: any } = {};
        return this.get<{ [index: string]: any }>('listerLesVersionsDeConfiguration', url, options, valeurParDefaut);
    }

    /**
     * Modification de la version de la configuration.
     * @param publique Configuration publique (true) ou interne (false).
    * @param codeDemarche Code de la démarche.
     * @param idConfiguration ID de la configuration à modifier.
     * @param configuration Configuration à sauvegarder.
     * @returns ID de la configuration.
     */
    public modifierVersionDeConfiguration(publique: boolean, codeDemarche: string, idConfiguration: string, configuration: string): Observable<string> {
        this.logger.info('Sauvegarde des modifications d\'une configuration pour ' + codeDemarche + '-' + idConfiguration);

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Chargement via le bouchon');
            return of(idConfiguration);
        }

        // Préparation de l'appel
        const url = this.construireUrl(ConfigurationService.URI_CONFIG_MODIFICATION_ET_LECTURE, publique, codeDemarche, idConfiguration);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = '';

        // Appel
        return this.post<string>('modifierVersionDeConfiguration', url, configuration, options, valeurParDefaut);
    }
}
