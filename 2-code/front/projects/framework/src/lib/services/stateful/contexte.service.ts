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
import { UntypedFormGroup } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { ConfigurationDemarche, Page } from '../../model/configurationdemarchegeneral.model';
import { MessageAafficher } from '../../model/message.model';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';
import { DonneesService } from './donnees.service';


@Injectable({ providedIn: 'root' })
export class ContexteService {

    /** Token JWT de sécurité actuel et valide */
    private tokenJwt: string | undefined = undefined;
    public get tokenJwtCourant(): string | undefined { return this.tokenJwt; }

    /** Flag permettant de savoir si l'usager et connecté OIDC*/
    public utilisateurConnecteOidc = false;

    /** Subject lié à la connexion de l'utilisateur. */
    private informationsUtilisateurConnecteSubject = new BehaviorSubject<any | undefined>(undefined);

    /** Identifiant du brouillon chargé (pour le mettre à jour à la demande de l'usager) */
    public idBrouillon: string | undefined;

    /** Soumission réalisée */
    private soumissionRealisee = false;
    public get soumissionRealiseeBloquantLeRetourArriere(): boolean { return this.soumissionRealisee; }

    /** Instance de configuration accessible à tous en cas de besoin */
    public get configurationDemarche(): ConfigurationDemarche | undefined { return this.configurationDemarcheSubject.value; }

    /** Subject lié au chargement de la configuration. */
    private configurationDemarcheSubject = new BehaviorSubject<ConfigurationDemarche | undefined>(undefined);

    /** Instance de langue en coursconfiguration accessible à tous en cas de besoin */
    public get langue(): string { return this.langueSubject.value || 'FR'; }

    /** Subject lié au chargement de la langue. */
    private langueSubject = new BehaviorSubject<string | undefined>(undefined);

    /** Subject lié au changement de page. */
    private indexPageSubject = new BehaviorSubject<number>(0);

    /** Subject lié aux messages généraux à afficher en haut de page. */
    private messageGeneralSubject = new BehaviorSubject<MessageAafficher | undefined>(undefined);

    /** Constructeur avec injection des dépendances */
    public constructor(private donneesService: DonneesService, private logger: NGXLogger) { }

    /** Setter déguisé */
    public stockerConfigurationDemarche(config: ConfigurationDemarche): void {

        // stockage de la donnée
        this.configurationDemarcheSubject.next(config);
    }

    /** Etre notifié quand un message général est créé */
    public obtenirUnObservableSurLesMessagesGeneraux(): Observable<MessageAafficher | undefined> {
        return this.messageGeneralSubject.asObservable();
    }

    /** Etre notifié quand la configuration change */
    public obtenirUnObservableDeChargementDeLaConfiguration(): Observable<ConfigurationDemarche | undefined> {
        return this.configurationDemarcheSubject.asObservable();
    }

    /** Etre notifié quand la langue change (valeur vide pour 'FR') */
    public obtenirUnObservableDeChargementDeLaLangue(): Observable<string | undefined> {
        return this.langueSubject.asObservable();
    }

    /** Etre notifié quand les informations de l'utilisateur connecté changent */
    public obtenirUnObservableDesInformationsDeLutilisateurConnecte(): Observable<any | undefined> {
        return this.informationsUtilisateurConnecteSubject.asObservable();
    }

    /** Récupérer la configuration quand elle sera là en cours */
    public obtenirUnObservableDeChangementDePage(): Observable<number> {
        return this.indexPageSubject.asObservable();
    }

    /** Sauvegarde du token */
    public sauvegarderNouveauTokenJwt(tokenJwt: string | undefined): void {
        this.tokenJwt = tokenJwt;
    }

    /** Sauvegarde informations post connexion */
    public sauvegarderDonnesUtilisateur(utilisateur: any): void {
        this.informationsUtilisateurConnecteSubject.next(utilisateur);
    }

    /**
     * Change la page en cours sans validation des données saisie.
     * A n'utiliser que pour revenir en arrière !
     */
    public changerDePage(nouvelIndexPage: number): void {
        if (nouvelIndexPage > this.indexPageSubject.value) {
            this.logger.error('Erreur dans le code, on ne doit pas aller directement à une page suppérieur !');
        } else if (nouvelIndexPage < this.indexPageSubject.value) {
            this.indexPageSubject.next(nouvelIndexPage);
        } else {
            // rien à faire car on est déjà sur la bonne page
        }
    }

    /**
     * Change la page en cours sans validation des données.
     * A n'utiliser qu'au chargement du brouillon !!
     */
    public changerDePageAuChargementDuBrouillonUniquement(nouvelIndexPage: number | undefined): void {
        if (nouvelIndexPage !== undefined && this.configurationDemarche && this.configurationDemarche.pages && nouvelIndexPage < this.configurationDemarche.pages.length) {
            this.indexPageSubject.next(nouvelIndexPage);
        }
    }

    /** Affiche un message général. */
    public afficherUnMessageGeneral(message: MessageAafficher | undefined): void {
        this.messageGeneralSubject.next(message);
    }

