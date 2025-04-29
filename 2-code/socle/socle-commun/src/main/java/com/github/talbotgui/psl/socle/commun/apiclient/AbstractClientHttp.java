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
package com.github.talbotgui.psl.socle.commun.apiclient;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.utils.HttpClientUtils;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;

import io.micrometer.tracing.Tracer;

/**
 * Classe de base de tous les client HTTP développés pour appeler les APIs REST du projet.
 *
 * Si les données d'un proxy sont passées en paramètre, ce dernier sera utilisé. Mais si la requête échoue avec une ConnectException et qu'un proxy
 * est paramétré, le proxy est retiré et l'appel à l'API est retenté.
 */
public abstract class AbstractClientHttp {

	/** Liste des codes HTTP considérés comme un retour positif. */
	private static final List<Integer> CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT = Arrays.asList(
			// Cas de la plus part des réponse
			HttpStatus.OK.value(),
			// Cas de HUBEE sur un POST à la création d'un FOLDER
			HttpStatus.CREATED.value(),
			// Cas de HUBEE sur un DELETE à la suppression d'un FOLDER
			HttpStatus.NO_CONTENT.value());

	public static final TypeReference<byte[]> DONNEES_BINAIRES = new TypeReference<>() {
	};
	/** Nom de l'entête W3C utilisé pour passer les informations de tracing. */
	private static final String ENTETE_W3C_TRACE_PARENT = "traceparent";

	/** Liste des entêtes sensibles à ne pas logguer. */
	private static final List<String> ENTETES_SENSIBLES = Arrays.asList(HttpHeaders.AUTHORIZATION);

	/** Nombre maximal de caractères de la réponse HTTP dans les logs. */
	private static final int NB_CARACTERES_MAX_DANS_LOG_REPONSE = 1000;

	/** Pattern des URLs du socle. */
	private static final Pattern PATTERN_URL_SOCLE = Pattern.compile("http[s]?://[^/]*/socle/.*");
	/** Préfixe à toutes les URI d'API non exposée sur Internet et utilisée pour des besoins d'administration. */
	public static final String PREFIXE_URI_ADMIN = "/admin/";
	/** Préfixe à toutes les URI d'API non exposée sur Internet et utilisée par un autre micro-service. */
	public static final String PREFIXE_URI_INTERNE = "/interne/";

	/** Préfixe à toutes les URI d'API exposée sur Internet. */
	public static final String PREFIXE_URI_PUBLIC = "/public/";

	/** Type de retour à utiliser pour récupérer l'objet Spring de réponse directement (avec un contenu binaire et donc totalement neutre). */
	public static final TypeReference<HttpResponse<byte[]>> REPONSE_ELLE_MEME = new TypeReference<>() {
	};

	/** Mot de passe certificat client à utiliser. */
	protected String certificatClientMotDePasse;

	/** Client HTTP initialisé à la création de l'instance de client */
	protected HttpClient clientHttp;

	/** Flag demandant la désactivation du contrôle du certificat SSL. */
	protected boolean desactiverSSL;

	/** Hôte du proxy - si présent avec le port, un proxy est défini. */
	protected String hoteDuProxy;

	/**
	 * Logger propre à l'instance réelle héritant de cette classe abstraite.
	 *
	 * Ainsi, il est possible de configurer les logs par classe concrête héritant de cette classe abstraite.
	 *
	 * Donc pas de mot clef "static".
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Mot de passe du proxy - si présent avec le login, une authentification de proxy est définie. */
	protected String motDePasseDuProxy;

	/** Login du proxy - si présent avec le mot de passe, une authentification de proxy est définie. */
	protected String nomUtilisateurDuProxy;

	/** Port du proxy - si présent avec l'hôte, un proxy est défini. */
	protected int portDuProxy;

	/** Token JWT à utiliser dans les appels */
	protected String tokenJwt;

	/** Traceur Sleuth */
	private final Tracer traceur;

	/** Cette URL est le début de l'URL appellée. A chaque appel (Get ou Post ou ...), l'URL fournit est concaténé à cette URL de base. */
	protected String urlDeBase;

