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


/** Classe utilitaire regroupant les méthodes de log */
export class LogUtils {
    /** Nom du fichier de log de la session en cours */
    private static nomFichier: string;

    /** Création du fichier de log de cette session pour la démarche 
     * @param nomUnique Nom unique à utiliser pendant cette session de test.
     */
    public static creerFichier(nomUnique: string): void {
        // Sauvegarde du code de la démarche
        LogUtils.nomFichier = './cypress/' + nomUnique + '.log';
        cy.writeFile(LogUtils.nomFichier, '');
    }

    /** La méthode écrit la ligne dans le fichier de session en cours. */
    private static ecrireDansFichier(prefixe: string, message: string): void {
        // Ajout du message
        const messageComplet = prefixe + ' ' + message.replaceAll('\r', '\\r').replaceAll('\n', '\\n') + '\n';

        // Ecriture
        cy.writeFile(LogUtils.nomFichier, messageComplet, { flag: 'a+' });
    }

    public static log(message: string): void {
        LogUtils.ecrireDansFichier('', message);
    }
    public static logPage(iPage: number | undefined, message: string): void {
        LogUtils.ecrireDansFichier(' p' + iPage, message);
    }
    public static logBloc(iPage: number | undefined, iBloc: number | undefined, message: string): void {
        LogUtils.ecrireDansFichier(' p' + iPage + ' b' + iBloc, message);
    }
    public static logSousBloc(iPage: number | undefined, iBloc: number | undefined, iSousBloc: number | undefined, message: string): void {
        LogUtils.ecrireDansFichier(' p' + iPage + ' b' + iBloc + (iSousBloc ? (' sb' + iSousBloc) : ''), message);
    }
    public static logContenu(iPage: number | undefined, iBloc: number | undefined, iSousBloc: number | undefined, iContenu: number | undefined, message: string): void {
        LogUtils.ecrireDansFichier(' p' + iPage + ' b' + iBloc + (iSousBloc ? (' sb' + iSousBloc) : '') + ' c' + iContenu, message);
    }
}
