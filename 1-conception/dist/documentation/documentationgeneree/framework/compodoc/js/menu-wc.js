'use strict';

customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">maj-angular documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Saisissez un texte"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Démarrage</a>
                    <ul class="links">
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Vue d&#x27;ensemble
                            </a>
                        </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dépendances
                                    </a>
                                </li>
                                <li class="link">
                                    <a href="properties.html" data-type="chapter-link">
                                        <span class="icon ion-ios-apps"></span>Propriétés
                                    </a>
                                </li>
                    </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#components-links"' :
                            'data-bs-target="#xs-components-links"' }>
                            <span class="icon ion-md-cog"></span>
                            <span>Composants</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="components-links"' : 'id="xs-components-links"' }>
                            <li class="link">
                                <a href="components/ConnexionBrouillonDialog.html" data-type="entity-link" >ConnexionBrouillonDialog</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkBlocComponent.html" data-type="entity-link" >FmkBlocComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkBlocDynamiqueComponent.html" data-type="entity-link" >FmkBlocDynamiqueComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkCaptchaComponent.html" data-type="entity-link" >FmkCaptchaComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkConteneurPagesComponent.html" data-type="entity-link" >FmkConteneurPagesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuAdresseFrOuRtrComponent.html" data-type="entity-link" >FmkContenuAdresseFrOuRtrComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuAutocompletionComponent.html" data-type="entity-link" >FmkContenuAutocompletionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuCaseComponent.html" data-type="entity-link" >FmkContenuCaseComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuComponent.html" data-type="entity-link" >FmkContenuComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuContactPersonnelComponent.html" data-type="entity-link" >FmkContenuContactPersonnelComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuDateComponent.html" data-type="entity-link" >FmkContenuDateComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuDocumentsComponent.html" data-type="entity-link" >FmkContenuDocumentsComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuFinDemarcheComponent.html" data-type="entity-link" >FmkContenuFinDemarcheComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuFinDemarcheConnecteComponent.html" data-type="entity-link" >FmkContenuFinDemarcheConnecteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuFinDemarcheNonConnecteComponent.html" data-type="entity-link" >FmkContenuFinDemarcheNonConnecteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuIdentiteComponent.html" data-type="entity-link" >FmkContenuIdentiteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuListeFinieComponent.html" data-type="entity-link" >FmkContenuListeFinieComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuParagrapheComponent.html" data-type="entity-link" >FmkContenuParagrapheComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuRadioComponent.html" data-type="entity-link" >FmkContenuRadioComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuRecapitulatifComponent.html" data-type="entity-link" >FmkContenuRecapitulatifComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuRicheComponent.html" data-type="entity-link" >FmkContenuRicheComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuSaisieComponent.html" data-type="entity-link" >FmkContenuSaisieComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuSaisieLongueComponent.html" data-type="entity-link" >FmkContenuSaisieLongueComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuSimpleComponent.html" data-type="entity-link" >FmkContenuSimpleComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuUploadDocumentComponent.html" data-type="entity-link" >FmkContenuUploadDocumentComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkContenuUtilisateurConnecteComponent.html" data-type="entity-link" >FmkContenuUtilisateurConnecteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkEnteteComponent.html" data-type="entity-link" >FmkEnteteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkFilDarianeComponent.html" data-type="entity-link" >FmkFilDarianeComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkListeContenusRecapitulatifComponent.html" data-type="entity-link" >FmkListeContenusRecapitulatifComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkMessageGlobalComponent.html" data-type="entity-link" >FmkMessageGlobalComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkMessagesValidationComponent.html" data-type="entity-link" >FmkMessagesValidationComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkNavigationComponent.html" data-type="entity-link" >FmkNavigationComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkPageComponent.html" data-type="entity-link" >FmkPageComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/FmkPiedDePageComponent.html" data-type="entity-link" >FmkPiedDePageComponent</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#directives-links"' :
                                'data-bs-target="#xs-directives-links"' }>
                                <span class="icon ion-md-code-working"></span>
                                <span>Directives</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="directives-links"' : 'id="xs-directives-links"' }>
                                <li class="link">
                                    <a href="directives/AbstractComponent.html" data-type="entity-link" >AbstractComponent</a>
                                </li>
                                <li class="link">
                                    <a href="directives/AncreTabulableDirective.html" data-type="entity-link" >AncreTabulableDirective</a>
                                </li>
                                <li class="link">
                                    <a href="directives/FmkApplicationAbstraitComponent.html" data-type="entity-link" >FmkApplicationAbstraitComponent</a>
                                </li>
                                <li class="link">
                                    <a href="directives/FmkContenuAbstraitComponent.html" data-type="entity-link" >FmkContenuAbstraitComponent</a>
                                </li>
                                <li class="link">
                                    <a href="directives/FmkContenuComplexeAbstraitComponent.html" data-type="entity-link" >FmkContenuComplexeAbstraitComponent</a>
                                </li>
                                <li class="link">
                                    <a href="directives/FmkContenuFinDemarcheAbstraitComponent.html" data-type="entity-link" >FmkContenuFinDemarcheAbstraitComponent</a>
                                </li>
                                <li class="link">
                                    <a href="directives/FmkContenuMonoSaisieAbstraitComponent.html" data-type="entity-link" >FmkContenuMonoSaisieAbstraitComponent</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#classes-links"' :
                            'data-bs-target="#xs-classes-links"' }>
                            <span class="icon ion-ios-paper"></span>
                            <span>Classes</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="classes-links"' : 'id="xs-classes-links"' }>
                            <li class="link">
                                <a href="classes/Bloc.html" data-type="entity-link" >Bloc</a>
                            </li>
                            <li class="link">
                                <a href="classes/BrouillonDemarche.html" data-type="entity-link" >BrouillonDemarche</a>
                            </li>
                            <li class="link">
                                <a href="classes/ClientApiService.html" data-type="entity-link" >ClientApiService</a>
                            </li>
                            <li class="link">
                                <a href="classes/ConfigurationDemarche.html" data-type="entity-link" >ConfigurationDemarche</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuAdresseFrOuEtr.html" data-type="entity-link" >ContenuAdresseFrOuEtr</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuAutocompletion.html" data-type="entity-link" >ContenuAutocompletion</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuCase.html" data-type="entity-link" >ContenuCase</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuContactPersonnel.html" data-type="entity-link" >ContenuContactPersonnel</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuDate.html" data-type="entity-link" >ContenuDate</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuDeBloc.html" data-type="entity-link" >ContenuDeBloc</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuDocuments.html" data-type="entity-link" >ContenuDocuments</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuFinDemarche.html" data-type="entity-link" >ContenuFinDemarche</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuIdentite.html" data-type="entity-link" >ContenuIdentite</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuListeFinie.html" data-type="entity-link" >ContenuListeFinie</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuOption.html" data-type="entity-link" >ContenuOption</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuParagraphe.html" data-type="entity-link" >ContenuParagraphe</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuRadio.html" data-type="entity-link" >ContenuRadio</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuRecapitulatif.html" data-type="entity-link" >ContenuRecapitulatif</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuSaisie.html" data-type="entity-link" >ContenuSaisie</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuSaisieComplexe.html" data-type="entity-link" >ContenuSaisieComplexe</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuUploadDocument.html" data-type="entity-link" >ContenuUploadDocument</a>
                            </li>
                            <li class="link">
                                <a href="classes/ContenuUtilisateurConnecte.html" data-type="entity-link" >ContenuUtilisateurConnecte</a>
                            </li>
                            <li class="link">
                                <a href="classes/DocumentsPostSoumission.html" data-type="entity-link" >DocumentsPostSoumission</a>
                            </li>
                            <li class="link">
                                <a href="classes/DonneesDeSoumission.html" data-type="entity-link" >DonneesDeSoumission</a>
                            </li>
                            <li class="link">
                                <a href="classes/DonneesUsager.html" data-type="entity-link" >DonneesUsager</a>
                            </li>
                            <li class="link">
                                <a href="classes/LibelleI18n.html" data-type="entity-link" >LibelleI18n</a>
                            </li>
                            <li class="link">
                                <a href="classes/ListeFonctionnalites.html" data-type="entity-link" >ListeFonctionnalites</a>
                            </li>
                            <li class="link">
                                <a href="classes/MessageAafficher.html" data-type="entity-link" >MessageAafficher</a>
                            </li>
                            <li class="link">
                                <a href="classes/Page.html" data-type="entity-link" >Page</a>
                            </li>
                            <li class="link">
                                <a href="classes/ParametrePointEntree.html" data-type="entity-link" >ParametrePointEntree</a>
                            </li>
                            <li class="link">
                                <a href="classes/PieceJointeAssociee.html" data-type="entity-link" >PieceJointeAssociee</a>
                            </li>
                            <li class="link">
                                <a href="classes/PointEntree.html" data-type="entity-link" >PointEntree</a>
                            </li>
                            <li class="link">
                                <a href="classes/ReponseConnexion.html" data-type="entity-link" >ReponseConnexion</a>
                            </li>
                            <li class="link">
                                <a href="classes/SousBloc.html" data-type="entity-link" >SousBloc</a>
                            </li>
                            <li class="link">
                                <a href="classes/UtilitaireBlocDynamique.html" data-type="entity-link" >UtilitaireBlocDynamique</a>
                            </li>
                            <li class="link">
                                <a href="classes/UtilitaireModel.html" data-type="entity-link" >UtilitaireModel</a>
                            </li>
                            <li class="link">
                                <a href="classes/Validation.html" data-type="entity-link" >Validation</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#injectables-links"' :
                                'data-bs-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/BouchonService.html" data-type="entity-link" >BouchonService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/BrouillonService.html" data-type="entity-link" >BrouillonService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ConfigurationService.html" data-type="entity-link" >ConfigurationService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ContexteService.html" data-type="entity-link" >ContexteService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/DateService.html" data-type="entity-link" >DateService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/DocumentService.html" data-type="entity-link" >DocumentService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/DonneesService.html" data-type="entity-link" >DonneesService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/EnvironnementService.html" data-type="entity-link" >EnvironnementService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/FormulaireService.html" data-type="entity-link" >FormulaireService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/OIDCService.html" data-type="entity-link" >OIDCService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/PieceJointeService.html" data-type="entity-link" >PieceJointeService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ReferentielService.html" data-type="entity-link" >ReferentielService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/SecuriteService.html" data-type="entity-link" >SecuriteService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/SoumissionService.html" data-type="entity-link" >SoumissionService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ValidationService.html" data-type="entity-link" >ValidationService</a>
                                </li>
                            </ul>
                        </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#pipes-links"' :
                                'data-bs-target="#xs-pipes-links"' }>
                                <span class="icon ion-md-add"></span>
                                <span>Pipes</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse " ${ isNormalMode ? 'id="pipes-links"' : 'id="xs-pipes-links"' }>
                                <li class="link">
                                    <a href="pipes/i18nPipeDirective.html" data-type="entity-link" >i18nPipeDirective</a>
                                </li>
                                <li class="link">
                                    <a href="pipes/LibelleDirective.html" data-type="entity-link" >LibelleDirective</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#miscellaneous-links"'
                            : 'data-bs-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Divers</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/enumerations.html" data-type="entity-link">Enumérations</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/functions.html" data-type="entity-link">Fonctions</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Couverture de documentation</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation générée avec <a href="https://compodoc.app/" target="_blank" rel="noopener noreferrer">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});