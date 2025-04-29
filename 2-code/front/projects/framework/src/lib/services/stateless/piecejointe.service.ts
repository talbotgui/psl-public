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
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ContexteService } from '../stateful/contexte.service';
import { DonneesService } from '../stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

@Injectable({ providedIn: 'root' })
export class PieceJointeService extends ClientApiService {

    /** URI d'upload d'une pièce jointe */
    private static readonly URI_UPLOAD_PIECEJOINTE = '/socle/document/piecejointe';

    /** URI de suppression d'une pièce jointe non soumise */
    private static readonly URI_SUPPRESSION_PIECEJOINTE = '/socle/document/piecejointe/{idPieceJointe}';

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, contexte: ContexteService, private bouchon: BouchonService, private donneesService: DonneesService, private environnementService: EnvironnementService, logger: NGXLogger) {
        super(http, logger, contexte);
    }

    /**
     * Envoi d'une pièce jointe au backend.
     * @param codeDocument code du document
     * @param contenu contenu du fichier
     */
    public verserPieceJointeEnUneUniqueRequete(codeDocument: string, contenu: File): Observable<HttpEvent<any>> {

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            return this.bouchon.verserPieceJointeEnUneUniqueRequete();
        }

        // Création du body
        const body = new FormData();
        body.append('codeDemarche', this.contexte.configurationDemarche?.codeDemarche as string);
        body.append('codePieceJointe', codeDocument);
        body.append('fichier', contenu);

        // Appel HTTP
        const urlUpload = this.environnementService.urls.socle.gateway + PieceJointeService.URI_UPLOAD_PIECEJOINTE;
        const options = this.creerOptionsSimplesAvecAuthentification();
        return this.postAvecProgression('verserPieceJointeEnUneUniqueRequete', urlUpload, body, options);
    }


    /**
     * Suppression d'une pièce jointe non soumise
     * @param idPieceJointe ID de la pièce jointe
     */
    public supprimerPieceJointe(idPieceJointe: string): Observable<boolean> {
        const url = this.environnementService.urls.socle.gateway + PieceJointeService.URI_SUPPRESSION_PIECEJOINTE.replace('{idPieceJointe}', idPieceJointe);

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            return of(true);
        }

        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = undefined;
        return this.delete<void>('supprimerPieceJointe', url, options, valeurParDefaut).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }
}
