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
package com.github.talbotgui.psl.socle.commun.securite;

/** Interface de service pour réaliser des appels internes entre micro-service quand aucun token d'usager n'est en jeu. */
public interface JwtServicePourAppelsInternes {

	/**
	 * Génération d'un token 'anonyme' et sa sauvegarde dans le context de sécurité Spring pour être utilisé par les appels Feign.
	 * 
	 * /!\ Attention, ceci n'est pas compatible avec un token récupéré suite à un appel d'API exposée sur le réseau.
	 */
	void genererEtSauvegarderNouveauTokenInterne();
}
