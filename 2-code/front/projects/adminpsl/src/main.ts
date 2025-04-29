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
import { ApplicationConfig } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { Routes, provideRouter, withHashLocation } from '@angular/router';
import { appConfig } from '../../framework/src/lib/conf/app.config';
import { AppComponent } from './app/app.component';
import { AuthentificationGarde } from './app/garde/authentificationGarde';
import { AppRouteAdminComponent } from './app/routes/routeadmin/app.routeadmin';
import { AppRouteConnexionComponent } from './app/routes/routeconnexion/app.routeconnexion';
import { AppRouteDemarcheComponent } from './app/routes/routedemarche/app.routedemarche';
import { AppRouteSoumissionComponent } from './app/routes/routesoumission/app.routesoumission';
import { AppRouteTransfertComponent } from './app/routes/routetransfert/app.routetransfert';

// Liste des routes disponibles dans l'application
const routes: Routes = [
    { canActivate: [AuthentificationGarde], path: 'route-demarche', component: AppRouteDemarcheComponent },
    { canActivate: [AuthentificationGarde], path: 'route-soumission', component: AppRouteSoumissionComponent },
    { canActivate: [AuthentificationGarde], path: 'route-transfert', component: AppRouteTransfertComponent },
    { canActivate: [AuthentificationGarde], path: 'route-admin', component: AppRouteAdminComponent },
    // Route sans protection 
    { path: 'connexion', component: AppRouteConnexionComponent },
    // Route par défaut si l'URL ne correspond à rien
    { path: '**', redirectTo: '/route-admin' },
];

export const appConfigAdminPsl: ApplicationConfig = {
    providers: [
        ...appConfig.providers,
        provideRouter(routes, withHashLocation())
    ]
};

bootstrapApplication(AppComponent, appConfigAdminPsl)
    .catch((err) => console.error(err));
