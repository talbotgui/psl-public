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
package com.github.talbotgui.psl.socle.adminpsl.controleur;

import java.util.Arrays;
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

import com.github.talbotgui.psl.socle.adminpsl.apiclient.api.AdminPslAPI;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.service.ConfigurationInterneService;
import com.github.talbotgui.psl.socle.adminpsl.service.ConfigurationPubliqueService;
import com.github.talbotgui.psl.socle.adminpsl.service.ConfigurationService;
import com.github.talbotgui.psl.socle.adminpsl.service.TransfertService;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractAdminPslControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private ConfigurationInterneService bouchonConfigurationPriveeService;

	/** Bouchon injecté. */
	@MockitoBean
	private ConfigurationPubliqueService bouchonConfigurationPubliqueService;

	/** Bouchon injecté. */
	@MockitoBean
	private ConfigurationService bouchonConfigurationService;

	/** Bouchon injecté. */
	@MockitoBean
	private TransfertService bouchonTransfertService;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private AdminPslControleur controleurAnePasUtiliser;

	/** Le client à utiliser pour réaliser l'appel */
	protected AdminPslAPI leClientAutiliser;

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
		Mockito.reset(this.bouchonConfigurationPubliqueService, this.bouchonConfigurationPriveeService, this.bouchonConfigurationService);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTestDansAdminPsl(this.secret);
	}

	@Test
	protected void test00Securite() {
		//

		//
		CatchException.catchException(() -> this.leClientAutiliser.listerLesDemarches());

		//
		Assertions.assertNotNull(CatchException.caughtException());
	}

	@Test
	void test01Commun01listeDesCodesDeDemarche() {
		//
		Collection<String> codesRetournes = Arrays.asList("code1", "code2");
		Mockito.doReturn(codesRetournes).when(this.bouchonConfigurationPubliqueService).listerLesCodesDeDemarche();

		//
		Collection<String> codes = this.leClientAutiliser.listerLesDemarches();

		//
		Mockito.verify(this.bouchonConfigurationPubliqueService).listerLesCodesDeDemarche();
		Assertions.assertIterableEquals(codesRetournes, codes);
	}

	@Test
	void test02ConfigurationPublique01listeVersions() {
		//
		String codeDemarche = "codedemarche";
		Map<String, String> versionsRetournees = Map.of("id1", "date1", "id2", "date2");
		Mockito.doReturn(versionsRetournees).when(this.bouchonConfigurationService).listerLesVersionsDeConfiguration(true, codeDemarche);
		//
		Map<String, String> versions = this.leClientAutiliser.listerLesVersionsDeConfigurationPublique(codeDemarche);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertIterableEquals(versionsRetournees.entrySet(), versions.entrySet());
	}

	@Test
	void test02ConfigurationPublique02chargement() {
		//
		String codeDemarche = "codedemarche";
		String id = "12345-67890";
		String configurationRetournee = "{}";
		Mockito.doReturn(configurationRetournee).when(this.bouchonConfigurationService).chargerVersionDeConfiguration(true, codeDemarche, id);
		//
		Object configuration = this.leClientAutiliser.chargerVersionDeConfigurationPublique(codeDemarche, id);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertEquals(configurationRetournee, configuration.toString());
	}

	@Test
	void test02ConfigurationPublique03SauvegardePourInsertion() {
		//
		String codeDemarche = "codedemarche";
		String nouvelId = "12345-67890";
		String configurationEnvoyee = "{}";
		Mockito.doReturn(nouvelId).when(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(true, codeDemarche, null,
				configurationEnvoyee, this.tokenJwtValableDurantLeTest);
		//
		String idRecu = this.leClientAutiliser.creerVersionDeConfigurationPublique(codeDemarche, configurationEnvoyee);
		//
		Assertions.assertNotNull(idRecu);
		Assertions.assertEquals(nouvelId, idRecu.replace("\"", ""));
		Mockito.verify(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(true, codeDemarche, null, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
	}

	@Test
	void test02ConfigurationPublique04SauvegardePourModification() {
		//
		String codeDemarche = "codedemarche";
		String id = "12345-67890";
		String configurationEnvoyee = "{}";
		Mockito.doReturn(id).when(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(true, codeDemarche, id, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
		//
		String idRecu = this.leClientAutiliser.modifierVersionDeConfigurationPublique(codeDemarche, id, configurationEnvoyee);
		//
		Assertions.assertNotNull(idRecu);
		Assertions.assertEquals(id, idRecu.replace("\"", ""));
		Mockito.verify(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(true, codeDemarche, id, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
	}

	@Test
	void test03ConfigurationInterne01listeVersions() {
		//
		String codeDemarche = "codedemarche";
		Map<String, String> versionsRetournees = Map.of("id1", "date1", "id2", "date2");
		Mockito.doReturn(versionsRetournees).when(this.bouchonConfigurationService).listerLesVersionsDeConfiguration(false, codeDemarche);
		//
		Map<String, String> versions = this.leClientAutiliser.listerLesVersionsDeConfigurationInterne(codeDemarche);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertIterableEquals(versionsRetournees.entrySet(), versions.entrySet());
	}

	@Test
	void test03ConfigurationInterne02chargement() {
		//
		String codeDemarche = "codedemarche";
		String id = "12345-67890";
		String configurationRetournee = "{}";
		Mockito.doReturn(configurationRetournee).when(this.bouchonConfigurationService).chargerVersionDeConfiguration(false, codeDemarche, id);
		//
		Object configuration = this.leClientAutiliser.chargerVersionDeConfigurationInterne(codeDemarche, id);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertEquals(configurationRetournee, configuration.toString());
	}

	@Test
	void test03ConfigurationInterne03SauvegardePourInsertion() {
		//
		String codeDemarche = "codedemarche";
		String nouvelId = "12345-67890";
		String configurationEnvoyee = "{}";
		Mockito.doReturn(nouvelId).when(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(false, codeDemarche, null,
				configurationEnvoyee, this.tokenJwtValableDurantLeTest);
		//
		String idRecu = this.leClientAutiliser.creerVersionDeConfigurationInterne(codeDemarche, configurationEnvoyee);
		//
		Assertions.assertNotNull(idRecu);
		Assertions.assertEquals(nouvelId, idRecu.replace("\"", ""));
		Mockito.verify(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(false, codeDemarche, null, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
	}

	@Test
	void test03ConfigurationInterne04SauvegardePourModification() {
		//
		String codeDemarche = "codedemarche";
		String id = "12345-67890";
		String configurationEnvoyee = "{}";
		Mockito.doReturn(id).when(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(false, codeDemarche, id, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
		//
		String idRecu = this.leClientAutiliser.modifierVersionDeConfigurationInterne(codeDemarche, id, configurationEnvoyee);
		//
		Assertions.assertNotNull(idRecu);
		Assertions.assertEquals(id, idRecu.replace("\"", ""));
		Mockito.verify(this.bouchonConfigurationService).sauvegarderVersionDeConfiguration(false, codeDemarche, id, configurationEnvoyee,
				this.tokenJwtValableDurantLeTest);
	}

	@Test
	void test04genererDesTeledossiers() {
		//
		int nb = 10;
		Mockito.doNothing().when(this.bouchonTransfertService).genererDesTeledossiers(nb);
		//
		this.leClientAutiliser.genererDesTeledossiers(nb);
		//
		Mockito.verify(this.bouchonTransfertService).genererDesTeledossiers(10);
	}

	@Test
	void test05rechercherDesTeledossiers() {
		//
		String numeroTeledossier = "DEM-1234-AZER-CVBN";
		RequeteRechercheTransfertsDto requete = new RequeteRechercheTransfertsDto(Arrays.asList("a", "b"), null, null, numeroTeledossier, 0, 5);
		Page<ResultatRechercheTransfertsDto> page = new Page<>(0, 5);
		Mockito.doReturn(page).when(this.bouchonTransfertService).rechercherDesTeledossiers(Mockito.any());
		//
		Page<ResultatRechercheTransfertsDto> pageRetournee = this.leClientAutiliser.rechercherDesTeledossiers(requete);
		//
		Assertions.assertNotNull(pageRetournee);
		Mockito.verify(this.bouchonTransfertService).rechercherDesTeledossiers(Mockito.any());
	}

	@Test
	void test06listerDemarche() {
		//
		Mockito.doReturn(Arrays.asList("d1", "d2")).when(this.bouchonConfigurationPubliqueService).listerLesCodesDeDemarche();
		//
		Collection<String> codes = this.leClientAutiliser.listerLesDemarches();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertEquals(2, codes.size());
		Mockito.verify(this.bouchonConfigurationPubliqueService).listerLesCodesDeDemarche();
	}

	@Test
	void test07CreerNouvelleDemarche() {
		//
		String codeDemarche = "codedemarche";
		Mockito.doNothing().when(this.bouchonConfigurationService).creerNouvelleDemarche(Mockito.eq(codeDemarche), Mockito.any());
		//
		this.leClientAutiliser.creerNouvelleDemarche(codeDemarche);
		//
		Mockito.verify(this.bouchonConfigurationService).creerNouvelleDemarche(Mockito.eq(codeDemarche), Mockito.any());
	}
}
