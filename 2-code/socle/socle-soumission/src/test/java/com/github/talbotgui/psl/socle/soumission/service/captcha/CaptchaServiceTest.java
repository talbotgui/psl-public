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
package com.github.talbotgui.psl.socle.soumission.service.captcha;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.soumission.application.SocleSoumissionApplication;
import com.github.talbotgui.psl.socle.soumission.client.CaptchaAifeClient;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;

@SpringBootTest(classes = SocleSoumissionApplication.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest extends AbstractTest {

	/** Bouchon du client d'appel à l'API Document présent dans le service. */
	@MockitoBean
	private CaptchaAifeClient bouchonClientCaptcha;

	/** Paramètres pour obtenir le code HTML du catcha */
	@Value("${captchaaife.parametrespourobtenirlehtml}")
	private String parametresPourObtenirLeHtml;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	private String secret;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private CaptchaService service;

	/** URL du socle PSL permettant d'obtenir le captcha. */
	@Value("${captchaaife.urlpubliqueapplicatif}")
	private String urlPubliqueApplicatif;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		// Reset du bouchon
		Mockito.reset(this.bouchonClientCaptcha);
		// Token pour le test
		String tokenPourLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
		// Insertion du token JWT dans la sécurité Spring pour qu'il soit transmis dans
		// l'appel via le client Feign (@see
		// com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter)
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("unTest", tokenPourLeTest));
	}

	@Test
	void testObtenirContenuDuCaptcha01casHtml() {
		//
		String contenuHtml = CaptchaService.CAPTCHA_URL_PAR_DEFAUT;
		ReponseRessourceCaptchaAife reponseClient = new ReponseRessourceCaptchaAife(contenuHtml.getBytes(), "entete");
		Mockito.doReturn(reponseClient).when(this.bouchonClientCaptcha).obtenirContenuDuCaptcha(this.parametresPourObtenirLeHtml);
		//
		ReponseRessourceCaptchaAife reponse = this.service.obtenirContenuDuCaptcha(null, null, null);
		//
		Assertions.assertNotNull(reponse);
		String contenu = new String(reponse.contenu());
		Assertions.assertTrue(contenu.startsWith(this.urlPubliqueApplicatif + JwtSecuriteFilter.PARAMETRE_CLEF + "="));
		Assertions.assertTrue(contenu.endsWith("&"));
	}

}
