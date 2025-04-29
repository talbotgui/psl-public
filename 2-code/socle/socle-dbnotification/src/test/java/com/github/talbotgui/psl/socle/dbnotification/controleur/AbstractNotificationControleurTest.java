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
package com.github.talbotgui.psl.socle.dbnotification.controleur;

import java.util.Arrays;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.feign.PslFeignException;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;
import com.github.talbotgui.psl.socle.dbnotification.client.NotificationObjectMother;
import com.github.talbotgui.psl.socle.dbnotification.service.NotificationService;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractNotificationControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private NotificationService bouchonService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private NotificationControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected NotificationAPI leClientAutiliser;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	protected String secret;

	/** Token JWT initialisé au début du test et utilisé tout le long */
	protected String tokenJwtValableDurantLeTest;

	/** Avant chaque méthode de test. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la classe parente
		super.avantChaqueTest(testInfo);
		// Reset du bouchon
		Mockito.reset(this.bouchonService);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
	}

	@Test
	void test00GestionErreur() {
		//
		AbstractException e = new ApiClientException(ApiClientException.ERREUR_APPEL);
		Mockito.doThrow(e).when(this.bouchonService).sauvegarderNotificationEmail(Mockito.any());
		MessageSauvegardeNotificationEmailDto message = new MessageSauvegardeNotificationEmailDto("contenu", false, Arrays.asList("toto@email.com"),
				null, null, "objet du mail", null);

		//
		CatchException.catchException(() -> this.leClientAutiliser.sauvegarderNotificationEmail(message));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verify(this.bouchonService).sauvegarderNotificationEmail(Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	protected void test00Securite() {
		//
		MessageSauvegardeNotificationEmailDto message = new MessageSauvegardeNotificationEmailDto("contenu", false, Arrays.asList("toto@email.com"),
				null, null, "objet du mail", null);
		//
		CatchException.catchException(() -> this.leClientAutiliser.sauvegarderNotificationEmail(message));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test01SauvegarderNotificationEmail() {
		//
		MessageSauvegardeNotificationEmailDto message = new MessageSauvegardeNotificationEmailDto("contenu", false, Arrays.asList("toto@email.com"),
				null, null, "objet du mail", null);
		String idMongo = "idMongo";
		Mockito.doReturn(idMongo).when(this.bouchonService).sauvegarderNotificationEmail(Mockito.any());
		//
		String reponse = this.leClientAutiliser.sauvegarderNotificationEmail(message);
		//
		Assertions.assertEquals(idMongo, reponse.replace("\"", ""));
		Mockito.verify(this.bouchonService).sauvegarderNotificationEmail(Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test02SauvegarderNotificationSp() {
		//
		MessageSauvegardeNotificationSpDto message = new MessageSauvegardeNotificationSpDto("", NotificationObjectMother.CODE_DEMARCHE, null, null,
				NotificationObjectMother.LIBELLE_DEMARCHE, NotificationObjectMother.MESSAGE, 45, NotificationObjectMother.genererNumeroTeledossier(),
				"", NotificationAPI.STATUT_NOTIFICATION_BROUILLON, null, null, null, null);
		String idMongo = "idMongo";
		Mockito.doReturn(idMongo).when(this.bouchonService).sauvegarderNotificationSp(Mockito.any());
		//
		String reponse = this.leClientAutiliser.sauvegarderNotificationSp(message);
		//
		Assertions.assertEquals(idMongo, reponse.replace("\"", ""));
		Mockito.verify(this.bouchonService).sauvegarderNotificationSp(Mockito.any());
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test03SupprimerNotification() {
		//
		String idMongo = "12345-67890";
		Mockito.doNothing().when(this.bouchonService).supprimerNotificationAvantTraitement(idMongo);
		//
		this.leClientAutiliser.supprimerNotificationAvantTraitement(idMongo);
		//
		Mockito.verify(this.bouchonService).supprimerNotificationAvantTraitement(idMongo);
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}

	@Test
	void test04calculerStatistiques() {
		//
		//
		CatchException.catchException(() -> this.leClientAutiliser.calculerStatistiques());
		//
		Assertions.assertNotNull(CatchException.caughtException());
		if (this.leClientAutiliser.getClass().getSimpleName().startsWith("$")) {
			// client Feign renvoyant une 401
			Assertions.assertEquals(PslFeignException.class, CatchException.caughtException().getClass());
		} else {
			// client Java renvoyant une NotificationException
			Assertions.assertTrue(NotificationException.equals(CatchException.caughtException(), NotificationException.API_ADMIN_NON_DISPONIBLE),
					CatchException.caughtException().getMessage());
		}
		Mockito.verifyNoMoreInteractions(this.bouchonService);
	}
}
