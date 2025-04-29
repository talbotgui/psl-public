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
import { ContenuAutocompletion, ContenuDeBloc, ContenuListeFinie, ContenuParagraphe, ContenuSaisie, ContenuSaisieComplexe, TypeContenuDeBloc, Validation } from '../model/configurationdemarchecontenubloc.model';
import { ConfigurationDemarche, Page, SousBloc } from '../model/configurationdemarchegeneral.model';
import { DonneesService } from '../services/stateful/donnees.service';

/**
 * Cette classe fournit exclusivement des méthodes statiques de lecture/écriture/manipulation de modèles.
 * 
 * Aucun appel à un composant externe n'est autorisé !
 */
export class UtilitaireModel {

    /** Cette constante est à maintenir en cohérence avec son équivalent dans la classe com.github.talbotgui.psl.socle.soumission.service.ValidationSoumissionServiceImpl */
    private static readonly TYPE_CONTENU_NON_SAISIE: TypeContenuDeBloc[] = [TypeContenuDeBloc.paragraphe, TypeContenuDeBloc.recapitulatif, TypeContenuDeBloc.finDemarche, TypeContenuDeBloc.documents, TypeContenuDeBloc.utilisateurConnecte];
    /** Cette constante est à maintenir en cohérence avec son équivalent dans la classe com.github.talbotgui.psl.socle.soumission.service.ValidationSoumissionServiceImpl */
    private static readonly TYPE_CONTENU_SAISIE_MONO_CHAMP: TypeContenuDeBloc[] = [TypeContenuDeBloc.case, TypeContenuDeBloc.date, TypeContenuDeBloc.listeFinie, TypeContenuDeBloc.saisie, TypeContenuDeBloc.saisieLongue, TypeContenuDeBloc.autocompletion, TypeContenuDeBloc.radio, TypeContenuDeBloc.uploadDocument];
    /** Cette constante est à maintenir en cohérence avec son équivalent dans la classe com.github.talbotgui.psl.socle.soumission.service.ValidationSoumissionServiceImpl */
    private static readonly TYPE_CONTENU_SAISIE_COMPLEXE: TypeContenuDeBloc[] = [TypeContenuDeBloc.identite, TypeContenuDeBloc.adresseFrOuEtr, TypeContenuDeBloc.contactPersonnel];
    /** Cette constante est à maintenir en cohérence avec son équivalent dans la classe com.github.talbotgui.psl.socle.soumission.service.ValidationSoumissionServiceImpl */
    private static readonly TYPE_CONTENU_VALEUR_OBJET: TypeContenuDeBloc[] = [TypeContenuDeBloc.autocompletion, TypeContenuDeBloc.uploadDocument];
    /** Cette constante est à maintenir en cohérence avec son équivalent dans la classe com.github.talbotgui.psl.socle.soumission.service.ValidationSoumissionServiceImpl */
    private static readonly TYPE_CONTENU_VALEUR_BOOLEAN: TypeContenuDeBloc[] = [TypeContenuDeBloc.case];

    /** Vérification du type vis-à-vis de TYPE_CONTENU_NON_SAISIE. */
    public static contenuNestPasUneSaisie(c: ContenuDeBloc): boolean {
        return c && !!c.type && UtilitaireModel.TYPE_CONTENU_NON_SAISIE.includes(c.type);
    }
    /** Vérification du type vis-à-vis de TYPE_CONTENU_SAISIE_COMPLEXE. */
    public static contenuEstUneSaisieComplexe(c: ContenuDeBloc): boolean {
        return c && !!c.type && UtilitaireModel.TYPE_CONTENU_SAISIE_COMPLEXE.includes(c.type);
    }
    /** Vérification du type vis-à-vis de TYPE_CONTENU_SAISIE_MONO_CHAMP. */
    public static contenuEstUneSaisieMonoChamp(c: ContenuDeBloc): boolean {
        return c && !!c.type && UtilitaireModel.TYPE_CONTENU_SAISIE_MONO_CHAMP.includes(c.type);
    }
    /** Vérification du type vis-à-vis de TYPE_CONTENU_VALEUR_OBJET. */
    public static contenuGereUneValeurDeTypeObjet(c: ContenuDeBloc): boolean {
        return c && !!c.type && UtilitaireModel.TYPE_CONTENU_VALEUR_OBJET.includes(c.type);
    }
    /** Vérification si les valeurs possibles sont TRUE/FALSE : soit un type TYPE_CONTENU_VALEUR_BOOLEAN soit un radio ou une liste avec les options truefalse ou falsetrue. */
    public static contenuGereUneValeurDeTypeBoolean(c: ContenuDeBloc): boolean {
        // Si c'est une case à cocher
        if (c && !!c.type && UtilitaireModel.TYPE_CONTENU_VALEUR_BOOLEAN.includes(c.type)) {
            return true;
        }
        // Si c'est un radio ou une listeFinie avec les options TRUE/FALSE
        if (c && c.type === TypeContenuDeBloc.radio || c.type === TypeContenuDeBloc.listeFinie) {
            const valeursPossibles = (c as ContenuListeFinie).valeurs;
            if (valeursPossibles.length !== 2) {
                return false;
            }
            const chaineDeComparaison = '' + valeursPossibles[0].valeur + valeursPossibles[1].valeur;
            return chaineDeComparaison === 'truefalse' || chaineDeComparaison === 'falsetrue';
        }
        return false;
    }

