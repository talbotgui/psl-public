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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.MethodName.class)
class LogUtilsTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogUtilsTest.class);

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
	}

	@Test
	void test01ChaineIntacte01nimporteQuoi() {
		String chaine = "sdkéàjsdfklsjdflak [ , - _";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertEquals(chaineNettoyee, chaine);
	}

	@Test
	void test01ChaineIntacte02URL() {
		String chaine = "https://www.google.com/?search=toto&param2=ti-_ti1";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertEquals(chaineNettoyee, chaine);
	}

	@Test
	void test01ChaineIntacte03JSON() {
		String chaine = "{\"att1\":\"v\",\"att2\":\"v2\"}";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertEquals(chaineNettoyee, chaine);
	}

	@Test
	void test01ChaineIntacte04XML() {
		String chaine = "<xml><att1>v</att1><att2>v2</att2></xml>";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertEquals(chaineNettoyee, chaine);
	}

	@Test
	void test02ChaineModifiee01PourCaractereInterdit() {
		String chaine = "%sdkéàjsdfklsjdflak [ , - _";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertNotEquals(chaineNettoyee, chaine);
	}

	@Test
	void test02ChaineModifiee02PourDoublePoints() {
		String chaine = "..sdkéàjsdfklsjdflak [ , - _";
		String chaineNettoyee = LogUtils.nettoyerDonneesAvantDeLogguer(chaine);
		Assertions.assertNotEquals(chaineNettoyee, chaine);
	}

}
