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

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.securite.dto.WebAuthenticationDetailsDto;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre de sécurité permettant de valider le token JWT.
 *
 * @see com.github.talbotgui.psl.socle.commun.application.configuration.WebSecurityConfig
 */
@Component
public class JwtSecuriteFilter extends OncePerRequestFilter {

	/** Clef de l'attribut HTTP contenant les certificats associés à la requête HTTP. */
	private static final String ATTRIBUT_REQUETE_CONTENANT_CERTIFICATS = "jakarta.servlet.request.X509Certificate";

	/** Préfixe des tokens JWT. */
	public static final String BEARER = "BEARER ";

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecuriteFilter.class);

	/** Paramètre de requête contenant un token temporaire */
	public static final String PARAMETRE_CLEF = "clef";

	/** URL spécifique à l'authentification OIDC */
	public static final String URI_AUTHENTIFICATION_OIDC = "/authentificationOIDC";

	/** URL de base du micro-service ADMINPSL. */
	public static final String URI_BASE_ADMINPSL = "/socle/adminpsl";

	/** URL de connexion du micro-service ADMINPSL. */
	public static final String URI_CONNEXION_ADMINPSL = URI_BASE_ADMINPSL + "/connexion";

	/** URL de base du micro-service */
	private static final String URI_DE_BASE_ACTUATOR = "/actuator";

	/** URL de base du micro-service */
	public static final String URI_DE_BASE_SECURITE = "/socle/securite";

	@Autowired
	private JwtService jwtService;

	/** Pattern des CN autorisé pour appeler les APIs d'administration. */
	@Value("${securite.ssl.admin.patternCnAutorise:}")
	private String patternCnAutorisePourApiAdmin;

	/**
	 * Création et stockage d'un User Spring valide.
	 *
	 * @param requete  La requête HTTP.
	 * @param identite Le "subject".
	 * @param token    L'éventuel token disponbile.
	 * @param groupes  Liste des groupes présente dans les claims.
	 */
	private void creerUnUserEtLeStockerDansLeContexteDeSecuriteSpring(HttpServletRequest requete, String identite, String token,
			List<String> groupes) {
		// Création des GrantedAuthority à partir de l'éventuelle liste de groupes (adminpsl)
		Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
		if (groupes != null) {
			authorities = groupes.stream().filter(StringUtils::hasLength).map(SimpleGrantedAuthority::new).toList();
		}

		// Chargement des données de l'utilisateur
		UserDetails utilisateur = new User(identite, "", authorities);

		// Création d'un objet Authentification de Spring pour le mettre dans le contexte de sécurité
		UsernamePasswordAuthenticationToken authenticationSpring = new UsernamePasswordAuthenticationToken(utilisateur, token,
				utilisateur.getAuthorities());

		// Ajout des données disponibles dans la requête (IP, ...)
		authenticationSpring.setDetails(new WebAuthenticationDetailsDto(requete));

		// Déclaration de l'authentification pour que Spring laisse passer la requête
		SecurityContextHolder.getContext().setAuthentication(authenticationSpring);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// Si la requête concerne Actuator
		if (request.getRequestURI().startsWith(URI_DE_BASE_ACTUATOR)) {
			// on ne fait rien car Actuator est protégé par un autre moyen
		}

		// Si la requête concerne une API d'administration protégée par un certificat
		else if (request.getRequestURI().matches("/socle/[a-zA-Z]*" + AbstractClientHttp.PREFIXE_URI_ADMIN + ".*")) {

			// Lecture des certificats dans la requête
			X509Certificate[] certs = (X509Certificate[]) request.getAttribute(ATTRIBUT_REQUETE_CONTENANT_CERTIFICATS);

			// Les APIs d'administration nécessite un certificat client
			if (certs == null) {
				LOGGER.warn("Appel à l'API d'administration '{}' sans certificat client",
						LogUtils.nettoyerDonneesAvantDeLogguer(request.getRequestURI()));
			}

			// Si au moins un certificat est présent
			else {

				// Recherche d'un certificat valide dont le CN correspond au pattern attendu
				List<String> bonsCn = Arrays.asList(certs).stream()//
						.map(c -> c.getSubjectX500Principal().getName())//
						.filter(cn -> cn.matches(this.patternCnAutorisePourApiAdmin))//
						.toList();

				// Si aucun certificat ne correspond
				if (bonsCn.isEmpty()) {
					LOGGER.warn("Appel à l'API d'administration '{}' sans certificat client",
							LogUtils.nettoyerDonneesAvantDeLogguer(request.getRequestURI()));
				}

				// Si un bon certificat est présent, on crée un User correspondant
				else {
					// On prend le premier (tant pis s'il y en a plusieurs)
					String bonCn = bonsCn.get(0);

					// Création du User et stokage dans le contexte de sécurité Spring
					this.creerUnUserEtLeStockerDansLeContexteDeSecuriteSpring(request, bonCn, null, null);
				}
			}
		}

		// Pour les APIs publiques ou internes
		else {
			// Récupération de l'entête AUTHORIZATION et du paramètre CLEF
			String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			String clef = request.getParameter(PARAMETRE_CLEF);

			// Si la requête contient un paramètre 'clef' et que le requestTokenHeader est vide
			// On est en présence d'un paramètre CLEF contenant un token JWT temporaire
			Claims claims = null;
			if (StringUtils.hasLength(clef) && !StringUtils.hasLength(requestTokenHeader)) {
				claims = this.jwtService.validerClefTemporaireSurTokenJwt(clef);
				requestTokenHeader = BEARER + clef;
			}

			// Sinon, en mode nominal, lecture du token présent dans l'entête
			else if (requestTokenHeader != null && requestTokenHeader.toUpperCase().startsWith(BEARER)) {
				String token = requestTokenHeader.substring(BEARER.length());

				// Validation et lecture du token
				claims = this.jwtService.validerToken(token);
			}

			// Si le token est valide
			if (claims != null) {
				// Extraction des groupes (utilisé pour protéger les APIs)
				List<String> groupes = null;
				Object claimGoupes = claims.get(JwtService.CLEF_CLAIMS_AUTHORITIES);
				if (claimGoupes != null) {
					groupes = Arrays.asList(claims.get(JwtService.CLEF_CLAIMS_AUTHORITIES).toString().split(","));
				}

				// Si l'API est une API d'adminpsl,
				if (request.getRequestURI().startsWith(URI_BASE_ADMINPSL)) {

					// il faut la présence du groupe pour créer un token valide (sinon warn)
					if (groupes != null && groupes.contains(JwtService.GROUPE_ADMIN)) {
						this.creerUnUserEtLeStockerDansLeContexteDeSecuriteSpring(request, claims.getSubject(), requestTokenHeader, groupes);
					} else {
						LOGGER.warn("Tentative d'accès à l'API '{}' avec un token valide sans les groupes d'administration",
								LogUtils.nettoyerDonneesAvantDeLogguer(request.getRequestURI()));
					}
				}

				// Création du User et stokage dans le contexte de sécurité Spring
				else {
					this.creerUnUserEtLeStockerDansLeContexteDeSecuriteSpring(request, claims.getSubject(), requestTokenHeader, groupes);
				}
			}
			// pas de log dans le ELSE car trop poluant
		}

		// Et le filtre laisse passer la requête pour le prochain filtre de la pile
		chain.doFilter(request, response);
	}
}
