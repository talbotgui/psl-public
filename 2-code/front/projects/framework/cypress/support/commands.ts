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
/// <reference types="cypress" />

// Import des compléments/plugins de Cypress pour être utilisés dans le code TS des tests Cypress
import "cypress-fail-fast";
import "cypress-html-validate/commands";
// Code présent aussi dans cypress.config.ts
// import '@cypress/code-coverage/support';

// cette syntaxe est imposée par Cypress
// eslint-disable-next-line @typescript-eslint/no-namespace
declare namespace Cypress {
    // cette syntaxe est imposée par Cypress
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    interface Chainable<Subject = any> {
        blocObtenirTitre<E extends HTMLSpanElement>(iPage: number, iBloc: number, iSousBloc: number | undefined): Chainable<JQuery<E>>;
        blocObtenir<E extends HTMLDivElement>(iPage: number, iBloc: number): Chainable<JQuery<E>>;
        blocObtenirOccurence<E extends HTMLDivElement>(iPage: number, iBloc: number, o: number): Chainable<JQuery<E>>;
        contenuObtenirContenu<E extends Node>(iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: any, occurenceDeBlocDynamique: number | undefined): Chainable<JQuery<E>>;
        filarianeObtenirNumero<E extends HTMLElement>(): Chainable<JQuery<E>>;
        navigationObtenirBoutonPrecedent<E extends Node = HTMLElement>(): Chainable<JQuery<E>>;
        navigationObtenirBoutonValider<E extends Node = HTMLElement>(): Chainable<JQuery<E>>;
        navigationObtenirBoutonBrouillon<E extends Node = HTMLElement>(): Chainable<JQuery<E>>;
        pageObtenirTitre<E extends HTMLHeadingElement>(): Chainable<JQuery<E>>;
    }
}

Cypress.Commands.add('blocObtenirTitre', (iPage: number, iBloc: number, iSousBloc: number | undefined)=>{
    if (iSousBloc !== undefined) {
        return cy.get('.fmk-bloc').eq(iBloc).find('.fmk-sbloc').eq(iSousBloc).find('h4').first().find('span').first();
    } else {
        return cy.get('.fmk-bloc').eq(iBloc).find('h3').first().find('span').first();
    }
});
Cypress.Commands.add('blocObtenir', (iPage: number, iBloc: number)=>{
    return cy.get('.fmk-bloc').eq(iBloc);
});
Cypress.Commands.add('blocObtenirOccurence', (iPage: number, iBloc: number, o: number)=>{
    return cy.get('.fieldset-' + o);
});
Cypress.Commands.add('contenuObtenirContenu', (iBloc: number, iSousBloc: number | undefined, iContenu: number, contenu: any, occurenceDeBlocDynamique: number | undefined)=>{
    let bloc = cy.get('.fmk-bloc').eq(iBloc);
    if (iSousBloc !== undefined) {
        bloc = bloc.find('.fmk-sbloc').eq(iSousBloc);
    }
    // recherche du bloc puis du contenu dans le bloc par clef
    if (contenu.clef) {
        return bloc.find('div[data-fmk-contenu' + ('' + contenu.type).toLowerCase() + '][data-clef="' + contenu.clef + '"]');
    } else if (typeof occurenceDeBlocDynamique !== 'undefined') {
        return bloc.find('.data-contenu' + iContenu + '-' + occurenceDeBlocDynamique).find('div[data-fmk-contenu' + contenu.type + ']');
    } else {
        return bloc.find('.data-contenu' + iContenu).find('div[data-fmk-contenu' + contenu.type + ']');
    }
});
Cypress.Commands.add('filarianeObtenirNumero', ()=>{
    return cy.get('span.fr-stepper__state').first();
});
Cypress.Commands.add('navigationObtenirBoutonPrecedent', () => {
    return cy.get('div[data-fmk-navigation]').find('button.btn-prev');
});
Cypress.Commands.add('navigationObtenirBoutonValider', () => {
    return cy.get('div[data-fmk-navigation]').find('button.btn-next');
});
Cypress.Commands.add('navigationObtenirBoutonBrouillon', () => {
    return cy.get('div[data-fmk-navigation]').find('button.btn-save');
});
Cypress.Commands.add('pageObtenirTitre', ()=>{
    return cy.get('.main-body').find('h2').first();
});