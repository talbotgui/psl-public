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
package com.github.talbotgui.psl.socle.adminpsl.service;

import java.util.Collection;
import java.util.Map;

import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.UtilisateurConnecte;
import com.github.talbotgui.psl.socle.adminpsl.client.LdapClient;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;

@Service
public class SecuriteServiceImpl implements SecuriteService {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(SecuriteServiceImpl.class);

	@Value("${ldap.baseDn:dc=psl,dc=talbotgui,dc=github,dc=com}")
	private String baseDn;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private LdapClient ldapClient;

	@Value("${ldap.url:ldap://localhost:1389}")
	private String ldapUrl;

	/**
	 * Tentative de connexion au LDAP.
	 *
	 * @param nomUtilisateur Paramètres de connexion.
	 * @param motDePasse     Paramètres de connexion.
	 */
	private UtilisateurConnecte seConnecterAuLdap(String nomUtilisateur, String motDePasse) {
		// Connexion au LDAP
		InitialDirContext connexionLdap = this.ldapClient.seConnecter(this.ldapUrl, this.baseDn, nomUtilisateur,
				motDePasse);

		// Chargement des données de l'utilisateur
		return this.ldapClient.chargerDonneesUtilisateur(connexionLdap, nomUtilisateur, this.baseDn);
	}

	@Override
	public String seConnecterEtCreerJeton(String nomUtilisateur, String motDePasse) {

		// log (debug car trace contiendrait le mot de passe)
		// Cette ligne ne contrevient pas à la règle sur les données personnelles dans
		// les logs autres que TRACE
		LOGGER.debug("Tentative de connexion avec le compte '{}'", nomUtilisateur);

		// Connexion au LDAP
		UtilisateurConnecte utilisateur = this.seConnecterAuLdap(nomUtilisateur, motDePasse);

		// Transformation des données
		Collection<? extends GrantedAuthority> authorities = utilisateur.getGroupes().stream()
				.map(SimpleGrantedAuthority::new).toList();
		UserDetails userDetails = new User(utilisateur.getNomUtilisateur(), "", authorities);
		Map<String, Object> claims = Map.of("cn", utilisateur.getNom());

		// Création d'un token JWT
		return this.jwtService.genererNouveauToken(userDetails, claims);
	}

}
