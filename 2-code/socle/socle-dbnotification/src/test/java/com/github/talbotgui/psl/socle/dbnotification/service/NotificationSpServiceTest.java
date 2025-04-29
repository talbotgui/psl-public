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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.DemandeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.application.SocleDbnotificationApplication;
import com.github.talbotgui.psl.socle.dbnotification.client.NotificationObjectMother;

/**
 * Test du service de notification.
 */
@SpringBootTest(classes = SocleDbnotificationApplication.class)
class NotificationSpServiceTest extends AbstractTest {

	@Autowired
	private NotificationSpService notificationSpService;

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void testCreerOuModifierNotificationPourTeledossier01casNominal() {
		//
		// Les token sont à créer depuis la collection Postman (99-Systèmes externes/01-SP/CréationNotification/01)
		String accessToken = "ey...";
		String refreshToken = "ey...";
		String numeroTeledossier = NotificationObjectMother.genererNumeroTeledossier();
		String statut = NotificationAPI.STATUT_NOTIFICATION_BROUILLON;
		DemandeNotificationSpDto demande = NotificationObjectMother.creerDemandeNotificationSpDeBrouillonDemarcheParticuliere(accessToken,
				refreshToken, numeroTeledossier, statut);
		//
		this.notificationSpService.creerOuModifierNotificationPourTeledossier(demande);
		//
		// Vérification manuelle
	}

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void testCreerOuModifierNotificationPourTeledossier02casDunTeledossierDejaExistant() {
		//
		// Les token sont à créer depuis la collection Postman (99-Systèmes externes/01-SP/CréationNotification/01)
		String accessToken = "ey...";
		String refreshToken = "ey...";
		String numeroTeledossier = NotificationObjectMother.genererNumeroTeledossier();
		String statut = NotificationAPI.STATUT_NOTIFICATION_BROUILLON;
		DemandeNotificationSpDto demande = NotificationObjectMother.creerDemandeNotificationSpDeBrouillonDemarcheParticuliere(accessToken,
				refreshToken, numeroTeledossier, statut);
		this.notificationSpService.creerOuModifierNotificationPourTeledossier(demande);
		demande = NotificationObjectMother.creerDemandeNotificationSpDeBrouillonDemarcheParticuliere(accessToken, refreshToken, numeroTeledossier,
				statut);
		demande.setMessageAafficher(demande.getMessageAafficher() + "(bis)");
		//
		this.notificationSpService.creerOuModifierNotificationPourTeledossier(demande);
		//
		// Vérification manuelle
	}
}
