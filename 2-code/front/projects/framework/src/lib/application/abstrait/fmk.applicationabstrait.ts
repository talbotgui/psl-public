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
import { Directive, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';
import { i18n } from '../../conf/i18n';
import { AuthentificationPossibles, ConfigurationDemarche, PointEntree } from '../../model/configurationdemarchegeneral.model';
import { MessageAafficher, TypeMessageAafficher } from '../../model/message.model';
import { STATUT_CONNEXION_OIDC } from '../../model/securite.model';
import { ContexteService } from '../../services/stateful/contexte.service';
import { DonneesService } from '../../services/stateful/donnees.service';
import { EnvironnementService } from '../../services/stateful/environnement.service';
import { BrouillonService } from '../../services/stateless/brouillon.service';
import { ConfigurationService } from '../../services/stateless/configuration.service';
import { OIDCService } from '../../services/stateless/oidc.service';
import { SecuriteService } from '../../services/stateless/securite.service';
import { AbstractComponent } from '../../utilitaires/abstract.component';

/**
 * Cette classe fournit les comportements de base de toute application.
 * Toutes les app.component.ts doivent en hérité
 */
// @Directive pour pouvoir utilisé les annotations Angular dans cette classe
@Directive()
export class FmkApplicationAbstraitComponent extends AbstractComponent implements OnInit {

    /*
     * Instance de configuration
     * - chargée au chargement de la démarche dans le navigateur de l'usager
     * - utilisée dans la page HTML et les sous-composants
     */
    public configuration: ConfigurationDemarche | undefined;

    /** Flag utile pour masquer l'application le temps du chargement des données (configuration / brouillon) */
    public afficherApplication = false;

    /** Constructeur pour injection des dépendances */
    public constructor(protected oidcService: OIDCService, protected contexteService: ContexteService, protected configurationService: ConfigurationService, protected donneesService: DonneesService, protected brouillonService: BrouillonService, protected securiteService: SecuriteService, private logger: NGXLogger, private environnementService: EnvironnementService) { super(); }

    /** A l'initialisation de l'application */
    public ngOnInit(): void {
        this.afficherApplication = false;

        // Dès le tout début du processus, détection et sauvegarde de la langue par défaut du navigateur
        // (uniquement la première mais c'est plus subtile dans l'entête une fois la configuration chargée)
        this.contexteService.changerLangue(navigator.language.substring(0, 2).toUpperCase());

        // Affichage d'un message à l'utilisateur (réutilisation de this.contexteService.langue car elle peut varier entre deux étapes)
        let message = i18n.application.debut1CreationToken.get(this.contexteService.langue);
        this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));

        // Récupération du code de la démarche depuis l'URL
        const codeDemarche = (new URLSearchParams(window.location.search)).get('codeDemarche');
        if (!codeDemarche || codeDemarche.length<3){
            const message = i18n.application.debutErreurLectureParametreCodeDemarche.get(this.contexteService.langue);
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));
            return;
        }

        // Connexion anonyme dans un premier temps pour charger la configuration
        const sub = this.securiteService.seConnecterEnAnonyme(codeDemarche).pipe(
            // Une fois connecté en anonyme, chargement du contenu de la configuration
            mergeMap(() => {
                message = i18n.application.debut2ChargementConfiguration.get(this.contexteService.langue);
                this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));

                return this.configurationService.chargerConfigurationDemarche(codeDemarche);
            }),

            // Sauvegarde de la configuration
            tap(conf => this.configuration = conf),

            // Vérification de l'état de l'application vis-à-vis des points d'entrée configurés
            mergeMap(() => this.definirEtDeclencerAuthentification(codeDemarche)),

            // Gestion de la réponse de la vérification
            tap(statutVerification => {
                // Si la vérification est bonne
                if (statutVerification === STATUT_CONNEXION_OIDC.connexionReussie || statutVerification === STATUT_CONNEXION_OIDC.modeAnonyme) {

                    // Initialisation des données initiales
                    if (this.configuration) {
                        for (const clef in this.configuration.valeursInitiales) {
                            const valeur = this.configuration.valeursInitiales[clef];
                            this.donneesService.sauvegarder(clef, valeur);
                        }
                    }

                    // Chargement du brouillon si le paramètre est présent
                    this.chargerBrouillon(codeDemarche);
                }
                // Sinon
                else {
                    // Pas de configuration pour ne rien chargé
                    this.configuration = undefined;
                    // Mais pas de message à afficher car l'OidcService les génère en détails
                }
            })
        ).subscribe();
        this.declarerSouscription(sub);
    }

    /** Méthode chargeant le brouillon si un id est passé en paramètre */
    private chargerBrouillon(codeDemarche: string): void {

        // Dans le cas d'une authentification OIDC, l'ID du brouillon a pu être récupéré et déjà initialisé dans le contexte
        let idBrouillon: string | null | undefined = this.contexteService.idBrouillon;

        // Si pas de brouillon OIDC, lecture depuis l'URL
        if (!idBrouillon) {
            // Lectures des paramètres de l'URL d'accès à la page
            const params = (new URL(window.location.href)).searchParams;

            // Vérification de la présence du paramètre
            idBrouillon = params.get('brouillon');
        }

        // Si on a un ID au final
        if (idBrouillon && idBrouillon.length > 2) {
            // message pour l'usager
            const message = i18n.application.debut3ChargementBrouillon.get(this.contexteService.langue);
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));
            // on conserve une instance
            // eslint-disable-next-line @typescript-eslint/no-this-alias
            const me = this;
            // Chargement du brouillon
            const sub = this.brouillonService.chargerBrouillon(codeDemarche, idBrouillon)
                .pipe(tap(() => {
                    this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, ''));
                    this.afficherApplication = true;
                }))
                .subscribe({
                    error() {
                        const message = i18n.application.debutErreurChargementBrouillon.get(me.contexteService.langue);
                        me.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));
                    }
                });
            this.declarerSouscription(sub);
        } else {
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, ''));
            this.afficherApplication = true;
        }
    }

    /** Méthode vérifiant les points d'entrée définidans la configuration */
    private definirEtDeclencerAuthentification(codeDemarche:string): Observable<STATUT_CONNEXION_OIDC> {

        // Lectures des paramètres de l'URL d'accès à la page
        const urlInitiale = this.oidcService.obtenirUrlInitiale(window.location.href);
        const paramsDansUrl = (new URL(urlInitiale)).searchParams;

        // Si un brouillon est à charger
        if (paramsDansUrl.has('brouillon') && paramsDansUrl.get('brouillon') != null) {
            const idBrouillon = paramsDansUrl.get('brouillon') as string;

            // Message à l'usager
            const message = i18n.application.debut3RechercheModeAuthBrouillon.get(this.contexteService.langue).replace('*idBrouillon*', idBrouillon);
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));

            // Recherche du mode d'authentification correspondant au brouillon
            this.logger.info('Recherche de l\'authentification du brouillon ' + idBrouillon);
            return this.brouillonService.obtenirAuthentificationNecessaireAuBrouillon(codeDemarche, idBrouillon).pipe(
                mergeMap(auth => {
                    this.logger.log('Authentification trouvée pour ce brouillon  : "', auth + '"');
                    const pe = new PointEntree();
                    if (auth) {
                        pe.authentification = AuthentificationPossibles.FranceConnect;
                    } else {
                        pe.authentification = AuthentificationPossibles.RepriseBrouillonAnonyme;
                    }
                    return this.gererLaSecurite(codeDemarche, pe);
                }));
        }

        // Si ce n'est pas un brouillon
        else {

            // Si la configuration ne définie pas de points d'entrée, rien à faire
            if (this.configuration && (!this.configuration.pointsEntree || this.configuration.pointsEntree.length === 0)) {
                return of(STATUT_CONNEXION_OIDC.modeAnonyme);
            }

            // Message à l'usager
            const message = i18n.application.debut3ChargementSansBrouillon.get(this.contexteService.langue);
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Information, message));

            this.logger.info('Recherche des points d\'entrée');
            let pointsEntreesAvecParam: PointEntree[] = [];
            let pointsEntreesSansParam: PointEntree[] = [];
            if (this.configuration) {
                // Recherche d'entrées correspondant aux paramètres de l'URL
                pointsEntreesAvecParam = this.configuration.pointsEntree.filter(
                    point => {
                        if (point.parametres) {
                            // Recherche, dans ce point d'entrée, du nombre de paramètres pour lesquels les paramsDansUrl sont cohérents
                            const nbParametresValides = point.parametres.filter(
                                param => param.parametre && paramsDansUrl.has(param.parametre) && param.valeurs.indexOf(paramsDansUrl.get(param.parametre) as string) !== -1
                            ).length

                            // Ce point d'entree est bon si tous les paramètres sont résolus
                            return point.parametres.length === nbParametresValides;
                        } else {
                            return false;
                        }
                    });

                // Recherche d'entrées sans paramètre (par défaut)
                pointsEntreesSansParam = this.configuration.pointsEntree.filter(p => !p.parametres);
            }

            // si plusieurs entrées avec param, on a un problème
            if (pointsEntreesAvecParam.length > 1) {
                const message = i18n.application.debutErreurPointEntreeMultiple.get(this.contexteService.langue);
                this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));
                this.configuration = undefined;
                this.logger.info('Erreur dans la vérification des points d\'entrées:', message);
                return of(STATUT_CONNEXION_OIDC.erreurURL);
            }

            // Si un unique point d'entrée avec param
            else if (pointsEntreesAvecParam.length === 1) {
                // sauvegarde des valeurs de l'URL correspondantes aux paramètres du point d'entrée sélectionné
                if (pointsEntreesAvecParam[0].parametres) {
                    for (let i = 0; i < pointsEntreesAvecParam[0].parametres.length; i++) {
                        const clefParam = pointsEntreesAvecParam[0].parametres[i].parametre;
                        if (clefParam) {
                            const valeurParam = paramsDansUrl.get(clefParam);
                            if (valeurParam) {
                                this.donneesService.sauvegarder(clefParam, valeurParam);
                            }
                        }
                    }
                }

                // gestion de la sécuritée
                this.logger.log('Entrée avec le point d\'entrée paramètres :', pointsEntreesAvecParam[0]);
                return this.gererLaSecurite(codeDemarche, pointsEntreesAvecParam[0]);
            }

            // Si un unique point d'entrée sans param
            else if (pointsEntreesSansParam.length === 1) {
                // Gestion de la sécurité
                this.logger.log('Entrée avec le point d\'entrée sans paramètres');
                return this.gererLaSecurite(codeDemarche, pointsEntreesSansParam[0]);
            }

            // Si aucun point d'entrée ne correspond
            else {
                const message = i18n.application.debutErreurPointEntree.get(this.contexteService.langue);
                this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));
                this.configuration = undefined;
                this.logger.info('Erreur dans la vérification des points d\'entrées:', message);
                return of(STATUT_CONNEXION_OIDC.erreurURL);
            }
        }
    }

    /**
     * Une fois la configuration chargée (après une connexion anonyme) et le bon point d'entrée trouvé, gestion de la sécurité
     * @param codeDemarche Code de la démarche
     * @param pointEntree Point d'entrée sélectionné
     */
    public gererLaSecurite(codeDemarche:string, pointEntree: PointEntree): Observable<STATUT_CONNEXION_OIDC> {

        // Si pas de sécurité demandée
        if (pointEntree && !pointEntree.authentification) {
            this.logger.info('Accès à la démarche sans authentification supplémentaire');
            return of(STATUT_CONNEXION_OIDC.modeAnonyme);
        }

        // Sinon connexion OIDC
        else if (pointEntree && pointEntree.authentification === AuthentificationPossibles.FranceConnect) {
            this.logger.info('Accès à la démarche nécessitant une authentification supplémentaire');
            return this.oidcService.initialiserAuthentification(codeDemarche);
        }

        // Sinon connexion pour reprise de brouillon anonyme
        else if (pointEntree && pointEntree.authentification === AuthentificationPossibles.RepriseBrouillonAnonyme) {
            this.logger.info('Accès à la démarche nécessitant une authentification de reprise de brouillon anonyme');
            // ouvrir un dialog
            return this.brouillonService.demanderSaisieParametreConnexionPourBrouillon().pipe(
                mergeMap(param => {
                    //récupérer un mail et un mdp
                    const email = param.nomUtilisateur;
                    const mdp = param.motDePasse;

                    // créer un token avec ces info
                    return this.securiteService.seConnecterAvecUnMotDePasse(email, mdp);
                }),
                // Transformation du boolean en résultat
                map(statut => statut ? STATUT_CONNEXION_OIDC.connexionReussie : STATUT_CONNEXION_OIDC.erreurConnexion)
            );
        }

        // si pas de point d'entrée ou valeur incohérente
        else {
            const message = i18n.application.debutErreurPointEntree.get(this.contexteService.langue);
            this.contexteService.afficherUnMessageGeneral(new MessageAafficher('oidc', TypeMessageAafficher.Erreur, message));
            this.configuration = undefined;
            this.logger.info('Erreur dans la vérification du point d\'entrée trouvé :', message);
            return of(STATUT_CONNEXION_OIDC.erreurURL);
        }
    }
}
