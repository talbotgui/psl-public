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

import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.application.SocleDbnotificationApplication;
import com.github.talbotgui.psl.socle.dbnotification.client.EmailClient;

/**
 * Test d'intégration à ne pas exécuter durant un build maven.
 */
@SpringBootTest(classes = SocleDbnotificationApplication.class)
class EmailServiceImplTest extends AbstractTest {

	@Autowired
	private EmailClient emailService;

	@Test
	@Disabled("Ce test ne peut être exécuté que manuellement")
	void testEnvoyerEmail01mailTextuelSansPieceJointe() {
		//
		//
		this.emailService.envoyerEmail(
				new MessageSauvegardeNotificationEmailDto("contenu", false, Arrays.asList("toto@email.com"), null, null, "objet du mail", null));
		//
		// Vérification manuelle à faire dans Smtp4dev (http://localhost:5000)
	}
}