    /** Si le contenu n'est dans aucune de ces listes, c'est un pb de conf */
    public static contenuEstDeTypeInconnu(c: ContenuDeBloc): boolean {
        return c && !!c.type && !UtilitaireModel.TYPE_CONTENU_NON_SAISIE.includes(c.type)
            && !UtilitaireModel.TYPE_CONTENU_SAISIE_MONO_CHAMP.includes(c.type)
            && !UtilitaireModel.TYPE_CONTENU_SAISIE_COMPLEXE.includes(c.type)
            && !UtilitaireModel.TYPE_CONTENU_VALEUR_OBJET.includes(c.type)
            && !UtilitaireModel.TYPE_CONTENU_VALEUR_BOOLEAN.includes(c.type);
    }

    /** Vérifie le type de contenu (avec transtypage du fait que l'enum n'est pas une STRING - pb des chargements JSON) */
    public static contenuEstDeType(contenu: ContenuDeBloc, type: TypeContenuDeBloc): boolean {
        return contenu.type === type as string;
    }

    /** Vérifie que le contenu contient cette validation précise */
    public static contenuContientCetteValidation(contenu: ContenuDeBloc, validation: Validation): boolean {
        const contenuAsAny = contenu as any;
        // Pas de contenu
        if (!contenu) {
            return false;
        }
        // composant héritant de ContenuSaisie
        else if (contenuAsAny['validationsSimples'] && Array.isArray(contenuAsAny['validationsSimples'])) {
            const cSaisie = contenu as ContenuSaisie;
            return cSaisie.validationsSimples.indexOf(validation as string) !== -1;
        }
        // composant héritant de ContenuSaisieComplexe
        else if (contenuAsAny['validationsComplexes']) {
            const cSaisie = contenu as ContenuSaisieComplexe;
            return Object.keys(cSaisie.validationsComplexes).indexOf(validation as string) !== -1;
        }
        // si pas de validation
        else {
            return false;
        }
    }

    /** Cette méthode vérifie que la clef n'est présente qu'une seule et unique fois dans toutes la démarche. */
    public static verifierDonneeEstPresenteeUneSeuleFoisDansTouteLaDemarche(configurationDemarche: ConfigurationDemarche, clefAverifier: string): boolean {
        // Fonction commune au traitement d'un bloc et d'un sous-bloc 
        // utilisée plus loin
        const fn = function (unBloc: SousBloc) {
            (unBloc.contenus || []).forEach(contenu => {
                const clef = contenu.clef;
                if (clef && (clef === clefAverifier || clef.indexOf(clefAverifier + '_') !== -1 || clefAverifier.indexOf(clef + '_') !== -1)) {
                    nbOccurences++;
                }
            })
        }
        let nbOccurences = 0;
        (configurationDemarche.pages || []).forEach(page =>
            (page.blocs || []).forEach(bloc => {
                fn(bloc);
                bloc.sousBlocs.forEach(sb => fn(sb));
            }
            )
        );
        return nbOccurences === 1;
    }

    /** Méthode recherchant un composant précis dans une page */
    public static rechercherComposantDansLaPage(page: Page, typeContenu: TypeContenuDeBloc): ContenuDeBloc | undefined {
        let resultat = undefined;
        (page.blocs || []).forEach(b => b.contenus.forEach(c => {
            if (c.type === typeContenu) {
                resultat = c;
            }
        }));
        return resultat;
    }

