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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysEtNationaliteDto;

import io.micrometer.tracing.Tracer;

@Service
public class AutreClientImpl extends AbstractClientHttp implements AutreClient {
	/** Code du cache des pays et nationalités. */
	public static final String CODE_CACHE_PAYS_NATIONNALITE = "referentiel-autre-paysEtNationalites";

	/** Service de cache */
	@Autowired
	private CacheDeFichierService cache;

	/** URL de l'API des adresses de gendarmerie. */
	private String urlPaysEtNationalites;

	@Autowired
	public AutreClientImpl(Tracer traceur, @Value("${autre.nationalites.url}") String urlPaysEtNationalites,
			@Value("${autre.desactiverSSL:false}") boolean desactiverSSL, @Value("${autre.proxy.hoteDuProxy:#{null}}") String hoteDuProxy,
			@Value("${autre.proxy.portDuProxy:0}") int portDuProxy, @Value("${autre.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${autre.proxy.motDePasse:#{null}}") String motDePasse) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.urlPaysEtNationalites = urlPaysEtNationalites;
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
	private byte[] executerRequeteGet(String codeCache, String url, long timeoutSpecifique) {

		// Appel au cache
		byte[] reponse = this.cache.obtenirContenuDuCacheSiActifEtDisponible(codeCache);

		// Si le cache est inactif ou vide
		if (reponse == null || reponse.length == 0) {
			// Exécution de la requête
			reponse = super.executerRequeteGet(url, AbstractClientHttp.DONNEES_BINAIRES, timeoutSpecifique);

			// Sauvegarde de la réponse dans le cache
			this.cache.sauvegarderContenuDuCache(codeCache, reponse);
		}

		// Renvoi de la réponse
		return reponse;
	}

	/** Petite méthode pour ne pas récupérer les valeurs vides ou bidon ou absente. */
	private String extraireValeur(Row ligne, int i) {

		// Si la case n'existe pas
		if (ligne.getLastCellNum() < i) {
			return null;
		}

		// Lecture de la valeur
		String valeur = ligne.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

		// Gestion des valeurs vides ou bidons
		if (valeur == null || valeur.length() == 0 || "-".equals(valeur)) {
			return null;
		} else {
			return valeur;
		}

	}

	@Override
	public Map<String, PaysEtNationaliteDto> telechargerReferentielPaysEtANationalites() {
		this.logger.info("Chargement des pays et nationalités.");

		// Exécution de la requête
		byte[] reponse = this.executerRequeteGet(CODE_CACHE_PAYS_NATIONNALITE, this.urlPaysEtNationalites, 180L);

		// Extraction des données
		Map<String, PaysEtNationaliteDto> resultats = new HashMap<>();

		// Création du workbook pour lire le fichier
		try (Workbook excel = WorkbookFactory.create(new ByteArrayInputStream(reponse))) {

			// Vérification du fichier
			if (excel.getNumberOfSheets() != 1) {
				this.logger.error("Fichier Excel contenant {} onglet(s) et non un seul", excel.getNumberOfSheets());
				return resultats;
			}

			// Récupération de l'onglet
			Sheet sheet = excel.getSheetAt(0);

			// Parcours des lignes
			for (Iterator<Row> iter = sheet.iterator(); iter.hasNext();) {
				Row ligne = iter.next();

				// Les 4 premières lignes sont inutiles (rowNum commence à 0)
				if (ligne.getRowNum() < 4) {
					continue;
				}

				// Vérification de la ligne (LastCellNum commence à 1)
				// Ne sont acceptées que les lignes sans la dernière case (au pire)
				if (ligne.getLastCellNum() < 8) {
					this.logger.warn("  La ligne {} ne contient que {} cellule(s)", ligne.getRowNum() + 1, ligne.getLastCellNum());
					continue;
				}

				// Ensuite, on récupère les données (en sautant les 3ème, 5ème et 7ème colonnes)
				int i = 0;
				String code2caracteres = this.extraireValeur(ligne, i++);
				String code3caracteres = this.extraireValeur(ligne, i++);
				i++;
				String nom = this.extraireValeur(ligne, i++);
				i++;
				String capitale = this.extraireValeur(ligne, i++);
				i++;
				String continent = this.extraireValeur(ligne, i++);
				String nationalite = this.extraireValeur(ligne, i);

				// Création de l'objet
				resultats.put(code2caracteres, new PaysEtNationaliteDto(code2caracteres, code3caracteres, nom, capitale, continent, nationalite));
			}
		} catch (IOException e) {
			this.logger.error("Fichier impossible à lire", e);
			return resultats;
		}

		this.logger.info("  {} pays et nationalités présents dans le référentiel ", resultats.size());
		return resultats;
	}
}
