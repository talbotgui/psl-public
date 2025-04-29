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
import java.util.Arrays;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {

	/** Clef de Claims pour l'accessToken OIDC */
	static final String CLEF_CLAIMS_ACCESS_TOKEN_OIDC = "at";

	/** Clef de Claims pour le type de compte (PART/ASSO) */
	static final String CLEF_CLAIMS_ACCOUNT_TYPE = "accountType";

	/** Clef de Claims pour les groupes autorisés (adminpsl). */
	static final String CLEF_CLAIMS_AUTHORITIES = "authorities";

	/** Clef de Claims pour la date d'expiration du accessToken OIDC */
	static final String CLEF_CLAIMS_EXPIRATION_ACCESSTOKEN_OIDC = "etat";

	/** Clef de Claims pour la date d'expiration du refreshtoken OIDC */
	static final String CLEF_CLAIMS_EXPIRATION_REFRESHTOKEN_OIDC = "etrt";

	/** Clef de Claims pour le type d'authentification SP ou FC */
	static final String CLEF_CLAIMS_FC = "FC";

	/** Clef de Claims pour le mot de passe dans les authentifications par nom d'utilisateur et mot de passe. */
	static final String CLEF_CLAIMS_MOT_DE_PASSE = "motDePasse";

	/** Clef de Claims pour le refreshToken OIDC */
	static final String CLEF_CLAIMS_REFRESH_TOKEN_OIDC = "rt";

	/** Clef de Claims pour l'identifiant unique de l'usager OIDC. */
	static final String CLEF_CLAIMS_UUID_SP = "oidcUuid";

	/** Clef de Claims pour la valeur aléatoire unique temporaire */
	static final String CLEF_CLAIMS_VALEUR_UNIQUE = "vu";

	/** Email de l'utilisateur dans un token anonyme */
	static final String EMAIL_UTILISATEUR_ANONYMOUS = "anonymous@psl.fr";

	/** Code du groupe ayant accès à l'API adminpsl. */
	static final String GROUPE_ADMIN = "ADMIN_SUPERVISION";

	/** Données d'un utilisateur ADMIN ayant accès à l'application adminpsl. */
	static final User USER_ADMIN = new User(EMAIL_UTILISATEUR_ANONYMOUS, "", Arrays.asList(new SimpleGrantedAuthority(GROUPE_ADMIN)));

	/** Données d'un utilisateur anonyme. */
	static final User USER_ANONYME = new User(EMAIL_UTILISATEUR_ANONYMOUS, "", new ArrayList<>());

	/**
	 * Extraction d'un claim (déchiffré s'il est chiffré).
	 *
	 * @param token     Token à traiter.
	 * @param clefClaim Clef du claim à lire.
	 * @return Valeur du claim (éventuellement déchiffrée).
	 */
	String extraireClaimDuToken(String token, String clefClaim);

	/**
	 * Déchiffrer le token et en extraire tous les claims.
	 *
	 * @param token Token à traiter.
	 * @return Valeurs obtenues.
	 */
	Claims extraireEtDechiffrerClaimsDuToken(String token);

	/**
	 * Générer une clef temporaire contenant toutes les données fournies.
	 *
	 * @param tokenJwt               Token de l'utilisateur.
	 * @param clefUnique             Chaine de caractères unique pour ce cas d'usage.
	 * @param temps                  Limite de validité (en secondes).
	 * @param donneesSupplementaires Données supplémentaires.
	 * @return Une clef temporaire chiffrée
	 */
	String genererClefTemporaireSurTokenJwt(String tokenJwt, String clefUnique, Long temps, Map<String, String> donneesSupplementaires);

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails Détails de l'utilisateur.
	 * @return Token généré.
	 */
	String genererNouveauToken(UserDetails userDetails);

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails Détails de l'utilisateur.
	 * @param claims      Données supplémentaires lisibles.
	 * @return Un token JWT.
	 */
	String genererNouveauToken(UserDetails userDetails, Map<String, Object> claims);

	/**
	 * Génération d'un token.
	 *
	 * @param userDetails      Détails de l'utilisateur.
	 * @param claims           Données supplémentaires lisibles.
	 * @param donneesAchiffrer Données à chiffrer
	 * @return Un token JWT.
	 */
	String genererNouveauToken(UserDetails userDetails, Map<String, Object> claims, Map<String, Object> donneesAchiffrer);

	/**
	 * Génération d'un token.
	 *
	 * @return Token généré.
	 */
	String genererNouveauTokenAnonyme();

	/**
	 * Validation du token vis-à-vis des informations fournies.
	 *
	 * @param token Token à valider.
	 * @return null si le token est mauvais/expiré/... Sinon renvoi des données.
	 */
	Claims validerClefTemporaireSurTokenJwt(String token);

	/**
	 * Validation du token vis-à-vis des informations fournies.
	 *
	 * @param token Token à valider.
	 * @return null si le token est mauvais/expiré/... Sinon renvoi des données.
	 */
	Claims validerToken(String token);
}