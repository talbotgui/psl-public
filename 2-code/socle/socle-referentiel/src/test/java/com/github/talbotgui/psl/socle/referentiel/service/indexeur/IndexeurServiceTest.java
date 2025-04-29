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
package com.github.talbotgui.psl.socle.referentiel.service.indexeur;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ITransformableEnDocumentLuceneDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifique.properties" })
@SuppressWarnings("unchecked")
class IndexeurServiceTest extends AbstractTest {

	private static final List<ITransformableEnDocumentLuceneDto> OBJETS_A_INDEXER_PAYS = Arrays.asList(new PaysNaissanceDto("FR", "France"),
			new CommuneNaissanceDto("USA", "Etats-Unis"));

	private static final List<ITransformableEnDocumentLuceneDto> OBJETS_A_INDEXER_VILLES = Arrays.asList(
			new CommuneNaissanceDto("80001", Arrays.asList("800021"), "Y"), new CommuneNaissanceDto("80400", Arrays.asList("80401"), "Ham"),
			new CommuneNaissanceDto("80000", Arrays.asList("80001"), "Amiens"));

	@Autowired
	private IndexeurService indexeur;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		super.avantChaqueTest(testInfo);
		this.indexeur.viderLesIndexes();
	}

	@Test
	void test01Indexation02Multiple() {
		//
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
	}

	@Test
	void test01Indexations01Unique() {
		//
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		//
	}

	@Test
	void test02Recherche01Vide() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsChaineVide = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "", 10,
				false);
		//
		Assertions.assertEquals(OBJETS_A_INDEXER_VILLES.size(), resultatsChaineVide.size());
	}

	@Test
	void test02Recherche02Ham() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsHam = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "Ham", 10,
				false);
		//
		Assertions.assertEquals(1, resultatsHam.size());
	}

	@Test
	void test02Recherche03Am() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsAm = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "aM", 10,
				false);
		//
		Assertions.assertEquals(2, resultatsAm.size());
	}

	@Test
	void test02Recherche04A() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsA = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "a", 10, false);
		//
		Assertions.assertEquals(2, resultatsA.size());
	}

	@Test
	void test02Recherche05PlusieursMots() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsA = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "Ha 8040", 10,
				false);
		//
		Assertions.assertEquals(1, resultatsA.size());
	}

	@Test
	void test02Recherche05PlusieursMotsEtPlusieursResultats() {
		//
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_VILLES);
		this.indexeur.initialiserIndex(OBJETS_A_INDEXER_PAYS);
		//
		List<CommuneNaissanceDto> resultatsA = (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, "A 80", 10,
				false);
		//
		Assertions.assertEquals(2, resultatsA.size());
	}

}
