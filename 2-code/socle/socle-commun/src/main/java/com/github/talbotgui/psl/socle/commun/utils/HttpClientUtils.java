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

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;

/** Classe utilitaire pour les appels REST via l'API Java java.net.http. */
public class HttpClientUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

	/**
	 * Initialisation du builder de client avec prise en compte du proxy du système.
	 *
	 * @param builder Un builder de client.
	 */
	public static void ajouterLeProxySysteme(HttpClient.Builder builder) {
		ProxySelector proxySysteme = ProxySelector.getDefault();
		LOGGER.info("Activation du proxy système : '{}'", proxySysteme);

		builder.proxy(proxySysteme);
	}

	/**
	 * Initialisation du builder de client avec prise en compte du proxy.
	 *
	 * @param builder     Un builder de client.
	 * @param hoteDuProxy Le nom d'hôte du proxy.
	 * @param portDuProxy Le port du proxy.
	 */
	public static void ajouterUnProxy(HttpClient.Builder builder, String hoteDuProxy, int portDuProxy) {
		LOGGER.info("Activation du proxy '{}:{}'", hoteDuProxy, portDuProxy);

		builder.proxy(creerConfigurationDuProxy(hoteDuProxy, portDuProxy));
	}

	/**
	 * Ajout, dans le builder de client, de la prise en compte du proxy et d'une authentification.
	 *
	 * @param hoteDuProxy    Le nom d'hôte du proxy.
	 * @param portDuProxy    Le port du proxy.
	 * @param nomUtilisateur Le login à utiliser pour le proxy.
	 * @param motDePasse     Le mot de passe à utiliser pour le proxy.
	 */
	public static void ajouterUnProxy(HttpClient.Builder builder, String hoteDuProxy, int portDuProxy, String nomUtilisateur, String motDePasse) {

		if (StringUtils.hasLength(hoteDuProxy) && portDuProxy != 0) {
			ajouterUnProxy(builder, hoteDuProxy, portDuProxy);

			if (StringUtils.hasLength(nomUtilisateur) && StringUtils.hasLength(motDePasse)) {
				LOGGER.info("Activation de l'authentification du proxy : **** / ****");

				builder.authenticator(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(nomUtilisateur, motDePasse.toCharArray());
					}
				});
			}
		}
	}

	private static ProxySelector creerConfigurationDuProxy(String hoteDuProxy, int portDuProxy) {
		return ProxySelector.of(new InetSocketAddress(hoteDuProxy, portDuProxy));
	}

	/**
	 * Initialisation du builder de requête avec prise en compte du proxy
	 *
	 * @param timoutSpecifique Timeout en seconde si une valeur spécifique est nécessaire.
	 * @return Un builder de requête.
	 */
	public static HttpRequest.Builder creerLeBuilderDeLaRequeteDeBase(Long timoutSpecifique) {
		if (timoutSpecifique == null) {
			timoutSpecifique = 60L;
		}
		return HttpRequest.newBuilder().timeout(Duration.ofSeconds(timoutSpecifique));
	}

	/**
	 * Initialisation du builder de client sans prise en compte du proxy
	 *
	 * @return Un builder de client.
	 */
	public static HttpClient.Builder creerLeBuilderDuClientHttp() {
		return HttpClient.newBuilder()//
				.followRedirects(HttpClient.Redirect.ALWAYS);
	}

	/**
	 * Méthode générant une URL.
	 *
	 * @param base       Base de l'URL
	 * @param parametres Liste des clef/valeur à ajouter
	 * @return l'URL complète
	 */
	public static String creerURL(String base, List<String> parametres) {
		// Programmation défensive
		if (base == null) {
			throw new IllegalArgumentException("Base ne peut être null");
		}
		if (parametres != null && parametres.size() % 2 == 1) {
			throw new IllegalArgumentException("Le nombre de paramètre est obligatoirement paire (clef et valeur)");
		}

		// Concaténation des paramètres à l'URL avec encodage des caractères spéciaux pour le paramètre et sa valeur
		StringBuilder sb = new StringBuilder(base);
		if (parametres != null) {
			for (int i = 0; i < parametres.size() - 1; i += 2) {
				sb.append(i == 0 ? "?" : "&");
				sb.append(URLEncoder.encode(parametres.get(i), Charset.forName(StandardCharsets.UTF_8.toString())));
				sb.append("=");
				sb.append(URLEncoder.encode(parametres.get(i + 1), Charset.forName(StandardCharsets.UTF_8.toString())));
			}
		}

		return sb.toString();
	}

	/**
	 * Méthode générant une URL.
	 *
	 * @param base       Base de l'URL
	 * @param parametres Liste des clef/valeur à ajouter
	 * @return l'URL complète
	 */
	public static String creerURL(String base, String... parametres) {
		return creerURL(base, Arrays.asList(parametres));
	}

	/**
	 * Désactivation de la vérification SSL car le certificat n'est pas reconnu
	 *
	 * @param builderClientHttp Le builder client à traiter
	 */
	public static void desactiverLesVerificationsSSL(Builder builderClientHttp) {
		LOGGER.warn("Désactivation de la vérification SSL. Est-ce véritablement nécessaire ?");
		try {

			// Création d'un TrustManager acceptant tout
			TrustManager[] trustAllCerts = { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// rien à faire ici
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// rien à faire ici
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} };

			// Ajout du TrustManager dans le contexte SSL
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			// Modification du client HTTP avec ce contexte SSL
			builderClientHttp.sslContext(sc);

		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new ApiClientException(ApiClientException.ERREUR_PREPARATION_APPEL_SSL, e);
		}
	}

	private HttpClientUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}

}
