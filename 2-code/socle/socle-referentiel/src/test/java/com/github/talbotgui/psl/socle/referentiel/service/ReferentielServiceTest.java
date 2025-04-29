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
package com.github.talbotgui.psl.socle.referentiel.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifique.properties" })
class ReferentielServiceTest extends AbstractTest {

	@Autowired
	private InitialisationReferentielService initialisationService;

	@Autowired
	private ReferentielService referentielService;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
	}

	@Test
	void test01InitialisationDesIndexes() {
		// rien à préparer
		//
		this.initialisationService.initialiserLesIndexes();
		// rien à vérifier si ce n'est que rien n'a planté
	}

	@Test
	void test02RechercherCommuneNaissance() {
		// rien à préparer
		//
		List<CommuneNaissanceDto> resultats = this.referentielService.rechercherCommuneNaissance("Amie");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test03RechercherPaysNaissance() {
		// rien à préparer
		//
		List<PaysNaissanceDto> resultats = this.referentielService.rechercherPaysNaissance("Fra");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test04RechercherCommuneUgle() {
		// rien à préparer
		//
		List<CommuneUgleDto> resultats = this.referentielService.rechercherCommuneUgle("Am");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test05RechercherCommuneActuelle() {
		// rien à préparer
		//
		List<CommuneActuelleDto> resultats = this.referentielService.rechercherCommunesActuelles("Am");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test06RechercherPaysActuel() {
		// rien à préparer
		//
		List<PaysActuelDto> resultats = this.referentielService.rechercherPaysActuels("Fra");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test07RechercherNationnalitesDePaysActuel() {
		// rien à préparer
		//
		List<PaysActuelDto> resultats = this.referentielService.rechercherNationalitesDePaysActuel("Fra");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test08RechercherRegionActuelle() {
		// rien à préparer
		//
		List<RegionActuelleDto> resultats = this.referentielService.rechercherRegionsActuelles("Hau");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

	@Test
	void test09RechercherDepartementActuel() {
		// rien à préparer
		//
		List<DepartementActuelDto> resultats = this.referentielService.rechercherDepartementsActuels("Cher");
		//
		Assertions.assertNotNull(resultats);
		Assertions.assertNotEquals(0, resultats.size());
	}

}
