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
package com.github.talbotgui.psl.socle.soumission.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.soumission.application.SocleSoumissionApplication;
import com.github.talbotgui.psl.socle.soumission.client.dto.ReponseRessourceCaptchaAife;
import com.github.talbotgui.psl.socle.soumission.service.captcha.CaptchaService;
import com.google.common.io.Files;

//Configuration du test
@SpringBootTest(classes = SocleSoumissionApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class CaptchaAifeClientTest extends AbstractTest {

	/** Clef de désactivation du captcha AIFE. */
	@Value("${captchaaife.desactive:false}")
	private boolean captchaAifeDesactive;

	@Autowired
	private CaptchaAifeClient client;

	/** Paramètres pour obtenir le code HTML du catcha */
	@Value("${captchaaife.parametrespourobtenirlehtml}")
	private String parametresPourObtenirLeHtml;

	@Value("${captchaaife.urlpubliqueapplicatif}")
	private String urlPubliqueApplicatif;

	@Test
	void test01GenererToken01appelSimple() {
		//
		this.client.purgerToken();
		//
		String tokenGenere = this.client.genererToken();
		//
		Assertions.assertNotNull(tokenGenere);
	}

	@Test
	void test01GenererToken02appelAvecCache() {
		//
		this.client.purgerToken();
		//
		String tokenGenere1 = this.client.genererToken();
		String tokenGenere2 = this.client.genererToken();
		//
		Assertions.assertNotNull(tokenGenere1);
		Assertions.assertNotNull(tokenGenere2);
		Assertions.assertEquals(tokenGenere1, tokenGenere2);
	}

	@Test
	void test02purgerToken() {
		//
		this.client.purgerToken();
		//
		String tokenGenere1 = this.client.genererToken();
		this.client.purgerToken();
		String tokenGenere2 = this.client.genererToken();
		//
		Assertions.assertNotNull(tokenGenere1);
		Assertions.assertNotNull(tokenGenere2);
		Assertions.assertNotEquals(tokenGenere1, tokenGenere2);
	}

	/**
	 * Ce test ne respecte pas les règles standards mais simule les appels successifs à l'API.
	 *
	 * @throws IOException
	 */
	@Test
	void test03genererLesRessources() throws IOException {

		// Appel au captcha pour générer le code HTML
		ReponseRessourceCaptchaAife reponse = this.client.obtenirContenuDuCaptcha(this.parametresPourObtenirLeHtml);

		// Extraction des requêtes suivantes à réaliser
		List<String> listeParametres = new ArrayList<>(
				Arrays.asList(new String(reponse.contenu()).split(CaptchaService.CAPTCHA_URL_PAR_DEFAUT)))//
				.stream().skip(1).map(s -> StringEscapeUtils.unescapeHtml(s.substring(1, s.indexOf("\"")))).toList();

		// Appels des ressources
		for (int i = 0; i < listeParametres.size(); i++) {
			String parametre = listeParametres.get(i);
			String idCaptcha = parametre.substring(parametre.lastIndexOf("=") + 1);
			Files.write(this.client.obtenirContenuDuCaptcha(parametre).contenu(), new File("./target/" + idCaptcha + "-" + i));
		}

		// Contrôles
		Assertions.assertNotNull(reponse.enteteContentType());
		Assertions.assertTrue(this.captchaAifeDesactive || !listeParametres.isEmpty());
	}

	@Test
	void test04validerValeurSaisieKo() {
		//
		String idCaptchaGenere = "9ee28233a5dd49ea8881f76bfd1e5959";
		String valeurCaptchaSaisie = "mauvaiseValeure";
		//
		boolean donneeValide = this.client.validerDonneeSaisie(valeurCaptchaSaisie, idCaptchaGenere);
		//
		Assertions.assertFalse(donneeValide);
	}

	@Test
	// Ce test ne peut être exécuté que manuellement
	// Et une seule fois : une fois validée, le captcha n'est plus utilisable
	@Disabled
	void test05validerValeurSaisieOk() {
		// si le test03 est bien passé, une image existe dans le répertoire TARGET
		// le nom de l'image contient l'ID du captcha
		// le contenu de l'image contient la valeur du captcha
		String idCaptchaGenere = "263a4f56ac97422590a619a6c72dc99e";
		String valeurCaptchaSaisie = "HYEAPS";
		//
		boolean donneeValide = this.client.validerDonneeSaisie(valeurCaptchaSaisie, idCaptchaGenere);
		//
		Assertions.assertTrue(donneeValide);
	}
}
