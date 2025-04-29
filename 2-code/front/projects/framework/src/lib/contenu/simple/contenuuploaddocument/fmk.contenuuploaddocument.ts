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

import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, ViewEncapsulation } from '@angular/core';
import { MatProgressBar } from '@angular/material/progress-bar';
import { NGXLogger } from 'ngx-logger';
import { i18n } from '../../../conf/i18n';
import { ContenuDeBloc } from '../../../model/configurationdemarchecontenubloc.model';
import { i18nPipeDirective } from '../../../morceaudepage/directives/i18nPipe';
import { LibelleDirective } from '../../../morceaudepage/directives/libelle';
import { ContexteService } from '../../../services/stateful/contexte.service';
import { DonneesService } from '../../../services/stateful/donnees.service';
import { PieceJointeService } from '../../../services/stateless/piecejointe.service';
import { FmkContenuMonoSaisieAbstraitComponent } from '../../abstrait/fmk.contenumonosaisieabstrait';
import { FmkMessagesValidationComponent } from '../../messagesvalidation/fmk.messagesvalidation';

@Component({
    selector: 'div[data-fmk-contenuuploaddocument]', templateUrl: './fmk.contenuuploaddocument.html', encapsulation: ViewEncapsulation.None, styleUrls: ['./fmk.contenuuploaddocument.scss'],
    standalone: true,
    imports: [MatProgressBar, FmkMessagesValidationComponent, LibelleDirective, i18nPipeDirective]
})
export class FmkContenuUploadDocumentComponent extends FmkContenuMonoSaisieAbstraitComponent {

    private static readonly LIBELLE_TYPE_DOCUMENT: { [index: string]: any } = {
        'application/vnd.oasis.opendocument.text': 'document OpenDocument',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'tableur OpenDocument',
        'image/gif': 'image GIF',
        'image/png': 'image PNG',
        'image/jpeg': 'image JPG',
        'image/jpg': 'image JPG',
        'application/jpg': 'image JPG',
        'application/x-jpg': 'image JPG',
        'image/pjpeg': 'image JPG',
        'application/pdf': 'document PDF',
        'application/vnd.pdf': 'document PDF',
        'application/x-pdf': 'document PDF',
        'text/pdf': 'document PDF',
        'text/x-pdf': 'document PDF',
        'application/acrobat': 'document PDF'
    };


    /** La taille maximale du document (en Mo) */
    public tailleMaximaleAutorisee: number | undefined;

    /** Les types MIME autorisés */
    public typesDeContenuAutorises: string[] = [];

    /** Nom du fichier (sans le chemin) */
    public nomFichier: string | undefined;

    /** Avancement de l'upload : pourcentage du volume du document complètement transmis au serveur. */
    public progressionValidee: number | undefined;

    /** Avancement de l'upload : pourcentage du volume du document en cours de transmission. */
    public progressionLancee = 0;

    /** Message d'erreur à afficher */
    public messageErreur: string | undefined;

    /** Suppression en cours */
    public suppressionEnCours = false;

    /** Constructeur avec injection des dépendances */
    public constructor(private piecejointeService: PieceJointeService, private contexteService: ContexteService, private logger: NGXLogger, private donneesService: DonneesService) {
        super();
    }

    /** Implémentation de l'initialisation */
    protected override initialiserComposant(contenu: ContenuDeBloc): void {
        // Dès réception de la configuration, 
        const sub = this.contexteService.obtenirUnObservableDeChargementDeLaConfiguration().subscribe(conf => {

            // on récupère les informations de la pièce jointe associée au contenu
            const pj = conf?.piecesJointesAssociees.find(pj => pj.codePieceJointe === contenu.clef);
            if (pj) {
                this.tailleMaximaleAutorisee = pj.tailleMaximaleAutorisee;
                const listeAvecDoublons = pj.typesDeContenuAutorises.map(t => FmkContenuUploadDocumentComponent.LIBELLE_TYPE_DOCUMENT[t] ? FmkContenuUploadDocumentComponent.LIBELLE_TYPE_DOCUMENT[t] : t);
                this.typesDeContenuAutorises = [...new Set(listeAvecDoublons)];
            } else {
                this.logger.error('Erreur de configuration : le contenu de type \'uploadDocument\' avec la clef \'' + contenu.clef + '\' ne correspond à aucune des piecesJointesAssociees');
            }

            // on vérifie si une valeur est disponible
            if (this.contenu && this.contenu.clef) {
                const donneesPresentes = this.donneesService.lireUnObjet(this.contenu.clef);
                if (donneesPresentes) {
                    this.logger.info('pièce jointe "' + this.contenu.clef + '" déjà présente', donneesPresentes);
                    this.progressionValidee = 100;
                    this.controle?.setValue(donneesPresentes);
                    this.nomFichier = donneesPresentes[this.contenu.clef + '_nom'];
                }
            }
        });
        super.declarerSouscription(sub);
    }

    /** Affiche le message d'erreur si aucune pièce jointe n'est fournie mais qu'elle est obligatoire */
    public validerPresenceFichierSiObligatoire(): void {
        if (this.validationObligatoirePresente && !this.nomFichier) {
            this.messageErreur = i18n.contenu.upload.erreurDocumentObligatoire.get(this.contexteService.langue);
        }
    }

    /** A la sélection d'un fichier */
    public selectionnerFichier(e: Event): void {
        // Cast du champ
        const champ = (e.target as HTMLInputElement);

        // si un document est bien sélectionné
        if (champ.files && champ.files.length > 0) {
            this.verserFichier(champ.files[0]);
        }

        // validation de saisie
        this.validerPresenceFichierSiObligatoire();
    }