    /** Permet d'avancer dans la navigation */
    public avancer(page: Page, formulaire: UntypedFormGroup): void {
        // Si le formulaire est invalide, on ne doit pas arriver jusque là.
        // Si la configuration n'est pas chargée, non plus
        // Mais au cas où
        if (!formulaire.valid || !this.configurationDemarche) {
            return;
        }

        // Sauvegarde des données dans le contexte tout de même
        this.donneesService.sauvegarderDonneesDuFormulaire(this.configurationDemarche, page, formulaire);

        // Changement de page
        const index = this.calculerIndexDeLaPageSuivante();
        if (index !== undefined) {
            this.indexPageSubject.next(index);
        }
    }

    /** Permet de reculer dans la navigation */
    public reculer(page: Page, formulaire: UntypedFormGroup): void {
        // pas de retour arrière après soumission
        if (this.soumissionRealisee) {
            return;
        }
        // Si la configuration n'est pas chargée, on part
        if (!this.configurationDemarche) {
            return;
        }

        // On peut reculer avec un formulaire invalide

        // Sauvegarde des données dans le contexte tout de même
        this.donneesService.sauvegarderDonneesDuFormulaire(this.configurationDemarche, page, formulaire);

        // Changement de page
        const index = this.calculerIndexDeLaPagePrecedente();
        if (index !== undefined) {
            this.indexPageSubject.next(index);
        }
    }

    /** Méthode définissant si la page en paramètre est la première affichable */
    public calculerSiPageEstPremiereAffichable(page: Page): boolean {
        return this.calculerIndexDeLaPagePrecedente(page) === undefined;
    }

    /** Méthode définissant si la page en paramètre est la dernière affichable */
    public calculerSiPageEstDerniereAffichable(page: Page): boolean {
        return this.calculerIndexDeLaPageSuivante(page) === undefined;
    }

    /** Renvoi l'index de la page passée en paramètre. */
    public rechercherIndexPage(page: Page): number | undefined {
        if (!this.configurationDemarche) {
            return undefined;
        } else {
            return this.configurationDemarche.pages.indexOf(page);
        }
    }

    /** Cette méthode est à usage exclusif du composant de soumission. */
    public avancerDepuisLaSoumissionDeDemarche(): void {
        // Si la configuration n'est pas chargée, on part
        if (!this.configurationDemarche) {
            return;
        }

        const page = this.configurationDemarche.pages[this.indexPageSubject.value];

        // Contrôle au cas où
        if (!UtilitaireModel.rechercherComposantDansLaPage(page, TypeContenuDeBloc.finDemarche)) {
            this.logger.error('Erreur de développement : cette méthode ne doit être appelée que depuis un composant de soumission');
        }

        // On conserve l'information
        this.soumissionRealisee = true;

        // On avance
        this.avancer(page, new UntypedFormGroup({}));
    }

    /** Changement de la langue de l'application et donc recalcul de tous les libellés */
    public changerLangue(langue: string): void {
        this.langueSubject.next(langue);
    }

    /** Calcul l'index de la page affichable suivante */
    private calculerIndexDeLaPageSuivante(page?: Page): number | undefined {

        // Calcul à partir de la page courante ou de la page en paramètre
        let indexAtester: number | undefined = this.indexPageSubject.value;
        if (page) {
            indexAtester = this.rechercherIndexPage(page);
        }

        // Si la page est la toute dernière
        if (!!this.configurationDemarche && indexAtester === this.configurationDemarche.pages.length - 1) {
            return undefined;
        }

        // Test de chaque page suivante
        if (indexAtester !== undefined && this.configurationDemarche) {
            for (let i = indexAtester + 1; i < this.configurationDemarche.pages.length; i++) {
                if (this.calculerSiIndexPageAffichable(i)) {
                    return i;
                }
            }
        }

        // Si aucune n'est bonne
        return undefined;
    }

    /** Calcul l'index de la précédente page affichable */
    private calculerIndexDeLaPagePrecedente(page?: Page): number | undefined {

        // Calcul à partir de la page courante ou de la page en paramètre
        let indexAtester: number | undefined = this.indexPageSubject.value;
        if (page) {
            indexAtester = this.rechercherIndexPage(page);
        }

        // Si la page est la toute première
        if (indexAtester === 0) {
            return undefined;
        }

        // Test de chaque page précédente
        if (indexAtester !== undefined) {
            for (let i = indexAtester - 1; i > -1; i--) {
                if (this.calculerSiIndexPageAffichable(i)) {
                    return i;
                }
            }
        }

        // Si aucune n'est bonne
        return undefined;
    }

    /** Calcul des conditions d'affichage d'une page */
    private calculerSiIndexPageAffichable(indexAtester: number): boolean {
        return !!this.configurationDemarche && this.calculerSiPageAffichable(this.configurationDemarche.pages[indexAtester]);
    }

    /** Calcul des conditions d'affichage d'une page */
    private calculerSiPageAffichable(page: Page): boolean {
        const toutesLesDonnees = this.donneesService.lireToutEtIntegrerLesDonneesFournies({});
        page.visibilite = this.donneesService.calculerCondition(page.conditionVisibilite, toutesLesDonnees, true);
        return page.visibilite;
    }
}
