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
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { EnvironnementService } from '../../../../framework/src/lib/services/stateful/environnement.service';
import { UtilisateurConnecte } from '../model/securite.model';
import { AdminContexteService } from './adminContexte.service';
import { AdminBouchonService } from './adminbouchon.service';
import { ClientAdminApiService } from './clientadminapi.service';

@Injectable({ providedIn: 'root' })
export class AdminService extends ClientAdminApiService {

    /** URI de connexion */
    private static readonly URI_AUTHENTIFICATION = '/socle/adminpsl/connexion';

    /** URI de la liste des codes de démarche */
    private static readonly URI_DEMARCHE_LISTE_ET_CREATION = '/socle/adminpsl/demarche';

    /** URI de recherche des trasferts */
    private static readonly URI_LISTE_TRANSFERTS = '/socle/adminpsl/transfert';

    /** Constructeur pour injection de dépendance. */
    public constructor(httpClient: HttpClient, adminContexte: AdminContexteService, logger: NGXLogger, private bouchon: AdminBouchonService, private environnementService: EnvironnementService) {
        super(httpClient, logger, adminContexte);
    }

    /** Récupération de la liste des codes de démarche */
    public listerCodesDemarche(): Observable<string[]> {
        this.logger.info('Chargement des codes de démarche');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Chargement via le bouchon');
            return this.bouchon.appelerApiBouchonne(AdminService.URI_DEMARCHE_LISTE_ET_CREATION, '');
        }

        // Appel réel
        const url = this.environnementService.urls.socle.adminpsl + AdminService.URI_DEMARCHE_LISTE_ET_CREATION;
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut: string[] = [];
        return this.get<string[]>('listerCodesDemarche', url, options, valeurParDefaut);
    }

    /** Tentative de connexion*/
    public tenterConnexion(nomUtilisateur: string, motDePasse: string): Observable<boolean> {
        this.logger.info('Tentative de connexion');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Connexion via le bouchon');
            this.contexte.sauvegarderNouveauTokenJwt('tokenBouchon');
            this.contexte.sauvegarderDonneesUtilisateur({ nom: 'DUPONT', login: 'remi.dupont@test.fr' });
            return of(true);
        }

        // Spécificités d'un appel en FormData avec un retour textuel pur (et pas JSON)
        const options = this.creerOptionsSimplesPourPostMultipart() as any;
        options.responseType = 'text';
        const donnees = new FormData();
        donnees.append('nomUtilisateur', nomUtilisateur);
        donnees.append('motDePasse', motDePasse);

        // Véritable appel
        const url = this.environnementService.urls.socle.adminpsl + AdminService.URI_AUTHENTIFICATION;
        const valeurParDefaut: any | undefined = undefined;
        return this.post<string | undefined>('tenterConnexion', url, donnees, options, valeurParDefaut).pipe(
            map(reponse => {
                if (reponse) {
                    const utilisateurConnecte = this.extraireDonneesUtilisateurDuToken(reponse);
                    if (utilisateurConnecte) {
                        this.contexte.sauvegarderNouveauTokenJwt(reponse);
                        this.contexte.sauvegarderDonneesUtilisateur(utilisateurConnecte);
                        this.logger.info('Connexion réussie avec un token de ' + reponse.length + 'o');
                        return true;
                    } else {
                        this.logger.error('Connexion échouée à la lecture des informations utilisateur dans le token');
                        return false;
                    }
                } else {
                    this.logger.error('Connexion échouée');
                    return false;
                }
            })
        );
    }

    /** Lecture du token pour en extraire les données de l'utilisateur */
    private extraireDonneesUtilisateurDuToken(token: string): UtilisateurConnecte | undefined {
        // Si pas de token ou mal formaté
        if (!token || token.indexOf('.') === -1) {
            return undefined;
        }

        // Découpage du token pour en récupérer les claims
        const claimsDecode = window.atob(token.split('.')[1]);
        if (!claimsDecode) {
            return undefined;
        }

        // Parse du JSON obtenu
        const claims = JSON.parse(claimsDecode);

        // Création de l'objet en sortie
        const utilisateur = new UtilisateurConnecte();
        utilisateur.login = claims['sub'];
        utilisateur.nom = claims['cn'].replace('cn: ', '');
        return utilisateur;
    }

    /** Recherche des transferts */
    public rechercherTransferts(params: { dateDebut: Date | undefined; dateFin: Date | undefined; codesDemarche: string[]; numeroTeledossier: string | undefined; numeroPage: number; nombreElementsParPage: number; }): Observable<{ resultats: [], nombreTotalResultats: number }> {
        this.logger.info('Recherche de transferts avec les paramètres', params);

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Recherche bouchonnée');
            return this.bouchon.appelerApiBouchonne(AdminService.URI_LISTE_TRANSFERTS, '');
        }

        // Appel réel
        const url = this.environnementService.urls.socle.adminpsl + AdminService.URI_LISTE_TRANSFERTS;
        const valeurParDefaut: any | undefined = { resultats: [], nombreTotalResultats: 0 };
        const options = this.creerOptionsSimplesAvecAuthentification();
        return this.post<{ resultats: [], nombreTotalResultats: number }>('rechercherTransferts', url, params, options, valeurParDefaut);
    }


    /**
     * Création d'une démarche (configuration publique et interne).
     * @param codeDemarche Code de la démarche.
     */
    public creerNouvelleDemarche(codeDemarche: string): Observable<void> {
        this.logger.info('Création d\'une démarche "' + codeDemarche + '"');

        // Pour bouchonner les APIs dans le cas des tests e2e
        if (this.bouchon.verifierSiApiEstBouchonne()) {
            this.logger.info('Appel bouchonné');
        }

        // Appel réel
        const url = this.environnementService.urls.socle.adminpsl + AdminService.URI_DEMARCHE_LISTE_ET_CREATION;
        const options = this.creerOptionsSimplesAvecAuthentification();
        return this.post<void>('creerNouvelleDemarche', url, codeDemarche, options, undefined);
    }
}
