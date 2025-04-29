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
import { DatePipe } from '@angular/common';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { APP_INITIALIZER, ApplicationConfig, Provider, provideZoneChangeDetection } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideOAuthClient } from 'angular-oauth2-oidc';
import { NGXLoggerConfigEngineFactory, NGXLoggerMapperService, NGXLoggerMetadataService, NGXLoggerRulesService, NGXLoggerServerService, NGXLoggerWriterService, NgxLoggerLevel, TOKEN_LOGGER_CONFIG, TOKEN_LOGGER_CONFIG_ENGINE_FACTORY, TOKEN_LOGGER_MAPPER_SERVICE, TOKEN_LOGGER_METADATA_SERVICE, TOKEN_LOGGER_RULES_SERVICE, TOKEN_LOGGER_SERVER_SERVICE, TOKEN_LOGGER_WRITER_SERVICE } from 'ngx-logger';
import { Observable } from 'rxjs';
import { EnvironnementService } from '../services/stateful/environnement.service';

/** Fonction appelant le chargement du JSON */
function initConfigService(configService: EnvironnementService): () => Observable<any> {
  return () => configService.chargerEnvironnement();
}

/** Provider personnalisé pour injecter l'instance de EnvironnementService */
export const fournirEnvironnement = (): Provider => {
  const provider: (Provider) =
  {
    provide: APP_INITIALIZER,
    deps: [EnvironnementService],
    useFactory: initConfigService,
    multi: true
  };
  return provider;
};

/** Configuration de l'application Angular */
export const appConfig: ApplicationConfig = {
  providers: [
    // Provider de base (https://angular.dev/api/core/provideZoneChangeDetection?tab=description)
    provideZoneChangeDetection({ eventCoalescing: true }),

    // Pour disposer du client HTTP (https://angular.dev/api/common/http/provideHttpClient?tab=description)
    provideHttpClient(withFetch()),

    // Pour fournir les parametres propres à un environnement
    fournirEnvironnement(),

    // Pour les animations
    provideAnimations(),

    // Pour démarrer l'OIDC
    provideOAuthClient(),

    // Pour les pipe du projet
    DatePipe,

    // Configuration des logs (en lieu et place de LoggerModule.forRoot)
    // @see https://github.com/dbfannin/ngx-logger/blob/master/src/lib/logger.module.ts
    { provide: TOKEN_LOGGER_CONFIG, useValue: { serverLoggingUrl: undefined, level: NgxLoggerLevel.TRACE, serverLogLevel: NgxLoggerLevel.OFF } },
    { provide: TOKEN_LOGGER_CONFIG_ENGINE_FACTORY, useClass: NGXLoggerConfigEngineFactory },
    { provide: TOKEN_LOGGER_METADATA_SERVICE, useClass: NGXLoggerMetadataService },
    { provide: TOKEN_LOGGER_RULES_SERVICE, useClass: NGXLoggerRulesService },
    { provide: TOKEN_LOGGER_MAPPER_SERVICE, useClass: NGXLoggerMapperService },
    { provide: TOKEN_LOGGER_WRITER_SERVICE, useClass: NGXLoggerWriterService },
    { provide: TOKEN_LOGGER_SERVER_SERVICE, useClass: NGXLoggerServerService }
  ]
};