	/**
	 * Création du client.
	 *
	 * @param traceur       Traceur Sleuth
	 * @param urlDeBase     Le début de l'URL peut être vide car il est concaténé avec la fin de l'URL fournie à chaque appel.
	 * @param desactiverSSL Flag permettant de désactiver les contrôles SSL (attention, cette modification se fait au niveau STATIC).
	 */
	protected AbstractClientHttp(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		this(traceur, urlDeBase, desactiverSSL, null, 0, null, null);
	}

	/**
	 * Création du client avec un proxy (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param traceur       Traceur Sleuth
	 * @param urlDeBase     Le début de l'URL peut être vide car il est concaténé avec la fin de l'URL fournie à chaque appel.
	 * @param desactiverSSL Flag permettant de désactiver les contrôles SSL (attention, cette modification se fait au niveau STATIC).
	 * @param hoteDuProxy   Hôte du proxy à utiliser (optionnel).
	 * @param portDuProxy   Port du proxy à utiliser (optionnel).
	 */
	protected AbstractClientHttp(Tracer traceur, String urlDeBase, boolean desactiverSSL, String hoteDuProxy, int portDuProxy) {
		this(traceur, urlDeBase, desactiverSSL, hoteDuProxy, portDuProxy, null, null);
	}

	/**
	 * Création du client avec un proxy sécurisé (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param traceur        Traceur Sleuth
	 * @param urlDeBase      Le début de l'URL peut être vide car il est concaténé avec la fin de l'URL fournie à chaque appel.
	 * @param desactiverSSL  Flag permettant de désactiver les contrôles SSL (attention, cette modification se fait au niveau STATIC).
	 * @param hoteDuProxy    Hôte du proxy à utiliser (optionnel).
	 * @param portDuProxy    Port du proxy à utiliser (optionnel).
	 * @param nomUtilisateur Utilisateur à utiliser pour s'authentifier auprès du proxy (optionnel).
	 * @param motDePasse     Mot de passe à utiliser pour s'authentifier auprès du proxy (optionnel).
	 */
	protected AbstractClientHttp(Tracer traceur, String urlDeBase, boolean desactiverSSL, String hoteDuProxy, int portDuProxy, String nomUtilisateur,
			String motDePasse) {
		this.traceur = traceur;
		this.urlDeBase = urlDeBase;
		this.desactiverSSL = desactiverSSL;
		this.hoteDuProxy = hoteDuProxy;
		this.portDuProxy = portDuProxy;
		this.nomUtilisateurDuProxy = nomUtilisateur;
		this.motDePasseDuProxy = motDePasse;

		// Initialisation une bonne fois des éléments mutualisables (performance)
		this.initialiserWebClient();

		// Message d'avertissement concernant les logs TRACE de cette classe.
		if (this.clientDapiTraitantDesDonneesSensiblesOuPersonnelles() && this.logger.isTraceEnabled()) {
			this.logger.warn(
					"Le niveau de log TRACE pour cette classe ne doit EN AUCUN CAS étre actif en production !! (données sensibles/personnelles)");
		}
	}

	/** Ajout des entêtes de traçage. */
	private void ajouterEntetesTraceurs(String url, final HttpRequest.Builder constructeurRequete) {
		if (this.traceur != null) {
			if (!PATTERN_URL_SOCLE.matcher(url).matches()) {
				this.logger.trace("Appel à une API externe donc pas d'entête de trace");
			} else if (this.traceur.currentSpan() != null && this.traceur.currentSpan().context() != null) {
				// voir https://www.w3.org/TR/trace-context/#traceparent-header et
				// https://github.com/w3c/trace-context/blob/main/spec/20-http_request_header_format.md
				String trace = "00-" + this.traceur.currentSpan().context().traceId() + "-" + this.traceur.currentSpan().context().spanId()
						+ "-00";
				constructeurRequete.header(ENTETE_W3C_TRACE_PARENT, trace);
			} else {
				this.logger.trace("Aucun contexte dans le traceur.");
			}
		} else {
			this.logger.warn("Aucun traceur n'est fourni au client. Ceci n'est autorisé que dans les tests automatisés.");
		}
	}

	/** Cette méthode indique si le client HTTP traite de données sensibles ou personnelles. Au besoin, des logs supplémentaires seront générés. */
	public abstract boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles();

