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
import { map } from 'rxjs/operators';
import { ContenuAdresseFrOuEtr, ContenuAutocompletion, ContenuCase, ContenuContactPersonnel, ContenuDate, ContenuDeBloc, ContenuDocuments, ContenuFinDemarche, ContenuIdentite, ContenuListeFinie, ContenuOption, ContenuParagraphe, ContenuRadio, ContenuRecapitulatif, ContenuSaisie, ContenuSaisieComplexe, ContenuUploadDocument, ContenuUtilisateurConnecte, TypeContenuDeBloc } from '../../model/configurationdemarchecontenubloc.model';
import { Bloc, ConfigurationDemarche, ListeFonctionnalites, Page, SousBloc } from '../../model/configurationdemarchegeneral.model';
import { UtilitaireBlocDynamique } from '../../utilitaires/utilitaire.blocdynamique';
import { UtilitaireModel } from '../../utilitaires/utilitaire.model';
import { ContexteService } from '../stateful/contexte.service';
import { DonneesService } from '../stateful/donnees.service';
import { EnvironnementService } from '../stateful/environnement.service';
import { BouchonService } from './bouchon.service';
import { ClientApiService } from './clientapi-service';

@Injectable({ providedIn: 'root' })
export class ConfigurationService extends ClientApiService {

    /** URI de recherche de la dernière configuration publique d'une démarche*/
    private static readonly URI_RECHERCHE_CONFIGURATION_PUBLIQUE = '/socle/configuration/demarche/{codeDemarche}';

    /** Liste des IDs de bloc dynamiques */
    private static readonly LISTE_IDS_BLOCS_DYNAMIQUES = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

    /** Constructeur pour injection de dépendance. */
    public constructor(http: HttpClient, contexte: ContexteService, private bouchon: BouchonService, private donneesService: DonneesService, private environnementService: EnvironnementService, logger: NGXLogger) {
        super(http, logger, contexte);
    }

    /**
     * Chargement de la dernière configuration d'une démarche.
     * @param codeDemarche Code de la démarche.
     * @returns La configuration.
     */
    public chargerConfigurationDemarche(codeDemarche: string): Observable<ConfigurationDemarche | undefined> {
        let url = this.environnementService.urls.socle.gateway + ConfigurationService.URI_RECHERCHE_CONFIGURATION_PUBLIQUE.replace('{codeDemarche}', codeDemarche);

        // Si le bouchon est actif, on s'arrête là
        if (this.bouchon.verifierSiDocumentEstBouchonnee()) {
            url = this.bouchon.recupererUrlConfigurationBouchonne(codeDemarche);
        }

        const options = this.creerOptionsSimplesAvecAuthentification();
        const valeurParDefaut = undefined;
        return this.get<ConfigurationDemarche | undefined>('chargerConfigurationDemarche', url, options, valeurParDefaut)
            .pipe(map(conf => this.appliquerConfigurationDemarche(conf)));
    }

