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
                                <a href="components/AppComponent.html" data-type="entity-link" >AppComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEnteteComponent.html" data-type="entity-link" >AppEnteteComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppMenuComponent.html" data-type="entity-link" >AppMenuComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppPiedDePageComponent.html" data-type="entity-link" >AppPiedDePageComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteAdminComponent.html" data-type="entity-link" >AppRouteAdminComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteAdminDemarchesComponent.html" data-type="entity-link" >AppRouteAdminDemarchesComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteAdminReferentielsComponent.html" data-type="entity-link" >AppRouteAdminReferentielsComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteAdminUtilisateursComponent.html" data-type="entity-link" >AppRouteAdminUtilisateursComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteConnexionComponent.html" data-type="entity-link" >AppRouteConnexionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteDemarcheComponent.html" data-type="entity-link" >AppRouteDemarcheComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteSoumissionComponent.html" data-type="entity-link" >AppRouteSoumissionComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppRouteTransfertComponent.html" data-type="entity-link" >AppRouteTransfertComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/ConfirmationDialog.html" data-type="entity-link" >ConfirmationDialog</a>
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
                                    <a href="directives/AbstractAppRouteDemarcheEtSoumissionComponent.html" data-type="entity-link" >AbstractAppRouteDemarcheEtSoumissionComponent</a>
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
                                <a href="classes/AdminConfigurationPublique.html" data-type="entity-link" >AdminConfigurationPublique</a>
                            </li>
                            <li class="link">
                                <a href="classes/ClientAdminApiService.html" data-type="entity-link" >ClientAdminApiService</a>
                            </li>
                            <li class="link">
                                <a href="classes/TransfertDansRechercheMulticriteres.html" data-type="entity-link" >TransfertDansRechercheMulticriteres</a>
                            </li>
                            <li class="link">
                                <a href="classes/UtilisateurConnecte.html" data-type="entity-link" >UtilisateurConnecte</a>
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
                                    <a href="injectables/AdminBouchonService.html" data-type="entity-link" >AdminBouchonService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AdminContexteService.html" data-type="entity-link" >AdminContexteService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/AdminService.html" data-type="entity-link" >AdminService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/ConfigurationService.html" data-type="entity-link" >ConfigurationService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-bs-toggle="collapse" ${ isNormalMode ? 'data-bs-target="#guards-links"' :
                            'data-bs-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Gardes</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/AuthentificationGarde.html" data-type="entity-link" >AuthentificationGarde</a>
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