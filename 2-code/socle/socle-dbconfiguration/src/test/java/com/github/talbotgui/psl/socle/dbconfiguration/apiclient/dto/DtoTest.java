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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.ValidationEnum;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DtoTest {

	public static final File FICHIER_JSON_TEST_1 = new File("../../front/projects/generique/public/bouchonapi/bibliotheque-param.json");

	public static final File FICHIER_JSON_TEST_2 = new File("../../front/projects/specifique/public/bouchonapi/param.json");

	@Test
	void test01ParseConfiguration01() throws IOException {
		//
		//
		final ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(FICHIER_JSON_TEST_1,
				ConfigurationPubliqueDemarcheDto.class);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertEquals(11, configuration.getPages().size());
		Assertions.assertEquals(ValidationEnum.required.toString(), configuration//
				.getPages().get(2).blocs().get(1).getContenus().get(0).getValidationsSimples().get(0));
		Assertions.assertEquals(ValidationEnum.required.toString(),
				configuration.getPages().get(4).blocs().get(0).getContenus().get(0).getValidationsComplexes().get("civilite").get(0));
	}

	@Test
	void test01ParseConfiguration02() throws IOException {
		//
		//
		final ConfigurationPubliqueDemarcheDto configuration = new ObjectMapper().readValue(FICHIER_JSON_TEST_2,
				ConfigurationPubliqueDemarcheDto.class);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertEquals(7, configuration.getPages().size());
	}
}
