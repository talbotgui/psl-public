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
/** Petite classe utile */
export class LibelleI18n {
    /** Constructeur avec la déclaration des membres. */
    constructor(private libelles: { [langue: string]: string }) { }
    /** Méthode récupérant le libellé de la langue ou le 'FR' à défaut */
    get(langue: string) {
        return this.libelles[langue] || this.libelles['FR'];
    }
    /** Méthode retournant la liste des langues supportées */
    getListeLanguesSupportees(): string[] {
        return Object.keys(this.libelles);
    }
}
/**
 * Classe de configuration de tous les messages et libellés
 */
export const i18n = {

    // Petits libellés simples et communs
    commun: {
        oui: new LibelleI18n({ 'FR': 'Oui', 'EN': 'Yes' }),
        non: new LibelleI18n({ 'FR': 'Non', 'EN': 'No' })
    },

    // Messages à l'usager venant de FmkApplicationAbstraitComponent
    application: {
        debut1CreationToken: new LibelleI18n({ 'FR': 'Chargement de l\'application en cours', 'EN': 'EN Chargement de l\'application en cours' }),
        debut2ChargementConfiguration: new LibelleI18n({ 'FR': 'Chargement de la configuration de la démarche', 'EN': 'EN Chargement de la configuration de la démarche' }),
        debut3RechercheModeAuthBrouillon: new LibelleI18n({ 'FR': 'Chargement du contexte du brouillon *idBrouillon*', 'EN': 'EN Chargement du contexte du brouillon *idBrouillon*' }),
        debut3ChargementSansBrouillon: new LibelleI18n({ 'FR': 'Vérification du lien utilisé pour accéder à la démarche', 'EN': 'EN Vérification du lien utilisé pour accéder à la démarche' }),
        debut3ChargementBrouillon: new LibelleI18n({ 'FR': 'Chargement de votre brouillon en cours', 'EN': 'EN Chargement de votre brouillon en cours' }),

        debutErreurPointEntree: new LibelleI18n({ 'FR': 'L\'adresse du navigateur n\'est pas cohérente avec l\'accès à la démarche.', 'EN': 'EN L\'adresse du navigateur n\'est pas cohérente avec l\'accès à la démarche.' }),
        debutErreurPointEntreeMultiple: new LibelleI18n({ 'FR': 'Les paramètres fournis dans l\'adresse du navigateur ne sont pas cohérents avec la démarche.', 'EN': 'EN Les paramètres fournis dans l\'adresse du navigateur ne sont pas cohérents avec la démarche.' }),
        debutErreurChargementBrouillon: new LibelleI18n({ 'FR': 'Erreur durant le chargement de votre brouillon', 'EN': 'EN Erreur durant le chargement de votre brouillon' }),
        debutErreurLectureParametreCodeDemarche: new LibelleI18n({ 'FR': 'Le paramètre du code de démarche est absent ou invalide', 'EN': 'Missing application code in request parameters' })
    },

    // erreurs HTTP traitées dans ClientApiService
    erreursHttpParDefaut: new LibelleI18n({ 'FR': 'Erreur inconnue ou fonctionnalité indisponible', 'EN': 'EN Erreur inconnue ou fonctionnalité indisponible' }),
    erreursHttp: {
        '0': new LibelleI18n({ 'FR': 'Requête non envoyée ou reçue. La connexion internet est-elle absente ou un proxy est-il mal paramétré ?', 'EN': 'EN ERR' }),
        '400': new LibelleI18n({ 'FR': 'Requête invalide.*messagePrecisDeLapi*', 'EN': 'EN ERR' }),
        '401': new LibelleI18n({ 'FR': 'Erreur de sécurité. Les paramètres de connexion sont invalides.', 'EN': 'EN ERR' }),
        '403': new LibelleI18n({ 'FR': 'Erreur de sécurité. La ressource est interdite.', 'EN': 'EN ERR' }),
        '404': new LibelleI18n({ 'FR': 'Erreur de donnée. La ressource demandée n\'existe pas.', 'EN': 'EN ERR' })
    },

    // Messages à l'usager venant de OidcService
    oidcService: {
        echangesOidcEnCours: new LibelleI18n({ 'FR': 'Vérification de l\'identité de la personne connectée. Veuillez patienter quelques secondes s\'il sous plaît.', 'EN': 'OIDC in progress' }),
        redirectionEnCours: new LibelleI18n({ 'FR': 'Connexion en cours (une redirection est en cours). Veuillez patienter quelques secondes', 'EN': 'Auth in progress' }),
        connexionReussie: new LibelleI18n({ 'FR': '', 'EN': '' }), // Vide volontairement pour faire disparaitre le message en cas de succès
        erreurSansState: new LibelleI18n({ 'FR': 'Echec de connexion', 'EN': 'Auth ko' })
    },

    // Messages à l'usager venant de BrouillonService
    brouillonService: {
        brouillonBienSauvegarde: new LibelleI18n({ 'FR': 'La sauvegarde de votre avancement a réussi. Vous pourrez reprendre la saisie de votre démarche avec le lien suivant : *lien*', 'EN': 'Progression saved : *lien*' })
    },

    // Libellés des contenus complexes
    contenu: {
        adresseFrOuEtr: {
            estFrance: {
                titre: new LibelleI18n({ 'FR': 'L\'adresse est-elle en France ?', 'EN': 'In France ?' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            etage: {
                titre: new LibelleI18n({ 'FR': 'Étage - escalier - appartement', 'EN': 'Floor' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            immeuble: {
                titre: new LibelleI18n({ 'FR': 'Immeuble - bâtiment - résidence', 'EN': 'Building' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            voie: {
                titre: new LibelleI18n({ 'FR': 'Numéro et libellé de voie', 'EN': 'Street' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            boitePostale: {
                titre: new LibelleI18n({ 'FR': 'Boite postale / lieu-dit', 'EN': 'BP' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            communeNaissance: {
                titre: new LibelleI18n({ 'FR': 'Code postal / Localité', 'EN': 'Postal code' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            communeActuelle: {
                titre: new LibelleI18n({ 'FR': 'Code postal / Localité', 'EN': 'Postal code' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            adresseETR: {
                titre: new LibelleI18n({ 'FR': 'Adresse', 'EN': 'Addresse' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            paysETR: {
                titre: new LibelleI18n({ 'FR': 'Pays', 'EN': 'Country' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            }
        },
        identite: {
            civilite: {
                titre: new LibelleI18n({ 'FR': 'Civilité', 'EN': 'Civility' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            monsieur: new LibelleI18n({ 'FR': 'Monsieur', 'EN': 'Mister' }),
            madame: new LibelleI18n({ 'FR': 'Madame', 'EN': 'Miss' }),
            nomFamille: {
                titre: new LibelleI18n({ 'FR': 'Nom de Famille', 'EN': 'Familiy name' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            nomUsage: {
                titre: new LibelleI18n({ 'FR': 'Nom d\'usage', 'EN': 'Use name' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            prenoms: {
                titre: new LibelleI18n({ 'FR': 'Prénoms', 'EN': 'EN Prénoms' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            dateNaissance: {
                titre: new LibelleI18n({ 'FR': 'Date de naissance', 'EN': 'Birthdate' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            paysNaissance: {
                titre: new LibelleI18n({ 'FR': 'Pays de naissance', 'EN': 'Birth country' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            communeNaissanceFR: {
                titre: new LibelleI18n({ 'FR': 'Commune de naissance', 'EN': 'Birth city' }),
                aide: new LibelleI18n({ 'FR': 'en France', 'EN': 'in France' })
            },
            communeNaissanceETR: {
                titre: new LibelleI18n({ 'FR': 'Commune de naissance', 'EN': 'Birth city' }),
                aide: new LibelleI18n({ 'FR': 'hors de France', 'EN': 'out of France' })
            },
            nationalite: {
                titre: new LibelleI18n({ 'FR': 'Nationalité', 'EN': 'Notionality' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            }
        },
        contactPersonnel: {
            email: {
                titre: new LibelleI18n({ 'FR': 'Email', 'EN': 'Email' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            },
            numTeleGeneral: {
                titre: new LibelleI18n({ 'FR': 'Numéro de téléphone', 'EN': 'Phone number' }),
                aide: new LibelleI18n({ 'FR': '', 'EN': '' })
            }
        },
        upload: {
            erreurDocumentObligatoire: new LibelleI18n({ 'FR': 'Le versement d\'une pièce jointe est obligatoire', 'EN': 'Required !!' }),
            erreurSuppressionDocument: new LibelleI18n({ 'FR': 'Impossible de retirer la pièce jointe.', 'EN': 'Can not be deleted' }),
            erreurTailleDocument: new LibelleI18n({ 'FR': 'Impossible de traiter un document si volumineux pour le moment (1Mo max).', 'EN': 'Max 1Mo' }),
            erreurVersementDocument: new LibelleI18n({ 'FR': 'Erreur au chargement de la pièce jointe', 'EN': '' }),
            erreurDocumentInvalide: new LibelleI18n({ 'FR': 'Le document fourni ne correspond pas au type de document attendu.', 'EN': '' })
        }
    },

    // Messages d'erreur d'API
    messageErreurApi: {
        // SoumissionService
        'soumettreTeledossier': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la soumission de vos données.', 'EN': 'ERR' }),
        'genererCaptcha': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la génération du CAPTCHA.', 'EN': 'ERR' }),
        'chargerRessourceCaptcha': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la génération des ressources du CAPTCHA.', 'EN': 'ERR' }),
        // SecuriteService
        'seConnecterEnAnonyme': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la connexion à l\'application.', 'EN': 'ERR' }),
        'recupererInformationsUsager': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la récupération de votre identité.', 'EN': 'ERR' }),
        'seConnecterAvecUnMotDePasse': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la connexion à l\'application avec vos paramètres de connexion.', 'EN': 'ERR' }),
        // ReférentielService
        'referentielcommuneban': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'une commune.', 'EN': 'ERR' }),
        'referentieladresseban': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'une adresse.', 'EN': 'ERR' }),
        'referentielcommuneNaissance': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'une commune.', 'EN': 'ERR' }),
        'referentielcommuneActuelle': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'une commune.', 'EN': 'ERR' }),
        'referentielnationalitePaysActuel': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'un pays ou d\'une nationnalité.', 'EN': 'ERR' }),
        'referentielpaysNaissance': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la recherche d\'un pays.', 'EN': 'ERR' }),
        // PieceJointeService
        'verserPieceJointeEnUneUniqueRequete': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le versement de votre pièce jointe.', 'EN': 'ERR' }),
        'supprimerPieceJointe': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le retrait de votre pièce jointe.', 'EN': 'ERR' }),
        // DocumentService
        'chargerListeDocumentsPostSoumission': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la récupération des documents générés lors de la soumission de vos données.', 'EN': 'ERR' }),
        'telechargerDocument': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le téléchargement du document généré.', 'EN': 'ERR' }),
        // ConfigurationService
        'chargerConfigurationDemarche': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le chargement de l\'application.', 'EN': 'ERR' }),
        // BrouillonService
        'obtenirAuthentificationNecessaireAuBrouillon': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le chargement des données précédemment renseignées.', 'EN': 'ERR' }),
        'chargerBrouillon': new LibelleI18n({ 'FR': 'Une erreur est survenue durant le chargement des données précédemment renseignées.', 'EN': 'ERR' }),
        'sauvegarderBrouillon': new LibelleI18n({ 'FR': 'Une erreur est survenue durant la sauvegarde des données renseignées.', 'EN': 'ERR' })
    },

    // Libellés de page HTML
    libellesPageHtml: {
        'commun.nouvelleFenetre': new LibelleI18n({ 'FR': 'Nouvelle fenêtre', 'EN': 'New window' }),
        'commun.facultatif': new LibelleI18n({ 'FR': 'Facultatif', 'EN': 'Not required' }),

        'document.telecharger': new LibelleI18n({ 'FR': 'Télécharger', 'EN': 'Download' }),

        'entete.republique.1': new LibelleI18n({ 'FR': 'République', 'EN': 'République' }),
        'entete.republique.2': new LibelleI18n({ 'FR': 'Française', 'EN': 'Française' }),
        'entete.service.titre': new LibelleI18n({ 'FR': 'Service-Public.fr', 'EN': 'Service-Public.fr' }),
        'entete.service.tag': new LibelleI18n({ 'FR': 'Le site officiel de l\'administration française', 'EN': 'EN Le site officiel de l\'administration française' }),
        'entete.bouton.menu': new LibelleI18n({ 'FR': 'Menu', 'EN': 'Menu' }),
        'entete.bouton.connexion': new LibelleI18n({ 'FR': 'Se connecter', 'EN': 'Log in' }),
        'entete.bouton.deconnexion': new LibelleI18n({ 'FR': 'Se déconnecter', 'EN': 'Log out' }),
        'entete.bouton.fermer': new LibelleI18n({ 'FR': 'Fermer', 'EN': 'Close' }),

        'filariane.etape.1': new LibelleI18n({ 'FR': 'Étape ', 'EN': 'Step ' }),
        'filariane.etape.2': new LibelleI18n({ 'FR': ' sur ', 'EN': ' of ' }),
        'filariane.etapesuivante': new LibelleI18n({ 'FR': 'Étape suivante :', 'EN': 'Next step' }),
        'filariane.pagesuivante': new LibelleI18n({ 'FR': 'Page suivante : ', 'EN': 'Next page :' }),

        'findemarche.captcha.saisie': new LibelleI18n({ 'FR': 'Veuillez saisir la valeur du captcha ci-dessus', 'EN': 'Capcha' }),
        'findemarche.bouton.soumettre': new LibelleI18n({ 'FR': 'Soumettre mon télé-dossier', 'EN': 'Launch' }),

        'navigation.bouton.precedent': new LibelleI18n({ 'FR': 'Précédent', 'EN': 'Previous' }),
        'navigation.bouton.brouillon': new LibelleI18n({ 'FR': 'Reprendre plus tard', 'EN': 'Save' }),
        'navigation.bouton.suivant': new LibelleI18n({ 'FR': 'Suivant', 'EN': 'Next' }),

        'page.modeObligatoire.1': new LibelleI18n({ 'FR': 'Sauf indication contraire, ', 'EN': 'EN Sauf indication contraire, ' }),
        'page.modeObligatoire.2': new LibelleI18n({ 'FR': 'toutes les informations demandées sont obligatoires', 'EN': 'toutes les informations demandées sont obligatoires ' }),
        'page.modeObligatoire.3': new LibelleI18n({ 'FR': ' pour pouvoir traiter votre demande.', 'EN': ' pour pouvoir traiter votre demande.' }),
        'page.modePasObligatoire.1': new LibelleI18n({ 'FR': 'Les champs marqués d\'un ', 'EN': 'EN ' }),
        'page.modePasObligatoire.2': new LibelleI18n({ 'FR': ' sont obligatoires', 'EN': ' sont obligatoires' }),

        'repriseBrouillon.titre': new LibelleI18n({ 'FR': 'Reprendre la saisie de ma démarche', 'EN': 'EN Reprendre la saisie de ma démarche' }),
        'repriseBrouillon.reprise': new LibelleI18n({ 'FR': 'Veuillez saisir les informations d\'authenfication fournis lors de la sauvegarde de votre brouillon.', 'EN': 'EN Veuillez saisir les informations d\'authenfication fournis lors de la sauvegarde de votre brouillon.' }),
        'repriseBrouillon.sauvegarde': new LibelleI18n({ 'FR': 'Veuillez saisir des informations d\'authenfication pour sauvegarder votre brouillon. Ces informations vous serons demandées à la reprise de ce brouillon.', 'EN': 'EN Veuillez saisir des informations d\'authenfication pour sauvegarder votre brouillon. Ces informations vous serons demandées à la reprise de ce brouillon.' }),
        'repriseBrouillon.email': new LibelleI18n({ 'FR': 'Email', 'EN': 'Login' }),
        'repriseBrouillon.motdepasse': new LibelleI18n({ 'FR': 'Mot de passe', 'EN': 'Password' }),
        'repriseBrouillon.caseAfficher': new LibelleI18n({ 'FR': 'Afficher', 'EN': 'Display' }),
        'repriseBrouillon.caseAfficherLibelle': new LibelleI18n({ 'FR': 'Afficher le mot de passe', 'EN': 'Display the password' }),

        'recapitulatif.bouton.modifier': new LibelleI18n({ 'FR': 'Modifier', 'EN': 'Edit' }),

        'saisielongue.nombreCaracteresRestants': new LibelleI18n({ 'FR': ' caractères restants', 'EN': ' chars left' }),

        'upload.attendu.1': new LibelleI18n({ 'FR': 'Le document doit faire moins de ', 'EN': 'EN Le document doit faire moins de ' }),
        'upload.attendu.2': new LibelleI18n({ 'FR': 'Mo et être de type ', 'EN': 'Mo et être de type ' }),
        'upload.cadreGlisseDeposer': new LibelleI18n({ 'FR': 'Glisser un document dans la zone ou y cliquer pour verser un fichier.', 'EN': 'EN ' }),
        'upload.versementEnCours': new LibelleI18n({ 'FR': 'Versement du fichier ', 'EN': 'EN Versement du fichier ' }),
        'upload.versementTermine.1': new LibelleI18n({ 'FR': 'Fichier ', 'EN': 'File' }),
        'upload.versementTermine.2': new LibelleI18n({ 'FR': ' déjà ', 'EN': ' already ' }),
        'upload.versementTermine.3': new LibelleI18n({ 'FR': ' versé', 'EN': ' uploaded' }),
        'upload.bouton.supprimer': new LibelleI18n({ 'FR': 'Suppression de la pièce jointe', 'EN': 'Delete file' }),

        'utilisateurconnecte.source': new LibelleI18n({ 'FR': 'Informations récupérées via votre compte France-Connect', 'EN': 'From FC' }),
        'utilisateurconnecte.nele': new LibelleI18n({ 'FR': 'Né(e) le ', 'EN': 'Born ' }),
        'utilisateurconnecte.sexe': new LibelleI18n({ 'FR': 'de sexe ', 'EN': 'Sexe' }),
        'utilisateurconnecte.masculin': new LibelleI18n({ 'FR': 'Masculin', 'EN': 'Male' }),
        'utilisateurconnecte.feminin': new LibelleI18n({ 'FR': 'Feminin', 'EN': 'Female' }),

        'validation.obligatoire': new LibelleI18n({ 'FR': 'Champ obligatoire.', 'EN': 'Required' }),
        'validation.obligatoireDate': new LibelleI18n({ 'FR': 'Champ obligatoire par exemple 31/12/2022.', 'EN': 'Required' }),
        'validation.email': new LibelleI18n({ 'FR': 'La saisie n\'est pas un email valide.', 'EN': 'Invalid email' }),
        'validation.secu': new LibelleI18n({ 'FR': 'La saisie n\'est pas un numéro de sécurité social valide.', 'EN': 'Secu' }),
        'validation.telephoneFR': new LibelleI18n({ 'FR': 'La saisie n\'est pas un numéro de téléphone français valide (10 chiffres commençant par 01 à 09).', 'EN': 'Phone' }),
        'validation.datePassee': new LibelleI18n({ 'FR': 'La date (au format 24/12/2021) doit être antérieure à la date du jour.', 'EN': 'Passed date ' }),
        'validation.dateFuture': new LibelleI18n({ 'FR': 'La date (au format 24/12/2021) doit être postérieure à la date du jour.', 'EN': 'Future date' }),
        'validation.autocompletionPrecise': new LibelleI18n({ 'FR': 'La valeur saisie doit être une de celles proposées.', 'EN': 'One of' }),
        'validation.url': new LibelleI18n({ 'FR': 'La valeur saisie doit être une adresse WEB valide.', 'EN': 'URL' }),
        'validation.regex': new LibelleI18n({ 'FR': 'La valeur saisie doit correspondre au format attendu.', 'EN': 'Format' }),
        'validation.dateAvantApres': new LibelleI18n({ 'FR': 'La date saisie doit correspondre aux contraintes calendaires fournies.', 'EN': 'Date' }),
        'validation.motDePasse': new LibelleI18n({ 'FR': 'Le mot de passe saisi doit contenir au moins 8 caractères dont au moins 3 éléments parmi :', 'EN': 'Password need...' }),
        'validation.motDePasse.1': new LibelleI18n({ 'FR': 'une majuscule', 'EN': 'an uppercase' }),
        'validation.motDePasse.2': new LibelleI18n({ 'FR': 'une minuscule', 'EN': 'a lowercase' }),
        'validation.motDePasse.3': new LibelleI18n({ 'FR': 'un chiffre', 'EN': 'a digit' }),
        'validation.motDePasse.4': new LibelleI18n({ 'FR': 'un caractère spécial parmi ', 'EN': 'a special character from ' })
    }
};
