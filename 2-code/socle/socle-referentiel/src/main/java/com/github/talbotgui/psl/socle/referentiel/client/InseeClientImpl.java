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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneDelegueeActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ReferentielGeographiqueDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;

import io.micrometer.tracing.Tracer;

/**
 * Chargement du référentiel géographique de l'INSEE
 */
@Service
public class InseeClientImpl extends AbstractClientHttp implements InseeClient {

	/** Code du cache des communes actuelles. */
	public static final String CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE = "referentiel-insee-referentielGeographique";

	/** LOGGER */

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** URL de l'API des communes actuelles */
	@Value("${insee.referentielGeographique.url}")
	private String urlReferentielGeographique;

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
	public InseeClientImpl(Tracer traceur, @Value("${insee.desactiverSSL:false}") boolean desactiverSSL,
			@Value("${insee.proxy.hoteDuProxy:#{null}}") String hoteDuProxy, @Value("${insee.proxy.portDuProxy:0}") int portDuProxy,
			@Value("${insee.proxy.nomUtilisateur:#{null}}") String nomUtilisateur, @Value("${insee.proxy.motDePasse:#{null}}") String motDePasse) {
		// Les différentes URL sont toutes différentes les unes des autres. Donc "".
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/**
	 * Traitement du fichier pour en extraire les communes actuelles.
	 *
	 * @param dto DTO du référentiel.
	 * @param zis ZIP.
	 * @throws IOException Exception.
	 */
	private void extraireLesCommunes(ReferentielGeographiqueDto dto, ZipInputStream zis) throws IOException {
		this.logger.info("Extration des communes");

		// Création de la Map contenant les communes déléguées
		MultiValueMap<String, CommuneDelegueeActuelleDto> mapCommunesDeleguees = new LinkedMultiValueMap<>();

		// Déclaration du traitement à réaliser pour chaque ligne
		TraitementUnitaireCsv traitement = elements -> {
			// Le CSV contient 12 éléments par ligne mais le dernier peut être vide.
			// Le type de commune est en position 0 (COMmune, COMmuneDeleguee ou ARondisseMent)
			// Le code INSEE de la commune est en position 1
			// Le libelle de la commune est en position 8
			if (elements.length > 8 && "COM".equals(elements[0])) {
				// Le code INSEE du département est en position 3
				dto.getCommunesActuelles().put(elements[1], new CommuneActuelleDto(elements[1], elements[8], elements[3]));
			} else if (elements.length > 11 && !"COM".equals(elements[0]) && elements[11] != null) {
				// Le code INSEE de la commune parente est en position 11
				mapCommunesDeleguees.add(elements[11], new CommuneDelegueeActuelleDto(elements[1], elements[8]));
			} else if (this.logger.isWarnEnabled()) {
				this.logger.warn("  ligne de commune non traitée : {}", String.join(",", elements));
			}
		};

		// Traitement du CSV
		this.traiterUnCsv(traitement, zis);

		// Ajout des communes déléguées dans les communes
		for (CommuneActuelleDto commune : dto.getCommunesActuelles().values()) {
			commune.setCommunesDeleguees(mapCommunesDeleguees.get(commune.getCodeInsee()));
		}

		this.logger.info("  {} communes trouvées", dto.getCommunesActuelles().size());
	}

	/**
	 * Traitement du fichier pour en extraire les départements actuels.
	 *
	 * @param dto DTO du référentiel.
	 * @param zis ZIP.
	 * @throws IOException Exception.
	 */
	private void extraireLesDepartements(ReferentielGeographiqueDto dto, ZipInputStream zis) throws IOException {
		this.logger.info("Extration des départements");

		// Déclaration du traitement à réaliser pour chaque ligne
		TraitementUnitaireCsv traitement = elements -> {
			// Le premier élément de l'entête est DEP
			if ("DEP".equals(elements[0])) {
				// rien à faire
			}
			// Le CSV contient 7 éléments par ligne
			else if (elements.length == 7) {
				// Le code INSEE est en position 0
				// Le libellé est en position 6
				// Le code INSEE de la région est en position 1
				// Le code INSEE de la commune chef lieu est en position 2
				dto.getDepartementsActuels().put(elements[0], new DepartementActuelDto(elements[0], elements[6], elements[1], elements[2]));
			}
			// Au cas où
			else if (this.logger.isWarnEnabled()) {
				this.logger.warn("  Ligne de département non traitée : {}", String.join(";", elements));
			}
		};

		// Traitement du CSV
		this.traiterUnCsv(traitement, zis);

		this.logger.info("  {} départements trouvés", dto.getDepartementsActuels().size());
	}

	/**
	 * Traitement du fichier pour en extraire les pays actuels.
	 *
	 * @param dto DTO du référentiel.
	 * @param zis ZIP.
	 * @throws IOException Exception.
	 */
	private void extraireLesPays(ReferentielGeographiqueDto dto, ZipInputStream zis) throws IOException {
		this.logger.info("Extration des pays");

		// Déclaration du traitement à réaliser pour chaque ligne
		TraitementUnitaireCsv traitement = elements -> {
			// L'entête commence par "COG"
			// Les pays actifs ont la valeur "1" en position 1
			if ("COG".equals(elements[0]) || elements.length > 1 && !"1".equals(elements[1])) {
				// Rien à faire
			}
			// Le CSV contient 11 éléments par ligne en théorie mais 9 suffisent
			else if (elements.length > 8) {
				// Le code INSEE est en position 0
				// Le libellé simple est en position 5
				// Le code officiel en 2 caractères
				dto.getPaysActuels().put(elements[0], new PaysActuelDto(elements[0], elements[5], elements[8]));
			}
			// Au cas où
			else if (this.logger.isWarnEnabled()) {
				this.logger.warn("  Ligne de pays non traitée : {}", String.join(";", elements));
			}
		};

		// Traitement du CSV
		this.traiterUnCsv(traitement, zis);

		this.logger.info("  {} pays trouvés", dto.getPaysActuels().size());
	}

	/**
	 * Traitement du fichier pour en extraire les régions actuelles.
	 *
	 * @param dto DTO du référentiel.
	 * @param zis ZIP.
	 * @throws IOException Exception.
	 */
	private void extraireLesRegions(ReferentielGeographiqueDto dto, ZipInputStream zis) throws IOException {
		this.logger.info("Extration des régions");

		// Déclaration du traitement à réaliser pour chaque ligne
		TraitementUnitaireCsv traitement = elements -> {
			// Le premier élément de l'entête est REG
			if ("REG".equals(elements[0])) {
				// rien à faire
			}
			// Le CSV contient 6 éléments par ligne
			else if (elements.length == 6) {
				// Le code INSEE est en position 0
				// Le libellé est en position 5
				// Le code INSEE du chef lieu est en position 1
				dto.getRegionsActuelles().put(elements[0], new RegionActuelleDto(elements[0], elements[5], elements[1]));
			}
			// Au cas où
			else if (this.logger.isWarnEnabled()) {
				this.logger.warn("  Ligne de région non traitée : {}", String.join(";", elements));
			}
		};

		// Traitement du CSV
		this.traiterUnCsv(traitement, zis);

		this.logger.info("  {} régions trouvées", dto.getRegionsActuelles().size());
	}

	@Override
	public ReferentielGeographiqueDto telechargerLeReferentielGeographique() {

		this.logger.info("Chargement du référentiel géographique de l'INSEE");

		// Exécution de la requête (ou cache)
		ReferentielGeographiqueDto reponseTypee = this.telechargerLesCommunesActuellesDepuisLinsee();

		// Création des liens (après le chargement du cache car les liens y sont supprimés)
		reponseTypee.crerLesLiensEntreLesElements();

		// Renvoi de la réponse
		return reponseTypee;
	}

	/**
	 * Téléchargement du ZIP et extraction des données.
	 *
	 * @return
	 */
	private ReferentielGeographiqueDto telechargerLesCommunesActuellesDepuisLinsee() {

		// Initialisation du DTO global
		ReferentielGeographiqueDto dto = new ReferentielGeographiqueDto();

		// Si le cache est disponible, on le prend
		byte[] reponse = null;
		if (this.cache.verifierActivation() && this.cache.definirNomFichierDunCache(CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE).toFile().exists()) {
			reponse = this.cache.obtenirContenuDuCacheSiActifEtDisponible(CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE);
		}
		// Sinon téléchargement du ZIP et sauvegarde en cache
		else {
			reponse = super.executerRequeteGet(this.urlReferentielGeographique, AbstractClientHttp.DONNEES_BINAIRES);
			this.cache.sauvegarderContenuDuCache(CODE_CACHE_REFERENTIEL_GEOGRAPHIQUE, reponse);
		}

		// Ouverture du ZIP
		try (ByteArrayInputStream bais = new ByteArrayInputStream(reponse); ZipInputStream zis = new ZipInputStream(bais)) {

			// Pour chaque entrée du ZIP
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {

				// Pour le fichier des pays (pays actuels uniquement)
				if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("pays_20")) {
					this.extraireLesPays(dto, zis);
				}

				// Pour le fichier des régions (le premier élément de l'entête est REG)
				else if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("region_20")) {
					this.extraireLesRegions(dto, zis);
				}

				// Pour le fichier des départements (le premier élément de l'entête est DEP)
				else if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("departement_20")) {
					this.extraireLesDepartements(dto, zis);
				}

				// Pour le fichier des communes (le premier élément de chaque commune est "COM")
				else if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("commune_20")) {
					this.extraireLesCommunes(dto, zis);
				}

