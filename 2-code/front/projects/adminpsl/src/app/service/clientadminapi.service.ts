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
import { MessageAafficher, TypeMessageAafficher } from '../../../../framework/src/lib/model/message.model';
import { AdminContexteService } from './adminContexte.service';

/** Classe abstraite permettant de construire des services appelant des APIs. */
export abstract class ClientAdminApiService {

    /**
     * Constructeur pour injection de dépendance.
     * Attention à bien appeler ce constructeur depuis les classes qui héritent de celle-ci !
     * */
    public constructor(private http: HttpClient, protected logger: NGXLogger, protected contexte: AdminContexteService) { }

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
        const entetes = new HttpHeaders({ 'Content-Type': 'application/json' });
        return { headers: entetes };
    }

    /** Options spécifiques à l'usage de FormData sans entête car il est généré automatique avec la partie boundary. */
    protected creerOptionsSimplesPourPostMultipart(): { headers: HttpHeaders; } {
        const entetes = new HttpHeaders();
        return { headers: entetes };
    }

    /**
     * Entêtes simples (@see creerOptionsSimples) avec un Authorization en plus.
     * @param tokenJwt Un token JWT.
     */
    protected creerOptionsSimplesAvecAuthentification(): { headers: HttpHeaders; } {
        const tokenJwt = this.contexte.tokenJwtCourant;
        const entetes = new HttpHeaders({ 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + tokenJwt });
        return { headers: entetes };
    }

    /** De simples entêtes (cas par défaut). */
    protected creerOptionsTexteSimple(): { headers: HttpHeaders; responseType: string, } {
        return { responseType: 'text', headers: new HttpHeaders({}) };
    }

    /**
     * Méthode de gestion des erreurs.
     * @param idAppelant ID de l'appelant (pour identifier l'émetteur des messages et les masquer)
     * @param nomMethode Nom de la méthode générant l'erreur.
     * @param parametres Paramètres d'appel à la méthode.
     * @param erreur Objet erreur à traiter.
     */
    private tracerErreur(idAppelant: string, nomMethode: string, parametres: string[], erreur: HttpErrorResponse): void {
        // Extraction de l'objet depuis l'URL présente dans le message
        let objet = '(' + erreur.message + ')';
        const debut = erreur.message.lastIndexOf('/');
        const fin = erreur.message.indexOf(':', debut);
        if (debut !== -1 && fin !== -1) {
            objet = erreur.message.substring(debut, fin);
        }

        // Création du message
        let message = '';
        if (erreur.status === 0 && !erreur.ok) {
            message = 'Erreur de sécurité ou proxy mal paramétré';
        } else if (erreur.status === 401) {
            message = 'Erreur de sécurité : paramètres de connexion invalide';
        } else if (erreur.status === 403) {
            message = 'Erreur de sécurité : ressource interdite : ' + objet;
        } else if (erreur.status === 404) {
            message = 'Erreur de donnée : La ressource demandée n\'existe pas : ' + objet;
        } else if (erreur.error && erreur.error.error) {
            message = erreur.error.error;
        } else {
            message = erreur.message;
        }

        // Ajout de l'ID du message dans tous les cas (s'il est présent)
        if (erreur.error && erreur.error.requestId) {
            message += ' (errorId=\'' + erreur.error.requestId + '\')';
        }

        // log à défaut de mieux pour le moment
        this.logger.error('Erreur dans la méthode ' + nomMethode + '(' + parametres + ') : ' + message);

        // Affichage d'un message à l'usager
        this.contexte.afficherUnMessageGeneral(new MessageAafficher(idAppelant, TypeMessageAafficher.Erreur, message));
    }
}
