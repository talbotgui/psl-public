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
package com.github.talbotgui.psl.socle.referentielexterne.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ReponseBanDto;
import com.github.talbotgui.psl.socle.referentielexterne.application.SocleReferentielExterneApplication;

@SpringBootTest(classes = SocleReferentielExterneApplication.class)
class BanClientTest extends AbstractTest {

	@Autowired
	private BanClient client;

	@Test
	void test01InterrogerCommuneBan() {
		//
		//
		ReponseBanDto reponse = this.client.rechercherCommuneBan("ham");
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotNull(reponse.features());
		Assertions.assertEquals(10, reponse.features().size());
	}

	@Test
	void test02InterrogerAdresseBan01SansCodeInsee() {
		//
		//
		ReponseBanDto reponse = this.client.rechercherAdresseBan("1 rue d", null);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotNull(reponse.features());
		Assertions.assertTrue(reponse.features().size() >= 13, "Attendu >=13 et obtenu " + reponse.features().size());
	}

	@Test
	void test02InterrogerAdresseBan02AvecCodeInsee() {
		//
		//
		ReponseBanDto reponse = this.client.rechercherAdresseBan("1 rue de", "80410");
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotNull(reponse.features());
		Assertions.assertTrue(reponse.features().size() >= 18, "Attendu >=19 obtenu " + reponse.features().size());
	}

	@Test
	void test02InterrogerAdresseBan03AvecUnAccent() {
		//
		//
		ReponseBanDto reponse = this.client.rechercherAdresseBan("1 rue de Pé", "80410");
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertNotNull(reponse.features());
		Assertions.assertTrue(reponse.features().size() >= 18, "Attendu >=19 obtenu " + reponse.features().size());
	}

}
