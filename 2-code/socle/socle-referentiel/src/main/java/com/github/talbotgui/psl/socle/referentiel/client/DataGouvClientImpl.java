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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.AdresseEtHorairesGendarmerieDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.InformationSiretDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ProtectionDeCommuneDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.TypeProtectionDeCommune;

import io.micrometer.tracing.Tracer;

@Service
public class DataGouvClientImpl extends AbstractClientHttp implements DataGouvClient {
	/** Code du cache des adresses de gendarmerie. */
	public static final String CODE_CACHE_ADRESSES_GENDARMERIES = "referentiel-dataGouv-adressesGendarmeries";

	/** Code du cache des codes postaux. */
	public static final String CODE_CACHE_CODE_POSTAUX = "referentiel-dataGouv-codePostaux";

	/** Code du cache des protections de commune. */
	public static final String CODE_CACHE_PROTECTION_COMMUNES = "referentiel-dataGouv-protectionCommunes";

	/** Code du cache des SIRETs. */
	public static final String CODE_CACHE_SIRETS = "referentiel-dataGouv-siret";

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** URL du fichier des adresses de gendarmerie. */
	private String urlAdressesGendarmeries;

	/** URL du fichier des codes postaux. */
	private String urlCodePostaux;

	/** URL du fichier des protections de commune. */
	private String urlProtectionsCommunes;

	/** URL du fichier des SIRETs. */
	private String urlSirets;

	@Autowired
	public DataGouvClientImpl(Tracer traceur, @Value("${dataGouv.url.codesPostaux}") String urlCodePostaux,
			@Value("${dataGouv.url.protectionsCommunes}") String urlProtectionsCommunes,
			@Value("${dataGouv.url.adressesGendarmeries}") String urlAdressesGendarmeries, @Value("${dataGouv.url.sirets}") String urlSirets,
			@Value("${dataGouv.desactiverSSL:false}") boolean desactiverSSL, @Value("${dataGouv.proxy.hoteDuProxy:#{null}}") String hoteDuProxy,
			@Value("${dataGouv.proxy.portDuProxy:0}") int portDuProxy, @Value("${dataGouv.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${dataGouv.proxy.motDePasse:#{null}}") String motDePasse) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.urlCodePostaux = urlCodePostaux;
		this.urlProtectionsCommunes = urlProtectionsCommunes;
		this.urlAdressesGendarmeries = urlAdressesGendarmeries;
		this.urlSirets = urlSirets;
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/**
	 * Appel avec gestion du cache si possible.
	 *
	 * @param codeCache         Code du cache.
	 * @param url               URL à appeler.
	 * @param timeoutSpecifique Timeout.
	 * @return Réponse obtenue.
	 */
	private String executerRequeteGet(String codeCache, String url, long timeoutSpecifique) {

		// Appel au cache
		String reponse = new String(this.cache.obtenirContenuDuCacheSiActifEtDisponible(codeCache), StandardCharsets.UTF_8);

		// Si le cache est inactif ou vide
		if (reponse.isEmpty()) {
			// Exécution de la requête
			reponse = super.executerRequeteGet(url, timeoutSpecifique);

			// Sauvegarde de la réponse dans le cache
			this.cache.sauvegarderContenuDuCache(codeCache, reponse.getBytes(StandardCharsets.UTF_8));
		}

		// Renvoi de la réponse
		return reponse;
	}

	@Override
	public Map<String, AdresseEtHorairesGendarmerieDto> telechargerAdressesGendarmeries() {
		this.logger.info("Chargement des adresses de gendarmerie.");

		// Exécution de la requête
		String reponse = this.executerRequeteGet(CODE_CACHE_ADRESSES_GENDARMERIES, this.urlAdressesGendarmeries, 180L);

		// Extraction des codes
		Map<String, AdresseEtHorairesGendarmerieDto> resultats = new HashMap<>();
		int noLigne = 0;
		for (String ligne : reponse.split("\n")) {
			noLigne++;

			// on ne traite pas la premiére ligne des codes postaux
			if (noLigne == 1) {
				continue;
			}

			// Chaque ligne est découpée par des ";"
			String[] cases = ligne.replace("\"", "").split(";");

			// Lecture des champs minimaux
			AdresseEtHorairesGendarmerieDto adresse = new AdresseEtHorairesGendarmerieDto();
			if (cases.length > 3) {
				// L'ID est dans la première case
				adresse.setId(cases[0]);
				// Le nom est dans la 2nd case
				adresse.setNom(cases[1]);
				// L'adresse est dans la 3ème case
				adresse.setAdresse(cases[2]);
				// Le téléphone est dans la 4ème case
				adresse.setTelephone(cases[3]);
				// Ajout aux résultats
				resultats.put(adresse.getId(), adresse);
			} else {
				this.logger.info("Attention en ligne {} : le nombre de cases ne correspond pas à l'attendu", noLigne);
				continue;
			}

			// Les horaires sont dans la 15ème case
			if (cases.length > 14) {
				adresse.setHoraires(cases[14]);
			}
			// L'URL est dans la 16ème case
			if (cases.length > 15) {
				adresse.setUrlSP(cases[15]);
			}

		}

		this.logger.info("  {} gendarmeries présentes dans le référentiel des adresses et horaires des gendarmeries", resultats.size());
		return resultats;

	}