    /** Méthode extrayant les clefs de donnée d'une chaine de caractères */
    private static rechercheClefsDeVariableDansUnLibelle(chaine: string | undefined): string[] {
        // Recherche de tous les {{xxx}}
        const m = (chaine) ? UtilitaireModel.retirerAppelsDeFonctions(chaine).match(DonneesService.REGEX_RECHERCHE_DONNEE) : [];
        // Retrait des {{ et }}
        return m ? [...m.map(e => e.substring(2, e.length - 2))] : [];
    }

    /** Retrait des noms de fonction manipulable dans la configuration (notamment pour les blocs dynamiques) */
    private static retirerAppelsDeFonctions(chaine: string): string {
        let chaineNettoyee = chaine;
        DonneesService.LISTE_FONCTIONS_DISPONIBLES.forEach(f => chaineNettoyee = chaineNettoyee.replace(new RegExp(f + "\\(\\'([^\\)]*)\\'\\)"), '$1'));
        return chaineNettoyee;
    }

    /** Méthode extrayant les clefs de donnée d'une chaine de caractères */
    private static rechercheClefsDeVariableDansUneExpression(chaine: string | undefined): string[] {
        // Au cas où
        if (!chaine) {
            return [];
        }
        // Retrait des noms de fonction
        let chaineNettoyee = UtilitaireModel.retirerAppelsDeFonctions(chaine)
        // Retrait des valeurs entre ''
        chaineNettoyee = chaineNettoyee.replace(/'[^']*'/ig, '');
        // Puis extraction des variables avec la regex
        const liste = chaineNettoyee.match(DonneesService.REGEX_RECHERCHE_DONNEE_EXPRESSION);
        // Si pas de résultats
        if (!liste) {
            return [];
        }
        // retrait des valeurs 'true', 'false', ...
        return liste.filter(e => e !== 'true' && e !== 'false');
    }

    /* Initialisation des données du contenu autour de la gestion des titres, des aides et des conditions (pouvant contenir des variables)*/
    public static initialiserLeOuLesTitresEtAides(c: ContenuDeBloc): void {

        // Recherche de toutes les clefs de données présentes dans la configuration de ce contenu (titre(s), aide(s), conditions, texte, ...)
        const listeClefs: string[] = [];
        listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(c.titre));
        listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(c.aide));
        listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUneExpression(c.conditionDesactivation));
        listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUneExpression(c.conditionVisibilite));
        if (UtilitaireModel.contenuEstUneSaisieComplexe(c)) {
            const cComplexe = c as ContenuSaisieComplexe;
            if (cComplexe.titres) {
                Object.keys(cComplexe.titres).forEach(t => listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(t)));
            }
            if (cComplexe.aides) {
                Object.keys(cComplexe.aides).forEach(a => listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(a)));
            }
        }

        // Initialisation des titres et aide (par défaut, la valeur est le modèle fourni)
        // rien à faire pour les composants complexes car ils sont fait de sous-composants simples dont l'aide et le titre sont initialisés dans FmkContenuComplexeAbstraitComponent.initialiserComposant
        c.aideAafficher = c.aide;
        c.titreAafficher = c.titre;

        // Spécificités ici pour recalculer le texte du paragraphe (et d'autres) en lien avec le code spécifique dans FormulaireService.recalculerLesElementsDuContenu
        if (c.type === TypeContenuDeBloc.paragraphe) {
            const cP = c as ContenuParagraphe;
            listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(cP.texte));
            cP.texteAafficher = (cP.texte && cP.texte.length > 0) ? cP.texte : undefined;
        }
        //  Spécificités ici pour les options de liste déroulante et de radio
        else if (c.type === TypeContenuDeBloc.radio || c.type === TypeContenuDeBloc.listeFinie) {
            const cL = c as ContenuListeFinie;
            cL.valeurs.forEach(v => {
                if (v.conditionVisibilite) {
                    listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUneExpression(v.conditionVisibilite));
                }
            });
        }
        // Spécificité pour les compléments d'appel aux API d'une autocomplétion
        else if (c.type === TypeContenuDeBloc.autocompletion) {
            const contenuType = c as ContenuAutocompletion;
            listeClefs.push(...UtilitaireModel.rechercheClefsDeVariableDansUnLibelle(contenuType.complementAppelApi));
        }

        // Affectation tardive du tableau (car les attributs du contenu sont surveillés par Angular et leur modification entraine des traitements de synchro)
        c.listeClefsDonneesPresentesDansConfiguration = listeClefs;
    }
}