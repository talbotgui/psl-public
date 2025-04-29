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
package com.github.talbotgui.psl.socle.referentiel.controleur;

import java.util.Arrays;
import java.util.List;

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
import com.github.talbotgui.psl.socle.referentiel.apiclient.api.ReferentielAPI;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.service.ReferentielService;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;
import com.googlecode.catchexception.CatchException;

/**
 * Cette classe contient les scénarios de test du contrôleur REST. Elle est
 * abstraite car il existe deux façons d'appeler le contrôleur: - le client - le
 * FeignClient
 */
//Activation de Mockito dans ce test qui ne génère qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractReferentielControleurTest extends AbstractTest {

	/** Bouchon injecté. */
	@MockitoBean
	private IndexeurService bouchonIndexeur;

	/** Le client à utiliser pour réaliser l'appel */
	protected ReferentielAPI leClientAutiliser;

	/**
	 * Nombre de résultat par défaut paramétré dans l'application (pour correctement
	 * mocker le service)
	 */
	@Value("${controleur.nbResultatsMax}")
	private int nbMaxResultats;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	private String secret;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private ReferentielService serviceAnePasUtiliser;

	/** Token JWT initialisé au début du test et utilisé tout le long */
	protected String tokenJwtValableDurantLeTest;

	/** Avant chaque méthode de test. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la classe parente
		super.avantChaqueTest(testInfo);
		// Purge de l'indexeur
		this.bouchonIndexeur.viderLesIndexes();
		// Reset du bouchon
		Mockito.reset(this.bouchonIndexeur);
		// Recréation d'un token JWT
		this.tokenJwtValableDurantLeTest = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
	}

	@Test
	protected void test00Securite() {
		//
		String recherche = "Ham";

		//
		CatchException.catchException(() -> this.leClientAutiliser.rechercherCommuneNaissance(recherche));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test01CommuneNaissance() {
		//
		String recherche = "Ham";
		CommuneNaissanceDto attendu1 = new CommuneNaissanceDto("c1", Arrays.asList("cp1"), "l1");
		CommuneNaissanceDto attendu2 = new CommuneNaissanceDto("c2", Arrays.asList("cp2"), "l2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(CommuneNaissanceDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<CommuneNaissanceDto> reponse = this.leClientAutiliser.rechercherCommuneNaissance(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(CommuneNaissanceDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);

	}

	@Test
	void test02PaysNaissance() {
		//
		String recherche = "Fra";
		PaysNaissanceDto attendu1 = new PaysNaissanceDto("c1", "l1");
		PaysNaissanceDto attendu2 = new PaysNaissanceDto("c2", "l2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(PaysNaissanceDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<PaysNaissanceDto> reponse = this.leClientAutiliser.rechercherPaysNaissance(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(PaysNaissanceDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test03CommuneUgle() {
		//
		String recherche = "Ham";
		CommuneUgleDto attendu1 = new CommuneUgleDto("c1", "l1");
		CommuneUgleDto attendu2 = new CommuneUgleDto("c2", "l2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(CommuneUgleDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<CommuneUgleDto> reponse = this.leClientAutiliser.rechercherCommuneUgle(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(CommuneUgleDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test04CommuneActuelle() {
		//
		String recherche = "Ham";
		CommuneActuelleDto attendu1 = new CommuneActuelleDto("c1", "l1", "d1");
		CommuneActuelleDto attendu2 = new CommuneActuelleDto("c2", "l2", "d2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(CommuneActuelleDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<CommuneActuelleDto> reponse = this.leClientAutiliser.rechercherCommunesActuelles(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(CommuneActuelleDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test05RegionActuelle() {
		//
		String recherche = "Ham";
		RegionActuelleDto attendu1 = new RegionActuelleDto("c1", "l1", "d1");
		RegionActuelleDto attendu2 = new RegionActuelleDto("c2", "l2", "d2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(RegionActuelleDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<RegionActuelleDto> reponse = this.leClientAutiliser.rechercherRegionsActuelles(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(RegionActuelleDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test06DepartementActuel() {
		//
		String recherche = "Ham";
		DepartementActuelDto attendu1 = new DepartementActuelDto("c1", "l1", "d1", "");
		DepartementActuelDto attendu2 = new DepartementActuelDto("c2", "l2", "d2", "");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(DepartementActuelDto.class, recherche,
				this.nbMaxResultats, false);
		//
		List<DepartementActuelDto> reponse = this.leClientAutiliser.rechercherDepartementsActuels(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(DepartementActuelDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test07PaysActuel() {
		//
		String recherche = "Ham";
		PaysActuelDto attendu1 = new PaysActuelDto("c1", "l1", "n1");
		PaysActuelDto attendu2 = new PaysActuelDto("c2", "l2", "n2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(PaysActuelDto.class, recherche, this.nbMaxResultats,
				false);
		//
		List<PaysActuelDto> reponse = this.leClientAutiliser.rechercherPaysActuels(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(PaysActuelDto.class, recherche, this.nbMaxResultats, false);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}

	@Test
	void test08NationnalitePaysActuel() {
		//
		String recherche = "Ham";
		PaysActuelDto attendu1 = new PaysActuelDto("c1", "l1", "n1");
		PaysActuelDto attendu2 = new PaysActuelDto("c2", "l2", "n2");
		Mockito.doReturn(Arrays.asList(attendu1, attendu2)).when(this.bouchonIndexeur).rechercher(PaysActuelDto.class, recherche, this.nbMaxResultats,
				true);
		//
		List<PaysActuelDto> reponse = this.leClientAutiliser.rechercherNationalitesDePaysActuel(recherche);
		//
		Assertions.assertNotNull(reponse);
		Assertions.assertEquals(2, reponse.size());
		Mockito.verify(this.bouchonIndexeur).rechercher(PaysActuelDto.class, recherche, this.nbMaxResultats, true);
		Mockito.verifyNoMoreInteractions(this.bouchonIndexeur);
	}
}
