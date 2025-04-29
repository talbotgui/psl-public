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
package com.github.talbotgui.psl.socle.commun.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class MotDePasseUtilsTest {

	@Test
	void test01hasherNomUtilisateurEtMotDePasse() {
		//
		String nomUtilisateur = "nomUtilisateur@exemple.com";
		String motDePasse = "motDePasse";
		//
		String hash = MotDePasseUtils.hasherNomUtilisateurEtMotDePasse(nomUtilisateur, motDePasse);
		//
		Assertions.assertEquals("$2a$10$", hash.substring(0, 7));
	}

	@Test
	void test02hasherNomUtilisateurEtMotDePasse01ok() {
		//
		String nomUtilisateur = "nomUtilisateur@exemple.com";
		String motDePasse = "motDePasse";
		String hash = MotDePasseUtils.hasherNomUtilisateurEtMotDePasse(nomUtilisateur, motDePasse);
		//
		boolean resultat = MotDePasseUtils.verifierCorrespondance(hash, nomUtilisateur, motDePasse);
		//
		Assertions.assertTrue(resultat);
	}

	@Test
	void test02hasherNomUtilisateurEtMotDePasse02ko() {
		//
		String nomUtilisateur = "nomUtilisateur@exemple.com";
		String motDePasse = "motDePasse";
		String motDePasse2 = "motDePasse2";
		String hash = MotDePasseUtils.hasherNomUtilisateurEtMotDePasse(nomUtilisateur, motDePasse);
		//
		boolean resultat = MotDePasseUtils.verifierCorrespondance(hash, nomUtilisateur, motDePasse2);
		//
		Assertions.assertFalse(resultat);
	}
}
