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
import { BrouillonDemarche } from '../../../framework/src/lib/model/brouillonsoumissiondemarche.model';
import { Bloc, ConfigurationDemarche } from '../../../framework/src/lib/model/configurationdemarchegeneral.model';
import { ContenuDeBloc, ContenuParagraphe, TypeContenuDeBloc } from '../../src/lib/model/configurationdemarchecontenubloc.model';
import { UtilitaireBlocDynamique } from '../../src/lib/utilitaires/utilitaire.blocdynamique';
import { BlocTestUtils } from './bloctest-utils';
import { ConsoleUtils } from './console-utils';
import { ContenuTestUtils } from './contenutest-utils';
import { DonneesUtils } from './donnees-utils';
import { FilArianeTestUtils } from './filarianetest-utils';
import { LogUtils } from './log-utils';
import { NavigationTestUtils } from './navigationtest-utils';

/** Classe utilitaire regroupant la globalité de l'exécution de tous les scénarios de test possibles */
export class GlobalTestUtils {

    /** ID unique de la session */
    private idSession: string = this.calculerTimestampCourant();

    /** Variable globale contenant la configuration de la démarche */
    public confDemarche: ConfigurationDemarche | undefined;

    /** Liste des brouillons disponibles en fonction de l'URL de démarrage */
    public brouillonsDisponibles: BrouillonDemarche[] = [];

    /** Variable globale contenant les données de brouillon de la démarche */
    public brouillonDemarche: BrouillonDemarche | undefined;

    /** Variable globale contenant le nombre de pages invisibles dans le fil d'Ariane (pour calculer l'index visible de chaque page) */
    public nbPagesInvisiblesDansAriane = 0;

    /** Liste des scénarios de test à réaliser*/
    public listeUrlDesScenariosDeTest: string[] = [];

    /** Index du scénario de test en cours */
    public indexScenarioDeTestEnCours = -1;

    /** Flag indiquant que le scénario de test précédent est terminé pour passer au scénario suivant */
    private scenarioPrecedentTermine = true;

    /** Index de la partie correspondant au début du scénario en cours */
    public indexPartieCorrespondantAuDebutDuScenarioEnCours = 0;

    /** Données disponibles au début du test d'une page */
    private donneesDeBase: { [index: string]: any } = {};

    /**
     * A exécuter dans la méthode before d'un test Cypress pour charger la configuration de la démarche et son brouillon.
     * 
     * @param codeDemarche Code de la démarche
     * @param urlConfigurationDemarche URL relative de la configuration de la démarche présente dans les ressources statiques de l'application.
     * @param urlsBrouillonDemarche URLs relatives des brouillons de la démarche présents dans les ressources statiques de l'application.
     */
    public chargerConfigurationEtBrouillonPuisDefinirLesScenariosAtester(codeDemarche: string, urlConfigurationDemarche: string, urlsBrouillonDemarche: string[]): void {

        LogUtils.creerFichier(codeDemarche + '-' + this.idSession);
        LogUtils.log('Démarrage des tests pour la démarche \'' + codeDemarche + '\' avec la configuration \'' + urlConfigurationDemarche + '\' et les brouillons ' + JSON.stringify(urlsBrouillonDemarche) + ' avec une application répondant sur \'' + Cypress.config().baseUrl + '\'');

        // Chargement de la configuration de la démarche et sauvegarde dans configurationDemarche
        cy.request(Cypress.config().baseUrl + 'generique' + urlConfigurationDemarche).then(reponseParam => {

            // Sauvegarde de la configuration dans une variable structurée (pour l'autocompletion)
            this.confDemarche = reponseParam.body as ConfigurationDemarche;

            //Petit passage sur les données de configuration pour alimenter les listes vides
            this.confDemarche.pages = this.confDemarche.pages || [];
            this.confDemarche.pages.forEach(p => {
                p.blocs = p.blocs || [];
                p.blocs.forEach(b => {
                    b.contenus = b.contenus || [];
                    b.sousBlocs = b.sousBlocs || [];
                    b.sousBlocs.forEach(sb => sb.contenus = sb.contenus || []);
                });
            });

            // Log du contenu de la configuration chargée (au besoin)
            LogUtils.log('Configuration chargée : ' + JSON.stringify(this.confDemarche));
            
            // Chargement du brouillon pour fournir des données de test
            urlsBrouillonDemarche.forEach(urlBrouillonDemarche => {
                cy.request(Cypress.config().baseUrl + 'generique' + urlBrouillonDemarche).then(reponseBrouillon => {

                    // Sauvegarde du brouillon dans une variable structurée (pour l'autocompletion)
                    this.brouillonsDisponibles.push(reponseBrouillon.body as BrouillonDemarche);

                    // Log du contenu de la configuration chargée (au besoin)
                    LogUtils.log('Brouillon chargé : ' + JSON.stringify(reponseBrouillon.body));
                });
            });

            // Calcul de tous les scénarios à réaliser
            this.definirLesScenariosAtester();
        });
    }