    /** Instancie le bon type de classe pour un contenu à partir de son attribut type et initialise les attributs non obligatoires de type 'objet' et 'array' pour limiter les exceptions dans le code. */
    public transtyperContenuDeBloc(contenu: any, indexPage: number, indexBloc: number, indexContenu: number): ContenuDeBloc {
        // Création de l'objet du bon type
        // (les données sont validées par l'API qui les fournie)
        if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.paragraphe.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuParagraphe);
            contenuDuBonType.type = TypeContenuDeBloc.paragraphe;
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.saisie.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuSaisie);
            contenuDuBonType.type = TypeContenuDeBloc.saisie;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.saisieLongue.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuSaisie);
            contenuDuBonType.type = TypeContenuDeBloc.saisieLongue;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.case.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuCase);
            contenuDuBonType.type = TypeContenuDeBloc.case;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.listeFinie.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuListeFinie);
            contenuDuBonType.type = TypeContenuDeBloc.listeFinie;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            contenuDuBonType.valeurs.map(v => this.transtyperObjet(v, ContenuOption));
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.radio.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuRadio);
            contenuDuBonType.type = TypeContenuDeBloc.radio;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            contenuDuBonType.valeurs.map(v => this.transtyperObjet(v, ContenuOption));
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.autocompletion.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuAutocompletion);
            contenuDuBonType.type = TypeContenuDeBloc.autocompletion;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.date.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuDate);
            contenuDuBonType.type = TypeContenuDeBloc.date;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.identite.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuIdentite);
            contenuDuBonType.type = TypeContenuDeBloc.identite;
            this.initialiserChampsDepuisChampsVisibles(contenuDuBonType);
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.adresseFrOuEtr.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuAdresseFrOuEtr);
            contenuDuBonType.type = TypeContenuDeBloc.adresseFrOuEtr;
            this.initialiserChampsDepuisChampsVisibles(contenuDuBonType);
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.recapitulatif.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuRecapitulatif);
            contenuDuBonType.type = TypeContenuDeBloc.recapitulatif;
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.finDemarche.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuFinDemarche);
            contenuDuBonType.type = TypeContenuDeBloc.finDemarche;
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.documents.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuDocuments);
            contenuDuBonType.type = TypeContenuDeBloc.documents;
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.uploadDocument.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuUploadDocument);
            contenuDuBonType.type = TypeContenuDeBloc.uploadDocument;
            contenuDuBonType.validationsSimples = (contenuDuBonType.validationsSimples) ? contenuDuBonType.validationsSimples : [];
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.utilisateurConnecte.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuUtilisateurConnecte);
            contenuDuBonType.type = TypeContenuDeBloc.utilisateurConnecte;
            return contenuDuBonType;

        } else if (!!contenu.type && contenu.type.toString() === TypeContenuDeBloc.contactPersonnel.toString()) {
            const contenuDuBonType = this.transtyperObjet(contenu, ContenuContactPersonnel);
            contenuDuBonType.type = TypeContenuDeBloc.contactPersonnel;
            this.initialiserChampsDepuisChampsVisibles(contenuDuBonType);
            return contenuDuBonType;

        } else {
            // Erreur de type de bloc qui va faire planter le code dans qq lignes.
            // Problème de version du framework vis-à-vis de la configuration ?
            this.logger.error('Le type de contenu \'' + contenu.type + '\' n\'existe pas (page ' + indexPage, ', bloc ' + indexBloc + ', contenu ' + indexContenu + ')');
            return {} as ContenuDeBloc;
        }
    }

    /**Sauvegarde et activation de la configuration passée en paramètre.*/
    public appliquerConfigurationDemarche(config: ConfigurationDemarche | undefined): ConfigurationDemarche | undefined {

        // retypage des objets venant de l'API dont la classe hérite de ContenuDeBloc
        if (config) {
            config.fonctionnalites = this.transtyperObjet(config.fonctionnalites, ListeFonctionnalites);
            config.pages = (config.pages || []).map((page) => this.transtyperObjet(page, Page));
            config.pages.forEach((page, indexPage) => {
                page.blocs = (page.blocs || []).map((bloc) => this.transtyperObjet(bloc, Bloc));
                (page.blocs || []).forEach((bloc, indexBloc) => {
                    bloc.contenus = (bloc.contenus || []).map((contenu, indexContenu) => this.transtyperContenuDeBloc(contenu, indexPage, indexBloc, indexContenu));
                    bloc.aideTitre = bloc.aideTitre || '';
                    bloc.sousBlocs = (bloc.sousBlocs || []).map(sousBloc => this.transtyperObjet(sousBloc, SousBloc));
                    (bloc.sousBlocs || []).forEach(sousBloc => {
                        sousBloc.contenus = (sousBloc.contenus || []).map((contenu, indexContenu) => this.transtyperContenuDeBloc(contenu, indexPage, indexBloc, indexContenu));
                        sousBloc.aideTitre = sousBloc.aideTitre || '';
                    });
                });
            });

            // Précalculer les conditions de visibilité des pages
            (config.pages || []).forEach(p => p.visibilite = this.donneesService.calculerCondition(p.conditionVisibilite, {}, true));

            (config.pages || []).forEach(p =>
                // Précalculs au niveau des contenus
                (p.blocs || []).forEach(b => {
                    (b.contenus || []).forEach(c => {
                        // Ajout de la condition de visibilité du bloc à la condition de visibilité de chacun de ses contenus
                        c.conditionVisibilite = this.ajouterConditionVisibiliteAuneAutreCondition(c.conditionVisibilite, b.conditionVisibilite);

                        // Pas de précalcul pour les contenus de bloc dynamique
                        if (!b.dynamique) {
                            // Calcul des conditions de désactivation et de visibilité
                            c.desactivation = this.donneesService.calculerCondition(c.conditionDesactivation, {}, false);
                            c.visibilite = this.donneesService.calculerCondition(c.conditionVisibilite, {}, true);
                            // Initialisation des données du contenu autour de la gestion des titres et des aides (pouvant contenir des variables)
                            UtilitaireModel.initialiserLeOuLesTitresEtAides(c);
                        }
                    });
                    // Ajout de la condition de visibilité du bloc à la condition de visibilité de chacun de ses contenus
                    (b.sousBlocs || []).forEach(sb => {
                        sb.contenus.forEach(c => {
                            // Ajout de la condition de visibilité du bloc à la condition de visibilité de chacun de ses contenus
                            c.conditionVisibilite = this.ajouterConditionVisibiliteAuneAutreCondition(c.conditionVisibilite, b.conditionVisibilite);
                            c.conditionVisibilite = this.ajouterConditionVisibiliteAuneAutreCondition(c.conditionVisibilite, sb.conditionVisibilite);
                            // Calcul des conditions de désactivation et de visibilité
                            c.desactivation = this.donneesService.calculerCondition(c.conditionDesactivation, {}, false);
                            c.visibilite = this.donneesService.calculerCondition(c.conditionVisibilite, {}, true);
                            // Initialisation des données du contenu autour de la gestion des titres et des aides (pouvant contenir des variables)
                            UtilitaireModel.initialiserLeOuLesTitresEtAides(c);
                        });
                    });
                })
            );
            // log
            this.logger.info('Chargement terminé de la configuration :', config);

            // Gestion des fonctionnalités
            if (config.fonctionnalites) {
                // Sauvegarde l'état de la fonctionnalité ListeFonctionnalites.modeObligatoireParDefaut dans la variable statique FmkContenuAbstraitComponent.FLAG_FONCTIONNALITE_MODE_OBLIGATOIRE_PAR_DEFAUT
                ContenuDeBloc.FLAG_FONCTIONNALITE_MODE_OBLIGATOIRE_PAR_DEFAUT = config.fonctionnalites.modeObligatoireParDefaut;
                if (config.fonctionnalites.deuil) {
                    (document.childNodes[document.childNodes.length - 1] as HTMLElement).setAttribute('data-fr-mourning', '')
                }
            }

            // Passage de la configuration au contexte
            this.contexte.stockerConfigurationDemarche(config);
        } else {
            // log
            this.logger.error('Chargement terminé de la configuration sans aucune configuration');
        }

        return config;
    }

    /**
     * Ajout d'une condition à une autre.
     * @param conditionExistante L'existante.
     * @param conditionAajouter Le complément.
     * @returns Une condition avec l'ensemble.
     */
    private ajouterConditionVisibiliteAuneAutreCondition(conditionExistante: string | undefined, conditionAajouter: string | undefined): string | undefined {
        if (conditionAajouter) {
            if (conditionExistante) {
                return '(' + conditionExistante + ') && (' + conditionAajouter + ')';
            } else {
                return conditionAajouter;
            }
        } else {
            return conditionExistante;
        }
    }

    /** Méthode initialisant les champs complexes VALIDATIONS et AIDES pour chaque élément de CHAMPSVISIBLES */
    private initialiserChampsDepuisChampsVisibles(contenuDuBonType: ContenuSaisieComplexe): void {
        contenuDuBonType.champsVisibles = (contenuDuBonType.champsVisibles) ? contenuDuBonType.champsVisibles : [];
        contenuDuBonType.validationsComplexes = (contenuDuBonType.validationsComplexes) ? contenuDuBonType.validationsComplexes : {};
        contenuDuBonType.aides = (contenuDuBonType.aides) ? contenuDuBonType.aides : {};
        contenuDuBonType.champsVisibles.forEach(c => {
            contenuDuBonType.validationsComplexes[c] = (contenuDuBonType.validationsComplexes[c]) ? contenuDuBonType.validationsComplexes[c] : [];
            contenuDuBonType.aides[c] = (contenuDuBonType.aides[c]) ? contenuDuBonType.aides[c] : '';
        })
    }

    /**
     * Méthode de transtypage d'objet.
     * @param source Objet à dupliquer.
     * @param type Nouveau type à utiliser.
     */
    private transtyperObjet<V>(source: any, type: (new () => V)): V {
        // Instanciation
        const nouvelObjet = new type();
        // Copie des attributs de la source
        if (source) {
            Object.keys(source).forEach(att => (nouvelObjet as any)[att] = source[att]);
        }
        // Renvoi en sortie
        return nouvelObjet;
    }

    /** Duplication des contenus d'un bloc dynamique pour une occurence précise. */
    public clonerContenusPourUneOccurenceDeBlocDynamique(bloc: Bloc, occurence: number): ContenuDeBloc[] {
        return bloc.contenus.map(contenu => {
            const nouveauContenu = this.transtyperContenuDeBloc(contenu, -1, -1, -1);
            // Copie de la clef avec le numéro d'occurence dedans
            if (nouveauContenu.clef) {
                nouveauContenu.clef = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(contenu.clef, occurence);
            }
            // avec gestion de l'index de l'occurence 
            // revue des conditions pour lier les champs d'une même occurence
            nouveauContenu.conditionVisibilite = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.conditionVisibilite, occurence);
            nouveauContenu.conditionDesactivation = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.conditionDesactivation, occurence);
            nouveauContenu.titre = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.titre, occurence);
            nouveauContenu.aide = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.aide, occurence);
            if (nouveauContenu.type === TypeContenuDeBloc.paragraphe) {
                const nouveauContenuType = nouveauContenu as ContenuParagraphe;
                nouveauContenuType.texte = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenuType.texte, occurence);
            }

            // Recalcul des clefs déclenchant le recalcul des contenus et conditions
            UtilitaireModel.initialiserLeOuLesTitresEtAides(nouveauContenu);
            return nouveauContenu;
        });
    }
}
