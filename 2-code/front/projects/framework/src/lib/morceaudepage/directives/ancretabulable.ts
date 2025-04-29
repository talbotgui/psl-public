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
import { Directive, ElementRef, HostListener, Renderer2 } from "@angular/core";

/**
 * Directive permettant de faire de toutes les balises <a> des éléments tabulables.
 *
 * Si l'ancre ne doit pas être tabulable, il suffit d'ajout l'attribut notab.
 */
@Directive({
    selector: "a:not([href])",
    standalone: true
})
export class AncreTabulableDirective {

    /** A l'application de la directive, */
    constructor(el: ElementRef, renderer: Renderer2) {
        // ajout du tabindex pour permettre la tabulation sur l'élément
        renderer.setAttribute(el.nativeElement, 'tabindex', '0');
        // ajout du role BOUTON pour bien signifier que ce n'est pas un lien vers l'exterieur
        renderer.setAttribute(el.nativeElement, 'role', 'button');
    }

    /** A la frappe sur ESPACE. */
    @HostListener('keydown.space', ['$event']) aLaFrappeSurEspace(e: KeyboardEvent) {
        e.preventDefault();
        e.stopPropagation();
        (e.target as HTMLElement).click();
    }

    /** A la frappe sur ENTRER. */
    @HostListener('keydown.enter', ['$event']) aLaFrappeSurEntrer(e: KeyboardEvent) {
        this.aLaFrappeSurEspace(e);
    }
}
