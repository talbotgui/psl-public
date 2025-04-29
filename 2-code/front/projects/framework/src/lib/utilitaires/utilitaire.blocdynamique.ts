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
import { DonneesService } from '../services/stateful/donnees.service';

/**
 * Cette classe fournit exclusivement des méthodes statiques de lecture/écriture/manipulation pour les blocs dynamiques.
 * 
 * Aucun appel à un composant externe n'est autorisé !
 */
export class UtilitaireBlocDynamique {

    /** Calcul d'une clef de contenu dupliqué */
    public static remplacerNumeroOccurenceDansUneClefDeContenu(chaine: string | undefined, occurence: number): string | undefined {
        // Si l'occurence contient un appel de fonction
        if (DonneesService.LISTE_FONCTIONS_DISPONIBLES.find(f => chaine?.includes(f + '(\''))) {
            return chaine;
        }
        // remplacement des @@ par l'occurence
        return chaine?.replace(/@@/g, ((occurence < 10) ? '0' : '') + occurence);
    }


}