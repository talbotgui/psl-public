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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.UtilisateurConnecte;
import com.github.talbotgui.psl.socle.adminpsl.exception.SecuriteException;

/**
 * Client d'appel au LDAP.
 *
 * Ce n'est pas une classe Utils avec des méthodes statiques pour être facilement bouchonnée dans les tests.
 */
@Component
public class LdapClient {

	/** Tentative de connexion au LDAP. */
	public UtilisateurConnecte chargerDonneesUtilisateur(InitialDirContext connexion, String utilisateur, String baseDn) {
		String branchePersonnes = "ou=people," + baseDn;
		String brancheGroupes = "ou=groups," + baseDn;
		String uidSimple = "uid=" + utilisateur;
		String uidComplet = uidSimple + "," + branchePersonnes;

		try {
			// Création d'un objet en sortie
			UtilisateurConnecte utilisateurDto = new UtilisateurConnecte();
			utilisateurDto.setNomUtilisateur(utilisateur);

			// Recherche du nom
			this.lireNomUtilisateur(connexion, branchePersonnes, uidSimple, utilisateurDto);

			// Recherche des groupes
			this.lireGroupes(connexion, brancheGroupes, uidComplet, utilisateurDto);

			return utilisateurDto;
		} catch (Exception e) {
			throw new SecuriteException(SecuriteException.CONNEXION_NON_AUTORISEE, e);
		}
	}

	/** Recherche des groupes de l'utilisateur. */
	private void lireGroupes(InitialDirContext connexion, String brancheGroupes, String uidComplet, UtilisateurConnecte utilisateurDto)
			throws NamingException {
		// Création de la requête
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(new String[] { "cn" });
		String criteresRecherche = "(member=" + uidComplet + ")";

		// Recherche
		NamingEnumeration<SearchResult> groupes = connexion.search(brancheGroupes, criteresRecherche, controls);

		// Parcours des résultats pour extraire le CN
		while (groupes.hasMore()) {
			SearchResult searchResult = groupes.next();
			Attribute cn = searchResult.getAttributes().get("cn");
			if (cn != null && cn.get(0) != null) {
				utilisateurDto.getGroupes().add(cn.toString().replace("cn: ", ""));
			}
		}
	}

	/** Recherche du nom de l'utilisateur (CN). */
	private void lireNomUtilisateur(InitialDirContext connexion, String branchePersonnes, String uidSimple, UtilisateurConnecte utilisateurDto)
			throws NamingException {
		// Création de la requête
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(new String[] { "uid", "cn" });
		String criteresRecherche = "(" + uidSimple + ")";

		// Exécution de la requete
		NamingEnumeration<SearchResult> users = connexion.search(branchePersonnes, criteresRecherche, controls);

		// Si aucun résultat, exception
		if (!users.hasMore()) {
			throw new SecuriteException(SecuriteException.CONNEXION_NON_AUTORISEE);
		}

		// Sinon lecture du CN
		SearchResult searchResult = users.next();
		Attribute cn = searchResult.getAttributes().get("cn");
		if (cn != null && cn.get(0) != null) {
			utilisateurDto.setNom(cn.toString());
		}
	}

	/** Tentative de connexion au LDAP. */
	public InitialDirContext seConnecter(String url, String baseDn, String utilisateur, String motDePasse) {
		String branchePersonnes = "ou=people," + baseDn;
		String uidSimple = "uid=" + utilisateur;

		// Création du contexte
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, uidSimple + "," + branchePersonnes);
		env.put(Context.SECURITY_CREDENTIALS, motDePasse);

		try {
			// Tentative de connexion au LDAP
			return new InitialDirContext(env);
		} catch (Exception e) {
			throw new SecuriteException(SecuriteException.CONNEXION_NON_AUTORISEE, e);
		}
	}
}