	/**
	 * Création d'une requête DELETE.
	 *
	 * @param constructeurRequete le constructeur.
	 */
	private void creerRequeteDelete(final HttpRequest.Builder constructeurRequete) {
		constructeurRequete.DELETE();
	}

	/**
	 * Création d'une requête GET.
	 *
	 * @param constructeurRequete le constructeur.
	 */
	private void creerRequeteGet(final HttpRequest.Builder constructeurRequete) {
		constructeurRequete.GET();
	}

	/**
	 * Création d'une requête POST.
	 *
	 * @param methode             méthode HTTP à utiliser.
	 * @param corps               Corps de la requête (fonction de la méthode).
	 * @param mediaType           MediaType.
	 * @param urlComplete         URL complète.
	 * @param constructeurRequete Constructeur de requête à compléter.
	 */
	private void creerRequetePostPutPatch(HttpMethod methode, Object corps, String mediaType, final String urlComplete,
			HttpRequest.Builder constructeurRequete) {
		try {

			// Le client HTTP de Java ne gère pas les post MULTIPART_FORM_DATA. Donc une méthode d'appel est dédiée
			if (HttpMethod.POST.equals(methode) && MediaType.MULTIPART_FORM_DATA_VALUE.equals(mediaType)) {
				this.preparerRequetePostPourMultipartFormData(constructeurRequete, corps);
			}

			// Le client HTTP de Java ne gère pas les post APPLICATION_FORM_URLENCODED. Donc une méthode d'appel est dédiée
			else if (HttpMethod.POST.equals(methode) && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(mediaType)) {
				this.preparerRequetePostPourFormUrlEncoded(constructeurRequete, corps);
			}

			// Pour traiter le type de média "application/octet-stream" et les corps de type Fichier
			else if (HttpMethod.PUT.equals(methode) && MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(mediaType) && corps != null
					&& corps instanceof Path corpsPath) {
				this.preparerRequetePutPourOctetStream(constructeurRequete, corpsPath);
			}

			// Pour les autres types de média
			else {

				// Pour les corps de type JSON spécifiquement qui ne sont pas déjà des String
				if (!(corps instanceof String) && (mediaType == null || MediaType.APPLICATION_JSON_VALUE.equals(mediaType))) {
					corps = new ObjectMapper().writeValueAsString(corps);
				}
				// Au cas où
				if (corps == null) {
					corps = "";
				}

				constructeurRequete.method(methode.name(), HttpRequest.BodyPublishers.ofString(corps.toString()))//
						.header(HttpHeaders.CONTENT_TYPE, mediaType);
			}

			// Log
			if (this.clientDapiTraitantDesDonneesSensiblesOuPersonnelles()) {
				this.logger.trace("Le corps de la requête \"{}:{}:{}\" est \"xxxx\"", methode, urlComplete, mediaType);
			} else {
				this.logger.trace("Le corps de la requête \"{}:{}:{}\" est \"{}\"", methode, urlComplete, mediaType, corps);
			}
		} catch (final IOException e) {
			throw new ApiClientException(ApiClientException.ERREUR_PREPARATION_APPEL, e, urlComplete);
		}
	}

	/**
	 * Méthode utilisée pour fournir le token JWT à utiliser dans les appels.
	 */
	public void enregistrerTokenJwtAutiliser(String tokenJwt) {
		this.tokenJwt = tokenJwt;
	}

