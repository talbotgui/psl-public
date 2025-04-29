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
package com.github.talbotgui.psl.socle.dbnotification.client;

import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationTeledossierAvecPremiereNotification;

/**
 * Client d'appel à Service-Public.
 */
public interface SpClient {

	/**
	 * Création d'une notification dans le télé-dossier (déjà présent) du fil d'actualité.
	 * 
	 * @param token   Token d'appel aux APIs SP.
	 * @param requete Le détail de la demande.
	 */
	void creerNouvelleNotificationDansUnTeledossierExistantDejaDansTdbParticulier(String token, RequeteCreationNotification requete);

	/**
	 * Création d'un télé-dossier particulier dans un fil d'actualité.
	 * 
	 * @param token   Token d'appel aux APIs SP.
	 * @param requete Détails de la demande.
	 */
	void creerTeledossierParticulierEtPremiereNotification(String token, RequeteCreationTeledossierAvecPremiereNotification requete);

	/**
	 * Appel à l'API permettant de créer un token.
	 * 
	 * @return Token SP valide
	 */
	String creerTokenOidcTechnique();

}