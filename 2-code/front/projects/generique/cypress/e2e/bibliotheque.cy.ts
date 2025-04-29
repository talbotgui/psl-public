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
import { GlobalTestUtils } from '../../../framework/cypress/utils/globaltest-utils';

// Pour intégrer l'audit RGAA
import 'cypress-axe';

describe('CasNominal', { testIsolation: false }, () => {

  // Instance du testeur
  const instanceDeTesteur = new GlobalTestUtils();

  // Avant même l'exécution du premier test
  before(() => {
    instanceDeTesteur.chargerConfigurationEtBrouillonPuisDefinirLesScenariosAtester('bibliotheque', '/bouchonapi/bibliotheque-param.json', ['/bouchonapi/bibliotheque-brouillon.json']);
  });

  // Comme il n'est pas possible, en dehors d'un IT(), d'accéder aux données chargées dans le BEFORE()
  // Boucle de 150 éléments pour couvrir le nbScenarios x nbPages
  // (c'est une limitation du test à augmenter le jour où une démarche le dépasse)
  Cypress._.range(0, 150).forEach((i) => {
    it(`Partie #${i}`, () => {
      instanceDeTesteur.executerUnePartieDesTests(i);
    });
  });
});