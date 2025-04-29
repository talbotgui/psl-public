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
import { ES6Parser, ES6StaticEval } from 'espression';
import { NGXLogger } from 'ngx-logger';
import { ConfigurationDemarche, Page } from '../../model/configurationdemarchegeneral.model';
import { DateService } from '../../services/stateless/date.service';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';

@Injectable({ providedIn: 'root' })
export class DonneesService {

    /** Regex de recherche des variables dans un libelle (titre, aide, texte de paragraphe, ...) */
    // les caractères a-zA-Z0-9_ sont des caractères autorisés pour toute variable
    // les caractères @()' viennent des appels de fonction évaluables comme sommeChampBlocDynamique('prejudicePaiements@@Montant')
    public static readonly REGEX_RECHERCHE_DONNEE = /{{([a-zA-Z0-9_@()']*)}}/ig;

    /** Regex de recherche des variables dans une expression JS (condition de visibilité ou de désactivation) */
    // les caractères a-zA-Z0-9_ sont des caractères autorisés pour toute variable
    public static readonly REGEX_RECHERCHE_DONNEE_EXPRESSION = /([a-zA-Z0-9_]+)/ig;

    /** Liste des fonctions disponibles dans les expressions */
    public static readonly LISTE_FONCTIONS_DISPONIBLES = ['sommeChampBlocDynamique', 'nombreOccurencesBlocDynamique']

    /** Toutes les données */
    private donnees: { [index: string]: any } = {};

    /** Analyseur d'expression conditionnelle */
    private parser = new ES6Parser();

    /** Evaluateur d'expression conditionnelle */
    private staticEval = new ES6StaticEval();

    /** Constructeur pour injection des dépendances */
    public constructor(private logger: NGXLogger, private dateService: DateService) { }

    // Suppression d'une donnée
    public supprimerDonnee(clef: string): void {
        delete this.donnees[clef];
    }

    /** Pour lire les données mise dans le contexte */
    public lire(clef: string): string | undefined {
        return this.donnees[clef] as string;
    }

    /** Pour lire les données mise dans le contexte */
    public lireUnObjet(clef: string): { [index: string]: any } | undefined {
        const resultat: { [index: string]: any } = {};
        Object.keys(this.donnees).forEach(k => {
            if (k.startsWith(clef + '_')) {
                resultat[k] = this.donnees[k];
            }
        });
        if (Object.keys(resultat).length === 0) {
            return undefined;
        } else {
            return resultat;
        }
    }

    /**
     * Sauvegarde d'une valeur. 
     * La validation de la donnée doit être faite avant l'appel à cette méthode.
    */
    public sauvegarder(clef: string, valeur: string | Date | undefined): void {
        this.donnees[clef] = valeur;
    }

    /**
     * Sauvegarde non pas d'une valeur mais d'un objet (dans le cas des autocompletion par exemple). 
     * La validation de la donnée doit être faite avant l'appel à cette méthode.
     */
    public sauvegarderUnObjet(clef: string, objet: { [index: string]: any }): void {
        // Suppression des données précédente avec cette clef au cas où le nouvel objet ait moins d'attribut que le précédent
        this.supprimer(clef);
        // Sauvegarde 
        if (typeof objet !== 'undefined' && objet !== null) {
            Object.keys(objet).forEach(k => {
                if (k.indexOf(clef) !== 0) {
                    this.logger.error('L\'attribut d\'objet "' + k + '" ne contient pas la clef du contenu "' + clef + "'");
                }
                if (typeof objet[k] !== 'undefined' && typeof objet[k] !== 'object' && objet[k] !== null) {
                    this.donnees[k] = objet[k];
                }
            });
        }
    }

    /** Sauvegarde des données du formulaire dans le contexte (qq soit l'état du formulaire) */
    public sauvegarderDonneesDuFormulaire(configurationDemarche: ConfigurationDemarche, page: Page, formulaire: UntypedFormGroup): void {
        // Pour chaque controle du formulaire
        Object.keys(formulaire.controls).forEach(clef => {
            const controle = formulaire.controls[clef];
            // si le controle est un sous-formulaire (cas des composants complexe)
            if (controle instanceof UntypedFormGroup) {
                this.sauvegarderDonneesDuFormulaire(configurationDemarche, page, controle);
            }
            // si le controle est actif (les masqués sont désactivés) et a été modifié, on en sauvegarde le contenu
            else if (!controle.disabled && controle.dirty) {

                // Attention, ce IF/ELSE doit être cohérent avec celui présent dans le calcul des conditions (DonneesService.calculerCondition)
                if (typeof controle.value === 'string') {
                    this.sauvegarder(clef, controle.value);
                } else {
                    this.sauvegarderUnObjet(clef, controle.value);
                }
            }
            // si le contrôle est inactif et que cette donnée n'est modifiable nulle part ailleurs, on supprime la donnée
            const donneePresenteeUniquementDansCeForm = UtilitaireModel.verifierDonneeEstPresenteeUneSeuleFoisDansTouteLaDemarche(configurationDemarche, clef);
            if (controle.disabled && donneePresenteeUniquementDansCeForm) {
                this.supprimer(clef);
            }
        });

        // Log
        this.logger.trace('Données en quittant la page \'' + page.titreAriane + '\' : ', this.donnees);
    }

    /** Calcul d'une condition d'affichage à partir des données stockées dans le service plus les données fournies en plus */
    public calculerCondition(conditionVisibilite: string | undefined, toutesLesDonnees: { [index: string]: string }, valeurParDefaut: boolean): boolean {

        if (conditionVisibilite === undefined || conditionVisibilite === '' || conditionVisibilite === null) {
            return valeurParDefaut;
        } else if (conditionVisibilite === 'true') {
            return true;
        } else if (conditionVisibilite === 'false') {
            return false;
        } else {
            try {
                // Parse de l'expression
                const expression = this.parser.parse(conditionVisibilite);

                // Calcul
                const resultat = this.staticEval.evaluate(expression, toutesLesDonnees);
                this.logger.trace('Calcul de la condition \'' + conditionVisibilite + '\'', { toutesLesDonnees, resultat });
                return resultat;
            } catch (erreur) {
                this.logger.error('Erreur de calcul de la condition \'' + conditionVisibilite + '\'', { toutesLesDonnees, erreur });
                return valeurParDefaut;
            }
        }
    }

    /** Fusion des données de la page prioritairement aux données globales */
    public lireToutEtIntegrerLesDonneesFournies(donneesDeLaPage: { [index: string]: any }): { [index: string]: string } {
        const toutesLesDonnees = { ...this.donnees };
        Object.keys(donneesDeLaPage).forEach(clef => {
            if (donneesDeLaPage[clef] === undefined || donneesDeLaPage[clef] === null) {
                // rien à faire
            }
            else if (typeof donneesDeLaPage[clef] === 'string' || donneesDeLaPage[clef] instanceof Date) {
                toutesLesDonnees[clef] = donneesDeLaPage[clef];
            } else {
                Object.keys(donneesDeLaPage[clef]).forEach(k => toutesLesDonnees[k] = donneesDeLaPage[clef][k]);
            }
        });

        return toutesLesDonnees;
    }

    /**
     * Remplace, dans le libellé fourni, les {{variable}} par la valeur présente dans les données globales (dans un premier temps) ou dans les données de la page (prioritairement).
     */
    public integrerDesVariablesDansUnLibelle(libelle: string | undefined, toutesLesDonnees: { [index: string]: string }): string | undefined {
        // Si pas de libellé, pas de traitement
        if (!libelle || libelle.length === 0) {
            return undefined;
        }

        // Ajout des fonctions dans les données pour l'évaluation
        const contexteEvaluation: { [index: string]: string | ((s: string) => number) } = Object.assign({}, toutesLesDonnees);
        contexteEvaluation[DonneesService.LISTE_FONCTIONS_DISPONIBLES[0]] = (clef: string): number => {
            // log
            this.logger.info('calcul de sommeChampBlocDynamique("' + clef + '")');
            // au cas où
            if (!clef) {
                return 0;
            }
            // Pré-découpage pour rechercher les clefs
            const clefDecoupee = clef.split('@@');
            if (clefDecoupee.length != 2) {
                return 0;
            }
            // Recherche des clefs
            return Object.keys(toutesLesDonnees).filter(k => k.startsWith(clefDecoupee[0]) && k.endsWith(clefDecoupee[1]))
                // transformation d'une liste de clef en liste de valeur numérique (parseFloat traite aussi les entiers)
                .map(k => {
                    const valeur = toutesLesDonnees[k];
                    if (valeur) {
                        const valeurF = parseFloat(valeur.replace(',', '.'));
                        if (!isNaN(valeurF)) {
                            return valeurF;
                        }
                    }
                    return 0;
                })
                // Réalisation de la somme (avec 0 comme valeur initiale pour traiter le cas des tableaux vides)
                .reduce((cumule: number, courant: number) => cumule + courant, 0);
        };
        contexteEvaluation[DonneesService.LISTE_FONCTIONS_DISPONIBLES[1]] = (clef: string): number => {
            // log
            this.logger.info('reche des occurences de ("' + clef + '")');
            // au cas où
            if (!clef) {
                return 0;
            }
            // Pré-découpage pour rechercher les clefs
            const clefDecoupee = clef.split('@@');
            if (clefDecoupee.length != 2) {
                return 0;
            }
            // Recherche des clefs
            return Object.keys(toutesLesDonnees).filter(k => k.startsWith(clefDecoupee[0]) && k.endsWith(clefDecoupee[1])).length;
        };

        // Traitement du libellé
        let txt = libelle;
        const donneesAremplacer = txt.match(DonneesService.REGEX_RECHERCHE_DONNEE);
        if (donneesAremplacer) {
            donneesAremplacer.forEach((d) => {
                const expression = d.substring(2, d.length - 2);
                let resultat;
                if (expression) {
                    try {
                        resultat = this.staticEval.evaluate(this.parser.parse(expression), contexteEvaluation);
                    } catch (erreur) {
                        this.logger.error('Erreur de calcul de l\'expression \'' + expression + '\'', { contexteEvaluation, erreur });
                    }
                }
                txt = txt.replace(d, resultat ? resultat : '');
            });
        }

        return txt;
    }

    /**
     * Méthode clonant l'objet contenant toutes les données
     * et transformant tout en string.
    */
    public lireTout(): { [index: string]: any } {
        // Seul et unique usage autorisé de Object.assign
        const toutesLesDonnees = Object.assign({}, this.donnees);
        Object.keys(toutesLesDonnees).forEach(clef => {
            if (toutesLesDonnees[clef] instanceof Date) {
                toutesLesDonnees[clef] = this.dateService.transformerDateEnString(toutesLesDonnees[clef]);
            }
        });
        return toutesLesDonnees;
    }

    /** Suppression d'une valeur. */
    private supprimer(clef: string): void {
        delete this.donnees[clef];
        Object.keys(this.donnees).forEach(k => {
            if (k.startsWith(clef + '_')) {
                delete this.donnees[k];
            }
        });
    }

    /** Sauvegarde, dans les données de l'id du captcha */
    public sauvegarderIdentifiantCaptcha(idCaptcha: string) {
        this.sauvegarder('captcha_id', idCaptcha);
    }

    /** Sauvegarde, dans les données de la valeur du captcha */
    public sauvegarderSaisieCaptcha(valeurCaptcha: string) {
        this.sauvegarder('captcha_valeur', valeurCaptcha);
    }
}
