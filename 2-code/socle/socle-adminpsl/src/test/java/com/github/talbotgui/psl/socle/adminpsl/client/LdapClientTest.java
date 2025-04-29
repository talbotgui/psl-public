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
package com.github.talbotgui.psl.socle.adminpsl.client;

import javax.naming.directory.InitialDirContext;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.UtilisateurConnecte;
import com.github.talbotgui.psl.socle.adminpsl.exception.SecuriteException;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.googlecode.catchexception.CatchException;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

@TestInstance(Lifecycle.PER_CLASS)
class LdapClientTest {

	private static final String ADMIN_LOGIN = "admin1";
	private static final String ADMIN_MDP = "admin";
	private static final String BASE_DN = "dc=psl,dc=talbotgui,dc=github,dc=com";
	private static final String CHEMIN_LDIF = "src/test/resources/test.ldif";
	private static final String MANAGER_LOGIN = "cn=DirectoryManager";
	private static final String MANAGER_MDP = "ADMIN";
	private static final int PORT = 1389;
	private static final String URL = "ldap://localhost:" + PORT;

	/** Instance à tester. */
	private LdapClient ldapClient;

	/** Instance du LDAP de test. */
	private InMemoryDirectoryServer server;

	@AfterAll
	void apresTousLesTests() {
		// Arrêt du LDAP
		this.server.shutDown(true);
	}

	@BeforeAll
	void avantTousLesTests() throws LDAPException {
		// Configuration du LDAP
		InMemoryListenerConfig interfaceLdap = InMemoryListenerConfig.createLDAPConfig("LDAP", null, PORT, null);
		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(BASE_DN);
		config.addAdditionalBindCredentials(MANAGER_LOGIN, MANAGER_MDP);
		config.setListenerConfigs(interfaceLdap);
		this.server = new InMemoryDirectoryServer(config);
		this.server.importFromLDIF(true, CHEMIN_LDIF);

		// Démarrage du LDAP
		this.server.startListening();

		// Création de l'instance de client à tester (pas besoin de Spring pour ça)
		this.ldapClient = new LdapClient();
	}

	@Test
	void test01SeConnecter01succes() {
		//
		//
		InitialDirContext co = this.ldapClient.seConnecter(URL, BASE_DN, ADMIN_LOGIN, ADMIN_MDP);
		//
		Assertions.assertNotNull(co);
	}

	@Test
	void test02SeConnecter02erreur() {
		//
		//
		CatchException.catchException(() -> this.ldapClient.seConnecter(URL, BASE_DN, ADMIN_LOGIN, ADMIN_MDP + "raté"));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(
				ApiClientException.equals(CatchException.caughtException(), SecuriteException.CONNEXION_NON_AUTORISEE),
				CatchException.caughtException().getMessage());
	}

	@Test
	void testChargerDonneesUtilisateur01succes() {
		//
		InitialDirContext co = this.ldapClient.seConnecter(URL, BASE_DN, ADMIN_LOGIN, ADMIN_MDP);
		//
		UtilisateurConnecte utilisateur = this.ldapClient.chargerDonneesUtilisateur(co, ADMIN_LOGIN, BASE_DN);
		//
		Assertions.assertNotNull(utilisateur);
		Assertions.assertEquals(ADMIN_LOGIN, utilisateur.getNomUtilisateur());
		Assertions.assertNotEquals(ADMIN_LOGIN, utilisateur.getNom());
		Assertions.assertEquals(1, utilisateur.getGroupes().size());
	}

}
