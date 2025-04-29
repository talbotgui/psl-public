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
import { DomSanitizer } from "@angular/platform-browser";

/**
 * Pipe permettant de modifier le contenu de la balise disposant de l'attribut "libelle" et d'en échaper le HTML s'il y en a.
 */
@Pipe({
    name: "libelle",
    standalone: true
})
export class LibelleDirective implements PipeTransform {

    /** Constructeur pour injection des dépendances */
    constructor(private domSanitizer: DomSanitizer) { }

    transform(value: any) {
        const texte = value as string;
        if (texte && texte.indexOf(">") !== -1 && texte.indexOf("<") !== -1) {
            return this.domSanitizer.bypassSecurityTrustHtml(texte);
        } else {
            return texte;
        }
    }
}