    /** Calcul de tous les scénarios à réaliser */
    private definirLesScenariosAtester(): void {

        // Au cas où
        if (!this.confDemarche) {
            return;
        }

        // Initialisation de la liste des URLs des scénarios de test (d'abord les non connectés puis les connectés)
        this.listeUrlDesScenariosDeTest = [];

        // Initialisation de la liste des URLs des scénarios de test connectés
        const listeUrlDesScenariosDeTestConnectes = [];

        // si pas de points d'entrée défini, un seul scénario de test est possible avec l'URL '/'
        if (!this.confDemarche.pointsEntree || this.confDemarche.pointsEntree.length === 0) {
            this.listeUrlDesScenariosDeTest.push('&');
        }

        // Sinon parcours des points d'entree pour créer une URL pour chaque combinaison possible des paramètres (ou simplement ['/'] si pas de paramètre)
        else {
            for (let i = 0; i < this.confDemarche.pointsEntree.length; i++) {
                const pe = this.confDemarche.pointsEntree[i];

                // Initialisation de la liste avec l'URL de base (qui sera retirée plus loin)
                let urlsDuPointEntree = [''];

                // Pour chaque paramètre, création, dans la liste, d'une nouvelle URL avec chaque valeur différente possible du paramètre
                pe.parametres?.forEach(p => {
                    const nouvelleListe: string[] = [];
                    urlsDuPointEntree.forEach(url => {
                        p.valeurs.forEach(v => {
                            const nouvelleUrl = url + '&' + p.parametre + '=' + v;
                            nouvelleListe.push(nouvelleUrl);
                        });
                    });
                    urlsDuPointEntree = nouvelleListe;
                });

                // Ajout des URLs de ce point d'entrée à la bonne liste
                if (pe.authentification) {
                    listeUrlDesScenariosDeTestConnectes.push(...urlsDuPointEntree);
                } else {
                    this.listeUrlDesScenariosDeTest.push(...urlsDuPointEntree);
                }
            }
        }

        // Ajout de la liste des URLs des scénarios connectés à la fin de la liste
        this.listeUrlDesScenariosDeTest.push(...listeUrlDesScenariosDeTestConnectes);

        // Log des scénarios
        LogUtils.log('Liste des scénarios de test à réaliser : ' + JSON.stringify(this.listeUrlDesScenariosDeTest));
    }

    /**
     * Démarrage de la navigation à appeler dans la méthode it('Démarrage de la navigation', () => {})
     */
    public demarrerLaNavigation(urlScenarioAdemarrer: string): void {

        // Initialisation des données au tout début du test avec les données de la configuration
        if (this.confDemarche && this.confDemarche.valeursInitiales) {
            this.donneesDeBase = this.confDemarche.valeursInitiales;
        }

        // Ajout des données présentes dans l'URL
        const indexInterrogation = urlScenarioAdemarrer.indexOf('&');
        if (indexInterrogation !== -1) {
            const listeParametres = urlScenarioAdemarrer.substring(indexInterrogation + 1).split('&').forEach(p => {
                const split = p.split('=');
                this.donneesDeBase[split[0]] = split[1];
            })
        }

        // Démarrage de la navigation
        NavigationTestUtils.demarrerNavigation(this.confDemarche as ConfigurationDemarche, urlScenarioAdemarrer);

        // Analyse de l'URL pour trouver le brouillon correspondant
        this.selectionnerBrouillon(urlScenarioAdemarrer);
    }