	@Override
	public Map<String, List<String>> telechargerCodesPostaux() {
		this.logger.info("Chargement des codes postaux.");

		// Exécution de la requête
		String reponse = this.executerRequeteGet(CODE_CACHE_CODE_POSTAUX, this.urlCodePostaux, 180L);

		// Extraction des codes
		MultiValueMap<String, String> resultats = new LinkedMultiValueMap<>();
		int noLigne = 0;
		for (String ligne : reponse.split("\n")) {
			noLigne++;

			// on ne traite pas la premiére ligne des codes postaux
			if (noLigne == 1) {
				continue;
			}

			// Chaque ligne est découpée par des ";" et doit compter plus de 3 cases
			String[] cases = ligne.split(";");
			if (cases.length < 3) {
				this.logger.warn("Erreur en ligne {} : le nombre de cases ne correspond pas à l'attendu", noLigne);
				continue;
			}

			// Le code INSEE est dans la première case
			// Le code POSTAL est dans la troisième case
			String codeInsee = cases[0];
			String codePostal = cases[2];

			// On évite les doublons
			if (resultats.get(codeInsee) == null || !resultats.get(codeInsee).contains(codePostal)) {
				resultats.add(codeInsee, codePostal);
			} else {
				this.logger.trace("Doublon en ligne {} : le code postal {} est déjà présent pour le code INSEE {}", noLigne, codePostal, codeInsee);
			}

			// Log Warning ici pour expliquer les incohérences plus tard entre le code postal et le code INSEE
			if (codeInsee != null && codePostal != null && !codeInsee.startsWith(codePostal.substring(0, 1))) {
				this.logger.warn("  Attention en ligne {} car le code INSEE {} n'est pas cohérent avec le code postal {}.", noLigne, codeInsee,
						codePostal);
			}
		}

		this.logger.info("  {} codes INSEE/POSTAUX trouvés", resultats.size());
		return resultats;
	}

	@Override
	public Map<String, ProtectionDeCommuneDto> telechargerProtectionsDeCommune() {
		this.logger.info("Chargement des protections de commune.");

		// Exécution de la requête
		String reponse = this.executerRequeteGet(CODE_CACHE_PROTECTION_COMMUNES, this.urlProtectionsCommunes, 180L);

		// Extraction des codes
		Map<String, ProtectionDeCommuneDto> resultats = new HashMap<>();
		int noLigne = 0;
		for (String ligne : reponse.split("\n")) {
			noLigne++;

			// on ne traite pas la premiére ligne des codes postaux
			if (noLigne == 1) {
				continue;
			}

			// Chaque ligne est découpée par des ";"
			String[] cases = ligne.replace("\"", "").split(";");
			if (cases.length >= 7) {
				// Le code INSEE est dans la premiére case
				String codeInsee = cases[0];
				// Le code du type de protection est dans la troisiéme case
				String type = cases[2];
				// Le nom du protecteur est dans la 5ème case
				String nom = cases[4];
				// L'identifiant est dans la ème case
				String id = cases[3];

				// Dans le cas où il n'y a pas de protecteur, log
				if (!StringUtils.hasLength(type)) {
					this.logger.warn("  Aucune protection n'est renseignée pour la commune '{}'", codeInsee);
				}

				// Si la commune est en double, la première des deux lignes est conservée et les suivantes sont ignorées
				else if (resultats.containsKey(codeInsee)) {
					this.logger.warn("  Protection en double pour la commune '{}' (elle n'est donc pas renseignée) : {} vs {}", codeInsee,
							resultats.get(codeInsee), new ProtectionDeCommuneDto(TypeProtectionDeCommune.valueOf(type), nom, id));
					resultats.remove(codeInsee);
				}

				// Si les données sont cohérentes
				else {
					resultats.put(codeInsee, new ProtectionDeCommuneDto(TypeProtectionDeCommune.valueOf(type), nom, id));
				}
			} else {
				this.logger.warn("  Erreur en ligne {} : le nombre de cases ne correspond pas à l'attendu", noLigne);
			}
		}

		this.logger.info("  {} codes INSEE trouvés dans le référentiel des communes protégées", resultats.size());
		return resultats;
	}

