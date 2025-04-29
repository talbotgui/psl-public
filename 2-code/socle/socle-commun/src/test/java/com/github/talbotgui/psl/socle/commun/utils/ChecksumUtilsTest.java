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
package com.github.talbotgui.psl.socle.commun.utils;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ChecksumUtilsTest {

	@Test
	void test01CreerChecksumMd5() {
		//
		//
		String md5 = ChecksumUtils.creerChecksumMd5("coucou".getBytes());
		//
		Assertions.assertEquals("721A9B52BFCEACC503C056E3B9B93CFA", md5);
	}

	@Test
	void test02CreerChecksumSha256casPassant() {
		//
		//
		String sha256 = ChecksumUtils.creerChecksumSha256(new File("./src/test/resources/testChecksumSha256"));
		//
		Assertions.assertEquals("c62a687da1b54b6bd92c02a12b8cb9b4f7663a95288be349cc51807a82589707", sha256);
	}

	@Test
	void test03CreerChecksumSha256casKo() {
		//
		//
		String sha256 = ChecksumUtils.creerChecksumSha256(new File("./src/test/resources/fichierPasPresent"));
		//
		Assertions.assertNull(sha256);
	}

}
