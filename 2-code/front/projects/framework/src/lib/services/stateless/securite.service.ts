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
import { Observable, Subscription, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { configuration } from '../../conf/configuration';
import { ReponseConnexion } from '../../model/securite.model';
import { ContexteService } from '../stateful/contexte.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

/** Composant d'appel aux APIs exposées par le micro-service de SECURITE */
@Injectable({ providedIn: 'root' })
export class SecuriteService extends ClientApiService {

    /** URI du service d'authentification anonyme */
    private static readonly URI_AUTHENTIFICATION_ANONYME = '/socle/securite/authentificationAnonyme';

    /** URI du service d'authentification par mot de passe */
    private static readonly URI_AUTHENTIFICATION_MOT_DE_PASSE = '/socle/securite/authentificationMotDePasse';

    // URL du micro-service de sécurité permettant de récupérer les informations de l'usager connecté
    private static readonly URI_USER_INFO = '/socle/securite/info';

    /** Timeout (appel régulier) en cours. Au début, c'est obligatoirement le timeout pour le refresh du token anonyme. Puis cela peut être celui de l'OIDC.  */
    private intervalCourant: any | undefined;

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, logger: NGXLogger, contexte: ContexteService, private bouchon: BouchonService, private environnementService: EnvironnementService) {
        super(http, logger, contexte);
    }

    /**
     * Connexion anonyme nécessaire dans tous les cas pour charger la configuration de la démarche.
     */
    public seConnecterEnAnonyme(codeDemarche:string): Observable<boolean> {
        this.logger.info('Connexion anonyme');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiSecuriteEstBouchonnee()) {
            this.logger.info('Connexion via le bouchon');
            this.sauvegardeTokenJwt('connexionViaBouchonDoncPasDeTokenValide', configuration.oidc.refreshTimeout, this.seConnecterEnAnonyme.bind(this,codeDemarche))
            return of(true).pipe(delay(1000));
        }

        // Véritable appel
        const url = this.environnementService.urls.socle.gateway + SecuriteService.URI_AUTHENTIFICATION_ANONYME;
        const options = this.creerOptionsSimples();
        const valeurParDefaut: ReponseConnexion | undefined = undefined;
        const donnees={codeDemarche:codeDemarche};
        return this.post<ReponseConnexion | undefined>('seConnecterEnAnonyme', url, donnees, options, valeurParDefaut).pipe(
            map(reponse => {
                if (reponse && reponse.token) {
                    this.sauvegardeTokenJwt(reponse.token, configuration.oidc.refreshTimeout, this.seConnecterEnAnonyme.bind(this,codeDemarche));
                    this.logger.info('Connexion anonyme réussie avec un token de ' + reponse.token.length + 'o');
                    return true;
                } else {
                    this.logger.error('Connexion anonyme échouée');
                    return false;
                }
            })
        );
    }

    /**
     * Récupération des informations de l'usager connecté (fonctionne en mode anonyme comme OIDC).
     */
    public recupererInformationsUsager(): Observable<object> {
        this.logger.info('Récupération des informations de l\'usager connecté');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiSecuriteEstBouchonnee()) {
            this.logger.info('Récupération via le bouchon');
            return this.bouchon.recupererInformationsUsager().pipe(delay(1000));
        }

        // Véritable appel
        const url = this.environnementService.urls.socle.gateway + SecuriteService.URI_USER_INFO;
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = {};
        return this.get<object>('recupererInformationsUsager', url, options, valeurParDefaut);
    }

    /**
     * Sauvegarde du token et démarrage du compte à rebourd pour recommencer.
     * @param  tokenJwt Le token à sauvegarder.
     * @param timeout le délai avant de déclencher le refresh.
     * @param methodeDeRefresh La méthode réalisant le refresh.
     */
    public sauvegardeTokenJwt(tokenJwt: string, timeout?: number | undefined, methodeDeRefresh?: undefined | (() => Observable<boolean>)): void {
        // Sauvegarde du token
        this.contexte.sauvegarderNouveauTokenJwt(tokenJwt);

        // Si une fonction et un timeout de refresh sont fournis
        if (methodeDeRefresh && timeout) {

            // Si un timeout court déjà, on l'arrête
            if (this.intervalCourant) {
                clearInterval(this.intervalCourant);
            }

            // Création d'un nouveau timeout pour raffraichir le token  
            let souscriptionPrecedente: Subscription;
            this.intervalCourant = setInterval(() => {
                // log
                this.logger.info("Raffraichissement du token");
                // Destruction de la souscription précédente (optimisation mémoire)
                if (souscriptionPrecedente) {
                    souscriptionPrecedente.unsubscribe();
                }
                // Souscription pour obtenir le nouveau token
                souscriptionPrecedente = methodeDeRefresh().subscribe();
            }, timeout);
        }
    }

    /**
     * Connexion avec un mot de passe.
     * @param nomUtilisateur Email de l'utilisateur.
     * @param motDePasse Mot de passe.
     * @returns TRUE si la connexion est réussie.
     */
    public seConnecterAvecUnMotDePasse(nomUtilisateur: string, motDePasse: string): Observable<boolean> {
        this.logger.info('Connexion avec un mot de passe');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiSecuriteEstBouchonnee()) {
            this.logger.info('Connexion via le bouchon');
            this.sauvegardeTokenJwt('connexionViaBouchonDoncPasDeTokenValide', configuration.oidc.refreshTimeout, this.seConnecterAvecUnMotDePasse.bind(this, nomUtilisateur, motDePasse))
            return of(true);
        }

        // Véritable appel
        const url = this.environnementService.urls.socle.gateway + SecuriteService.URI_AUTHENTIFICATION_MOT_DE_PASSE;
        const options = this.creerOptionsSimples();
        const valeurParDefaut: ReponseConnexion | undefined = undefined;
        const corps = { nomUtilisateur, motDePasse };
        return this.post<ReponseConnexion | undefined>('seConnecterAvecUnMotDePasse', url, corps, options, valeurParDefaut).pipe(
            map(reponse => {
                if (reponse && reponse.token) {
                    this.sauvegardeTokenJwt(reponse.token, configuration.oidc.refreshTimeout, this.seConnecterAvecUnMotDePasse.bind(this, nomUtilisateur, motDePasse));
                    this.logger.info('Connexion par mot de passe réussie avec un token de ' + reponse.token.length + 'o');
                    return true;
                } else {
                    this.logger.error('Connexion par mot de passe échouée');
                    return false;
                }
            })
        );
    }
}