	@Override
	public Collection<InformationSiretDto> telechargerSirets() {
		this.logger.info("Téléchargement du référentiel des SIRETs");
		// Cette méthode télécharge un fichier ZIP de plus de 1Go.
		// Donc pas de chargement d'un si volumineux fichier en mémoire mais tout se fait en ligne à ligne (ou paquet par paquet)

		// Résolution des chemins
		Path cheminDuCache = this.cache.definirNomFichierDunCache(CODE_CACHE_SIRETS);
		Path cheminFichierDezipe = cheminDuCache.getParent().resolve("fichierAtraiter");

		// Si le fichier de données est présent, on le supprimer car ce n'est pas lui le cache
		if (cheminFichierDezipe.toFile().exists() && !cheminFichierDezipe.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_PREPARATION_APPEL, this.urlSirets);
		}

		// Si le fichier de cache est absent, on retélécharge l'archive
		if (!this.cache.verifierActivation() || !cheminDuCache.toFile().exists()) {
			super.executerRequeteGet(this.urlSirets, 1800L, cheminDuCache);
		} else {
			this.logger.info("  Utilisation du cache");
		}

		// Maintenant le fichier de cache est présent (déjà présent ou retéléchargé), donc on extrait son contenu
		this.logger.info("  Extraction de l'archive");
		try (//
				FileInputStream fis = new FileInputStream(cheminDuCache.toFile()); //
				ZipInputStream zis = new ZipInputStream(fis); //
				FileOutputStream fos = new FileOutputStream(cheminFichierDezipe.toFile());//
		) {

			// Il n'y a qu'un unique fichier donc pas de boucle
			zis.getNextEntry();

			// Copie de l'entrée dans un fichier (par lot de 100ko car le fichier est très volumineux)
			int len;
			byte[] buffer = new byte[102400];
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			// Fermeture de l'entrée de l'archive (les Stream seront fermés du fait d'être déclarés dans le try-with-resources)
			zis.closeEntry();

		} catch (IOException e) {
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, e, this.urlSirets);
		}

		// Lecture et parse du fichier
		this.logger.info("  Traitement du contenu extrait de l'archive");
		Collection<InformationSiretDto> sirets = new ArrayList<>();
		try (Stream<String> stream = Files.lines(cheminFichierDezipe, StandardCharsets.UTF_8)) {
			stream.forEach(ligne -> {
				String[] cases = ligne.split(",");
				// Les mairies sont filtrables sur etablissementSiege=true + etatAdministratifEtablissement=A + activitePrincipaleEtablissement=84.11Z
				if (cases.length > 50 && "true".equals(cases[9]) && "A".equals(cases[45]) && "84.11Z".equals(cases[50])) {
					// siret (2), nom (17), codePostal (16), codeInsee (20)
					sirets.add(new InformationSiretDto(cases[2], cases[17], cases[16], cases[20]));
				}
			});
		} catch (IOException e) {
			// Suppression du fichier de données (sans se préoccuper que la suppression fonctionne car on est déjà dans un CATCH
			if (!cheminFichierDezipe.toFile().delete()) {
				this.logger.warn("Echec de la suppression du fichier '{}' durant le traitement d'erreur", cheminFichierDezipe);
			}
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, e, this.urlSirets);
		}

		// Suppression du fichier de données
		if (!cheminFichierDezipe.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, this.urlSirets);
		}

		// Si le cache n'est pas actif, suppression du fichier de cache aussi
		if (!this.cache.verifierActivation() && cheminDuCache.toFile().exists() && !cheminDuCache.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, this.urlSirets);
		}

		return sirets;
	}
}
