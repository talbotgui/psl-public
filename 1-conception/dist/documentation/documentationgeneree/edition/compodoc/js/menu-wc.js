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
                                <a href="components/AppEditeurComponent.html" data-type="entity-link" >AppEditeurComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEditeurJsonComponent.html" data-type="entity-link" >AppEditeurJsonComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEditeurNiveau1Component.html" data-type="entity-link" >AppEditeurNiveau1Component</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEditeurNiveau2Component.html" data-type="entity-link" >AppEditeurNiveau2Component</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEditeurNiveau3Component.html" data-type="entity-link" >AppEditeurNiveau3Component</a>
                            </li>
                            <li class="link">
                                <a href="components/AppEditeurNiveau4Component.html" data-type="entity-link" >AppEditeurNiveau4Component</a>
                            </li>
                            <li class="link">
                                <a href="components/AppSaisieValeurDeListeFinieComponent.html" data-type="entity-link" >AppSaisieValeurDeListeFinieComponent</a>
                            </li>
                            <li class="link">
                                <a href="components/AppSelecteurTypeContenuComponent.html" data-type="entity-link" >AppSelecteurTypeContenuComponent</a>
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