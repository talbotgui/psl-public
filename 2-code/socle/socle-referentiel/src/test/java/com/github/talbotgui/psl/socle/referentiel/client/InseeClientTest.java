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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ReferentielGeographiqueDto;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifique.properties" })
class InseeClientTest extends AbstractTest {

	@Autowired
	private InseeClient client;

	@Test
	void testReferentielGeographique() {
		//
		//
		ReferentielGeographiqueDto dto = this.client.telechargerLeReferentielGeographique();
		//
		Assertions.assertNotNull(dto);
		Assertions.assertTrue(dto.getCommunesActuelles().size() >= 34000, "Manque de communes");
		Assertions.assertTrue(dto.getDepartementsActuels().size() >= 100, "Manque de départements");
		Assertions.assertTrue(dto.getRegionsActuelles().size() >= 15, "Manque de régions");
		Assertions.assertTrue(dto.getPaysActuels().size() >= 150, "Manque de pays");
	}
}
