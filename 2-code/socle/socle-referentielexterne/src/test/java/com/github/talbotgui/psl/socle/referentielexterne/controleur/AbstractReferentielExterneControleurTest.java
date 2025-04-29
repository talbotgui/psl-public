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
package com.github.talbotgui.psl.socle.referentielexterne.controleur;

import java.util.Collection;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.api.ReferentielExterneAPI;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ReponseBanDto;
import com.github.talbotgui.psl.socle.referentielexterne.apiclient.dto.ResultatBanDto;
import com.github.talbotgui.psl.socle.referentielexterne.client.BanClient;
import com.github.talbotgui.psl.socle.referentielexterne.client.BanClientImpl;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractReferentielExterneControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private BanClient bouchonClient;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private ReferentielExterneControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected ReferentielExterneAPI leClientAutiliser;

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
		Mockito.reset(this.bouchonClient);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
	}

	@Test
	protected void test00Securite() {
		//
		String recherche = "Ham";

		//
		CatchException.catchException(() -> this.leClientAutiliser.rechercherCommuneBan(recherche));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonClient);
	}

	@Test
	void test01RechercherCommuneBan() {
		//
		String recherche = "Ham";
		ReponseBanDto reponseDuClient = BanClientImpl.creerReponseBanVide();
		reponseDuClient.features()
				.add(new ResultatBanDto(null, Map.of("city", "Ham", "citycode", "80400"), "typeQuiDoitDisparaitreAlaSerialisation"));
		Mockito.doReturn(reponseDuClient).when(this.bouchonClient).rechercherCommuneBan(recherche);

		//
		Collection<Map<String, String>> reponseHttp = this.leClientAutiliser.rechercherCommuneBan(recherche);

		//
		Assertions.assertNotNull(reponseHttp);
		Assertions.assertEquals(1, reponseHttp.size());
		Map<String, String> premierResultat = reponseHttp.iterator().next();
		Assertions.assertNotNull(premierResultat.entrySet());
		Assertions.assertEquals(3, premierResultat.entrySet().size());
		Mockito.verify(this.bouchonClient).rechercherCommuneBan(recherche);
		Mockito.verifyNoMoreInteractions(this.bouchonClient);
	}

	@Test
	void test02RechercherAdresseBan01SansCodeInsee() {
		//
		String recherche = "1 rue de";
		ReponseBanDto reponseDuClient = BanClientImpl.creerReponseBanVide();
		reponseDuClient.features().add(new ResultatBanDto(null, Map.of("city", "Ham"), "typeQuiDoitDisparaitreAlaSerialisation"));
		Mockito.doReturn(reponseDuClient).when(this.bouchonClient).rechercherAdresseBan(recherche, null);

		//
		Collection<Map<String, String>> reponseHttp = this.leClientAutiliser.rechercherAdresseBan(recherche, null);

		//
		Assertions.assertNotNull(reponseHttp);
		Assertions.assertEquals(1, reponseHttp.size());
		Map<String, String> premierResultat = reponseHttp.iterator().next();
		Assertions.assertNotNull(premierResultat.entrySet());
		Assertions.assertEquals(1, premierResultat.entrySet().size());
		Mockito.verify(this.bouchonClient).rechercherAdresseBan(recherche, null);
		Mockito.verifyNoMoreInteractions(this.bouchonClient);
	}

	@Test
	void test02RechercherAdresseBan02AvecCodeInsee() {
		//
		String recherche = "1 rue de";
		String codeInsee = "80410";
		ReponseBanDto reponseDuClient = BanClientImpl.creerReponseBanVide();
		reponseDuClient.features().add(new ResultatBanDto(null, Map.of("city", "Ham"), "typeQuiDoitDisparaitreAlaSerialisation"));
		Mockito.doReturn(reponseDuClient).when(this.bouchonClient).rechercherAdresseBan(recherche, codeInsee);

		//
		Collection<Map<String, String>> reponseHttp = this.leClientAutiliser.rechercherAdresseBan(recherche, codeInsee);

		//
		Assertions.assertNotNull(reponseHttp);
		Assertions.assertEquals(1, reponseHttp.size());
		Map<String, String> premierResultat = reponseHttp.iterator().next();
		Assertions.assertNotNull(premierResultat.entrySet());
		Assertions.assertEquals(1, premierResultat.entrySet().size());
		Mockito.verify(this.bouchonClient).rechercherAdresseBan(recherche, codeInsee);
		Mockito.verifyNoMoreInteractions(this.bouchonClient);
	}
}
