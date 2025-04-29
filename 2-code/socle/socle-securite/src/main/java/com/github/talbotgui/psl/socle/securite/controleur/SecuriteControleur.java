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
package com.github.talbotgui.psl.socle.securite.controleur;

import java.util.List;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.commun.oidc.OidcClient;
import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;
import com.github.talbotgui.psl.socle.securite.apiclient.api.SecuriteAPI;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteAuthentificationMotDePasseDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteCreationTokenOidcDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;
import com.github.talbotgui.psl.socle.securite.service.OidcService;

/**
 * Principal controleur REST du projet
 */
@RestController
public class SecuriteControleur implements SecuriteAPI {

	private final OidcService oidcService;

	/** Constructeur pour l'injection des dépendances (cf. chapitre §3.22.5). */
	public SecuriteControleur(OidcService oidcService) {
		super();
		this.oidcService = oidcService;
	}

	@Override
	public InformationSpUsagerDto chargerInformationUtilisateur() {
		// Lecture des entêtes
		String tokenPSL = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Appel aux APIs SP via le service
		return this.oidcService.chargerDonneesUsagerEtCompteAvecUnTokenPsl(tokenPSL);
	}

	@Override
	public ReponseTokenOIDC creerTokenOidc(MultiValueMap<String, Object> paramMap) throws Exception {

		// Lecture des informations
		String code = this.lirePremiereValeur(paramMap, OidcClient.PARAM_CODE);
		String codeVerifier = this.lirePremiereValeur(paramMap, OidcClient.PARAM_CODE_VERIFIER);
		String redirectURI = this.lirePremiereValeur(paramMap, OidcClient.PARAM_REDIRECT_URI);
		String grantType = this.lirePremiereValeur(paramMap, OidcClient.PARAM_GRANT_TYPE);
		String refreshToken = this.lirePremiereValeur(paramMap, OidcClient.PARAM_REFRESH_TOKEN);
		RequeteCreationTokenOidcDto requeteDeclarationOIDC = new RequeteCreationTokenOidcDto(code, codeVerifier, redirectURI, grantType,
				refreshToken);

		// Création et renvoi du token
		return this.oidcService.creerOuRaffrachirLeToken(requeteDeclarationOIDC);
	}

	/**
	 * Petite méthode de récupération de données dans un MultiValueMap.
	 *
	 * @param paramMap Map de multi-valeurs.
	 * @param clef     Clef à rechercher.
	 * @return Première valeur correspondante à la clef.
	 */
	private String lirePremiereValeur(MultiValueMap<String, Object> paramMap, String clef) {
		// Récupération de la liste des valeurs
		List<Object> liste = paramMap.get(clef);

		// Récupération de la première valeur (s'il y en a une)
		if ((liste != null) && !liste.isEmpty() && (liste.get(0) != null)) {
			return liste.get(0).toString();
		}

		// Par défaut, renvoi de NULL
		else {
			return null;
		}
	}

	@Override
	public ReponseJwtDto sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(RequeteAuthentificationMotDePasseDto requete) {
		return this.oidcService.sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(requete.nomUtilisateur(), requete.motDePasse());
	}

	@Override
	public ReponseJwtDto sauthentifierEnAnonyme() {
		return this.oidcService.sauthentifierEnAnonyme();
	}

}
