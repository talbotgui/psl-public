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
import { Component, ElementRef, OnInit, ViewEncapsulation } from '@angular/core';
import { tap } from 'rxjs';
import { MessageAafficher } from '../../../model/message.model';
import { ContexteService } from '../../../services/stateful/contexte.service';
import { AbstractComponent } from '../../../utilitaires/abstract.component';
import { LibelleDirective } from '../../directives/libelle';


/**
 * Ce composant permet d'afficher des messages venant de n'importe quel composant applicatif en passant par le ContexteService.
 * 
 * Historiquement, ce composant n'était présent que dans le composant fmk-entete. Mais il a été placé aussi sous le fil d'Ariane.
 * Les deux instances du composant ne réagisse pas ensemble. Seul l'un affiche les messages et pas l'autre.
 */
@Component({
    selector: 'div[data-fmk-messageglobal]', templateUrl: './fmk.messageglobal.html', encapsulation: ViewEncapsulation.None,
    standalone: true,
    imports: [LibelleDirective]
})
export class FmkMessageGlobalComponent extends AbstractComponent implements OnInit {

    /** Message à afficher */
    public messagesAafficher: MessageAafficher[] = [];

    /** Flag indiquant si la configuration d'une démarche est chargée. */
    private flagConfigurationEstChargee = false;

    /** Flag indiquant si un message doit être affiché en fonction du chargement de la configuration d'une démarche. */
    private flagAfficherMessageSiConfigurationEstChargee = false;

    /** Constructeur pour injection des dépendances. */
    public constructor(private contexte: ContexteService, private elRef: ElementRef) { super(); }

    /** Au chargement du composant */
    public ngOnInit(): void {

        // Si la configuration est chargée, l'instance de composant dans l'entête n'affiche plus de message
        this.flagAfficherMessageSiConfigurationEstChargee = !(this.elRef.nativeElement as HTMLElement).parentElement?.hasAttribute('data-fmk-entete');

        // Surveillance du chargement de la configuration
        const sub1 = this.contexte.obtenirUnObservableDeChangementDePage()
            .pipe(tap(conf => {

                // Récupération de l'état de chargement de la configuration 
                // (ce composant existe en 2 exemplaires à des endroits différents de la page)
                this.flagConfigurationEstChargee = !!conf;

                // Dans tous les cas, on vide les erreurs au changement de page
                this.messagesAafficher = [];
            }))
            .subscribe();
        super.declarerSouscription(sub1);

        // à chaque message généré, on l'affiche en masquant le précédent
        const sub2 = this.contexte.obtenirUnObservableSurLesMessagesGeneraux()
            .pipe(tap(message => {
                // Si le message n'est pas à afficher dans cette instance
                if (this.flagConfigurationEstChargee != this.flagAfficherMessageSiConfigurationEstChargee) {
                    // Sinon, on masque le contenu de ce composant
                    this.messagesAafficher = [];
                    // fin
                    return;
                }
                if (message) {
                    // on retire tout message venant de la même source
                    this.messagesAafficher = this.messagesAafficher.filter(m => m.codeEmetteurDuMessage !== message.codeEmetteurDuMessage);
                    // on ajoute le nouveau message (s'il n'est pas vide)
                    if (message.message) {
                        this.messagesAafficher.push(message);
                    }
                    // pour remettre l'utilisateur en haut de la page
                    window.scrollTo(0, 0);
                }
            }))
            .subscribe();
        super.declarerSouscription(sub2);
    }
}
