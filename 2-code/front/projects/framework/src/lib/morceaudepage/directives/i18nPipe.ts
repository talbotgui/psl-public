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
import { Pipe, PipeTransform } from "@angular/core";
import { i18n } from "../../conf/i18n";
import { ContexteService } from "../../services/stateful/contexte.service";

/**
 * Pipe permettant de traiter les libellés dans les pages HTML.
 * 
 * L'attribut 'pure:false' force le recalcul de chaque libellé à chaque interaction de l'usager avec l'écran.
 * Attention aux performances
 */
@Pipe({
    name: 'i18n', pure: false,
    standalone: true
})
export class i18nPipeDirective implements PipeTransform {

    /** Constructeur pour injection des dépendances */
    constructor(private contexte: ContexteService) { }

    /** Transformation réalisée par ce PIPE */
    public transform(value: any): string {
        const clefLibelle = value as string;
        const libelleTrouve = (i18n.libellesPageHtml as any)[clefLibelle];
        if (libelleTrouve) {
            return libelleTrouve.get(this.contexte.langue);
        } else {
            return '!!libellé indisponible pour la clef \'' + clefLibelle + '\'!!';
        }
    }
}