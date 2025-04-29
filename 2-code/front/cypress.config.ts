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
import { defineConfig } from "cypress";

const webpackPreprocessor = require('@cypress/webpack-batteries-included-preprocessor');

export default defineConfig({
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'projects/framework/cypress/fixtures',
  scrollBehavior: 'center',
  // Pour permettre de tester l'application sur un nom de domaine et pouvoir être redirigé sur SP pour l'authentification
  chromeWebSecurity: false,
  // Pour moins générer d'OutOfMemory (@see https://stackoverflow.com/questions/51630143/cypress-long-automation-script-crashes-the-chrome-browser-with-aw-snap-error)
  // Réduction du nombre de screenshots générés par Cypress (par défaut 50 - @see https://docs.cypress.io/guides/references/configuration#Global)
  numTestsKeptInMemory: 0,
  e2e: {
    baseUrl: 'https://dev-psl.guillaumetalbot.com/mademarche/',
    specPattern: 'projects/**/cypress/e2e/**/*.cy.ts',
    supportFile: 'projects/framework/cypress/support/commands.ts',
    setupNodeEvents(on: any, config: any) {

      // Customisation de webpack pour faire fonctionner la compilation des tests avec le code TS utilisant le code applicatif
      // @see https://github.com/cypress-io/cypress-watch-preprocessor
      // @see https://github.com/cypress-io/cypress/issues/19066
      const webpackOptions = webpackPreprocessor.defaultOptions.webpackOptions;
      webpackOptions.module.rules.unshift({
        test: /[/\\]@angular[/\\].+\.m?js$/,
        resolve: {
          fullySpecified: false,
        },
        use: {
          loader: 'babel-loader',
          options: {
            plugins: ['@angular/compiler-cli/linker/babel'],
            compact: false,
            cacheDirectory: true
          }
        }
      });
      on('file:preprocessor', webpackPreprocessor({
        webpackOptions: webpackOptions,
        typescript: require.resolve('typescript')
      }));

      // Couverture de code (@see https://docs.cypress.io/guides/tooling/code-coverage#Install-the-plugin) en plus du reste du code du bug https://github.com/cypress-io/cypress/issues/19066
      // Code présent aussi dans commands.ts
      // require('@cypress/code-coverage/task')(on, config);

      // Import de failFast (@see https://github.com/javierbrea/cypress-fail-fast)
      require('cypress-fail-fast/plugin')(on, config);

      // Validation HTML des pages 
      // @see https://www.npmjs.com/package/cypress-html-validate
      require('cypress-html-validate/plugin').install(on,
        {
          rules: {
            // Le titre de page peut être aussi long que nécessaire
            // @see https://html-validate.org/rules/long-title.html
            'long-title': 'off',
            // L'attribut 'integrity' sur les balises SCRIPT n'est pas disponible sur la commande 'ng serve' mais la fonctionnalité est active
            // @see https://html-validate.org/rules/require-sri.html
            'require-sri': 'off'
          }
        },
        {
          exclude: [
            // Exclusion de la DIV des autocompletion qui utilise un role="listbox" enfreignant la règle prefer-native-element
            // @see https://html-validate.org/rules/prefer-native-element.html
            '.mat-mdc-autocomplete-panel',
            // Exclusion de la DIV de progression des uploads de fichier qui utilise un role="progressbar" enfreignant la règle prefer-native-element
            // @see https://html-validate.org/rules/prefer-native-element.html
            '.mat-mdc-progress-bar', 'mat-progress-bar',
            // Exclusion du fieldset des blocs dynamique enfreignant la règle wcag/h71
            // @see https://html-validate.org/rules/wcag/h71.html
            '.fmk-blocdynamiquefieldset'
          ]
        }
      );

      // renvoi de la configuration enrichie
      return config;
    }
  }
});