				// Sinon, on ignore les autres fichiers
				else {
					// rien à faire
				}

				// On passe à la suite
				zipEntry = zis.getNextEntry();
			}

		} catch (IOException e) {
			throw new ApiClientException(ApiClientException.ERREUR_APPEL, e, this.urlReferentielGeographique);
		}

		// Renvoi de l'ensemble
		return dto;
	}

	/**
	 * Méthode utilitaire permettant de traiter un ZIP simple dont les champs sont séparés par des ",".
	 *
	 * @param traitement Traitement à réaliser pour chaque ligne du CSV.
	 * @param zis        Zip à traiter.
	 * @throws IOException Exception possible à la lecture.
	 */
	private void traiterUnCsv(TraitementUnitaireCsv traitement, ZipInputStream zis) throws IOException {
		// Le CSV est traité, en mémoire, en entier
		String contenuCsv = new String(zis.readAllBytes(), StandardCharsets.UTF_8).replace("\r", "");
		// Découpage par ligne
		for (String ligne : contenuCsv.split("\n")) {
			// Découpage des cases séparées par une virgule
			// le premier replaceAll traite des chaines de caractères "...,..." existant dans le fichier des pays
			// le second remplace les double-espace par des simple
			String[] elements = ligne.replaceAll(",\"([^,]*),([^,]*)\"", ",$1 $2").replace("  ", " ").split(",");
			// Traitement des cases
			traitement.traiter(elements);
		}
	}

}
