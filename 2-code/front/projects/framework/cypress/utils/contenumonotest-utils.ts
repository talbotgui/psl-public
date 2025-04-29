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
// Depuis les tests CYPRESS, les imports doivent se faire directement sur le fichier et non sur le 'public-api'
import { BrouillonDemarche } from '../../../framework/src/lib/model/brouillonsoumissiondemarche.model';
import { ContenuAutocompletion, ContenuDeBloc, ContenuRadio, ContenuSaisie, TypeContenuDeBloc, Validation } from '../../../framework/src/lib/model/configurationdemarchecontenubloc.model';
import { FmkContenuSaisieLongueComponent } from '../../src/lib/contenu/simple/contenusaisielongue/fmk.contenusaisielongue';
import { LogUtils } from './log-utils';

/** Classe utilitaire regroupant des actions et validations */
export class ContenuMonoTestUtils {
    private static readonly VALEURS_INVALIDES_POUR_REGEX = ['', ' ', 'azertyuiop', 'AZERT6', 'azertyuiopmlkjhgfdsqwxcvbn', '0123456789', '0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOP0123456789AZERTYUIOPQSDFGHJKLMWXCVBN'];

    /** Définition d'une mauvaise valeur  */
    public static definirMauvaiseValeur(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuSaisie, validation: Validation): string | { nomFichier: string, typeFichier: string } | undefined {

        // Pour les validations de date que la saisie soit libre (type=saisie) ou contrainte (type=date)
        if (Validation.datePassee === validation) {
            return '2050-12-31';
        }
        // Pour les validations de date que la saisie soit libre (type=saisie) ou contrainte (type=date)
        else if (Validation.dateFuture === validation) {
            return '2000-12-31';
        }
        //Si c'est une validation de dateAvant 
        else if (('' + validation).toUpperCase().startsWith(Validation.dateAvant.toUpperCase())) {
            return '2050-12-31';
        }
        //Si c'est une validation de dateApres 
        else if (('' + validation).toUpperCase().startsWith(Validation.dateApres.toUpperCase())) {
            return '1800-01-01';
        }
        // Pour toute autre validation d'une saisie de date (type=date)
        else if (TypeContenuDeBloc.date === contenu.type) {
            return 'aze';
        }
        // cas spécifique de l'upload
        else if (contenu.type === TypeContenuDeBloc.uploadDocument) {
            return { nomFichier: 'grossePieceJointe.png', typeFichier: 'image/png' };
        }
        //Si c'est une validation de regex
        else if (('' + validation).toUpperCase().startsWith(Validation.regex.toUpperCase())) {
            // Extraction de la regex
            const regex = (validation + '').substring((Validation.regex + '').length + 1)
            // Extraction (s'il y a) du nombre max de caractères
            const nbCaracteresMaxSelonRegex = FmkContenuSaisieLongueComponent.extraireLongueurDeSaisieMaximale(contenu);
            // recherche d'une valeur dans la liste ne correspondant pas à la regex 
            // (pour une autre raison que la longueur de chaine car le composant de saisieLongue bloque la saisie quand la limite est atteine)
            const valeurInvalideTrouvee = ContenuMonoTestUtils.VALEURS_INVALIDES_POUR_REGEX.find(val =>
                contenu.type !== TypeContenuDeBloc.saisieLongue && !val.match(regex) ||//
                contenu.type === TypeContenuDeBloc.saisieLongue && nbCaracteresMaxSelonRegex && val.length <= nbCaracteresMaxSelonRegex && !val.match(regex)
            );
            if (valeurInvalideTrouvee) {
                return valeurInvalideTrouvee;
            } else {
                LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Impossible de trouver une valeur invalide pour la validation ' + validation);
                return undefined;
            }
        }
        // Pour les autres validations connues 
        else if (Validation.secu === validation || Validation.email === validation || Validation.autocompletionPrecise === validation || Validation.telephoneFR === validation || Validation.url === validation) {
            return 'azertyuiop';
        }
        // Pour les validations non implémentées, on renvoi 'azertyuiop'
        else {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, '/!\\ attention le type de validation \'' + validation + '\' n\'est pas géré spécifiquement dans le code de test');
            return 'azertyuiop';
        }
    }

    /** Définition d'une bonne valeur en fonction du type de contenu */
    public static definirBonneValeur(contenu: ContenuSaisie, brouillonDemarche: BrouillonDemarche): string | { nomFichier: string, typeFichier: string } {
        // cas nominal : on prend la valeur dans le brouillon
        let bonneValeur = brouillonDemarche.donnees[contenu.clef as string];

        // cas spécifique de l'autocompletion
        if (contenu.type === TypeContenuDeBloc.autocompletion) {
            // Recherche de l'attribut de l'API utilisé comme libellé
            bonneValeur = brouillonDemarche.donnees[contenu.clef + '_' + (contenu as ContenuAutocompletion).attributReponseApiPourLibelle] as string;
            if (bonneValeur) {
                // Si '(' dans la valeur
                if (bonneValeur.includes('(')) {
                    // On retire les parenthèses de on envoi tout sauf le dernier caractère.
                    bonneValeur = bonneValeur.replace(/\(/g, '');
                    bonneValeur = bonneValeur.substring(0, bonneValeur.length - 2);
                }
                // Sinon
                else {
                    // Seuls les premiers caractères sont pris pour tester l'autocompletion
                    if (bonneValeur && bonneValeur.length < 10) {
                        bonneValeur = bonneValeur.substring(0, 3);
                    } else if (bonneValeur && bonneValeur.length >= 10) {
                        bonneValeur = bonneValeur.substring(0, bonneValeur.length - 3);
                    }
                }
            }
        }

        // cas spécifique de l'upload
        if (contenu.type === TypeContenuDeBloc.uploadDocument) {
            const nom = brouillonDemarche.donnees[contenu.clef + '_nom'];
            const type = brouillonDemarche.donnees[contenu.clef + '_type'];
            if (nom && type) {
                return { nomFichier: nom, typeFichier: type };
            } else {
                return { nomFichier: 'petitePieceJointe.png', typeFichier: 'image/png' };
            }
        }

        // si la valeur existe, on la renvoie
        if (bonneValeur) {
            return bonneValeur;
        }
        // Si aucune valeur mais que c'est une case, renvoi de 'true'
        else if (contenu.type === TypeContenuDeBloc.case) {
            return 'true';
        }
        // Si aucune valeur mais que c'est un radio, renvoi de 'true'
        else if (contenu.type === TypeContenuDeBloc.radio) {
            const cr = contenu as ContenuRadio;
            if (cr.valeurs && cr.valeurs[0].valeur) {
                return cr.valeurs[0].valeur + '';
            } else {
                return '';
            }
        }
        // Si aucune valeur mais que c'est une saisie de date avec une validation dateAvant ou dateApres, renvoi de la date du jour
        else if (contenu.type === TypeContenuDeBloc.date && contenu.validationsSimples && contenu.validationsSimples.filter(v => v.toUpperCase().startsWith(Validation.dateAvant.toUpperCase()) || v.toUpperCase().startsWith(Validation.dateApres.toUpperCase())).length > 0) {
            const dateDuJour = new Date();
            let jour = dateDuJour.getDate().toString();
            jour = (+jour < 10) ? '0' + jour : jour;
            let mois: string = (dateDuJour.getMonth() + 1).toString();
            mois = (+mois < 10) ? '0' + mois : mois;
            return dateDuJour.getFullYear() + '-' + mois + '-' + jour;
        }
        // Si aucune valeur, renvoi d'une chaine vide
        else {
            return '';
        }
    }

    /**  Sauvegarde de la donnée saisie dans les données de la page courante (données nécessaires pour le calcul des conditions d'affichage/désactivation. */
    public static enregistrerLaSaisieDansLesDonneesDeLaPage(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, valeur: string | { nomFichier: string, typeFichier: string }, brouillonDemarche: BrouillonDemarche, donneesDeLaPage: { [index: string]: any }): void {
        LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Enregistrement de la valeur pour la clef \'' + contenu.clef + '\':' + JSON.stringify(valeur));

        // cas spécifique de l'autocompletion
        if (contenu && contenu.type === TypeContenuDeBloc.autocompletion) {
            const contenuType = contenu as ContenuAutocompletion;

            // Si la saisie demandée est une valeur correspondant à celles du brouillon (et pas un test d'une des validations)
            const valeurBrouillon = brouillonDemarche.donnees[contenuType.clef + '_' + contenuType.attributReponseApiPourLibelle] as string;
            if (valeurBrouillon && typeof valeur === 'string') {
                // on copie les attributs de l'objet sélectionné
                Object.keys(brouillonDemarche.donnees)
                    .filter(k => k.startsWith(contenuType.clef + '_'))
                    .forEach(k => donneesDeLaPage[k] = brouillonDemarche.donnees[k]);
            }

            // Si ce n'est pas la valeur du brouillon
            else {
                // on écrase les attributs
                Object.keys(brouillonDemarche.donnees)
                    .filter(k => k.startsWith(contenuType.clef + '_'))
                    .forEach(k => delete donneesDeLaPage[k]);
            }
        }

        // cas spécifique de l'upload
        else if (contenu.type === TypeContenuDeBloc.uploadDocument) {
            // rien à faire
        }

        // Sinon, sauvegarde de la saisie dans les données de la page
        else {
            donneesDeLaPage[contenu.clef as string] = valeur;
        }

        LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Post enregistrement :' + JSON.stringify(donneesDeLaPage));
    }

    /**
     * Saisie d'une valeur (undefined ou '' pour vider le champs)
     * 
     * Il n'est pas possible de désélectionner une liste déroulante ou un radio  mais il est possible de sélectionner la première option (la vide en général si elle existe)
     */
    public static saisirValeurDunContenuMonoChamp(iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, valeur: string | undefined | { nomFichier: string, typeFichier: string }, occurenceDeBlocDynamique: number | undefined): void {

        // Cas d'une case
        if (contenu.type === TypeContenuDeBloc.case) {
            // le focus est nécessaire pour faire pendant au blur présent en fin de méthode
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input').focus();

            if (valeur === 'true') {
                // le {force:true} permet de cocher l'input même s'il n'est pas réellement visible
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input').check({ force: true });
            } else {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input').uncheck({ force: true });
            }
        }

        // Cas d'une liste finie
        else if (contenu.type === TypeContenuDeBloc.listeFinie) {
            // ouverture de la liste par un click
            const elementAselectionne = (valeur === undefined || valeur === '' || typeof valeur !== 'string') ? 0 : valeur;
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('select').select(elementAselectionne);
        }

        // Cas d'un radio
        else if (contenu.type === TypeContenuDeBloc.radio) {
            // extraction du libellé des options correspondantes à la valeur demandée (il ne devrait y en avoir qu'une ou aucune)
            const libelles = (contenu as ContenuRadio).valeurs.filter(v => v.valeur === valeur).map(v => v.libelle);
            // Click sur le bon libellé ou sur le premier élément à défaut.
            if (libelles.length === 1) {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input[v=' + valeur + ']').parent().find('.fr-label').click();
            } else {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('.fr-radio-group').eq(0).click();
            }
            // le focus est nécessaire pour faire pendant au blur présent en fin de méthode
            ContenuMonoTestUtils.focusUnContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, true, occurenceDeBlocDynamique);
        }

        // Cas d'un upload de document
        else if (contenu.type === TypeContenuDeBloc.uploadDocument) {
            // La zone de drop doit exister
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('.zoneDepot').should('exist');
            // si la valeur à saisir est UNDEFINED, click sur le bouton de suppression (si présent)
            if (typeof valeur === 'undefined') {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).then((contenu: any) => {
                    const rechercheBouton = contenu.find('.fr-fi-delete-line');
                    if (rechercheBouton.length > 0) {
                        (rechercheBouton[0] as HTMLElement).click();
                    }
                });
            }
            // upload du document si un est présent
            else if (typeof valeur === 'object') {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input[type=file]').then((inputFile: any) => {
                    // Lecture du fichier à uploader
                    cy.fixture(valeur.nomFichier, 'base64').then(Cypress.Blob.base64StringToBlob).then((blob) => {
                        // Ajout du document à l'input
                        const dataTransfer = new DataTransfer();
                        dataTransfer.items.add(new File([blob], valeur.nomFichier, { type: valeur.typeFichier }));
                        (inputFile[0] as HTMLInputElement).files = dataTransfer.files;
                        // déclenchement forcé de l'event
                        inputFile[0].dispatchEvent(new Event('change', { bubbles: true }));
                    });
                });
            }
        }

        // Cas simple
        else {
            // Purge de la saisie car il n'est pas possible de faire un TYPE avec une valeur vide
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input,textarea').focus();
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input,textarea').clear();

            // TYPE de la valeur dans le champ
            if (valeur !== undefined && valeur !== '' && typeof valeur === 'string') {
                cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('input,textarea').type(valeur);
            }
        }

        // On sort du champ pour déclencher les validations
        if (contenu.type !== TypeContenuDeBloc.uploadDocument) {
            ContenuMonoTestUtils.focusUnContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, false, occurenceDeBlocDynamique);
        }

        // Si autocompletion, petite attente pour que l'unique option soit sélectionnée
        if (contenu.type === TypeContenuDeBloc.autocompletion) {
            cy.wait(400);
        }
    }

    /** Prise ou libération du focus sur un champ */
    public static focusUnContenuMonoChamp(iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuDeBloc, focus: boolean, occurenceDeBlocDynamique: number | undefined): void {

        // Définition de la balise à rechercher dans le contenu en fonction du type de contenu
        let balise = 'input,textarea';
        if (contenu.type === TypeContenuDeBloc.listeFinie) {
            balise = 'select';
        } else if (contenu.type === TypeContenuDeBloc.radio) {
            balise = '.fr-radio-group:first input';
        } else if (contenu.type === TypeContenuDeBloc.uploadDocument) {
            balise = '.zoneDepot';
        }

        // FOCUS ou BLUR
        if (focus) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find(balise).focus();
        } else {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find(balise).blur();
        }
    }

    /** Vérification du fonctionnement de toutes les validations définies sur un champ de saisie 'mono-champ' */
    public static validerLesValidationDunContenuMonoChamp(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuSaisie, brouillonDemarche: BrouillonDemarche, occurenceDeBlocDynamique: number | undefined): void {
        // Si aucune validation, on passe
        if (contenu.validationsSimples) {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Validation des validations du contenu \'' + contenu.clef + '\'');

            // Définition de la bonne
            const bonneValeur = ContenuMonoTestUtils.definirBonneValeur(contenu, brouillonDemarche);

            // Pour chaque validation
            contenu.validationsSimples.forEach(v => {
                LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Validation de la validation \'' + v + '\' avec la bonne valeur \'' + bonneValeur + '\'');

                // Saisie de la mauvaise valeur
                if (Validation.autocompletionPrecise === v || Validation.dateFuture === v || Validation.datePassee === v || Validation.email === v || Validation.secu === v || Validation.telephoneFR === v || v.toUpperCase().startsWith(Validation.regex.toUpperCase()) || v.toUpperCase().startsWith(Validation.dateAvant.toUpperCase()) || v.toUpperCase().startsWith(Validation.dateApres.toUpperCase()) || Validation.url === v) {

                    // Définition de la mauvaise valeur en fonction du type du controle
                    const mauvaiseValeur = ContenuMonoTestUtils.definirMauvaiseValeur(iPage, iBloc, iSousBloc, iContenu, contenu, v);

                    // Calcul du nom de la classe associée à la validation
                    const validationRegex = (v.toUpperCase().startsWith(Validation.regex.toUpperCase()));
                    const validationDateAvantApres = (v.toUpperCase().startsWith(Validation.dateAvant.toUpperCase()) || v.toUpperCase().startsWith(Validation.dateApres.toUpperCase()));
                    const nomClasseAssocieAvalidation = validationRegex ? 'regex' : (validationDateAvantApres ? 'dateAvantApres' : v);

                    // Si une mauvaise valeur existe (cas d'une regex non gérée)
                    if (mauvaiseValeur) {
                        // Saisie de la mauvaise valeur
                        ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, mauvaiseValeur, occurenceDeBlocDynamique);

                        // Vérification de la présence du message d'erreur
                        cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.erreur.' + nomClasseAssocieAvalidation).should('have.length', 1);
                    }

                    // Saisie de la bonne valeur
                    ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, bonneValeur, occurenceDeBlocDynamique);

                    // Vérification de l'absence du message d'erreur
                    cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.erreur.' + nomClasseAssocieAvalidation).should('not.exist');

                    // Purge du champ pour réinitialiser (oui, sur un required, cela réaffiche le message d'erreur)
                    ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, undefined, occurenceDeBlocDynamique);

                } else if (Validation.required === v) {

                    // Recherche du champ
                    cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).then((elementHtmlJquery: any) => {

                        // Il n'est pas possible de vider un radio ou une liste
                        // Donc recherche d'un radio ou d'une liste renseignée
                        const radioOuListeDejaRenseigne = (contenu.type === TypeContenuDeBloc.radio && elementHtmlJquery.find('input:checked').length !== 0)
                            || (contenu.type === TypeContenuDeBloc.listeFinie && elementHtmlJquery.find('option:selected').length !== 0);

                        // Si aucune valeur indélébile n'est saisie 
                        if (!radioOuListeDejaRenseigne) {

                            // Si le contenu est réinitialisable
                            if (contenu.type !== TypeContenuDeBloc.listeFinie && contenu.type !== TypeContenuDeBloc.radio) {
                                ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, undefined, occurenceDeBlocDynamique);
                            }

                            // simple enchainement de focus puis blur
                            ContenuMonoTestUtils.focusUnContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, true, occurenceDeBlocDynamique);
                            ContenuMonoTestUtils.focusUnContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, false, occurenceDeBlocDynamique);

                            // Vérification de la présence du message d'erreur
                            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.erreur.' + v).should('have.length', 1);

                            // Saisie de la bonne valeur
                            ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, bonneValeur, occurenceDeBlocDynamique);

                            // Vérification de l'absence du message d'erreur
                            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.erreur.' + v).should('not.exist');

                            // Purge du champ pour réinitialiser (oui, sur un required, cela réaffiche le message d'erreur)
                            ContenuMonoTestUtils.saisirValeurDunContenuMonoChamp(iBloc, iSousBloc, iContenu, contenu, undefined, occurenceDeBlocDynamique);
                        }
                        // Sinon rien à faire
                    });

                } else {
                    throw new Error('Le type de validation \'' + v + '\' n\'est pas géré');
                }
            });
        }

        // Si pas de validation, log quand même
        else {
            LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Aucune validation à valider pour le contenu \'' + contenu.clef + '\'');
        }
    }

    /** Vérification du libellé du champ et de son aide à la saisie */
    public static validerLeLibelleEtAide(iPage: number, iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: ContenuSaisie, modeObligatoireParDefaut: boolean, occurenceDeBlocDynamique: number | undefined): void {
        if (contenu.titre) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.textelibelle').should('exist').should('be.visible');
        }
        if (contenu.aide && contenu.clef) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.fr-hint-text').should('exist').should('be.visible');
        }
        const champObligatoire = contenu.validationsSimples && contenu.validationsSimples.indexOf(Validation.required) !== -1;
        if (!champObligatoire && modeObligatoireParDefaut) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.fr-hint-text').should('be.visible')
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.fr-hint-text').should('contain.text', 'Facultatif');
        }
        if (champObligatoire && !modeObligatoireParDefaut) {
            cy.contenuObtenirContenu(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique).find('span.symbol-required').should('be.visible');
        }
        LogUtils.logContenu(iPage, iBloc, iSousBloc, iContenu, 'Libellé, aide et mention facultative/obligatoire du contenu \'' + contenu.clef + '\' validés');
    }
}
