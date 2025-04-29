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
// Depuis les tests CYPRESS, les imports doivent se faire directement sur le fichier et non sur le 'public-api'


/** Classe utilitaire regroupant les méthodes de gestion de la console */
export class ConsoleUtils {

    /** Mise en place des espions de la console. */
    public static installerEspionConsole(): void {
        cy.window().then((win) => {
          cy.spy(win.console, 'error').as('spyWinConsoleError');
        });
    }

    /** Controle du nombre d'appels à la console */
    public static verifierAbsenceAppelConsole(): void {
        cy.window().then((win) => {
            // Pas plus d'un appel à console.error
            cy.get('@spyWinConsoleError').its('callCount').then(nbAppel => {
                if (nbAppel>1) {
                    expect(nbAppel).to.eq(0);
                }
              });
        });
    }
}
