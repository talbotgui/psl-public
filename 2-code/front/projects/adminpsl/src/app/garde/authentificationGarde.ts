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
import { Injectable } from "@angular/core";
import { CanActivate, Router, UrlTree } from "@angular/router";
import { NGXLogger } from "ngx-logger";
import { Observable } from "rxjs";
import { AdminContexteService } from "../service/adminContexte.service";

/**
 *  Garde forçant le passage par la page de connexion
 * @see https://angular.io/api/router/CanActivate
 */
@Injectable({ providedIn: 'root' })
export class AuthentificationGarde implements CanActivate {

    /** Constructeur pour injection de dépendances */
    constructor(private contexteService: AdminContexteService, private router: Router, private logger: NGXLogger) { }

    /** Méthode vérifiant l'état de l'utilisateur */
    public canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

        // Si l'utilisateur est connecté, on laisse passé
        if (this.contexteService.utilisateurEstConnecte()) {
            this.logger.log('Utilisateur connecté.');
            return true;
        }
        // Sinon, redirection vers la page de login
        else {
            this.logger.log('Utilisateur redirigé vers la page de connexion.');
            this.router.navigate(['/connexion']);
            return false;
        }
    }
}

/** Garde permettant de rediriger vers la page de login si l'utilisateur n'est pas connecté ou bloquer l'accès à une page non autorisée. */
