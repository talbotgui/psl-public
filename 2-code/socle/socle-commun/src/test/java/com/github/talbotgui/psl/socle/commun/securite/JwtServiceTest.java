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
package com.github.talbotgui.psl.socle.commun.securite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;

@TestMethodOrder(MethodOrderer.MethodName.class)
class JwtServiceTest {
	/** Claims des tests */
	private static final Map<String, Object> CLAIMS = Map.of("email", "toto@email.fr", "nom", "MonNom", "prenom",
			"MonPrenom");

	private static final Map<String, Object> DONNEES_A_CHIFFRER = new HashMap<>(
			Map.of(JwtService.CLEF_CLAIMS_ACCESS_TOKEN_OIDC, "xxxx"));

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceTest.class);

	private static final UserDetails UTILISATEUR = new User("nomUtilisateur", "", new ArrayList<>());

	private final JwtService service = new JwtServiceImpl();

	@BeforeEach()
	void avantChaqueTest() {
		LOGGER.info("**********************");
		ReflectionTestUtils.setField(this.service, "dureeValidite", 10L);
		// Chaîne de caractères de chiffrement autorisée (ce n'est pas un secret)
		ReflectionTestUtils.setField(this.service, "secretJWT", "rNF8/rflvlxLQRRxzrffx1LMHh4O4HT7EqSPi6geTs4=");
		// Chaîne de caractères de chiffrement autorisée (ce n'est pas un secret)
		ReflectionTestUtils.setField(this.service, "secretTokenTemporaire",
				"rNF8/rflvlxLQRRxzrffx1LMHh4O4HT7EqSPi6geTs5=");
	}

	@Test
	void test01GenererNouveauToken01SansClaims() {
		//
		//
		String token = this.service.genererNouveauToken(UTILISATEUR);
		Claims claims = this.service.validerToken(token);
		//
		Assertions.assertNotNull(token);
		Assertions.assertNotNull(claims);
	}

	@Test
	void test01GenererNouveauToken02AvecClaims() {
		//
		//
		String token = this.service.genererNouveauToken(UTILISATEUR, CLAIMS);
		Claims claims = this.service.validerToken(token);
		//
		Assertions.assertNotNull(token);
		Assertions.assertNotNull(claims);
	}

	@Test
	void test01GenererNouveauToken03AvecClaimsEtDonneesAchiffrer() {
		//
		//
		String token = this.service.genererNouveauToken(UTILISATEUR, CLAIMS, DONNEES_A_CHIFFRER);
		Claims claims = this.service.validerToken(token);
		//
		Assertions.assertNotNull(token);
		Assertions.assertNotNull(claims);
	}

	@Test
	void test02GenererNouveauTokenAnonyme() {
		//
		//
		String token = this.service.genererNouveauTokenAnonyme();
		Claims claims = this.service.validerToken(token);
		//
		Assertions.assertNotNull(token);
		Assertions.assertNotNull(claims);
	}

	@Test
	void test04genererClefTemporaireSurTokenJwt() {
		//
		String token = this.service.genererNouveauTokenAnonyme();
		String clefUnique = UUID.randomUUID().toString();
		long temps = new Date().getTime();
		//
		String clefTemporaire = this.service.genererClefTemporaireSurTokenJwt(token, clefUnique, temps, Map.of());
		//
		Assertions.assertNotNull(clefTemporaire);
	}

	@Test
	void test06validerTokenDeClefTemporaire() {
		//
		String token = this.service.genererNouveauTokenAnonyme();
		String clefUnique = UUID.randomUUID().toString();
		long temps = 300;
		String clefTemporaireChiffree = this.service.genererClefTemporaireSurTokenJwt(token, clefUnique, temps,
				Map.of());
		//
		Claims claims = this.service.validerClefTemporaireSurTokenJwt(clefTemporaireChiffree);
		//
		Assertions.assertNotNull(claims);
	}

	@Test
	void test07extraireClaim01nonChiffre() {
		//
		String token = this.service.genererNouveauToken(UTILISATEUR, CLAIMS, DONNEES_A_CHIFFRER);
		Map.Entry<String, Object> entreeAlire = CLAIMS.entrySet().iterator().next();
		//
		String valeur = this.service.extraireClaimDuToken(token, entreeAlire.getKey());
		//
		Assertions.assertEquals(entreeAlire.getValue(), valeur);
	}

	@Test
	void test07extraireClaim02chiffre() {
		//
		String token = this.service.genererNouveauToken(UTILISATEUR, CLAIMS, DONNEES_A_CHIFFRER);
		Map.Entry<String, Object> entreeAlire = DONNEES_A_CHIFFRER.entrySet().iterator().next();
		//
		String valeur = this.service.extraireClaimDuToken(token, entreeAlire.getKey());
		//
		Assertions.assertEquals(entreeAlire.getValue(), valeur);
	}
}
