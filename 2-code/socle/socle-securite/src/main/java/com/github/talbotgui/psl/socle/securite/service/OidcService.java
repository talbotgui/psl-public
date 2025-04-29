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
package com.github.talbotgui.psl.socle.securite.service;

import com.github.talbotgui.psl.socle.commun.oidc.dto.ReponseTokenOIDC;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.ReponseJwtDto;
import com.github.talbotgui.psl.socle.securite.apiclient.dto.RequeteCreationTokenOidcDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;

public interface OidcService {

	/**
	 * Chargement des données de l'usager et du compte (2 requêtes) à partir d'un token PSL.
	 *
	 * @param tokenPSL TokenPSL.
	 * @return Les données de l'usager et du compte dans un même DTO.
	 */
	InformationSpUsagerDto chargerDonneesUsagerEtCompteAvecUnTokenPsl(String tokenPSL);

	/**
	 * Création d'un nouveau token OIDC depuis le fournisseur.
	 *
	 * @param requeteDeclarationOIDC Données nécessaires à la création d'un token auprès du fournisseur OIDC.
	 * @return Nouveau token venant du fournisseur OIDC.
	 */
	ReponseTokenOIDC creerOuRaffrachirLeToken(RequeteCreationTokenOidcDto requeteDeclarationOIDC);

	/**
	 * Création d'un token PSL à partir d'un nom d'utilisateur et d'un mot de passe.
	 *
	 * @param nomUtilisateur Nom de l'utilisateur.
	 * @param motDePasse     Mot de passe.
	 * @return Token PSL
	 */
	ReponseJwtDto sauthentifierAvecUnNomDutilisateurEtUnMotDePasse(String nomUtilisateur, String motDePasse);

	/**
	 * Création d'un token PSL anonyme
	 *
	 * @return
	 */
	ReponseJwtDto sauthentifierEnAnonyme();
}
