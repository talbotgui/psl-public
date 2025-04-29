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
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";

/** Composant contenant la configuration chargée au démarrage de l'application depuis le fichier env.json */
@Injectable({ providedIn: 'root' })
export class EnvironnementService {

  public urls = {
    liensServicePublic: "",
    lienConnexionServicePublic: "",
    socle: {
      gateway: "",
      referentielBouchonne: false,
      soumissionBouchonne: false,
      securiteBouchonne: true,
      documentBouchonne: true,
      urlBrouillonBouchonne: "",
      urlConfigurationBouchonne: "",
      adminpslBouchonnee: false,
      adminpsl: "",
      modeAuthentificationRepriseBrouillon: ""
    }
  }

  /** Constructeur avec injection des dépendances */
  constructor(private httpClient: HttpClient) { }

  /** Chargement de la configuration JSON et copie de toutes les données dans le service (d'où le statefull) */
  public chargerEnvironnement(): Observable<any> {
    return this.httpClient.get("env.json").pipe(
      tap((data: any) => {
        this.urls = data.urls;
      })
    );
  }

}