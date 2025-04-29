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
package com.github.talbotgui.psl.socle.referentiel.apiclient.dto;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReferentielGeographiqueDtoTest {

	@Test
	void test01ajouterLesSiretsDeCommune01CodesExactes() {
		//
		ReferentielGeographiqueDto dto = new ReferentielGeographiqueDto();
		dto.getCommunesActuelles().put("INSEE", new CommuneActuelleDto("INSEE", "libelle", "INSEEDEPARTEMENT"));
		dto.getCommunesActuelles().get("INSEE").setCodesPostaux(Arrays.asList("codePostal"));
		List<InformationSiretDto> sirets = Arrays.asList(new InformationSiretDto("siret", "nom", "codePostal", "INSEE"));
		//
		dto.ajouterLesSiretsDeCommune(sirets);
		//
		Assertions.assertEquals("siret", dto.getCommunesActuelles().get("INSEE").getSiret());
	}

	@Test
	void test01ajouterLesSiretsDeCommune02CodesApprochantEtNom() {
		//
		ReferentielGeographiqueDto dto = new ReferentielGeographiqueDto();
		dto.getCommunesActuelles().put("INSEE", new CommuneActuelleDto("INSEE", "BORDEAUX", "INSEEDEPARTEMENT"));
		dto.getCommunesActuelles().get("INSEE").setCodesPostaux(Arrays.asList("33100"));
		List<InformationSiretDto> sirets = Arrays.asList(new InformationSiretDto("siret", "BORDeAUX", "33000", "INSEE"));
		//
		dto.ajouterLesSiretsDeCommune(sirets);
		//
		Assertions.assertEquals("siret", dto.getCommunesActuelles().get("INSEE").getSiret());
	}
}
