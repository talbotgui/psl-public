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
package com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.MethodName.class)
class DtoTest {

	static final File FICHIER_JSON_TEST_1 = new File("../../front/projects/generique/public/bouchonapi/etatcivil-brouillon.json");

	@Test
	void test01Parse() throws IOException {
		//
		//
		final BrouillonDto brouillon = new ObjectMapper().readValue(FICHIER_JSON_TEST_1, BrouillonDto.class);
		//
		Assertions.assertNotNull(brouillon);
		Assertions.assertNotNull(brouillon.getCodeDemarche());
		Assertions.assertNotNull(brouillon.getVersionConfiguration());
		Assertions.assertNotNull(brouillon.getIndexPage());
		Assertions.assertNotNull(brouillon.getDonnees());
		Assertions.assertNotEquals(0, brouillon.getDonnees().size());
	}
}
