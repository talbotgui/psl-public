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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.client.NotificationObjectMother;

/**
 * Classe de génération de jeux de données
 */
public class ObjectMother {

	public static MessageSauvegardeNotificationEmailDto creerNotificationEmail() {
		// Création DTO
		MessageSauvegardeNotificationEmailDto messageEmail = new MessageSauvegardeNotificationEmailDto("contenu", false,
				Arrays.asList("toto@email.com"), null, null, "objet du mail", null);

		// Antidatage de la date de création
		Date maintenantMoins10secondes = Date.from(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
		messageEmail.setDateCreation(maintenantMoins10secondes);

		return messageEmail;
	}

	public static MessageSauvegardeNotificationSpDto creerNotificationSpAvecToken() {
		// Création DTO
		MessageSauvegardeNotificationSpDto message = new MessageSauvegardeNotificationSpDto("token", NotificationObjectMother.CODE_DEMARCHE, null,
				null, NotificationObjectMother.LIBELLE_DEMARCHE, NotificationObjectMother.MESSAGE, 45,
				NotificationObjectMother.genererNumeroTeledossier(), "token", NotificationAPI.STATUT_NOTIFICATION_BROUILLON, null, null, null, null);

		// Antidatage de la date de création
		Date maintenantMoins20secondes = Date.from(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant());
		message.setDateCreation(maintenantMoins20secondes);

		return message;
	}

	public static MessageSauvegardeNotificationSpDto creerNotificationSpAvecUUID() {
		// Création DTO
		MessageSauvegardeNotificationSpDto message = new MessageSauvegardeNotificationSpDto("", NotificationObjectMother.CODE_DEMARCHE, null, null,
				NotificationObjectMother.LIBELLE_DEMARCHE, NotificationObjectMother.MESSAGE, 45, NotificationObjectMother.genererNumeroTeledossier(),
				"", NotificationAPI.STATUT_NOTIFICATION_BROUILLON, null, null, null, null);

		// Antidatage de la date de création
		Date maintenantMoins20secondes = Date.from(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant());
		message.setDateCreation(maintenantMoins20secondes);

		return message;
	}
}
