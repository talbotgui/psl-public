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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.dbnotification.application.SocleDbnotificationApplication;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationTeledossierAvecPremiereNotification;

/**
 * Test d'intégration à ne pas exécuter durant un build maven.
 */
@SpringBootTest(classes = SocleDbnotificationApplication.class)
class SpClientTest extends AbstractTest {

	@Autowired
	private SpClient client;

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void test01particulier01casNominal() {

		// AccessToken à obtenir depuis l'application Front Angular (dans la console) ou depuis Postman
		String accessToken = "ey...";

		// Création du télé-dossier
		RequeteCreationTeledossierAvecPremiereNotification requete = NotificationObjectMother.creerRequeteCreationTeledossierAvecPremiereNotification();
		this.client.creerTeledossierParticulierEtPremiereNotification(accessToken, requete);

		// Création d'une seconde notification dans le télé-dossier
		RequeteCreationNotification requete2 = NotificationObjectMother.creerRequeteSecondeNotification(requete.id());
		this.client.creerNouvelleNotificationDansUnTeledossierExistantDejaDansTdbParticulier(accessToken, requete2);
	}
}
