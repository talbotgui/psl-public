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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MessageAafficher } from '../../../framework/src/lib/model/message.model';
import { AbstractComponent } from '../../../framework/src/lib/utilitaires/abstract.component';
import { AdminContexteService } from './service/adminContexte.service';
import { AppPiedDePageComponent } from './structure/pieddepage/app.pieddepage';

import { AppEnteteComponent } from './structure/entete/app.entete';
import { AppMenuComponent } from './structure/menu/app.menu';

@Component({
  selector: 'app-root', templateUrl: './app.component.html', encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [AppEnteteComponent, AppMenuComponent, RouterOutlet, AppPiedDePageComponent]
})
export class AppComponent extends AbstractComponent implements OnInit {

  /** Constructeur pour injection de dépendance. */
  public constructor(private adminContexte: AdminContexteService) { super(); }

  /** Dernier message reçu */
  public dernierMessageAfficher: MessageAafficher | undefined;

  /** A l'initialisation */
  public ngOnInit(): void {

    // Abonnement aux messages généraux pour être affichés
    const sub = this.adminContexte.obtenirUnObservableSurLesMessagesGeneraux().subscribe(message => {

      // Affichage du message
      this.dernierMessageAfficher = message;

      // Masquage après 10 secondes
      setTimeout(() => this.dernierMessageAfficher = undefined, 10000);
    });
    super.declarerSouscription(sub);
  }
}
