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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifique.properties" })
class InseeOidcClientTest extends AbstractTest {

	@Autowired
	private InseeOidcClient client;

	@Test
	void testCommunesNaissance() {
		//
		//
		List<CommuneNaissanceDto> liste = this.client.telechargerLesCommunesDeNaissance(this.client.genererToken());
		//
		Assertions.assertNotNull(liste);
		Assertions.assertEquals(41899, liste.size());
	}

	@Test
	void testCommunesUgle() {
		//
		//
		List<CommuneUgleDto> liste = this.client.telechargerLesCommunesUgle(this.client.genererToken());
		//
		Assertions.assertNotNull(liste);
		Assertions.assertTrue(liste.size() >= 10);
	}

	@Test
	void testPaysNaissance() {
		//
		//
		List<PaysNaissanceDto> liste = this.client.telechargerLesPaysDeNaissance(this.client.genererToken());
		//
		Assertions.assertNotNull(liste);
		Assertions.assertTrue(liste.size() >= 10);
	}
}