    /**
     * Sélection du bon brouillon en fonction des données de l'URL.
     * @param urlScenarioAdemarrer URL du test.
     */
    private selectionnerBrouillon(urlScenarioAdemarrer: string) {

        // Cas simple : il n'y a qu'un unique brouillon disponible
        if (this.brouillonsDisponibles.length == 1) {
            this.brouillonDemarche = this.brouillonsDisponibles[0];
        }
        // Cas plus complexe : analyse de l'URL 
        else {
            // Extraction des paramètres de l'URL
            // substring(1) pour le & au début
            // les replace pour transformer tout ça en JSON
            const params = JSON.parse('{"' + decodeURI(urlScenarioAdemarrer.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}')
            // Recherche du dernier brouillon (de la liste) correspondant aux paramètres
            this.brouillonsDisponibles.forEach(b => {
                const bCorrespond = Object.keys(params).map(p => b.donnees[p] == params[p]).reduce((cumul: boolean, val: boolean) => cumul && val, true);
                if (bCorrespond) {
                    this.brouillonDemarche = b;
                }
            })
        }
        // si toujours pas de brouillon, on plante
        if (!this.brouillonDemarche) {
            throw new Error("Aucun brouillon n'a été trouvé pour ce scénario");
        }

        // Log du brouillon
        LogUtils.log('Brouillon sélectionné : ' + JSON.stringify(this.brouillonDemarche));
    
    }

    /**
     * Execution d'une étape i de l'ensemble des tests.
     * @param i Etape i.
     */
    public executerUnePartieDesTests(i: number): void {

        // Si le scénario précédent est fini, on en démarre un nouveau
        if (this.scenarioPrecedentTermine) {
            // Sélection du scénario suivant
            this.indexScenarioDeTestEnCours++;
            this.indexPartieCorrespondantAuDebutDuScenarioEnCours = i + 1;
            const urlScenario = this.listeUrlDesScenariosDeTest[this.indexScenarioDeTestEnCours];
            // Si un scénario suivant existe
            if (typeof urlScenario !== 'undefined') {
                // Flag à false pour, durant la prochaine partie, tester une page
                this.scenarioPrecedentTermine = false;
                // reset du compteur des pages invisibles entre deux scénarios
                this.nbPagesInvisiblesDansAriane = 0;
                // on le démarre
                this.demarrerLaNavigation(urlScenario);
            }
        }

        // Si la partie 'i' correspond au test d'une page (le scénario précédent n'est pas fini)
        else {

            // En arrivant sur la page de recapitulatif
            // TODO: tester l'aller-retour complet avec les boutons RETOUR
            // TODO: tester l'aller-retour complet avec les boutons MODIFIER
            // TODO: tester l'aller-retour complet avec les boutons du fil d'Ariane

            this.testerPage(i - this.indexPartieCorrespondantAuDebutDuScenarioEnCours);
        }
    }

    /**
     * Execution du test d'une page i.
     * @param i Page i du test (une étape/page d'un déroulé de démarche).
     */
    public testerPage(i: number): void {
        // On arrête tout de suite cette itération si la configuration ne contient pas autant de pages
        if (!this.confDemarche || i >= this.confDemarche.pages.length - 1) {
            this.scenarioPrecedentTermine = true;
            return;
        }

        // Création des espions de console pour ce test (au sens Jasmin)
        ConsoleUtils.installerEspionConsole();

        // Récupération de la page à tester
        const page = this.confDemarche.pages[i];
        LogUtils.logPage(i, 'Validation de la page \'' + page.titre + '\'');

        // Initialisation de la variable contenant les données saisies dans la page et pas encore présentes dans le contexte
        const donneesDeLaPage = {};

        // Log de l'état initial des données
        LogUtils.logPage(i, 'Données de base au début du test de la page : ' + JSON.stringify(this.donneesDeBase));
        LogUtils.logPage(i, 'Données de la page au début du test de la page : ' + JSON.stringify(donneesDeLaPage));

        // Calcul de la visibilité de la page
        if (!DonneesUtils.calculerCondition(page.conditionVisibilite, DonneesUtils.integrerDonnees(this.donneesDeBase, donneesDeLaPage), true)) {
            // Si la page n'est pas visible, fin du traitement de cette page
            LogUtils.logPage(i, 'Page non visible');
            this.nbPagesInvisiblesDansAriane++;
            return;
        }

        // Vérification du nombre de boutons de navigation
        NavigationTestUtils.validerBoutonsDeLaPage(this.confDemarche as ConfigurationDemarche, i);

        // Vérification du fil d'Ariane à cette page (on récupère, en sortie, le nombre de pages invisibles dans le fil)
        this.nbPagesInvisiblesDansAriane = FilArianeTestUtils.validerFilDunePage(page, i, this.nbPagesInvisiblesDansAriane, this.confDemarche?.fonctionnalites?.modeObligatoireParDefaut || false);

        // Pour chaque bloc de la page
        page.blocs.forEach((bloc, iBloc) => {

            // Calcul de la visibilité du bloc
            if (!BlocTestUtils.verifierSiLeBlocEstVisible(i, iBloc, undefined, bloc, DonneesUtils.integrerDonnees(this.donneesDeBase, donneesDeLaPage))) {
                // Si le contenu est inactif, on cesse de le traiter
                return;
            }

            // Si le bloc est dynamique
            if (bloc.dynamique) {
                // Recherche des occurences présentes dans le brouillon
                const listeOccurrences = this.calculerListeOccurencesApartirDesDonnees(bloc);

                // log
                LogUtils.logBloc(i, iBloc, 'Validation du bloc dynamique avec ' + listeOccurrences.length + ' occurences dans les données');

                // Création des occurences (attention, le brouillon ne doit pas contenir de saut d'occurence)
                listeOccurrences.forEach(o => {

                    // Création des contenus correspondant à l'occurence
                    const contenusOccurence = this.clonerAminimaContenusPourLoccurence(bloc, o);

                    // Création de l'occurence
                    cy.blocObtenir(i, iBloc).find('.btn-ajout').click();
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-supprimer').should('be.visible');
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-quitter').should('be.visible');
                    cy.blocObtenir(i, iBloc).find('.btn-ajout').should('be.disabled');

                    // Validation des contenus
                    this.validerContenus(i, iBloc, undefined, contenusOccurence, donneesDeLaPage, o, true);

                    // Enregistrement/Validation de l'occurence 
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-quitter').click();
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-supprimer').should('be.visible');
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-quitter').should('not.exist');
                    cy.blocObtenirOccurence(i, iBloc, o).find('.btn-editer').should('be.visible');
                    cy.blocObtenir(i, iBloc).find('.btn-ajout').should('be.enabled');

                    // Validation des contenus
                    this.validerContenus(i, iBloc, undefined, contenusOccurence, donneesDeLaPage, o, false);
                });

                // Pour la dernière occurence disponible
                if (listeOccurrences && listeOccurrences.length > 0) {
                    const derniereOccurence = listeOccurrences[listeOccurrences.length - 1];
                    // Click sur le bouton d'édition
                    cy.blocObtenirOccurence(i, iBloc, derniereOccurence).find('.btn-editer').click();
                    cy.blocObtenirOccurence(i, iBloc, derniereOccurence).find('.btn-supprimer').should('be.visible');
                    cy.blocObtenirOccurence(i, iBloc, derniereOccurence).find('.btn-quitter').should('be.visible');
                    cy.blocObtenir(i, iBloc).find('.btn-ajout').should('be.disabled');

                    // Validation des contenus
                    const contenusOccurence = this.clonerAminimaContenusPourLoccurence(bloc, derniereOccurence);
                    this.validerContenus(i, iBloc, undefined, contenusOccurence, donneesDeLaPage, derniereOccurence, true);

                    // Enregistrement/Validation de l'occurence 
                    cy.blocObtenirOccurence(i, iBloc, derniereOccurence).find('.btn-quitter').click;
                }
            }
            // Si le bloc n'est pas dynamique
            else {
                LogUtils.logBloc(i, iBloc, 'Validation du bloc \'' + bloc.titre + '\'');
                // Vérification de la présence du bloc
                BlocTestUtils.validerBloc(i, iBloc, undefined, bloc);
                // Validation des contenus
                this.validerContenus(i, iBloc, undefined, bloc.contenus || [], donneesDeLaPage, undefined, undefined);

                // Pour chaque sous-bloc, la même chose
                (bloc.sousBlocs || []).forEach((sousBloc, iSousBloc) => {

                    // Calcul de la visibilité du bloc
                    if (!BlocTestUtils.verifierSiLeBlocEstVisible(i, iBloc, iSousBloc, sousBloc, DonneesUtils.integrerDonnees(this.donneesDeBase, donneesDeLaPage))) {
                        // Si le contenu est inactif, on cesse de le traiter
                        LogUtils.logBloc(i, iBloc, 'sousBloc non visible');
                        return;
                    }
                    LogUtils.logBloc(i, iBloc, 'Validation du sous-bloc \'' + sousBloc.titre + '\' du bloc \'' + bloc.titre + '\'');

                    // Vérification de la présence du bloc
                    BlocTestUtils.validerBloc(i, iBloc, iSousBloc, sousBloc);

                    // Validation des contenus
                    this.validerContenus(i, iBloc, iSousBloc, sousBloc.contenus || [], donneesDeLaPage, undefined, undefined);
                });
            }
        });

        // Avant de passer à la page suivante, on cumule les donnée de cette page dans les données de base pour le test suivant
        LogUtils.logPage(i, 'Fin du test de la page avec les données de page : ' + JSON.stringify(donneesDeLaPage));
        this.donneesDeBase = DonneesUtils.integrerDonnees(this.donneesDeBase, donneesDeLaPage);

        // Controle la console
        ConsoleUtils.verifierAbsenceAppelConsole();

        // Creation d'un screenshot de la page
        const nomFichierCapture = this.confDemarche.codeDemarche + '-' + this.idSession + '/page' + i + '.png';
        LogUtils.logPage(i, 'Capture du contenu de la page dans le fichier ./cypress/screenshots/' + nomFichierCapture);
        cy.screenshot(nomFichierCapture, { log: false, capture: 'fullPage' });

        // Sauvegarde du contenu de la page
        const nomFichierSource = './cypress/pagesHTML/' + this.confDemarche.codeDemarche + '-' + this.idSession + '/page' + i + '.html';
        LogUtils.logPage(i, 'Sauvegarde du contenu HTML de la page dans le fichier ' + nomFichierSource);
        cy.get('html:root').eq(0).invoke('prop', 'outerHTML').then((doc: any) => {
            cy.writeFile(nomFichierSource, doc);
        });

        // Validation HTML
        LogUtils.logPage(i, 'Validation du code HTML (à analyser depuis le fichier sauvegardé dans ' + nomFichierSource + ')');
        cy.htmlvalidate();

        // Validation RGAA
        LogUtils.logPage(i, 'Validation RGAA de la page (cf. documentation https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md) (à analyser depuis le fichier sauvegardé dans ' + nomFichierSource + ')');
        cy.checkA11y();

        // Après les tests de cette page, on passe à la suivante
        NavigationTestUtils.passerAlaPageSuivante(page);
    }

    /** Méthode clonant les contenus du bloc pour une occurence précise (pour en changer la clef et les conditions notamment) */
    private clonerAminimaContenusPourLoccurence(bloc: Bloc, o: number): ContenuDeBloc[] {
        return bloc.contenus.map(c => {
            const nouveauContenu = Object.assign({}, c);
            nouveauContenu.clef = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.clef, o);
            nouveauContenu.conditionVisibilite = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.conditionVisibilite, o);
            nouveauContenu.conditionDesactivation = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(nouveauContenu.conditionDesactivation, o);
            return nouveauContenu;
        });
    }

    /** Recherche de la liste des occurences pour lesquelles existent une valeur dans les données du brouillon. */
    private calculerListeOccurencesApartirDesDonnees(bloc: Bloc): number[] {
        const liste: number[] = [];
        // Pour chaque occurence possible (en commençant par la fin)
        if (bloc && bloc.maxOccurences) {
            for (let i = 0; i < bloc?.maxOccurences; i++) {
                // Recherche d'une valeur pour un des champs (en remettant la clef originale)
                const contenusActifTrouve = bloc?.contenus.find(c => {
                    if (c.clef) {
                        const clefContenuDansOccurence = UtilitaireBlocDynamique.remplacerNumeroOccurenceDansUneClefDeContenu(c.clef, i);
                        const valeurTrouvee = this.brouillonDemarche?.donnees[clefContenuDansOccurence as string];
                        return typeof valeurTrouvee !== 'undefined';
                    } else {
                        return false;
                    }
                });
                // Si contenu actif trouvée
                if (contenusActifTrouve) {
                    liste.push(i);
                }
            }
        }
        return liste;
    }

    /**
     * Test du bon fonctionnement des contenus du bloc
     * @param i Page i du test (une étape/page d'un déroulé de démarche).     * 
     * @param iBloc L'index du bloc.
     * @param iSousBloc L'index du sous-bloc (ou undefined)
     * @param contenus Les contenus du bloc.
     * @param donneesDeLaPage Les données de la page
     * @param occurenceDeBlocDynamique Occurence si c'est un bloc dynamique sinon UNDEFINED 
     * @param modeEdition Mode LECTURE ou EDITION
     */
    private validerContenus(i: number, iBloc: number, iSousBloc: number | undefined, contenus: ContenuDeBloc[], donneesDeLaPage: { [index: string]: any }, occurenceDeBlocDynamique: number | undefined, modeEdition: boolean | undefined): void {

        // Pour chaque contenu du bloc
        contenus.forEach((contenu, iContenu) => {
            LogUtils.logContenu(i, iBloc, iSousBloc, iContenu, 'Validation du contenu de type \'' + contenu.type + '\' avec la clef \'' + contenu.clef + '\'');

            // Si on est dans une occurence de bloc dynamique
            if (typeof occurenceDeBlocDynamique !== 'undefined') {
                // en mode EDITION, ne s'affichent pas les contenu de type PARAGRAPHE avec l'attribut estIncluDansUnBlocDynamiquePourLectureSeule=true
                if (modeEdition && contenu.type === TypeContenuDeBloc.paragraphe && !!(contenu as ContenuParagraphe).estIncluDansUnBlocDynamiquePourLectureSeule) {
                    return;
                }
                // en mode LECTURE, ne s'affichent que les contenu de type PARAGRAPHE avec l'attribut estIncluDansUnBlocDynamiquePourLectureSeule=true
                if (!modeEdition && !(contenu.type === TypeContenuDeBloc.paragraphe && !!(contenu as ContenuParagraphe).estIncluDansUnBlocDynamiquePourLectureSeule)) {
                    return;
                }
            }

            if (ContenuTestUtils.verifierSiLeContenuEstActif(i, iBloc, iContenu, contenu, DonneesUtils.integrerDonnees(this.donneesDeBase, donneesDeLaPage))) {
                // Validation de l'existence du contenu
                ContenuTestUtils.validerExistence(iBloc, iSousBloc, iContenu, contenu, occurenceDeBlocDynamique);

                // Saisie d'une valeur valide
                ContenuTestUtils.validerBonFonctionnementContenu(i, iBloc, iSousBloc, iContenu, contenu, this.confDemarche, this.brouillonDemarche as BrouillonDemarche, this.donneesDeBase, donneesDeLaPage, occurenceDeBlocDynamique);
            }
        });
    }

    /** Calcul du timestamp courant. */
    private calculerTimestampCourant(): string {
        const dateDuJour = new Date();
        const mois = this.formatterNombre((dateDuJour.getMonth() + 1).toString());
        const jour = this.formatterNombre(dateDuJour.getDate().toString());
        const heure = this.formatterNombre(dateDuJour.getHours().toString());
        const minute = this.formatterNombre(dateDuJour.getMinutes().toString());
        const seconde = this.formatterNombre(dateDuJour.getSeconds().toString());
        return dateDuJour.getFullYear() + mois + jour + heure + minute + seconde;
    }
    /** Formattage du nombre sur deux chiffres. */
    private formatterNombre(nombre: string): string {
        return (+nombre < 10) ? '0' + nombre : nombre;
    }
}
