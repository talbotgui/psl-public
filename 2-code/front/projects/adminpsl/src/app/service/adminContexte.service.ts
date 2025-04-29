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
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageAafficher } from '../../../../framework/src/lib/model/message.model';
import { UtilisateurConnecte } from '../model/securite.model';

@Injectable({ providedIn: 'root' })
export class AdminContexteService {

    /** Token JWT de sécurité actuel et valide */
    private tokenJwt: string | undefined = undefined;
    public get tokenJwtCourant(): string | undefined { return this.tokenJwt; }

    /** Subject lié à la connexion de l'utilisateur. */
    private informationsUtilisateurConnecteSubject = new BehaviorSubject<UtilisateurConnecte | undefined>(undefined);

    /** Subject lié aux messages généraux à afficher en haut de page. */
    private messageGeneralSubject = new BehaviorSubject<MessageAafficher | undefined>(undefined);

    /** Constructeur avec injection des dépendances */
    public constructor(private logger: NGXLogger) { }

    /** Etre notifié quand un message général est créé */
    public obtenirUnObservableSurLesMessagesGeneraux(): Observable<MessageAafficher | undefined> {
        return this.messageGeneralSubject.asObservable();
    }

    /** Etre notifié quand les informations de l'utilisateur connecté changent */
    public obtenirUnObservableDesInformationsDeLutilisateurConnecte(): Observable<UtilisateurConnecte | undefined> {
        return this.informationsUtilisateurConnecteSubject.asObservable();
    }

    /** L'utilisateur est connecté ? */
    public utilisateurEstConnecte(): boolean {
        return !!this.tokenJwt;
    }

    /** Sauvegarde du token */
    public sauvegarderNouveauTokenJwt(tokenJwt: string | undefined): void {
        this.tokenJwt = tokenJwt;
    }

    /** Sauvegarde informations post connexion */
    public sauvegarderDonneesUtilisateur(utilisateur: UtilisateurConnecte): void {
        this.informationsUtilisateurConnecteSubject.next(utilisateur);
    }

    /** Affiche un message général. */
    public afficherUnMessageGeneral(message: MessageAafficher | undefined): void {
        this.messageGeneralSubject.next(message);
    }
}