    /** Suppression du fichier */
    public supprimerFichier(event: Event, inputFile: HTMLInputElement): void {
        // Pour éviter que le clic ne se propage à la DIV parente et ne déclenche l'ouverture de la sélection de fichier
        event.preventDefault();
        event.stopPropagation();

        // Si on a une valeur
        if (this.controle && this.controle?.value && this.contenu && this.contenu.clef) {
            const donneesPJ = this.controle.value as { [index: string]: any };

            // flag d'affichage
            this.suppressionEnCours = true;

            // Suppression de la PJ
            const sub = this.piecejointeService.supprimerPieceJointe(donneesPJ[this.contenu.clef + '_id']).subscribe((ok) => {
                // si tout va bien, reset des données
                if (ok && this.controle) {
                    this.controle.reset();
                    this.nomFichier = undefined;
                    this.progressionValidee = undefined;
                    this.progressionLancee = 0;
                    this.messageErreur = undefined;
                    inputFile.value = '';
                } else {
                    this.messageErreur = i18n.contenu.upload.erreurSuppressionDocument.get(this.contexteService.langue);
                }
                this.suppressionEnCours = false;
            })
            this.declarerSouscription(sub);
        }
    }

    /** Upload du fichier */
    private verserFichier(fichier: File): void {
        // TODO: validation du type de document
        // TODO: validation de la taille maximale

        // Gestion des erreurs (reset)
        this.messageErreur = undefined;

        // pour les fichiers de moins de 10Mo, envoi du document au socle en une seule requête
        if (this.contenu && this.contenu.clef) {
            if (fichier.size <= 1024 * 1024) {
                this.verserFichierEnUneUniqueRequete(this.contenu.clef, fichier);
            }
            // Sinon, chunk
            else {
                console.error('CHUNK non implémenté');
                this.messageErreur = i18n.contenu.upload.erreurSuppressionDocument.get(this.contexteService.langue);
            }
        }
    }

    /** Envoi du fichier en une seule requête HTTP */
    private verserFichierEnUneUniqueRequete(clef: string, fichier: File): void {
        this.detruireLesSouscriptions();
        const sub = this.piecejointeService.verserPieceJointeEnUneUniqueRequete(clef, fichier)
            .subscribe((event: HttpEvent<any>) => {
                this.logger.log('versement du fichier : ', event);

                // A l'envoi
                if (event.type === HttpEventType.Sent) {
                    const split = fichier.name.split('[/\\]');
                    this.nomFichier = split[split.length - 1];
                    this.progressionLancee = 1;
                    this.logger.info('Pièce jointe "' + clef + '" : requête d\'envoi du fichier envoyée');
                }

                // A chaque étape de progression
                else if (event.type === HttpEventType.UploadProgress) {
                    if (event.loaded && event.total) {
                        this.progressionLancee = Math.round(event.loaded / event.total * 100);
                        this.logger.info('Pièce jointe "' + clef + '" : progression à ' + this.progressionLancee);
                    } else {
                        this.logger.error('Pièce jointe "' + clef + '" : pas de progression (' + event.loaded + '/' + event.total + ')');
                    }
                }

                // A la réception des entêtes de réponse
                else if (event.type === HttpEventType.ResponseHeader) {
                    this.logger.info('Pièce jointe "' + clef + '" : entête de la réponse reçus', event);
                    this.progressionLancee = 100;
                    if (!event.ok) {
                        if (event.status === 400) {
                            this.messageErreur = i18n.contenu.upload.erreurDocumentInvalide.get(this.contexteService.langue);
                        } else {
                            this.messageErreur = i18n.contenu.upload.erreurVersementDocument.get(this.contexteService.langue);
                        }
                    }
                }

                // Une fois la requête terminée
                else if (event.type === HttpEventType.Response) {
                    this.logger.info('Pièce jointe "' + clef + '" : réponse obtenue', event);
                    // En cas d'erreur
                    if (!event.ok) {
                        this.messageErreur = i18n.contenu.upload.erreurVersementDocument.get(this.contexteService.langue);
                    }
                    // Si tout est bon
                    else {
                        // progression à 100%
                        this.progressionValidee = 100;
                        // Sauvegarde de l'ID de la PJ dans les données de la démarche (dirty pour que la donnée soit prise en compte)
                        const objetAsauvegarder: any = {};
                        objetAsauvegarder[this.contenu?.clef + '_id'] = event.body;
                        objetAsauvegarder[this.contenu?.clef + '_nom'] = this.nomFichier;
                        objetAsauvegarder[this.contenu?.clef + '_longueur'] = fichier.size;
                        objetAsauvegarder[this.contenu?.clef + '_type'] = fichier.type;
                        this.controle?.setValue(objetAsauvegarder);
                        this.controle?.markAsDirty();
                    }
                }
            });
        this.declarerSouscription(sub);
    }

    /** Au clic sur la zone, on ouvre le popup si aucun fichier n'est encore versé */
    public ouvrirPopupFichier(fichier: HTMLInputElement) {
        if (!this.progressionValidee) {
            fichier.click();
        }
    }

    /** Gestion du drag&drop */
    public dragNdropEnCours = false;
    public dragOver(event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.dragNdropEnCours = true;
    }
    /** Gestion du drag&drop */
    public dragLeave(event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.dragNdropEnCours = false;
    }
    /** Gestion du drag&drop */
    public drop(event: Event): void {
        event.preventDefault();
        event.stopPropagation();
        this.dragNdropEnCours = false;

        if ((event as any).dataTransfer && (event as any).dataTransfer.files && (event as any).dataTransfer.files.length && (event as any).dataTransfer.files.length > 0) {
            this.verserFichier((event as any).dataTransfer.files[0]);
        }
    }
}
