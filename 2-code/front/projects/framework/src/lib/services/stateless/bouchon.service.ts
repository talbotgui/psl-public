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
import { HttpEvent, HttpEventType, HttpHeaderResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { DocumentsPostSoumission, DonneesDeSoumission } from '../../model/brouillonsoumissiondemarche.model';
import { EnvironnementService } from '../stateful/environnement.service';

/**
 * Cette classe contient les méthodes de bouchonnage des APIs.
 * 
 * Les bouchons sont impérativement utilisés dans les tests E2E.
 * Ils peuvent aussi l'être durant les développements.
 * 
 * 
 * Comment bouchonner un simple appel GET :
 * - mettre un fichier JSON (ou autre) dans le répertoire bouchonapi
 * - modifier l'URL dans le fichier this.environnementService.ts (et ses variantes) pour pointer sur le fichier du répertoire bouchonapi
 * Comment bouchonner un GET complexe ou un PUT/POST/DELETE
 * - ajouter une variable 'xxxxxBouchonne' dans la configuration
 * - vérifier cette variable dans le service réel
 * - si la variable existe et est 'true', faire appel à une méthode statique de cette classe
 */
@Injectable({ providedIn: 'root' })
export class BouchonService {

    /** La liste des APIs est à maintenir dans schemajson.ts */
    private static readonly MINI_REFERENTIEL = {
        communeNaissance: [
            { codePostal: '80000', code: '80001', libelle: 'Amiens', libelleLong: 'Amiens (80000)' },
            { codePostal: '80400', code: '80401', libelle: 'Ham', libelleLong: 'Ham (80400)' },
            { codePostal: '18000', code: '18001', libelle: 'Bourges', libelleLong: 'Bourges (18000)' },
            { code: '18801', libelle: 'Baugy', libelleLong: 'Baugy' },
        ],
        communeActuelle: [
            { codePostal: '80000', code: '80001', libelle: 'Amiens', libelleLong: 'Amiens (80000)', typeProtection: "PN" },
            { codePostal: '80400', code: '80401', libelle: 'Ham', libelleLong: 'Ham (80400)', typeProtection: "GN" },
            { codePostal: '18000', code: '18001', libelle: 'Bourges', libelleLong: 'Bourges (18000)', typeProtection: "PN" },
            { code: '18801', libelle: 'Baugy', libelleLong: 'Baugy' },
        ],
        paysNaissance: [
            { code: 1, libelle: 'France' },
            { code: 2, libelle: 'Etats-Unis' },
            { code: 3, libelle: 'Italie' },
            { code: 4, libelle: 'Belgique' },
        ],
        nationalitePaysActuel: [
            { codeInsee: 1, libelle: 'France', nationalite: 'Française', capitale: 'Paris' },
            { codeInsee: 2, libelle: 'Etats-Unis', nationalite: 'Etasuniene', capitale: 'New York' },
            { codeInsee: 3, libelle: 'Italie', nationalite: 'Italiène', capitale: 'Rome' },
            { codeInsee: 4, libelle: 'Belgique', nationalite: 'Belge', capitale: 'Bruxelles' },
        ],
        pays: [
            { codeInsee: 1, libelle: 'France', nationalite: 'Française', capitale: 'Paris' },
            { codeInsee: 2, libelle: 'Etats-Unis', nationalite: 'Etasuniene', capitale: 'New York' },
            { codeInsee: 3, libelle: 'Italie', nationalite: 'Italiène', capitale: 'Rome' },
            { codeInsee: 4, libelle: 'Belgique', nationalite: 'Belge', capitale: 'Bruxelles' },
        ],
        communeban: [
            { name: 'Amiens', libelleLong: 'Amiens (80000)' },
            { name: 'Ham', libelleLong: 'Ham (80400)' },
            { name: 'Bourges', libelleLong: 'Bourges (18000)' },
            { name: 'Baugy', libelleLong: 'Baugy (18800)' },
        ],
        adresseban: [
            { label: '2 rue de l\'église 80000 Amiens' },
            { label: '1 rue de la république 80400 Ham' },
            { label: '3 place de la cathédrale 18000 Bourges' },
            { label: '4 place du chateau 18800 Baugy' },
            { label: '59 rue du port 80400 Ham' },
        ]
    };

    /** Constructeur pour injection de dépendance. */
    public constructor(private logger: NGXLogger, private environnementService: EnvironnementService) { }

    /** Vérification de l'état du bouchon dans la configuration */
    public verifierSiReferentielEstBouchonne(): boolean {
        return this.environnementService.urls.socle.referentielBouchonne && this.environnementService.urls.socle.referentielBouchonne === true;
    }

    /** Vérification de l'état du bouchon dans la configuration */
    public verifierSiSoumissionEstBouchonnee(): boolean {
        return this.environnementService.urls.socle.soumissionBouchonne && this.environnementService.urls.socle.soumissionBouchonne === true;
    }

    /** Vérification de l'état du bouchon dans la configuration */
    public verifierSiSecuriteEstBouchonnee(): boolean {
        return this.environnementService.urls.socle.securiteBouchonne && this.environnementService.urls.socle.securiteBouchonne === true;
    }

    /** Vérification de l'état du bouchon dans la configuration */
    public verifierSiDocumentEstBouchonnee(): boolean {
        return this.environnementService.urls.socle.documentBouchonne && this.environnementService.urls.socle.documentBouchonne === true;
    }

    /** Simulation de l'appel à un référentiel. En réalité, utilisation de données en constantes dans cette classe */
    public appelerReferentielBouchonne(api: string, valeur: string): Observable<any[]> {
        this.logger.trace('Appel bouchonné à l\'API \'' + api + '\' avec le paramètre \'' + valeur + '\'');
        return of((BouchonService.MINI_REFERENTIEL as any)[api]
            .filter((e: any) => (e.nom || e.libelleLong || e.libelle || e.name || e.label).toUpperCase().replace(/\(/g, '').indexOf(valeur.toUpperCase()) !== -1));
    }

    /** Simulation d'appel à la soumission. Mais simple renvoi d'un ID fixe */
    public appelerSoumissionBouchonnee(donnees: DonneesDeSoumission): Observable<string> {
        this.logger.trace('Appel bouchonné à la soumission avec les données', donnees);
        return of('123456');
    }

    /** Simulation d'appel à la récupération des informations de l'usager connecté. Les données sont en dur dans la méthode. */
    public recupererInformationsUsager(): Observable<object> {
        return of(
            {
                "email": "wossewodda-3728@yopmail.com",
                "emailTechnique": "wossewodda-3728@yopmailTECHNIQUE.com",
                "accountType": "particulier", "franceConnect": true,
                "civilite": "M", "dateDeNaissance": "2022-04-23T08:15:24.419Z", "nom": "nom", "prenoms": "prenom1 prenom2", "sexe": 1, "nomDeNaissance": "NOM",
                "adresse": { "libelleVoie": "voie", "lieuDitOuBoitePostale": "lieuDit", "batimentOuImmeuble": "batiment", "appartementOuEtage": "appartement", "ville": "Amiens", "pays": "France", "codePostal": "80000", "codeInsee": "80021", "complement": "complément", "etatProvince": "Somme" },
                "codeInseeDeNaissance": "80021", "codeInseePaysDeNaissance": "12345", "communeDeNaissance": "Amiens", "departementDeNaissance": "Somme", "paysDeNaissance": "France",
                "situationFamiliale": "Célibataire",
                "telephoneFixe": { "numero": "0123456789", "indicatif": "+33" }, "telephoneMobile": { "numero": "0623456789", "indicatif": "+33" }
            });
    }

    /** Chargement de la liste des documents post-soumission */
    public chargerListeDocumentsPostSoumission(codeDemarche: string|undefined): Observable<DocumentsPostSoumission[]> {
        this.logger.trace('Appel bouchonné à la recherche de documents post-soumission');
        const documents: DocumentsPostSoumission[] = [];
        const doc1 = new DocumentsPostSoumission();
        doc1.codeDocument = 'code1';
        doc1.contentType = 'application/pdf';
        doc1.nomDocument = 'Le premier document';
        doc1.libelleDocument = 'monPdf1.pdf'
        doc1.ordrePresentation = 1;
        doc1.taille = 10;
        doc1.urlTelechargement = this.recupererUrlBrouillonBouchonne(codeDemarche);
        documents.push(doc1);
        const doc2 = new DocumentsPostSoumission();
        doc2.codeDocument = 'code2';
        doc2.contentType = 'application/pdf';
        doc2.nomDocument = 'Le second document';
        doc1.libelleDocument = 'monPdf2.pdf'
        doc2.ordrePresentation = 2;
        doc2.taille = 11;
        doc2.urlTelechargement = this.recupererUrlConfigurationBouchonne(codeDemarche);
        documents.push(doc2);
        return of(documents);
    }

    /** Récupération de l'URL du brouillon de bouchon (en relatif ou en absolu) */
    public recupererUrlBrouillonBouchonne(codeDemarche: string|undefined): string {
        let url = this.environnementService.urls.socle.urlBrouillonBouchonne as string;
        url = url.replace('CODE_DEMARCHE', codeDemarche?.toLocaleLowerCase() || '');
        if (url && url.startsWith('http')) {
            return url;
        } else {
            return document.baseURI + url;
        }
    }

    /** Récupération de l'URL de la configuration de bouchon (en relatif ou en absolu) */
    public recupererUrlConfigurationBouchonne(codeDemarche: string|undefined): string {
        let url = this.environnementService.urls.socle.urlConfigurationBouchonne as string;
        url = url.replace('CODE_DEMARCHE', codeDemarche?.toLocaleLowerCase() || '');
        if (url && url.startsWith('http')) {
            return url;
        } else {
            return document.baseURI + url;
        }
    }

    /** Simulation d'un upload de pièce jointe bien long/lent pour voir la barre de progression */
    public verserPieceJointeEnUneUniqueRequete(): Observable<HttpEvent<any>> {
        // Renvoi immédiat de l'évènement de démarrage de l'upload
        const subject = new BehaviorSubject({ type: HttpEventType.Sent } as HttpEvent<any>);
        // Progression à 25%
        setTimeout(() => subject.next({ type: HttpEventType.UploadProgress, loaded: 25, total: 100 } as HttpEvent<any>), 500);
        // Progression à 50%
        setTimeout(() => subject.next({ type: HttpEventType.UploadProgress, loaded: 50, total: 100 } as HttpEvent<any>), 1000);
        // Progression à 75%
        setTimeout(() => subject.next({ type: HttpEventType.UploadProgress, loaded: 75, total: 100 } as HttpEvent<any>), 1500);
        // Envoi de l'upload terminé
        setTimeout(() => subject.next(new HttpHeaderResponse()), 2000);
        // réponse reçue
        setTimeout(() => subject.next(new HttpResponse<string>({ body: 'idPJ1' })), 2500);

        return subject.asObservable();
    }

    /** Bouchon de téléchargement de document qui propose le téléchargement de la configuration bouchonnée */
    public telechargerDocument(): Observable<void> {
        window.open(this.environnementService.urls.socle.urlConfigurationBouchonne, '_blank');
        return of();
    }

    /** Bouchon de la génération d'un captcha */
    public simulerAppelGenerationCaptcha(): Observable<string> {
        this.logger.trace('Appel au captcha bouchonné');
        return of('<div style="border:solid 1px black"><input type="hidden" name="idInput" value="captchaFormulaireExtInput"><span>bouchon captcha dans Angular</span><br/><span>Toute valeur est autorisée.</span></div>');
    }
}
