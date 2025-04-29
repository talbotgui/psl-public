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
package com.github.talbotgui.psl.socle.soumission.controleur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.CaptchaAPI;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;
import com.github.talbotgui.psl.socle.soumission.service.captcha.CaptchaService;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractCaptchaControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private CaptchaService bouchonServiceCaptcha;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés
	 * L'usage de @Autowired avec la classe et non l'interface est nécessaire pour
	 * Mockito. A ne pas reproduire pour tout autre usage.
	 */
	@Autowired
	private CaptchaControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected CaptchaAPI leClientAutiliser;

	/** Pour récupérer le port dynamique sur lequel est démarré le serveur */
	@LocalServerPort
	private int port;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	private String secret;

	/** Token JWT initialisé au début du test et utilisé tout le long */
	protected String tokenJwtValableDurantLeTest;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la classe parente
		super.avantChaqueTest(testInfo);
		// Reset du bouchon
		Mockito.reset(this.bouchonServiceCaptcha);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
	}

	@Test
	void test00Securite01() {
		//

		//
		CatchException.catchException(() -> this.leClientAutiliser.obtenirHtmlDuCaptcha());

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonServiceCaptcha);
	}

	@Test
	void test01Captcha01Html() {
		//
		ReponseRessourceCaptchaAife reponseClient = new ReponseRessourceCaptchaAife("".getBytes(), "");
		Mockito.doReturn(reponseClient).when(this.bouchonServiceCaptcha).obtenirContenuDuCaptcha(null, null, null);
		//
		ResponseEntity<byte[]> reponse = this.leClientAutiliser.obtenirHtmlDuCaptcha();
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonServiceCaptcha).obtenirContenuDuCaptcha(null, null, null);
		Mockito.verifyNoMoreInteractions(this.bouchonServiceCaptcha);
	}

	@Test
	void test01Captcha02ressource() {
		//
		String paramGet = "get";
		String paramC = "c";
		String paramT = "123";
		ReponseRessourceCaptchaAife reponseClient = new ReponseRessourceCaptchaAife("".getBytes(), "");
		Mockito.doReturn(reponseClient).when(this.bouchonServiceCaptcha).obtenirContenuDuCaptcha(paramGet, paramC, paramT);
		//
		ResponseEntity<byte[]> reponse = this.leClientAutiliser.obtenirContenuDuCaptcha(paramGet, paramC, paramT);
		//
		Assertions.assertNotNull(reponse);
		Mockito.verify(this.bouchonServiceCaptcha).obtenirContenuDuCaptcha(paramGet, paramC, paramT);
		Mockito.verifyNoMoreInteractions(this.bouchonServiceCaptcha);
	}
}
