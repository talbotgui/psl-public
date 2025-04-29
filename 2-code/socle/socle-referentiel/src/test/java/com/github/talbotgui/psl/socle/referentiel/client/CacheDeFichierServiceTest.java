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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.github.talbotgui.psl.socle.commun.test.AbstractTest;
import com.github.talbotgui.psl.socle.referentiel.application.SocleReferentielApplication;

@SpringBootTest(classes = SocleReferentielApplication.class)
@TestPropertySource(inheritLocations = true, locations = { "classpath:/application-testsspecifiquePourCacheService.properties" })
class CacheDeFichierServiceTest extends AbstractTest {

	private static final String CODE_CACHE = "codeCache";
	private static final String CONTENU = "coucou";
	private static final byte[] CONTENU_EN_BYTES = CONTENU.getBytes(StandardCharsets.UTF_8);
	private static final String CONTENU2 = "coucou2";

	@Autowired
	private CacheDeFichierService cache;

	@Value("${cache.chemin}")
	private String cheminDuCache;

	/** Reset entre chaque test. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Suppression du fichier de cache s'il existe et FAIL si un problème survient
		File fichier = new File(this.cheminDuCache + File.separator + CODE_CACHE);
		if (fichier.exists() && !fichier.delete()) {
			Assertions.fail("Impossible de supprimer le fichier de cache");
		}
		// Suppression du répertoire parent
		if (fichier.getParentFile().exists() && !fichier.getParentFile().delete()) {
			Assertions.fail("Impossible de supprimer le répertoire de cache");
		}
	}

	private void creerFichierCache(String contenu) throws IOException {
		Files.writeString(Paths.get(this.cheminDuCache, CODE_CACHE), contenu);
	}

	private void creerRepertoireCache() {
		File fichier = new File(this.cheminDuCache);
		if (!fichier.mkdir()) {
			Assertions.fail("Impossible de créer le répertoire de cache");
		}
	}

	@Test
	void test01Lecture01SansRepertoireExistant() {
		//
		//
		byte[] contenuObtenu = this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE);
		//
		Assertions.assertArrayEquals(new byte[0], contenuObtenu);
	}

	@Test
	void test01Lecture02SansFichierExistant() {
		//
		this.creerRepertoireCache();
		//
		byte[] contenuObtenu = this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE);
		//
		Assertions.assertArrayEquals(new byte[0], contenuObtenu);
	}

	@Test
	void test01Lecture03ContenuVide() throws IOException {
		//
		this.creerRepertoireCache();
		this.creerFichierCache("");
		//
		byte[] contenuObtenu = this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE);
		//
		Assertions.assertArrayEquals(new byte[0], contenuObtenu);
	}

	@Test
	void test01Lecture04CasNominal() throws IOException {
		//
		this.creerRepertoireCache();
		this.creerFichierCache(CONTENU);
		//
		byte[] contenuObtenu = this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE);
		//
		Assertions.assertArrayEquals(CONTENU_EN_BYTES, contenuObtenu);
	}

	@Test
	void test02Ecriture01SansRepertoireExistant() throws IOException {
		//
		//
		this.cache.sauvegarderContenuDuCache(CODE_CACHE, CONTENU_EN_BYTES);
		//
		byte[] contenuObtenu = Files.readAllBytes(Paths.get(this.cheminDuCache, CODE_CACHE));
		Assertions.assertArrayEquals(CONTENU_EN_BYTES, contenuObtenu);
	}

	@Test
	void test02Ecriture02SansFichierDejaExistant() throws IOException {
		//
		this.creerRepertoireCache();
		//
		this.cache.sauvegarderContenuDuCache(CODE_CACHE, CONTENU_EN_BYTES);
		//
		byte[] contenuObtenu = Files.readAllBytes(Paths.get(this.cheminDuCache, CODE_CACHE));
		Assertions.assertArrayEquals(CONTENU_EN_BYTES, contenuObtenu);
	}

	@Test
	void test02Ecriture03AvecFichierDejaExistant() throws IOException {
		//
		this.creerRepertoireCache();
		this.creerFichierCache(CONTENU2);
		//
		this.cache.sauvegarderContenuDuCache(CODE_CACHE, CONTENU_EN_BYTES);
		//
		byte[] contenuObtenu = Files.readAllBytes(Paths.get(this.cheminDuCache, CODE_CACHE));
		Assertions.assertArrayEquals(CONTENU_EN_BYTES, contenuObtenu);
	}

	@Test
	void test02Ecriture04AvecContenuVide() throws IOException {
		//
		this.creerRepertoireCache();
		//
		this.cache.sauvegarderContenuDuCache(CODE_CACHE, "".getBytes());
		//
		byte[] contenuObtenu = Files.readAllBytes(Paths.get(this.cheminDuCache, CODE_CACHE));
		Assertions.assertArrayEquals("".getBytes(), contenuObtenu);
	}

	@Test
	void test02Ecriture05CasNominal() throws IOException {
		//
		this.creerRepertoireCache();
		//
		this.cache.sauvegarderContenuDuCache(CODE_CACHE, CONTENU_EN_BYTES);
		//
		byte[] contenuObtenu = Files.readAllBytes(Paths.get(this.cheminDuCache, CODE_CACHE));
		Assertions.assertArrayEquals(CONTENU_EN_BYTES, contenuObtenu);
	}

	@Test
	void test03Purge() throws IOException {
		//
		this.creerRepertoireCache();
		this.creerFichierCache(CONTENU);
		//
		this.cache.reinitialiserLesCaches(Arrays.asList(CODE_CACHE));
		//
		Assertions.assertFalse(Paths.get(this.cheminDuCache, CODE_CACHE).toFile().exists());
	}

}
