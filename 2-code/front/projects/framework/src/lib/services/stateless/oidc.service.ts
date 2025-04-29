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
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { NGXLogger } from 'ngx-logger';
import { Observable, catchError, from, map, mergeMap, of, tap } from 'rxjs';
import { configuration } from '../../conf/configuration';
import { i18n } from '../../conf/i18n';
import { MessageAafficher, TypeMessageAafficher } from '../../model/message.model';
import { CLEF_UTILISATEUR, STATUT_CONNEXION_OIDC } from '../../model/securite.model';
import { ContexteService } from '../stateful/contexte.service';
import { DonneesService } from '../stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { SecuriteService } from './securite.service';

/**
 * Cette classe contient la gestion de l'authentification OIDC.
 */
@Injectable({ providedIn: 'root' })
export class OIDCService {

    /** Constructeur avec injection des dépendances */
    constructor(private contexte: ContexteService, private oauthService: OAuthService, private bouchon: BouchonService, private logger: NGXLogger, private donneesService: DonneesService, private securiteService: SecuriteService, private environnementService: EnvironnementService) { }

    /** Initialisation OIDC et authentification */
    public initialiserAuthentification(codeDemarche:string): Observable<STATUT_CONNEXION_OIDC> {

        // Message à l'usager
        this.logger.info('Configuration OIDC');
        const message = i18n.oidcService.echangesOidcEnCours.get(this.contexte.langue);
        this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));

        // Calcul de l'URI de retour OIDC (par défaut, l'URL du navigateur avec ses paramètres)
        const redirectUri = this.obtenirUrlInitiale(window.location.href);

        // Création de la configuration
        const authCodeFlowConfig: AuthConfig = {
            // URL du fournisseur d'identité (celle à partir de laquelle se déduit le ./.well-known/openid-configuration)
            // issuer: configuration.oidc.url,
            // URL de connexion (/auth)
            loginUrl: configuration.oidc.url,
            // On fait sauter la vérification de l'ISSUER car 
            //  - l'URL est https://xxx.service-public.fr/auth/realms/service-public/protocol/openid-connect/auth
            //  - mais le token retour un issuer avec la valeur https://xxx.service-public.fr/auth/realms/service-public
            skipIssuerCheck: true,
            // URL de l'application vers laquelle revenir (pas de valeur fixe pour conserver les paramètres de l'URL avec le HMR et les autres paramètres fonctionnels)
            redirectUri: redirectUri,
            // Identifiant OIDC (le secret est côté Back pas dans le front)
            clientId: configuration.oidc.clientId,
            dummyClientSecret: 'bidon',
            // 'code' pour activer l'implicit-flow
            // L'implicit-flow est maintenant DEPRECATED en OAuth-2.1 (https://connect2id.com/learn/oauth-2-1#:~:text=The%20implicit%20and%20password%20grants,via%20the%20client%20credentials%20grant.)
            responseType: 'code',
            // Les SCOPES demandés (cf. configuration)
            scope: configuration.oidc.scopes,
            // URL du micro-service de sécurité permettant de récupérer un accessToken et un refreshToken
            tokenEndpoint: this.environnementService.urls.socle.gateway + '/socle/securite/token',
            // L'URL précédente est en HTTP et non en HTTPs sur le poste local
            requireHttps: false,

            // EN PLUS :
            // Pour être verbeux (si besoin)
            showDebugInformation: false,
            // Pour traiter un bug en lien avec les #xxx d'Angular
            // https://github.com/manfredsteyer/angular-oauth2-oidc/issues/457#issuecomment-431807040,
            clearHashAfterLogin: false,
        };

        // Si la sécurité est bouchonnée, 
        if (this.bouchon.verifierSiSecuriteEstBouchonnee()) {
            // connexion anonyme pour disposer d'un token
            return this.securiteService.seConnecterEnAnonyme(codeDemarche).pipe(
                // Récupération des informations de l'usager
                mergeMap(() => this.securiteService.recupererInformationsUsager()),
                // Sauvegarde de ces informations
                tap(donneesUsager => this.sauvegarderDonneesDeSecurite(donneesUsager)),
                // renvoi du statut de success
                map(() => STATUT_CONNEXION_OIDC.connexionReussie),
                // Suppression du message affiché à l'usager
                tap(() => this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, '')))
            );
        }

        // Log de la configuration
        this.logger.info('Découverte et connexion OIDC', authCodeFlowConfig);

        // Initialisation du service
        this.oauthService.configure(authCodeFlowConfig);

        // Lectures des paramètres de l'URL d'accès à la page
        const params = (new URL(window.location.href)).searchParams;

        // Si les paramètres code et state sont présents, on ne revient donc pas juste de la page de connexion OIDC.
        // Donc déclenchement de la connexion via le flow implicite (c'est le but de cette méthode)
        if (!params.has('code') || !params.has('state')) {
            this.oauthService.initImplicitFlow(window.location.href);
            return of(STATUT_CONNEXION_OIDC.connexionEnCours);
        }

        // Mais, si les deux paramètres sont là, c'est qu'on a déjà démarré le flow implicite
        // Donc on tente la connexion à partir des informations échangées dans le navigateur entre l'application et la(es) page(s) de connexion OIDC
        return from(this.oauthService.tryLogin()).pipe(
            // en cas d'échec de la connexion
            catchError((err: any) => {
                this.logger.error('Connexion OIDC échouée', err);
                return of(STATUT_CONNEXION_OIDC.erreurConnexion);
            }),
            // A la fin de la tentative de connexion, est fourni le couple accesToken/refreshToken obtenu depuis le socle PSL
            // Ce ne sont pas les token OIDC originaux mais les tokens PSL !
            map(resultat => {

                // lecture des données obtenues
                const tokenPSL = this.oauthService.getAccessToken();

                // log
                this.logger.info('Statut de l\'initialisation OIDC: ', resultat);
                this.logger.info('tokenPSL : ', tokenPSL);
                this.logger.info('jwks : ', this.oauthService.jwks);
                this.logger.info('claims : ', this.oauthService.getIdentityClaims());
                this.logger.info('state : ', this.oauthService.state);

                // récupération de l'ID de brouillon s'il est présent dans l'URL initial de l'authentification
                // (l'URL est passée dans la méthode 'loadDiscoveryDocumentAndLogin')
                if (this.oauthService.state && this.oauthService.state.startsWith('http')) {
                    const index = this.oauthService.state.indexOf('brouillon%3D');
                    if (index !== -1) {
                        this.contexte.idBrouillon = this.oauthService.state.substring(index + 12);
                    }
                }

                // Si le tokenPSL est là, on est bien connecté et on passe le token à l'étape suivante
                if (tokenPSL) {
                    return tokenPSL;
                }

                // Si la connexion est échouée, message à l'usager et on passe UNDEFINED à l'étape suivante
                else if (this.oauthService.state) {

                    // Message à l'usager
                    const message = i18n.oidcService.erreurSansState.get(this.contexte.langue);
                    this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));

                    // Dans tous les cas, on renvoi la valeur UNDEFINED à la place du tokenPSL
                    return undefined;
                }

                // Si la connexion est à faire (avec la redirection à venir)
                else {

                    // Message à l'usager
                    const message = i18n.oidcService.redirectionEnCours.get(this.contexte.langue);
                    this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Avertissement, message));

                    // Dans tous les cas, on renvoi le valeur UNDEFINED à la place du tokenPSL
                    return undefined;
                }
            }),

            // Déclaration du token OIDC vers le microservice du socle
            map(tokenPSL => {

                // Si la connexion est un succès et qu'on a bien le tokenPSL
                if (tokenPSL) {

                    // Méthode de refresh du token appelée régulièrement par SecuriteService après la sauvegarde du premier token
                    const methodeDeRefreshDuToken = () => from(this.oauthService.refreshToken()).pipe(
                        // Pour sauvegarder le nouveau token (sans fournir de méthode de refresh car on est déjà dedans)
                        tap(reponse => {
                            this.logger.info('Refresh du token : ', reponse)
                            this.securiteService.sauvegardeTokenJwt(reponse.access_token);
                        }),
                        // Pour renvoyer un Observable<boolean>
                        map(reponse => !!reponse)
                    );

                    // Sauvegarde du token dans le contexte
                    this.securiteService.sauvegardeTokenJwt(tokenPSL, configuration.oidc.refreshTimeout, methodeDeRefreshDuToken);

                    // Suppression du message affiché à l'usager
                    this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, ''));

                    // Renvoi du statut de connexion
                    return STATUT_CONNEXION_OIDC.connexionReussie;
                }

                // Sinon, c'est un échec
                else {
                    return STATUT_CONNEXION_OIDC.erreurConnexion;
                }
            }),

            // Appel à l'API des informations de l'usager (dans un mergeMap pour que la méthode globale attende la réponse)
            // et sauvegarde des données
            mergeMap(statut => {
                this.contexte.utilisateurConnecteOidc = (statut == STATUT_CONNEXION_OIDC.connexionReussie);
                return this.securiteService.recupererInformationsUsager().pipe(
                    map(donneesUsager => {
                        this.sauvegarderDonneesDeSecurite(donneesUsager);
                        return statut;
                    })
                );
            }),

            // Message à l'usager en cas de succès
            tap(statut => {
                if (statut == STATUT_CONNEXION_OIDC.connexionReussie) {
                    const message = i18n.oidcService.connexionReussie.get(this.contexte.langue);
                    this.contexte.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));
                }
                // Pas de else car les cas d'erreur sont traités plus haut
            })
        );
    }

    /** Déconnexion OIDC */
    public seDeconnecter(): void {
        // Suppression du token de sécurité
        this.contexte.sauvegarderNouveauTokenJwt(undefined);
        // Déconnexion OIDC
        this.oauthService.revokeTokenAndLogout();
        // On va sur la page de déconnexion du fournisseur d'identité OU on recharge la page
        if (this.oauthService.logoutUrl) {
            window.location.href = this.oauthService.logoutUrl;
        } else {
            window.location.reload();
        }
    }

    /** Sauvegarde des données obtenues depuis l'API des informations de l'usager  pour alimenter les données de la démarche */
    private sauvegarderDonneesDeSecurite(donneesUsager: any): void {

        // Création d'un objet avec tous les attributs préfixés par 'utilisateur_'
        const utilisateurDansLesDonnees: any = {};
        Object.keys(donneesUsager).forEach(k => utilisateurDansLesDonnees['utilisateur_' + k] = donneesUsager[k]);

        // Ajout de données pré-calculées
        if (utilisateurDansLesDonnees.utilisateur_prenoms && utilisateurDansLesDonnees.utilisateur_nom) {
            utilisateurDansLesDonnees.utilisateur_nomComplet = utilisateurDansLesDonnees.utilisateur_prenoms + ' ' + utilisateurDansLesDonnees.utilisateur_nom;
        } else if (utilisateurDansLesDonnees.prenoms) {
            utilisateurDansLesDonnees.utilisateur_nomComplet = utilisateurDansLesDonnees.utilisateur_prenoms;
        } else if (utilisateurDansLesDonnees.nom) {
            utilisateurDansLesDonnees.utilisateur_nomComplet = utilisateurDansLesDonnees.utilisateur_nom;
        } else {
            this.logger.warn('Pas de nom ni de prenoms dans les données de l\'usager connecté')
        }

        // log
        this.logger.info('Utilisateur inséré dans les données soumises : ', utilisateurDansLesDonnees);

        // Sauvegarde des données
        this.donneesService.sauvegarderUnObjet(CLEF_UTILISATEUR, utilisateurDansLesDonnees);

        // Sauvegarde des données utilisateur
        this.contexte.sauvegarderDonnesUtilisateur(utilisateurDansLesDonnees);
    }

    /**
     * Méthode extrayant l'URL originale dans une URL de redirection OIDC (ou renvoyant l'URL passée en paramètre)
     * @param url URL à traiter
     * @return l'URL initiale 
     */
    public obtenirUrlInitiale(url: string): string {
        // Si la démarche se charge avec les paramètres code, scope et state
        // on est donc au milieu d'une authentification OIDC !
        // L'URL originale contenant les paramètres sont donc dans le state
        const params = (new URL(url)).searchParams;
        if (params.has('code') && params.has('state')) {
            const urlOriginelleS = params.get('state')?.split('http');
            if (urlOriginelleS && urlOriginelleS.length === 2) {
                return decodeURIComponent('http' + urlOriginelleS[1]);
            }
        }

        // Si l'URL n'est pas une redirection OIDC
        return url;
    }
}
