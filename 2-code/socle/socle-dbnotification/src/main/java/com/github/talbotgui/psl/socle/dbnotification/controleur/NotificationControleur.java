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
package com.github.talbotgui.psl.socle.dbnotification.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;
import com.github.talbotgui.psl.socle.dbnotification.service.NotificationService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class NotificationControleur implements NotificationAPI {

	@Autowired
	private NotificationService service;

	@Override
	public StatistiquesNotificationDto calculerStatistiques() {
		return this.service.calculerStatistiques();
	}

	@Override
	public String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message) {
		return "\"" + this.service.sauvegarderNotificationEmail(message) + "\"";
	}

	@Override
	public String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message) {
		return "\"" + this.service.sauvegarderNotificationSp(message) + "\"";
	}

	@Override
	public void supprimerNotificationAvantTraitement(String idNotification) {
		this.service.supprimerNotificationAvantTraitement(idNotification);
	}
}