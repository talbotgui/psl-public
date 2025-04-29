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
package com.github.talbotgui.psl.socle.commun.securite;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@TestMethodOrder(MethodOrderer.MethodName.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class JwtSecuriteFilterTest {
	private static final String ENTETE_AUTHORIZATION_INVALIDE = "enteteInvalide";

	private static final String ENTETE_AUTHORIZATION_VALIDE = "enteteValide";
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecuriteFilterTest.class);

	// Pas d'annotation @Autowired donc @Mock et pas @MockBean
	@Mock
	private FilterChain bouchonChaine;

	// Pas d'annotation @Autowired donc @Mock et pas @MockBean
	@Mock
	private JwtService bouchonJwtService;

	// Composant qui se fera injecté les bouchons
	@InjectMocks
	private final JwtSecuriteFilter filtre = new JwtSecuriteFilter();

	@BeforeEach
	void avantChaqueTest() {
		LOGGER.info("**********************");
		Mockito.reset(this.bouchonJwtService, this.bouchonChaine);
	}

	@Test
	void test01SansEntete() throws ServletException, IOException {
		//
		ServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		ServletResponse reponse = new MockHttpServletResponse();
		//
		this.filtre.doFilter(requete, reponse, this.bouchonChaine);
		//
		Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
		Mockito.verify(this.bouchonChaine).doFilter(requete, reponse);
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService, this.bouchonChaine);
	}

	@Test
	void test02AvecEnteteInvalide() throws ServletException, IOException {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		requete.addHeader(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + ENTETE_AUTHORIZATION_INVALIDE);
		ServletResponse reponse = new MockHttpServletResponse();
		Mockito.doReturn(null).when(this.bouchonJwtService).validerToken(ENTETE_AUTHORIZATION_INVALIDE);
		//
		this.filtre.doFilter(requete, reponse, this.bouchonChaine);
		//
		Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
		Mockito.verify(this.bouchonChaine).doFilter(requete, reponse);
		Mockito.verify(this.bouchonJwtService).validerToken(ENTETE_AUTHORIZATION_INVALIDE);
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService, this.bouchonChaine);
	}

	@Test
	void test03AvecEnteteValide() throws ServletException, IOException {
		//
		MockHttpServletRequest requete = new MockHttpServletRequest("GET", "uneUrl");
		String emailUtilisateur = JwtService.EMAIL_UTILISATEUR_ANONYMOUS;
		requete.addHeader(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + ENTETE_AUTHORIZATION_VALIDE);
		ServletResponse reponse = new MockHttpServletResponse();
		Mockito.doReturn(new DefaultClaims(Map.of(Claims.SUBJECT, emailUtilisateur))).when(this.bouchonJwtService)
				.validerToken(ENTETE_AUTHORIZATION_VALIDE);
		//
		this.filtre.doFilter(requete, reponse, this.bouchonChaine);
		//
		Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Mockito.verify(this.bouchonChaine).doFilter(requete, reponse);
		Mockito.verify(this.bouchonJwtService).validerToken(ENTETE_AUTHORIZATION_VALIDE);
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService, this.bouchonChaine);
	}
}
