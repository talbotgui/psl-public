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
import { Directive, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";

/**
 * Classe abstraite pour tout composant voulant gérer correctement les souscriptions et notamment leur destruction quand le composant Angular est détruit.
 */
@Directive()
export abstract class AbstractComponent implements OnDestroy {

    /** Liste des souscriptions réalisées par le composant concrêt et à détruire */
    private souscriptions: Subscription[] = [];

    /** Déclaration d'une souscription qui sera détruite à la destruction du composant */
    public declarerSouscription(sub: Subscription): void {
        this.souscriptions.push(sub);
    }

    /** Déclaration d'une souscription qui sera détruite à la destruction du composant */
    public declarerSouscriptions(subs: Subscription[]): void {
        subs.forEach(s => this.souscriptions.push(s));
    }

    /** Destruction des souscriptions créées */
    public detruireLesSouscriptions(): void {
        this.souscriptions.forEach(s => s.unsubscribe());
    }

    /** A la destruction du composant, on détruit ses souscriptions */
    public ngOnDestroy(): void {
        this.detruireLesSouscriptions();
    }
}