	/**
	 * Exécution de la requete
	 * <ul>
	 * <li>en prenant en compte sa méthode (POST/GET)</li>
	 * <li>retournant une String si le type de retour est vide</li>
	 * <li>retournant un objet Java du type défini par la variable typeRetour</li>
	 * <li>lançant une ApiClientException si le code de retour n'est pas dans la liste des codes attendus</li>
	 * <li>en appliquant la gestion spécifique du proxy décrite dans la documentation de la classe /li>
	 * </ul>
	 *
	 * @param <T>                              Type de retour attendu.
	 * @param url                              Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour                       Type de retour selon Jackson.
	 * @param methode                          Méthode HTTP à utiliser
	 * @param corps                            Corps de la requête POST (null pour les appels GET)
	 * @param codeRetoursAcceptes              Codes de retours acceptés
	 * @param mediaType                        Valeur de l'entête MEDIA_TYPE (uniquement pour les POST et valeur APPLICATION_JSON_VALUE si null)
	 * @param entetes                          Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être
	 *                                         paire.
	 * @param timeoutSpecifique                Timeout en seconde si une valeur spécifique est nécessaire.
	 * @param cheminDuFichierOuEcrireLaReponse Chemin du fichier dans lequel sauvegarder la réponse.
	 * @return L'objet désérialisé
	 */
	@SuppressWarnings("unchecked")
	private <T> T executerRequete(String url, TypeReference<T> typeRetour, HttpMethod methode, Object corps, List<Integer> codeRetoursAcceptes,
			String mediaType, List<String> entetes, Long timeoutSpecifique, Path cheminDuFichierOuEcrireLaReponse) {
		String urlComplete = this.urlDeBase + url;
		this.logger.trace("Appel à l'API \"{}:{}\"", methode, urlComplete);

		// Si pas de mediaType par défaut, c'est du JSON
		if (mediaType == null) {
			mediaType = MediaType.APPLICATION_JSON_VALUE;
		}

		// Préparation de la requête
		HttpRequest.Builder constructeurRequete = HttpClientUtils.creerLeBuilderDeLaRequeteDeBase(timeoutSpecifique)//
				.uri(URI.create(urlComplete))//
				.version(Version.HTTP_1_1);

		// DELETE
		if (HttpMethod.DELETE.equals(methode)) {
			this.creerRequeteDelete(constructeurRequete);
		}
		// POST // PUT (Si nécessaire, traitement du corps de la requête)
		else if (HttpMethod.POST.equals(methode) || HttpMethod.PUT.equals(methode) || HttpMethod.PATCH.equals(methode)) {
			this.creerRequetePostPutPatch(methode, corps, mediaType, urlComplete, constructeurRequete);
		}
		// GET
		else {
			this.creerRequeteGet(constructeurRequete);
		}

		// Ajout des entêtes
		if (entetes != null && !entetes.isEmpty() && entetes.stream().noneMatch(e -> e == null)) {
			constructeurRequete.headers(entetes.toArray(new String[entetes.size()]));
		}
		// Ajout des entêtes Sleuth (https://cloud.spring.io/spring-cloud-sleuth/1.3.x/multi/multi__customizations.html)
		this.ajouterEntetesTraceurs(url, constructeurRequete);

		// Création de la requête HTTP à partir du builder
		HttpRequest requeteHTTP = constructeurRequete.build();
		if (this.logger.isTraceEnabled()) {
			Map<String, List<String>> entetesNonSensibles = this.filtrerEntetesSensibles(requeteHTTP.headers());
			this.logger.trace("Les entêtes de la requête \"{}:{}\" sont \"{}\"", methode, urlComplete, entetesNonSensibles);
		}

		HttpResponse<?> reponse;
		Path temp = null;
		try {
			// Exécution de la requête de type binaire
			if (typeRetour != null && (DONNEES_BINAIRES.equals(typeRetour) || REPONSE_ELLE_MEME.equals(typeRetour))) {
				reponse = this.clientHttp.send(requeteHTTP, HttpResponse.BodyHandlers.ofByteArray());
			}
			// Exécution du téléchargement de fichier
			else if (cheminDuFichierOuEcrireLaReponse != null) {
				temp = Files.createTempFile("tempPsl", cheminDuFichierOuEcrireLaReponse.toFile().getName());
				this.logger.debug("Téléchargement dans le fichier temporaire '{}'", temp);

				reponse = this.clientHttp.send(requeteHTTP, HttpResponse.BodyHandlers.ofFile(temp));

				try {
					Files.move(temp, cheminDuFichierOuEcrireLaReponse, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
				} catch (AtomicMoveNotSupportedException e) {
					this.logger.warn("Déplacement atomique IMPOSSIBLE du fichier temporaire. Ce n'est pas le même lecteur Windows ?");
					Files.move(temp, cheminDuFichierOuEcrireLaReponse, StandardCopyOption.REPLACE_EXISTING);
				}
				this.logger.debug("Déplacement du fichier temporaire '{}' vers la cible '{}'", temp, cheminDuFichierOuEcrireLaReponse);
			}
			// Simple exécution pour le reste
			else {
				reponse = this.clientHttp.send(requeteHTTP, HttpResponse.BodyHandlers.ofString());
			}

			// Log
			this.loggerLaReponse(typeRetour, methode, cheminDuFichierOuEcrireLaReponse, urlComplete, reponse);
		} catch (final ConnectException e) {
			// Si un proxy est défini, il est retiré pour un second test
			if (this.clientHttp.proxy().isPresent()) {
				this.logger.warn("Echec de l'appel à l'API \"{}\" à cause du proxy paramétré. Donc nouvelle tentative sans le proxy",
						LogUtils.nettoyerDonneesAvantDeLogguer(urlComplete));
				this.hoteDuProxy = null;
				this.portDuProxy = 0;
				this.initialiserWebClient();
				return this.executerRequete(url, typeRetour, methode, corps, codeRetoursAcceptes, mediaType, entetes, null, null);
			}
			// Si aucun proxy n'est présent, renvoi de l'erreur
			else {
				throw new ApiClientException(ApiClientException.ERREUR_APPEL, e, urlComplete);
			}
		} catch (IOException | InterruptedException e) {
			throw new ApiClientException(ApiClientException.ERREUR_APPEL, e, urlComplete);
		} finally {
			// Dans tous les cas, si le fichier temporaire est encore là, on le supprime
			if (temp != null && temp.toFile().exists()) {
				try {
					Files.delete(temp);
				} catch (IOException e1) {
					this.logger.warn("Erreur de suppression du fichier temporaire '{}' durant le téléchargement depuis '{}'", temp,
							LogUtils.nettoyerDonneesAvantDeLogguer(urlComplete));
				}
			}
		}

		// Traitement des codes d'erreurs
		if (!codeRetoursAcceptes.contains(reponse.statusCode())) {
			throw new ApiClientException(ApiClientException.ERREUR_CODE_RETOUR, urlComplete, Integer.toString(reponse.statusCode()));
		}

		// Sans transformation, renvoi de la réponse elle-même
		if (typeRetour != null && REPONSE_ELLE_MEME.equals(typeRetour)) {
			return (T) reponse;
		}
		// Sans transformation JSON, renvoi du contenu de la réponse
		else if (typeRetour == null || DONNEES_BINAIRES.equals(typeRetour)) {
			return (T) reponse.body();
		}
		// Transformation dans le type attendu
		else {
			return this.transformerReponseVersTypeAttendu(typeRetour, urlComplete, reponse);
		}
	}

	/**
	 * Exécution de la requete DELETE (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param url     Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param entetes Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 */
	protected void executerRequeteDelete(String url, List<String> entetes) {
		TypeReference<Void> typeDeRetour = new TypeReference<>() {
		};
		this.executerRequete(url, typeDeRetour, HttpMethod.DELETE, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, entetes, null, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param url Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @return L'objet désérialisé
	 */
	protected String executerRequeteGet(String url) {
		return this.executerRequete(url, null, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, null, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param url               Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param timeoutSpecifique Timeout en seconde si une valeur spécifique est nécessaire.
	 * @return L'objet désérialisé
	 */
	protected String executerRequeteGet(String url, Long timeoutSpecifique) {
		return this.executerRequete(url, null, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, timeoutSpecifique, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param url                              Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param timeoutSpecifique                Timeout en seconde si une valeur spécifique est nécessaire.
	 * @param cheminDuFichierOuEcrireLaReponse Chemin du fichier dans lequel sauvegarder la réponse.
	 */
	protected void executerRequeteGet(String url, Long timeoutSpecifique, Path cheminDuFichierOuEcrireLaReponse) {
		this.executerRequete(url, null, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, timeoutSpecifique,
				cheminDuFichierOuEcrireLaReponse);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequeteGet(String url, TypeReference<T> typeRetour) {
		return this.executerRequete(url, typeRetour, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, null, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param parametres Paramètres à ajouter à l'URL avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 * @param entetes    Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequeteGet(String url, TypeReference<T> typeRetour, List<String> parametres, List<String> entetes) {
		String urlAutiliser = HttpClientUtils.creerURL(url, parametres);
		return this.executerRequete(urlAutiliser, typeRetour, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, entetes, null, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>              Type de retour attendu.
	 * @param url              Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour       Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param timoutSpecifique Timeout en seconde si une valeur spécifique est nécessaire.
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequeteGet(String url, TypeReference<T> typeRetour, Long timoutSpecifique) {
		return this.executerRequete(url, typeRetour, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, timoutSpecifique, null);
	}

	/**
	 * Exécution de la requete GET (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param parametres Paramètres à ajouter à l'URL avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequeteGet(String url, TypeReference<T> typeRetour, String... parametres) {
		String urlAutiliser = HttpClientUtils.creerURL(url, parametres);
		return this.executerRequete(urlAutiliser, typeRetour, HttpMethod.GET, null, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, null, null);
	}

	/**
	 * Exécution de la requete PATCH (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param corps      Corps de la requête POST (null pour les appels GET)
	 * @param mediaType  Valeur de l'entête MEDIA_TYPE (uniquement pour les POST et valeur APPLICATION_JSON_VALUE si null)
	 * @param entetes    Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequetePatch(String url, TypeReference<T> typeRetour, Object corps, String mediaType, List<String> entetes) {
		return this.executerRequete(url, typeRetour, HttpMethod.PATCH, corps, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, mediaType, entetes, null, null);
	}

	/**
	 * Exécution de la requete POST (gestion du proxy décrite dans la documentation de la classe) avec entête MEDIA_TYPE à APPLICATION_JSON_VALUE.
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param corps      Corps de la requête POST (null pour les appels GET)
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequetePost(String url, TypeReference<T> typeRetour, Object corps) {
		return this.executerRequete(url, typeRetour, HttpMethod.POST, corps, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, null, null, null, null);
	}

	/**
	 * Exécution de la requete POST (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param corps      Corps de la requête POST (null pour les appels GET)
	 * @param mediaType  Valeur de l'entête MEDIA_TYPE (uniquement pour les POST et valeur APPLICATION_JSON_VALUE si null)
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequetePost(String url, TypeReference<T> typeRetour, Object corps, String mediaType) {
		return this.executerRequete(url, typeRetour, HttpMethod.POST, corps, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, mediaType, null, null, null);
	}

	/**
	 * Exécution de la requete POST (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param <T>        Type de retour attendu.
	 * @param url        Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param typeRetour Type de retour selon Jackson (null pour ne pas parser le type de retour).
	 * @param corps      Corps de la requête POST (null pour les appels GET)
	 * @param mediaType  Valeur de l'entête MEDIA_TYPE (uniquement pour les POST et valeur APPLICATION_JSON_VALUE si null)
	 * @param entetes    Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 * @return L'objet désérialisé
	 */
	protected <T> T executerRequetePost(String url, TypeReference<T> typeRetour, Object corps, String mediaType, List<String> entetes) {
		return this.executerRequete(url, typeRetour, HttpMethod.POST, corps, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, mediaType, entetes, null, null);
	}

	/**
	 * Exécution de la requete POST (gestion du proxy décrite dans la documentation de la classe).
	 *
	 * @param url       Fin de l'URL à appeler (la base a été fournie dans le constructeur).
	 * @param corps     Corps de la requête POST (null pour les appels GET)
	 * @param mediaType Valeur de l'entête MEDIA_TYPE (uniquement pour les POST et valeur APPLICATION_JSON_VALUE si null)
	 * @param entetes   Entêtes à ajouter à la requête avec le code puis la valeur. Le nombre de paramètres doit donc être paire.
	 */
	protected void executerRequetePut(String url, Object corps, String mediaType, List<String> entetes) {
		TypeReference<Void> typeDeRetour = new TypeReference<>() {
		};
		this.executerRequete(url, typeDeRetour, HttpMethod.PUT, corps, CODES_RETOUR_HTTP_ACCEPTES_PAR_DEFAUT, mediaType, entetes, null, null);
	}

	/**
	 * Méthode filtrant les entêtes sensibles à ne pas logguer.
	 *
	 * @param headers Entêtes HTTP.
	 * @return Map d'entête
	 */
	private Map<String, List<String>> filtrerEntetesSensibles(java.net.http.HttpHeaders headers) {
		Map<String, List<String>> copie = new HashMap<>(headers.map());
		for (String clefAretirer : ENTETES_SENSIBLES) {
			if (copie.containsKey(clefAretirer)) {
				copie.replace(clefAretirer, Arrays.asList("***"));
			}
		}
		return copie;
	}

	/**
	 * Initialisation du WebClient (pour ne pas le faire plusieurs fois)
	 */
	private void initialiserWebClient() {
		HttpClient.Builder builderClientHttp = HttpClientUtils.creerLeBuilderDuClientHttp();

		// Gestion du proxy
		this.logger.info("Appels via le proxy {}:{}", this.hoteDuProxy, this.portDuProxy);
		HttpClientUtils.ajouterUnProxy(builderClientHttp, this.hoteDuProxy, this.portDuProxy, this.nomUtilisateurDuProxy, this.motDePasseDuProxy);

		// Désactivation SSL
		if (this.desactiverSSL) {
			HttpClientUtils.desactiverLesVerificationsSSL(builderClientHttp);
		}

		// Création du client
		this.clientHttp = builderClientHttp.build();
	}

	/**
	 * Créer une log avec la réponse en fonction du paramétrage du framework de log et de la confidentialité des données.
	 *
	 * @param <T>                              Type de retour de la requête HTTP.
	 * @param typeRetour                       Type de retour de la requête HTTP.
	 * @param methode                          Méthode HTTP.
	 * @param cheminDuFichierOuEcrireLaReponse Chemin dans lequel est envoyé la réponse (optionnel).
	 * @param urlComplete                      URL complète appelée.
	 * @param reponse                          Réponse obtenue.
	 */
	private <T> void loggerLaReponse(TypeReference<T> typeRetour, HttpMethod methode, Path cheminDuFichierOuEcrireLaReponse, final String urlComplete,
			HttpResponse<?> reponse) {
		if (this.logger.isTraceEnabled()) {
			if (this.clientDapiTraitantDesDonneesSensiblesOuPersonnelles()) {
				this.logger.trace(
						"La réponse à la requête \"{}:{}\" est (code:{}) \"***ce client traite de données personnelles donc pas de contenu dans les logs***\"",
						methode, urlComplete, reponse.statusCode());
			} else {
				String reponseS;
				if (typeRetour != null && (DONNEES_BINAIRES.equals(typeRetour) || REPONSE_ELLE_MEME.equals(typeRetour))) {
					reponseS = "données binaires";
				} else if (cheminDuFichierOuEcrireLaReponse != null) {
					reponseS = cheminDuFichierOuEcrireLaReponse.toFile().getName();
				} else {
					reponseS = (String) reponse.body();
					if (reponseS.length() > NB_CARACTERES_MAX_DANS_LOG_REPONSE) {
						reponseS = reponseS.substring(0, NB_CARACTERES_MAX_DANS_LOG_REPONSE) + "...";
					}
				}
				this.logger.trace("La réponse à la requête \"{}:{}\" est (code:{}) \"{}\"", methode, urlComplete, reponse.statusCode(),
						reponseS.replaceAll("[\r\n]", ""));
			}
		}
	}

	/**
	 * Méthode préparant un appel MultipartFormData avec le client HTTP de Java (qu'il ne gère pas).
	 *
	 * @see https://golb.hplar.ch/2019/01/java-11-http-client.html
	 *
	 * @param constructeurRequete Builder de la requete en cours
	 * @param corps               Corps à envoyer de type MultiValueMap
	 */
	private void preparerRequetePostPourFormUrlEncoded(HttpRequest.Builder constructeurRequete, Object corps) {

		// Vérification que le corps est du bon type
		if (corps == null) {
			throw new ApiClientException(ApiClientException.ERREUR_CORP_INVALIDE);
		}

		String form;
		// Si c'est une string
		if (corps instanceof String corpsString) {
			form = corpsString;
		}
		// Si c'est une Map à transformer en String
		else if (corps instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> donneesAenvoyer = (Map<String, Object>) corps;

			// Sérialisation du formulaire
			form = donneesAenvoyer.entrySet().stream()//
					.filter(e -> e.getKey() != null && e.getValue() != null)//
					.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8))//
					.collect(Collectors.joining("&"));
		}
		// Sinon, erreur
		else {
			throw new ApiClientException(ApiClientException.ERREUR_CORP_INVALIDE);
		}

		// Ajout du BodyPublisher et du ContentType
		constructeurRequete.POST(BodyPublishers.ofString(form))//
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	}

	/**
	 * Méthode préparant un appel MultipartFormData avec le client HTTP de Java (qu'il ne gère pas).
	 *
	 * @see https://golb.hplar.ch/2019/01/java-11-http-client.html
	 *
	 * @param constructeurRequete Builder de la requete en cours
	 * @param corps               Corps à envoyer de type MultiValueMap
	 * @throws IOException
	 */
	private void preparerRequetePostPourMultipartFormData(HttpRequest.Builder constructeurRequete, Object corps) throws IOException {

		// Vérification que le corps est du bon type
		if (!(corps instanceof Map)) {
			throw new ApiClientException(ApiClientException.ERREUR_APPEL);
		}

		// Cast
		@SuppressWarnings("unchecked")
		Map<String, Object> donneesAenvoyer = (Map<String, Object>) corps;

		// Création du boundary
		String boundary = new BigInteger(256, new Random()).toString();
		byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

		// Création du contenu binaire à envoyer
		var byteArrays = new ArrayList<byte[]>();
		for (final Map.Entry<String, Object> entry : donneesAenvoyer.entrySet()) {
			byteArrays.add(separator);

			if (entry.getValue() instanceof Path path) {
				String mimeType = Files.probeContentType(path);
				byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName() + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n")
						.getBytes(StandardCharsets.UTF_8));
				byteArrays.add(Files.readAllBytes(path));
				byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
			} else {
				byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
			}
		}
		byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

		// Ajout du BodyPublisher et du ContentType
		constructeurRequete.POST(BodyPublishers.ofByteArrays(byteArrays))//
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE + ";boundary=" + boundary);
	}

	/**
	 * Méthode préparant un appel OctetStream avec le client HTTP de Java (qu'il ne gère pas).
	 *
	 * @param constructeurRequete Builder de la requete en cours.
	 * @param cheminDuFichier     Chemin du fichier qui contiendra la réponse.
	 * @throws IOException
	 */
	private void preparerRequetePutPourOctetStream(HttpRequest.Builder constructeurRequete, Path cheminDuFichier) throws IOException {

		// Vérification que le corps est du bon type
		if (!(cheminDuFichier instanceof Path) || !cheminDuFichier.toFile().exists()) {
			throw new ApiClientException(ApiClientException.ERREUR_APPEL);
		}

		// Ajout du BodyPublisher et du ContentType
		constructeurRequete.PUT(BodyPublishers.ofFile(cheminDuFichier))//
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	/**
	 * Transformation en DTO et renvoi
	 *
	 * @param <T>         Type attendu
	 * @param typeRetour  Type attendu
	 * @param urlComplete URL appelée
	 * @param reponse     Réponse obtenue
	 * @return le DTO
	 */
	private <T> T transformerReponseVersTypeAttendu(TypeReference<T> typeRetour, final String urlComplete, HttpResponse<?> reponse) {
		if (!StringUtils.hasLength((String) reponse.body())) {
			return null;
		} else {
			try {
				return new ObjectMapper().readValue((String) reponse.body(), typeRetour);
			} catch (final JsonProcessingException e) {
				if (this.logger.isTraceEnabled()) {
					String reponseS = ((String) reponse.body()).length() > 300 ? ((String) reponse.body()).substring(0, 299)
							: (String) reponse.body();

					// Log potentiel de données sensibles mais le format étant invalide, elles ne sont donc valide et le niveau TRACE n'est pas à
					// activer en production
					this.logger.trace("Erreur de parse avec les données {}", reponseS);
				}
				throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, e, urlComplete);
			}
		}
	}
}
