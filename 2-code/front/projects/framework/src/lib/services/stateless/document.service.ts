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
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { DocumentsPostSoumission } from '../../model/brouillonsoumissiondemarche.model';
import { ContexteService } from '../stateful/contexte.service';
import { DonneesService } from '../stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

@Injectable({ providedIn: 'root' })
export class DocumentService extends ClientApiService {

    /**URI listant les documents visibles du télé-dossier*/
    private static readonly URI_LISTE_DOCUMENT = '/socle/document/teledossier/{numeroTeledossier}/document';

    /** URI de téléchargement d'un document du télé-dossier */
    private static readonly URI_TELECHARGEMENT_DOCUMENT = '/socle/document/teledossier/{numeroTeledossier}/document/{codeDocument}';

    /** URI de demande de la clef de téléchargement d'un document du télé-dossier */
    private static readonly URI_TELECHARGEMENT_DOCUMENT_CLEF = DocumentService.URI_TELECHARGEMENT_DOCUMENT;

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, contexte: ContexteService, private bouchon: BouchonService, private donneesService: DonneesService, private environnementService: EnvironnementService, logger: NGXLogger) {
        super(http, logger, contexte);
    }

    /**
     * Recherche les méta-données des documents générés par la soumission (uniquement les documents visibles à l'usager)
     * @returns Les documents
     */
    public chargerListeDocumentsPostSoumission(): Observable<DocumentsPostSoumission[]> {

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            return this.bouchon.chargerListeDocumentsPostSoumission(this.contexte.configurationDemarche?.codeDemarche);
        }

        const numeroTeledossier = this.donneesService.lire('numeroTeledossier') as string;
        const url = this.environnementService.urls.socle.gateway + DocumentService.URI_LISTE_DOCUMENT.replace('{numeroTeledossier}', numeroTeledossier);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut: [] = [];
        return this.get<DocumentsPostSoumission[]>('chargerListeDocumentsPostSoumission', url, options, valeurParDefaut).pipe(
            tap(liste => liste.map(doc => doc.urlTelechargement = this.environnementService.urls.socle.gateway + DocumentService.URI_TELECHARGEMENT_DOCUMENT.replace('{numeroTeledossier}', numeroTeledossier).replace('{codeDocument}', doc.codeDocument as string)))
        );
    }

    /**
     * Lance le téléchargement d'un document
     * @returns rien
     */
    public telechargerDocument(document: DocumentsPostSoumission): Observable<void> {

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            return this.bouchon.telechargerDocument();
        }

        // Demande de clef unique de téléchargement pour un document
        const numeroTeledossier = this.donneesService.lire('numeroTeledossier');
        const urlDemandeClef = this.environnementService.urls.socle.gateway + DocumentService.URI_TELECHARGEMENT_DOCUMENT_CLEF.replace('{numeroTeledossier}', numeroTeledossier as string).replace('{codeDocument}', document.codeDocument as string);
        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = "";
        return this.post<string>('telechargerDocument', urlDemandeClef, undefined, options, valeurParDefaut).pipe(
            map(clefGeneree => {
                // Ouverture d'un onglet (ou d'une fenêtre selon le navigateur et sa configuration)
                // pour télécharger le document
                const urlDocument = urlDemandeClef + '?clef=' + clefGeneree;
                window.open(urlDocument, '_blank');
            })
        );
    }
}
