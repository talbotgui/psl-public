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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ExceptionHandlerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerTest.class);

	/** Instance à tester. */
	private final ExceptionHandler handler = new ExceptionHandler();

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
	}

	@Test
	void testGererErreurGenerique01npe01langueParDefaut() {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		Exception e = new NullPointerException();
		//
		ResponseEntity<Object> reponse = this.handler.gererErreurGenerique(requete, e);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(500, reponse.getStatusCode().value());
		Assertions.assertNotNull(reponse.getBody());
		Assertions.assertEquals(String.class, reponse.getBody().getClass());
		Assertions.assertEquals("{\"status\":500,\"error\":\"Erreur inconnue\",\"message\":\"Erreur inconnue\"}", reponse.getBody());
	}

	@Test
	void testGererErreurGenerique01npe02langueEN() {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		requete.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-en");
		Exception e = new NullPointerException();
		//
		ResponseEntity<Object> reponse = this.handler.gererErreurGenerique(requete, e);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(500, reponse.getStatusCode().value());
		Assertions.assertNotNull(reponse.getBody());
		Assertions.assertEquals(String.class, reponse.getBody().getClass());
		Assertions.assertEquals("{\"status\":500,\"error\":\"Unknown error\",\"message\":\"Unknown error\"}", reponse.getBody());
	}

	@Test
	void testGererErreurGenerique02communExceptionAvecParametre01langueParDefaut() {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		AbstractException e = new CommunException(CommunException.ERREUR_VALIDATION_DONNEES, "toto");
		//
		ResponseEntity<Object> reponse = this.handler.gererErreurGenerique(requete, e);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(e.getId().getCodeHttp(), reponse.getStatusCode().value());
		Assertions.assertNotNull(reponse.getBody());
		Assertions.assertEquals(String.class, reponse.getBody().getClass());
		Assertions.assertTrue(reponse.getBody().toString().startsWith(
				"{\"type\":\"psl\",\"status\":400,\"error\":\"Les donnees envoyees ne sont pas valides\",\"requestId\":\"null\",\"timestamp\":"),
				"mauvais message car reçu : " + reponse.getBody().toString());
	}

	@Test
	void testGererErreurGenerique02communExceptionAvecParametre02langueEN() {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		requete.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-en");
		AbstractException e = new CommunException(CommunException.ERREUR_VALIDATION_DONNEES, "toto");
		//
		ResponseEntity<Object> reponse = this.handler.gererErreurGenerique(requete, e);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(e.getId().getCodeHttp(), reponse.getStatusCode().value());
		Assertions.assertNotNull(reponse.getBody());
		Assertions.assertEquals(String.class, reponse.getBody().getClass());
		Assertions.assertTrue(
				reponse.getBody().toString()
						.startsWith("{\"type\":\"psl\",\"status\":400,\"error\":\"Invalid sent data\",\"requestId\":\"null\",\"timestamp\":"),
				"mauvais message car reçu : " + reponse.getBody().toString());
	}

}
