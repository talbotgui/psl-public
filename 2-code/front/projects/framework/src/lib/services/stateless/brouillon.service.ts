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
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { i18n } from '../../conf/i18n';
import { ConnexionBrouillonDialog } from '../../modale/connexionbrouillon/fmk.connexionbrouillon';
import { BrouillonDemarche } from '../../model/brouillonsoumissiondemarche.model';
import { MessageAafficher, TypeMessageAafficher } from '../../model/message.model';
import { ContexteService } from '../../services/stateful/contexte.service';
import { DonneesService } from '../../services/stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';
import { SecuriteService } from './securite.service';

@Injectable({ providedIn: 'root' })
export class BrouillonService extends ClientApiService {
    /** URI de sauvegarde d'un brouillon */
    private static readonly URI_SAUVEGARDE_BROUILLON = '/socle/brouillon/brouillon';

    /**URI de chargement d'un brouillon*/
    private static readonly URI_CHARGEMENT_BROUILLON = '/socle/brouillon/brouillon/{codeDemarche}/{idBrouillon}';

    /**URI de chargement d'un brouillon*/
    private static readonly URI_AUTHENTIFICATION_BROUILLON = '/socle/brouillon/brouillon/{codeDemarche}/{idBrouillon}/authentification';

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, contexte: ContexteService, private donnees: DonneesService, private securiteService: SecuriteService, private bouchon: BouchonService, private dialog: MatDialog, private environnementService: EnvironnementService, logger: NGXLogger) {
        super(http, logger, contexte);
    }

    /** Recherche de l'authentification nécessaire à l'accès à un brouillon */
    public obtenirAuthentificationNecessaireAuBrouillon(codeDemarche: string, idBrouillon: string): Observable<string> {
        const url = this.environnementService.urls.socle.gateway + BrouillonService.URI_AUTHENTIFICATION_BROUILLON.replace('{codeDemarche}', codeDemarche).replace('{idBrouillon}', idBrouillon);

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            return of(this.environnementService.urls.socle.modeAuthentificationRepriseBrouillon);
        }

        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = '';
        return this.get<string>('obtenirAuthentificationNecessaireAuBrouillon', url, options, valeurParDefaut);
    }


    /** Chargement du brouillon par son identifiant */
    public chargerBrouillon(codeDemarche:string, idBrouillon: string): Observable<BrouillonDemarche | undefined> {
        let url = this.environnementService.urls.socle.gateway + BrouillonService.URI_CHARGEMENT_BROUILLON.replace('{codeDemarche}', codeDemarche).replace('{idBrouillon}', idBrouillon);

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            url = this.bouchon.recupererUrlBrouillonBouchonne(codeDemarche);
        }

        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = undefined;
        return this.get<BrouillonDemarche | undefined>('chargerBrouillon', url, options, valeurParDefaut)
            .pipe(map(brouillon => {
                // Log
                this.logger.info('Chargement du brouillon', brouillon);

                if (brouillon) {
                    // Chargement des données dans le contexte
                    // avec potentielle transformation des dates '01/01/2020' en véritables date
                    Object.keys(brouillon.donnees).forEach(clef => {
                        const valeur = brouillon.donnees[clef];
                        this.donnees.sauvegarder(clef, valeur);
                    });

                    // Affichage de la bonne page à l'usager
                    // Si la configuration a changé, on le remet au début
                    // Sinon on le laisse sur la page depuis laquelle il a fait sa sauvegarde
                    let bonIndex = brouillon.indexPage;
                    if (!bonIndex || this.contexte.configurationDemarche && brouillon.versionConfiguration !== this.contexte.configurationDemarche.versionConfiguration) {
                        bonIndex = 0;
                    }
                    this.contexte.changerDePageAuChargementDuBrouillonUniquement(bonIndex);

                    // Recalcul de toutes les conditions d'affichage de tous les contenus de tous les blocs non dynamiques (ceux-là sont traités spécifiquement partout) jusqu'à la page à afficher (le recap en a besoin)
                    if (bonIndex > 0) {
                        for (let i = 0; i < bonIndex - 1; i++) {
                            this.contexte.configurationDemarche?.pages[i].blocs
                                .filter(b => !b.dynamique)
                                .forEach(b => b.contenus.forEach(c => c.visibilite = this.donnees.calculerCondition(c.conditionVisibilite, brouillon.donnees, true)));
                        }
                    }

                    // Sauvegarde de l'ID du brouillon sauvegardé
                    this.contexte.idBrouillon = idBrouillon;
                }

                // Si pas de brouillon, il faut lancer une erreur
                else {
                    throw new Error('Aucun brouillon trouvé');
                }

                return brouillon;
            }));
    }

    public sauvegarderBrouillon(indexPageCourante: number): Observable<void> {
        this.logger.info('Sauvegarde du brouillon !');

        // Vérification de l'existance d'un id de brouillon précédement chargé
        const idBrouillon = this.contexte.idBrouillon;
        if (idBrouillon) {
            this.logger.info('Le brouillon \'' + idBrouillon + '\' a été précédemment chargé ou sauvegardé');
        } else {
            this.logger.info('Aucun brouillon précédemment chargé ou sauvegardé, il faudra saisir un mot de passe');
        }

        // Création du brouillon
        const brouillon = new BrouillonDemarche();
        if (idBrouillon) {
            brouillon.id = idBrouillon;
        }
        if (this.contexte.configurationDemarche) {
            brouillon.codeDemarche = this.contexte.configurationDemarche.codeDemarche;
            brouillon.versionConfiguration = this.contexte.configurationDemarche.versionConfiguration;
        }
        brouillon.indexPage = indexPageCourante;
        brouillon.donnees = this.donnees.lireTout();

        // log
        this.logger.info('Le brouillon à envoyer à une API est : ', brouillon);

        // Préparation de l'appel dans une méthode ()=>{} pour mettre, dans les options, le token généré dans le cas d'une authentification par mot de passe
        let appel = () => {
            const url = this.environnementService.urls.socle.gateway + BrouillonService.URI_SAUVEGARDE_BROUILLON;
            const options = this.creerOptionsSimplesAvecAuthentification();
            const valeurParDefaut = undefined;
            return this.post<string | undefined>('sauvegarderBrouillon', url, brouillon, options, valeurParDefaut);
        }

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            this.logger.info('Le brouillon est bouchonné');
            appel = () => of('idBrouillonBidon');
        }

        const afficherSuccess = (idBrouillon: string | undefined) => {
            if (idBrouillon) {
                this.logger.info('Brouillon sauvegardé avec succès : ' + idBrouillon);
                this.contexte.idBrouillon = idBrouillon;
                this.afficherMessageDeSucces();
            }
        };

        //Si on a déjà l'authentification adéquate
        if (idBrouillon) {
            // Appel 
            return appel().pipe(map(afficherSuccess));
        }

        // S'il faut saisir un mot de passe en premier lieu
        else {
            // demande de paramètres de connexion
            return this.demanderSaisieParametreConnexionPourBrouillon().pipe(
                mergeMap(param => {
                    //récupérer un mail et un mdp
                    const email = param.nomUtilisateur;
                    const mdp = param.motDePasse;

                    // créer un token avec ces info
                    return this.securiteService.seConnecterAvecUnMotDePasse(email, mdp);
                }),
                mergeMap(() => {
                    // Faire l'appel à la sauvegarde de brouillon
                    return appel().pipe(map(afficherSuccess));
                })
            );
        }
    }

    /** Affichage du message avec le lien de reprise */
    private afficherMessageDeSucces(): void {
        // Si l'URL ne contient pas encore l'ID du brouillon, on l'ajoute dans la variable 'url'
        let url = window.location.href;
        if (!url.includes('brouillon=' + this.contexte.idBrouillon)) {
            const lienActuelContientUnParametre = window.location.href.indexOf('?') !== -1;
            url += (lienActuelContientUnParametre ? '&' : '?') + 'brouillon=' + this.contexte.idBrouillon;
        }
        // Et on affiche un message avec l'URL de reprise
        const lien = '<a href="' + url + '">' + url + '</a>';
        const message = i18n.brouillonService.brouillonBienSauvegarde.get(this.contexte.langue).replace('*lien*', lien);
        this.contexte.afficherUnMessageGeneral(new MessageAafficher('sauvegarderBrouillon', TypeMessageAafficher.Information, message));
    }

    /** Demande de confirmation */
    public demanderSaisieParametreConnexionPourBrouillon(): Observable<{ nomUtilisateur: string, motDePasse: string }> {

        // Ouverture de la popup sans possibilité de la fermer (car on est en mode connexion)
        const dialogRef = this.dialog.open(ConnexionBrouillonDialog, { disableClose: true });
        dialogRef.componentInstance.flagReprise = true;

        // Renvoi de la callback
        return dialogRef.afterClosed();
    }
}
