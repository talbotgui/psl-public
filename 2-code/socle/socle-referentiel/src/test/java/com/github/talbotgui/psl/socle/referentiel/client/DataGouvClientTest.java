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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.AdresseEtHorairesGendarmerieDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.InformationSiretDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ProtectionDeCommuneDto;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifique.properties" })
class DataGouvClientTest extends AbstractTest {

	@Autowired
	private DataGouvClient client;

	@Test
	void testAdressesGendarmeries() {
		//
		//
		Map<String, AdresseEtHorairesGendarmerieDto> codes = this.client.telechargerAdressesGendarmeries();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertTrue(codes.size() > 3400);
	}

	@Test
	void testCodesPostaux() {
		//
		//
		Map<String, List<String>> codes = this.client.telechargerCodesPostaux();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertTrue(codes.size() > 35000);
	}

	@Test
	void testProtections() {
		//
		//
		Map<String, ProtectionDeCommuneDto> codes = this.client.telechargerProtectionsDeCommune();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertTrue(codes.size() > 34000);
	}

	@Test
	void testSirets() {
		//
		//
		Collection<InformationSiretDto> sirets = this.client.telechargerSirets();
		//
		Assertions.assertNotNull(sirets);
		Assertions.assertTrue(sirets.size() > 34000, "Attendu >34000 mais obtenu " + sirets.size());
	}

}
