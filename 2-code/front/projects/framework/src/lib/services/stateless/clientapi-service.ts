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
import { HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders, HttpRequest } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LibelleI18n, i18n } from '../../conf/i18n';
import { MessageAafficher, TypeMessageAafficher } from '../../model/message.model';
import { ContexteService } from '../stateful/contexte.service';

/** Classe abstraite permettant de construire des services appelant des APIs. */
export abstract class ClientApiService {

    /**
     * Constructeur pour injection de dépendance.
     * Attention à bien appeler ce constructeur depuis les classes qui héritent de celle-ci !
     * */
    public constructor(private http: HttpClient, protected logger: NGXLogger, protected contexte: ContexteService) { }

    /**
     * Méthode de base pour appeler une API.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param url L'URL de la ressource.
     * @param options Les entêtes à utiliser.
     * @param valeurParDefaut La valeur par défaut en cas d'erreur.
     */
    protected get<T>(idAppelant: string, url: string, options: { headers: HttpHeaders; }, valeurParDefaut: T): Observable<T> {
        return this.http.get<T>(url, options).pipe(
            // gestion d'erreur
            catchError(erreur => {
                this.tracerErreur(idAppelant, 'get', [url], erreur);
                return of(valeurParDefaut);
            }),
        );
    }

    /**
     * Méthode de base pour appeler une API.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param url L'URL de la ressource.
     * @param options Les entêtes à utiliser.
     * @param valeurParDefaut La valeur par défaut en cas d'erreur.
     */
    protected delete<T>(idAppelant: string, url: string, options: { headers: HttpHeaders; }, valeurParDefaut: T): Observable<T> {
        return this.http.delete<T>(url, options).pipe(
            // gestion d'erreur
            catchError(erreur => {
                this.tracerErreur(idAppelant, 'delete', [url], erreur);
                return of(valeurParDefaut);
            }),
        );
    }

    /**
     * Méthode de base pour appeler une API.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param url L'URL de la ressource.
     * @param body Le corps de la requête.
     * @param options Les entêtes à utiliser.
     * @param valeurParDefaut La valeur par défaut en cas d'erreur.
     */
    protected post<T>(idAppelant: string, url: string, body: any, options: { headers: HttpHeaders; }, valeurParDefaut: T): Observable<T> {
        return this.http.post<T>(url, body, options).pipe(
            // gestion d'erreur
            catchError(erreur => {
                this.tracerErreur(idAppelant, 'post', [url, body], erreur);
                return of(valeurParDefaut);
            }),
        );
    }

    /**
     * Méthode de base pour appeler une API.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param url L'URL de la ressource.
     * @param body Le corps de la requête.
     * @param options Les entêtes à utiliser.
     */
    protected postAvecProgression(idAppelant: string, url: string, body: FormData, options: { headers: HttpHeaders; }): Observable<HttpEvent<any>> {
        const optionsComplete = options as { headers?: HttpHeaders | { [header: string]: string | string[]; }; observe: string; reportProgress: boolean; };
        optionsComplete.observe = 'events';
        optionsComplete.reportProgress = true;

        // retrait du Content-Type pour laisser faire le navigateur
        optionsComplete.headers = (optionsComplete.headers as HttpHeaders).delete('Content-Type', 'application/json');

        return this.http.request(new HttpRequest('POST', url, body, options)).pipe(
            // gestion d'erreur
            catchError(erreur => {
                this.tracerErreur(idAppelant, 'post', [url, 'formulaire'], erreur);
                return EMPTY;
            }),
        );
    }

    /** De simples entêtes (cas par défaut). */
    protected creerOptionsSimples(): { headers: HttpHeaders; } {
        const langue = this.contexte.langue.toLocaleLowerCase();
        const entetes = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept-language': langue });
        return { headers: entetes };
    }

    /**
     * Entêtes simples (@see creerOptionsSimples) avec un Authorization en plus.
     * @param tokenJwt Un token JWT.
    */
    protected creerOptionsSimplesAvecAuthentification(): { headers: HttpHeaders; } {
        const langue = this.contexte.langue.toLocaleLowerCase();
        const tokenJwt = this.contexte.tokenJwtCourant;
        const entetes = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept-language': langue, 'Authorization': 'Bearer ' + tokenJwt });
        return { headers: entetes };
    }

    /**
     * Entêtes simples (@see creerOptionsSimples) avec un Authorization en plus.
     * @param tokenJwt Un token JWT.
    */
    protected creerOptionsTexteSimpleAvecAuthentification(): { headers: HttpHeaders; responseType: string; } {
        const langue = this.contexte.langue.toLocaleLowerCase();
        const tokenJwt = this.contexte.tokenJwtCourant;
        const entetes = new HttpHeaders({ 'Authorization': 'Bearer ' + tokenJwt, 'Accept-language': langue });
        return { responseType: 'text', headers: entetes };
    }

    /** De simples entêtes (cas par défaut). */
    protected creerOptionsTexteSimple(): { headers: HttpHeaders; responseType: string, } {
        const langue = this.contexte.langue.toLocaleLowerCase();
        return { responseType: 'text', headers: new HttpHeaders({ 'Accept-language': langue }) };
    }

    /**
     * Méthode de gestion des erreurs.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param methodeHttp Nom de la méthode générant l'erreur.
     * @param parametres Paramètres d'appel à la méthode.
     * @param erreur Objet erreur à traiter.
     */
    private tracerErreur(idAppelant: string, methodeHttp: string, parametres: string[], erreur: HttpErrorResponse): void {
        // Si un message explicite venant de l'API du socle est présent
        let messagePrecisDeLapi = '';
        if (erreur.error && erreur.error.error) {
            messagePrecisDeLapi = erreur.error.error;
        }

        // Récupération de la langue courante
        const langue = this.contexte.langue;

        // Création du début du message dépendant de la fonctionnalité appelée
        let messagePublic = '';
        if ((i18n.messageErreurApi as any)[idAppelant]) {
            messagePublic = ((i18n.messageErreurApi as any)[idAppelant] as LibelleI18n).get(langue);
            messagePublic += ' ';
        }

        // Ajout d'un message précisant l'erreur en fonction du code HTTP ou du contenu de la réponse
        if (erreur.status === 0 && !erreur.ok) {
            messagePublic += i18n.erreursHttp[0].get(langue);
        } else if (erreur.status === 400) {
            messagePublic += i18n.erreursHttp[400].get(langue).replace('*messagePrecisDeLapi*', ' ' + messagePrecisDeLapi);
        } else if (erreur.status === 401) {
            messagePublic += i18n.erreursHttp[401].get(langue);
        } else if (erreur.status === 403) {
            messagePublic += i18n.erreursHttp[403].get(langue);
        } else if (erreur.status === 404) {
            messagePublic += i18n.erreursHttp[404].get(langue);
        } else if (messagePrecisDeLapi) {
            messagePublic += messagePrecisDeLapi;
        } else {
            messagePublic += i18n.erreursHttpParDefaut.get(langue);
        }

        // log à défaut de mieux pour le moment
        this.logger.error('Erreur dans l\'appel ' + methodeHttp + '(' + parametres + ') : ' + messagePublic, erreur);

        // Affichage d'un message à l'usager
        this.contexte.afficherUnMessageGeneral(new MessageAafficher(idAppelant, TypeMessageAafficher.Erreur, messagePublic));
    }
}
