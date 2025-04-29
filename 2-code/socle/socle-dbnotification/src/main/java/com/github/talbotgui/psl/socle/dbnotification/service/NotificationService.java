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
package com.github.talbotgui.psl.socle.dbnotification.service;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;

public interface NotificationService {

	/**
	 * Calcul des données statistiques du micro-service.
	 * 
	 * @return Stats
	 */
	StatistiquesNotificationDto calculerStatistiques();

	/**
	 * Méthode recherchant une notification SP ou email à traiter.
	 */
	void rechercherEtTraiterUneNotification();

	/**
	 * Sauvegarde de la demande de notification Email.
	 * 
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message);

	/**
	 * Sauvegarde de la demande de notification SP.
	 * 
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message);

	/**
	 * Suppression d'une demande de notification tant qu'elle n'est pas envoyee.
	 * 
	 * @param idNotification Identifiant de la demande.
	 */
	void supprimerNotificationAvantTraitement(String idNotification);

}