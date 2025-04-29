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
package com.github.talbotgui.psl.socle.commun.exception;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.MethodName.class)
class AbstractExceptionTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExceptionTest.class);

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
	}

	@Test
	void test01EqualsExceptionExceptionId01Ko() {
		//
		PourLesTestsException e = new PourLesTestsException(PourLesTestsException.ERREUR_DE_TEST);

		//
		Assertions.assertFalse(AbstractException.equals(e, PourLesTestsException.ERREUR_DE_TEST_SIMPLE));
	}

	@Test
	void test01EqualsExceptionExceptionId02Ok() {
		//
		PourLesTestsException e = new PourLesTestsException(PourLesTestsException.ERREUR_DE_TEST);

		//
		Assertions.assertTrue(AbstractException.equals(e, PourLesTestsException.ERREUR_DE_TEST));
	}

	@Test
	void test02GetMessage() {
		//
		Exception cause = new InvalidParameterException();
		String[] array = new String[] { "a", "b", "c", };
		Serializable[] params = new Serializable[] { "toto", array, (Serializable) Arrays.asList(array) };

		//
		PourLesTestsException e = new PourLesTestsException(PourLesTestsException.ERREUR_DE_TEST, cause, params);

		//
		Assertions.assertEquals("Message avec une  String 'toto', un array '[a, b, c]' et une collection '[a, b, c]'", e.getMessage());
	}

	@Test
	void test03PourLesTestsExceptionExceptionIdArray() {

		//
		Exception cause = new InvalidParameterException();
		String[] array = { "a", "b", "c", };
		Serializable[] params = { "toto", array, (Serializable) Arrays.asList(array) };

		//
		PourLesTestsException e = new PourLesTestsException(PourLesTestsException.ERREUR_DE_TEST, cause, params);

		//
		Assertions.assertEquals(cause, e.getCause());
		Assertions.assertEquals(Arrays.asList(params), Arrays.asList(e.getParametres()));
		Assertions.assertEquals(PourLesTestsException.ERREUR_DE_TEST, e.getId());
	}

}
