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
package com.github.talbotgui.psl.socle.securite.controleur;

import java.util.Map;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.securite.apiclient.SecuriteClient;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteAuthentificationMotDePasseDto;
import com.github.talbotgui.psl.socle.securite.application.SocleSecuriteApplication;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;
import com.github.talbotgui.psl.socle.securite.service.OidcService;
import com.googlecode.catchexception.CatchException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

// Configuration du test
@SpringBootTest(classes = SocleSecuriteApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
//Activation de Mockito dans ce test qui ne génère qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@DirtiesContext
class SecuriteControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private JwtService bouchonJwtService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private OidcService oidcServiceAnePasUtiliser;

	/** Pour récupérer le port dynamique sur lequel est démarré le serveur */
	@LocalServerPort
	private int port;

	/**
	 * Le client permettant l'appel au controleur (pas le client Feign qui est testé
	 * ailleurs mais le client utilisable en externe)
	 */
	private SecuriteClient securiteClient;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		Mockito.reset(this.bouchonJwtService);
		this.securiteClient = new SecuriteClient(null, "http://localhost:" + this.port, false);
		// Pas d'appel à ajouterOuRemplacerEnteteJwt car cette API doit être non
		// sécurisée
		// securiteClient.ajouterOuRemplacerEnteteJwt(JwtPourLesTestsUtils.genererTokenJwtPourUnTest(secret));
	}

	@Test
	void test01ConnexionAnonyme() {
		//
		String token = "token";
		Mockito.doReturn(token).when(this.bouchonJwtService).genererNouveauToken(Mockito.any());
		//
		ReponseJwtDto reponse = this.securiteClient.sauthentifierEnAnonyme();
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(token, reponse.token());
		Mockito.verify(this.bouchonJwtService).genererNouveauToken(JwtService.USER_ANONYME);
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService);
	}

	@Test
	void test02chargerInformationUtilisateur01anonyme() {
		//
		Claims claims = new DefaultClaims(Map.of(Claims.SUBJECT, JwtService.EMAIL_UTILISATEUR_ANONYMOUS));
		Mockito.doReturn(claims).when(this.bouchonJwtService).validerToken(null);
		//
		InformationSpUsagerDto reponse = this.securiteClient.chargerInformationUtilisateur();
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(JwtService.EMAIL_UTILISATEUR_ANONYMOUS, reponse.getEmail());
		Mockito.verify(this.bouchonJwtService).validerToken(null);
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService);
	}

	@Test
	void test03creerTokenOidc() {
		// Fonctionnalité non testable sans une usine à gaz
	}

	@Test
	void test04ConnexionMotDePasse01ok() {
		//
		String nomUtilisatrur = "nomUtilisateur@test.com";
		String motDePasse = "motDePasse1*";
		String token = "token";
		Mockito.doReturn(token).when(this.bouchonJwtService).genererNouveauToken(Mockito.any());
		//
		RequeteAuthentificationMotDePasseDto demande = new RequeteAuthentificationMotDePasseDto(nomUtilisatrur, motDePasse);
		ReponseJwtDto reponse = this.securiteClient.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(demande);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(token, reponse.token());
		Mockito.verify(this.bouchonJwtService).genererNouveauToken(Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonJwtService);
	}

	@Test
	void test04ConnexionMotDePasse02ko() {
		//
		//
		RequeteAuthentificationMotDePasseDto demande = new RequeteAuthentificationMotDePasseDto(null, null);
		CatchException.catchException(() -> this.securiteClient.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(demande));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ApiClientException.equals(CatchException.caughtException(), ApiClientException.ERREUR_CODE_RETOUR),
				CatchException.caughtException().getMessage());

		Mockito.verifyNoMoreInteractions(this.bouchonJwtService);
	}
}
