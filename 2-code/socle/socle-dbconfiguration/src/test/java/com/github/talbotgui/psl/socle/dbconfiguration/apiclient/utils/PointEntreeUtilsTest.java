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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.ParametrePointEntreeDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PointEntreeDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.exception.ConfigurationException;
import com.googlecode.catchexception.CatchException;

class PointEntreeUtilsTest {

	@Test
	void testRechercherPointEntreeCorrespondant01sansPointEntree() {
		//
		List<PointEntreeDto> pointsEntree = null;
		Map<String, String> donnees = null;
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(pointsEntree, donnees);
		//
		Assertions.assertNull(peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant02avecUnPointEntreeSansParam() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth", new ArrayList<>());
		Map<String, String> donnees = null;
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1), donnees);
		//
		Assertions.assertEquals(pe1, peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant03avecUnPointEntreeAvecParam() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramA", "valeur1");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1), donnees);
		//
		Assertions.assertEquals(pe1, peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant04avecPlusieursPointsEntree01() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth1", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		PointEntreeDto pe2 = new PointEntreeDto("auth2", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur2"))));
		PointEntreeDto pe3 = new PointEntreeDto("", Arrays.asList(new ParametrePointEntreeDto("paramB", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramA", "valeur2");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1, pe2, pe3), donnees);
		//
		Assertions.assertEquals(pe2, peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant04avecPlusieursPointsEntree02() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth1", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		PointEntreeDto pe2 = new PointEntreeDto("auth2", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur2"))));
		PointEntreeDto pe3 = new PointEntreeDto("", Arrays.asList(new ParametrePointEntreeDto("paramB", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramA", "valeur2");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1, pe2, pe3), donnees);
		//
		Assertions.assertEquals(pe2, peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant04avecPlusieursPointsEntree03() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth1", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		PointEntreeDto pe2 = new PointEntreeDto("auth2", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur2"))));
		PointEntreeDto pe3 = new PointEntreeDto("", Arrays.asList(new ParametrePointEntreeDto("paramB", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramB", "valeur1");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1, pe2, pe3), donnees);
		//
		Assertions.assertEquals(pe3, peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant05erreurSansPointEntreeCorrespondant01pasBonnesValeurs() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramA", "zzz");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1), donnees);
		//
		Assertions.assertNull(peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant05erreurSansPointEntreeCorrespondant01pasDeDonneesCorrespondante() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("zzz", "zzz");
		//
		PointEntreeDto peTrouve = PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1), donnees);
		//
		Assertions.assertNull(peTrouve);
	}

	@Test
	void testRechercherPointEntreeCorrespondant05ErreurTropDePointCorrespondant() {
		//
		PointEntreeDto pe1 = new PointEntreeDto("auth1", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur1"))));
		PointEntreeDto pe2 = new PointEntreeDto("auth2", Arrays.asList(new ParametrePointEntreeDto("paramA", Arrays.asList("valeur2"))));
		PointEntreeDto pe3 = new PointEntreeDto("", Arrays.asList(new ParametrePointEntreeDto("paramB", Arrays.asList("valeur1"))));
		Map<String, String> donnees = Map.of("paramA", "valeur1", "paramB", "valeur1");
		//
		CatchException.catchException(() -> PointEntreeUtils.rechercherPointEntreeCorrespondant(Arrays.asList(pe1, pe2, pe3), donnees));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(
				ApiClientException.equals(CatchException.caughtException(), ConfigurationException.ERREUR_POINT_ENTREE_CORRESPONDANT_TROP_NOMBREUX),
				CatchException.caughtException().getMessage());

	}
}
