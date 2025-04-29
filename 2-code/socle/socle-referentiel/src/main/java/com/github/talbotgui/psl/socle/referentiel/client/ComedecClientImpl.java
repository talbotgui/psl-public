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
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.utils.ChecksumUtils;
import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.DonneeReferentielComedec;
import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.DonneesComedec;
import com.github.talbotgui.psl.socle.referentiel.exception.ReferentielException;

import io.micrometer.tracing.Tracer;

/**
 * Chargement du référentiel des abonnés HUBEE
 */
@Service
public class ComedecClientImpl extends AbstractClientHttp implements ComedecClient {

	/** Code du cache du ZIP Mairie. */
	private static final String CODE_CACHE_REQUETE_MAIRIE = "referentiel-comedec-mairie";
	/** Code du cache du ZIP Notaire. */
	private static final String CODE_CACHE_REQUETE_NOT = "referentiel-comedec-not";
	/** Code du cache du ZIP TES. */
	private static final String CODE_CACHE_REQUETE_TES = "referentiel-comedec-tes";

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ComedecClientImpl.class);

	/** Code présent dans le CSV : MAIRIE connecté DECES. */
	private static final String MAIRIE_VAD = "MAIRIE:VAD";
	/** Code présent dans le CSV : MAIRIE connecté MARIAGE. */
	private static final String MAIRIE_VAM = "MAIRIE:VAM";
	/** Code présent dans le CSV : MAIRIE connecté NAISSANCE. */
	private static final String MAIRIE_VAN = "MAIRIE:VAN";
	/** Code présent dans le CSV : NOTAIRE connecté DECES. */
	private static final String NOT_VAD = "NOT:VAD";
	/** Code présent dans le CSV : NOTAIRE connecté MARIAGE. */
	private static final String NOT_VAM = "NOT:VAM";
	/** Code présent dans le CSV : NOTAIRE connecté NAISSANCE. */
	private static final String NOT_VAN = "NOT:VAN";
	/** Code présent dans le CSV : ANTS connecté NAISSANCE. */
	private static final String TES_VAN = "TES:VAN";

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** URL COMEDEC MAIRIE */
	@Value("${comedec.mairie.url}")
	private String urlComedecMairie;

	/** URL SHA COMEDEC MAIRIE */
	@Value("${comedec.mairie.url.sha}")
	private String urlComedecMairieSha;

	/** URL COMEDEC NOT */
	@Value("${comedec.not.url}")
	private String urlComedecNot;

	/** URL SHA COMEDEC NOT */
	@Value("${comedec.not.url.sha}")
	private String urlComedecNotSha;

	/** URL COMEDEC TES */
	@Value("${comedec.tes.url}")
	private String urlComedecTes;

	/** URL SHA COMEDEC TES */
	@Value("${comedec.tes.url.sha}")
	private String urlComedecTesSha;

	/**
	 * Constructeur.
	 *
	 * @param desactiverSSL  Flag demandant la désactivation du contrôle du certificat SSL.
	 * @param hoteDuProxy    Hôte du proxy - si présent avec le port, un proxy est défini.
	 * @param portDuProxy    Port du proxy - si présent avec l'hôte, un proxy est défini.
	 * @param nomUtilisateur Login du proxy - si présent avec le mot de passe, une authentification de proxy est définie.
	 * @param motDePasse     Mot de passe du proxy - si présent avec le login, une authentification de proxy est définie.
	 */
	@Autowired
	public ComedecClientImpl(Tracer traceur, @Value("${comedec.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${comedec.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${comedec.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${comedec.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${comedec.proxy.motDePasse:#{null}}") String motDePasse) {
		// Les différentes URL sont toutes différentes les unes des autres. Donc "".
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/**
	 * Extraction de l'archive.
	 *
	 * @param url                 URL du fichier (pour les logs).
	 * @param cheminDuCache       Chemin du fichier ZIP (de cache).
	 * @param cheminFichierDezipe Chemin du fichier dézippé.
	 */
	private void extractionDuContenuDeLarchive(String url, Path cheminDuCache, Path cheminFichierDezipe) {
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
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, e, url);
		}
	}

	@Override
	public List<DonneeReferentielComedec> telechargerReferentielComedec() {
		this.logger.info("Téléchargement du référentiel COMEDEC des mairies");

		// Initialisation de la liste de résultats
		List<DonneeReferentielComedec> donnees = new ArrayList<>();

		// Intégration du fichier des mairies
		this.telechargerUnFichier(donnees, CODE_CACHE_REQUETE_MAIRIE, this.urlComedecMairie, this.urlComedecMairieSha);
		// Intégration du fichier des notaires
		this.telechargerUnFichier(donnees, CODE_CACHE_REQUETE_NOT, this.urlComedecNot, this.urlComedecNotSha);
		// Intégration du fichier de TES
		this.telechargerUnFichier(donnees, CODE_CACHE_REQUETE_TES, this.urlComedecTes, this.urlComedecTesSha);

		return donnees;
	}

	/**
	 * Téléchargement d'un fichier de données COMEDEC.
	 *
	 * @param donnees   Données déjà intégrées plus les données récupérées.
	 * @param codeCache Code du cache pour le ZIP
	 * @param url       URL du ZIP
	 * @param urlSha    URL du SHA
	 */
	private void telechargerUnFichier(List<DonneeReferentielComedec> donnees, String codeCache, String url, String urlSha) {
		// Résolution des chemins
		Path cheminDuCache = this.cache.definirNomFichierDunCache(codeCache);
		Path cheminFichierDezipe = cheminDuCache.getParent().resolve("fichierAtraiter");

		// Si le fichier de données est présent, on le supprimer car ce n'est pas lui le cache
		if (cheminFichierDezipe.toFile().exists() && !cheminFichierDezipe.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_PREPARATION_APPEL, url);
		}

		// Si le fichier de cache est absent, on retélécharge l'archive et le SHA
		if (!this.cache.verifierActivation() || !cheminDuCache.toFile().exists()) {

			// Téléchargement du fichier
			super.executerRequeteGet(url, 1800L, cheminDuCache);

			// Téléchargement du SHA
			String sha = super.executerRequeteGet(urlSha);

			// Contrôle du SHA
			String shaCalcule = ChecksumUtils.creerChecksumSha256(cheminDuCache.toFile());
			if (sha == null || !sha.toUpperCase().equals(shaCalcule.toUpperCase())) {
				LOGGER.warn("Erreur de SHA1 pour l'URL '{}' : '{}' vs '{}'", url, sha, shaCalcule);
				throw new ReferentielException(ReferentielException.ERREUR_APPEL_EXTERNE);
			}
		}

		// Sinon on utilise le cache déjà présent
		else {
			this.logger.info("  Utilisation du cache {}", codeCache);
		}

		// Maintenant le fichier de cache est présent (déjà présent ou retéléchargé), donc on extrait son contenu
		this.extractionDuContenuDeLarchive(url, cheminDuCache, cheminFichierDezipe);

		// Lecture et parse du fichier
		this.logger.info("  Traitement du contenu extrait de l'archive");
		try (Stream<String> stream = Files.lines(cheminFichierDezipe, StandardCharsets.UTF_8)) {
			stream.forEach(ligne -> this.traitementDeChaqueLigneCsv(donnees, codeCache, ligne));
		} catch (IOException e) {
			// Suppression du fichier de données (sans se préoccuper que la suppression fonctionne car on est déjà dans un CATCH
			if (!cheminFichierDezipe.toFile().delete()) {
				this.logger.warn("Echec de la suppression du fichier '{}' durant le traitement d'erreur", cheminFichierDezipe);
			}
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, e, url);
		}

		// Résultat du traitement
		this.logger.info("  Après analyse, {} communes obtenues", donnees.size());

		// Suppression du fichier de données
		if (!cheminFichierDezipe.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, url);
		}

		// Si le cache n'est pas actif, suppression du fichier de cache aussi
		if (!this.cache.verifierActivation() && cheminDuCache.toFile().exists() && !cheminDuCache.toFile().delete()) {
			throw new ApiClientException(ApiClientException.ERREUR_TRAITEMENT_REPONSE, url);
		}
	}

	/**
	 * Traitement unitaire de chaque ligne du CSV.
	 *
	 * @param donnees   Données déjà intégrées plus les données récupérées.
	 * @param codeCache Code du cache du ZIP.
	 * @param ligne     Ligne à traiter.
	 */
	private void traitementDeChaqueLigneCsv(List<DonneeReferentielComedec> donnees, String codeCache, String ligne) {
		String[] cases = ligne.split(";");

		// Recherche de la commune dans les données (ou création de la donnée)
		String codeInsee = cases[3];
		DonneeReferentielComedec donneesCommune = donnees.stream()//
				.filter(d -> d.codeInsee() != null && d.codeInsee().equals(codeInsee))//
				.findFirst().orElseGet(() -> {
					DonneeReferentielComedec nouvelleLigne = new DonneeReferentielComedec(cases[0], cases[1], cases[2], codeInsee,
							new DonneesComedec());
					donnees.add(nouvelleLigne);
					return nouvelleLigne;
				});

		// Préparation des données COMEDEC
		if (CODE_CACHE_REQUETE_MAIRIE.equals(codeCache)) {
			donneesCommune.comedec().setMairieDeces(ligne.contains(MAIRIE_VAD));
			donneesCommune.comedec().setMairieNaissance(ligne.contains(MAIRIE_VAN));
			donneesCommune.comedec().setMairieMariage(ligne.contains(MAIRIE_VAM));
		}
		if (CODE_CACHE_REQUETE_NOT.equals(codeCache)) {
			donneesCommune.comedec().setNotaireDeces(ligne.contains(NOT_VAD));
			donneesCommune.comedec().setNotaireNaissance(ligne.contains(NOT_VAN));
			donneesCommune.comedec().setNotaireMariage(ligne.contains(NOT_VAM));
		}
		if (CODE_CACHE_REQUETE_TES.equals(codeCache)) {
			donneesCommune.comedec().setTesNaissance(ligne.contains(TES_VAN));
		}
	}
}
