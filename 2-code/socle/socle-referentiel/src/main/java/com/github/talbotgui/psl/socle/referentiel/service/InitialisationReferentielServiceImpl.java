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
package com.github.talbotgui.psl.socle.referentiel.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.AbonneHubeeDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.AdresseEtHorairesGendarmerieDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ITransformableEnDocumentLuceneDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.InformationSiretDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysEtNationaliteDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ProtectionDeCommuneDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ReferentielGeographiqueDto;
import com.github.talbotgui.psl.socle.referentiel.client.AutreClient;
import com.github.talbotgui.psl.socle.referentiel.client.ComedecClient;
import com.github.talbotgui.psl.socle.referentiel.client.DataGouvClient;
import com.github.talbotgui.psl.socle.referentiel.client.HubeeClient;
import com.github.talbotgui.psl.socle.referentiel.client.InseeClient;
import com.github.talbotgui.psl.socle.referentiel.client.InseeOidcClient;
import com.github.talbotgui.psl.socle.referentiel.dto.oauth2.Oauth2TokenDto;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexationException;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

/**
 * <pre>
 * Organisation de la classe :
 *  - une méthode par groupe logique (commune, géographique, ...).
 *  - une méthode par téléchargement pour gérer les erreurs
 * </pre>
 */
@Service
public class InitialisationReferentielServiceImpl implements InitialisationReferentielService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InitialisationReferentielServiceImpl.class);

	@Autowired
	private AutreClient autre;

	@Autowired
	private DataGouvClient clientDataGouv;

	@Autowired
	private InseeClient clientInsee;

	@Autowired
	private InseeOidcClient clientOidcInsee;

	@Autowired
	private ComedecClient comedecClient;

	@Autowired
	private HubeeClient hubeeClient;

	@Autowired
	private IndexeurService indexeur;

	// pas de valeur par défaut pour forcer la présence d'un fichier de configuration 'application.properties'
	@Value("${controleur.nbResultatsMax}")
	private int nbMaxResultats;

	/**
	 * Génération du token INSEE.
	 *
	 * @param erreursDeTraitement Erreurs de traitement.
	 * @return Le token obtenu ou null.
	 */
	private Oauth2TokenDto creerTokenInsee(List<String> erreursDeTraitement) {
		try {
			return this.clientOidcInsee.genererToken();
		} catch (Exception e) {
			LOGGER.error("Erreur à la création du token INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return null;
		}
	}

	/**
	 * Sauvegarde des données dans l'indexeur.
	 *
	 * @param listeAindexr        Données à indexr.
	 * @param erreursDeTraitement Erreurs de traitement.
	 */
	private void initialiserIndex(Collection<? extends ITransformableEnDocumentLuceneDto> listeAindexr, List<String> erreursDeTraitement) {
		try {
			this.indexeur.initialiserIndex(listeAindexr);
		} catch (Exception e) {
			LOGGER.error("Erreur à l'initialisation de l'index", e);
			erreursDeTraitement.add(e.getMessage());
		}
	}

	private void initialiserIndexCommunesUgle(Oauth2TokenDto tokenInsee, List<String> erreursDeTraitement) {
		try {
			List<CommuneUgleDto> liste = this.clientOidcInsee.telechargerLesCommunesUgle(tokenInsee);
			this.initialiserIndex(liste, erreursDeTraitement);
		} catch (Exception e) {
			LOGGER.error("Erreur à l'initialisation des communes ugles", e);
			erreursDeTraitement.add(e.getMessage());
		}
	}

	private void initialiserIndexPaysNaissance(Oauth2TokenDto tokenInsee, List<String> erreursDeTraitement) {
		try {
			List<PaysNaissanceDto> liste = this.clientOidcInsee.telechargerLesPaysDeNaissance(tokenInsee);
			this.indexeur.initialiserIndex(liste);
		} catch (Exception e) {
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
		}
	}

	/**
	 * Initialisation des indexes liés au référentiel géographique.
	 *
	 * @param codesInseeVsPostaux Codes INSEE et codes postaux
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private void initialiserLeReferentielGeographique(Map<String, List<String>> codesInseeVsPostaux, List<String> erreursDeTraitement) {

		// Téléchargement du référentiel géographique
		ReferentielGeographiqueDto refGeo;
		try {
			// Téléchargement du référentiel géographique
			// En cas d'echec, on arrete le traitement
			refGeo = this.clientInsee.telechargerLeReferentielGeographique();
		} catch (Exception e) {
			LOGGER.error("Erreur au telechargement du référentiel géographique de l'INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return;
		}

		// log
		LOGGER.info("Début de l'intégration des données dans le référentiel géographique");

		// Ajout des codes postaux conservés du début
		refGeo.ajouterLesCodesPostaux(codesInseeVsPostaux);

		// Ajout des données comedec
		refGeo.ajouterLesDonneesComedec(this.comedecClient.telechargerReferentielComedec());

		// Téléchargement des nationalités
		Map<String, PaysEtNationaliteDto> nationalites = this.telechargerPays(erreursDeTraitement);

		// Ajout des nationalités aux pays du référentiel
		if (nationalites != null) {
			refGeo.ajouterLesNationalites(nationalites);
		}

		// Téléchargement du référentiel des protections de communes
		Map<String, ProtectionDeCommuneDto> codesInseeVsProtectionsDeCommunes = this.telechargerProtectionsDeCommune(erreursDeTraitement);

		// Téléchargement du référentiel des adresses et horaires de gendarmerie
		Map<String, AdresseEtHorairesGendarmerieDto> idGendarmerieVsAdresseEtHoraires = this.telechargerAdressesGendarmeries(erreursDeTraitement);

		// Ajout de la protection
		refGeo.ajouterLesProtectionsDeCommune(codesInseeVsProtectionsDeCommunes, idGendarmerieVsAdresseEtHoraires);

		// Téléchargement des SIRETs
		Collection<InformationSiretDto> sirets = this.telechargerSirets(erreursDeTraitement);

		// Ajout des SIRET
		refGeo.ajouterLesSiretsDeCommune(sirets);

		// Téléchargement des abonnés HUBEE
		Map<String, List<AbonneHubeeDto>> abonnes = this.hubeeClient.telechargerLeReferentielDesAbonnesActifsHubee();

		// Intégration des abonnés
		refGeo.ajouterAbonnesHubee(abonnes);

		// Recherche et log d'anomalies
		refGeo.rechercherDesAnomalies();

		// log
		LOGGER.info("Début de l'indexation des données du référentiel géographique");

		// Indexation des données
		this.initialiserIndex(refGeo.getPaysActuels().values(), erreursDeTraitement);
		this.initialiserIndex(refGeo.getRegionsActuelles().values(), erreursDeTraitement);
		this.initialiserIndex(refGeo.getDepartementsActuels().values(), erreursDeTraitement);
		this.initialiserIndex(refGeo.getCommunesActuelles().values(), erreursDeTraitement);
	}

	@Override
	public void initialiserLesIndexes() {
		LOGGER.info("Initialisation des indexes");
		List<String> erreursDeTraitement = new ArrayList<>();

		// Création du token pour appeler les APIs de l'INSEE
		Oauth2TokenDto tokenInsee = this.creerTokenInsee(erreursDeTraitement);

		// Téléchargement de la map de codes postaux et de codes INSEE (venant de DataGouv)
		Map<String, List<String>> codesInseeVsPostaux = this.telechargerLesCodesPostauxDepuisDataGouv(erreursDeTraitement);

		// Initialisation des indexes des communes de naissance
		this.initialiserLesIndexesDesCommunesDeNaissance(tokenInsee, codesInseeVsPostaux, erreursDeTraitement);

		// Initialisation de l'index des communes UGLE
		this.initialiserIndexCommunesUgle(tokenInsee, erreursDeTraitement);

		// Initialisation de l'index des pays de naissance
		this.initialiserIndexPaysNaissance(tokenInsee, erreursDeTraitement);

		// Initialisation des indexes de données géographiques (et données associées)
		this.initialiserLeReferentielGeographique(codesInseeVsPostaux, erreursDeTraitement);

		// Si des erreurs sont détectées
		if (!erreursDeTraitement.isEmpty()) {
			throw new IndexationException(IndexationException.ERREUR_INITIALISATION_INDEXES, erreursDeTraitement.size(),
					String.join("\n", erreursDeTraitement));
		}

		// sinon
		else {
			LOGGER.info("Initialisation réalisée avec succés");
		}
	}

	/**
	 * Initialisation de l'index des communes de naissance.
	 *
	 * @param tokenInsee          Token d'appel à l'INSEE.
	 * @param codesInseeVsPostaux Codes INSEE et codes postaux
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private void initialiserLesIndexesDesCommunesDeNaissance(Oauth2TokenDto tokenInsee, Map<String, List<String>> codesInseeVsPostaux,
			List<String> erreursDeTraitement) {

		// Si pas de token, impossible de rien faire
		if (tokenInsee == null) {
			LOGGER.warn("Index des communes ne naissance non initialisé car aucun token INSEE fourni");
			return;
		}

		// Communes de naissance de l'INSEE
		List<CommuneNaissanceDto> listeCommunes = this.telechargerLesCommunesDeNaissance(tokenInsee, erreursDeTraitement);

		// pour chaque commune de naissance, ajout du code postal
		if (codesInseeVsPostaux != null) {
			listeCommunes.stream().forEach(commune -> commune.setCodesPostaux(codesInseeVsPostaux.get(commune.getCode())));
		}

		// Envoi des données dans l'indexeur
		this.initialiserIndex(listeCommunes, erreursDeTraitement);
	}

	/**
	 * Chargement du référentiel des adresses de gendarmerie à partir des données DATA.GOUV.FR
	 *
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private Map<String, AdresseEtHorairesGendarmerieDto> telechargerAdressesGendarmeries(List<String> erreursDeTraitement) {
		try {
			return this.clientDataGouv.telechargerAdressesGendarmeries();
		} catch (Exception e) {
			// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return new HashMap<>();
		}
	}

	/**
	 * Chargement du référentiel des codes postaux à partir des données DATA.GOUV.FR.
	 *
	 * <pre>
	 * /!\ Il n'y a que peu de codes postaux (6k) pour les 35k codes INSEE de communes.
	 * /!\ Mais certains codes INSEE ont plusieurs codes postaux !!
	 * </pre>
	 *
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private Map<String, List<String>> telechargerLesCodesPostauxDepuisDataGouv(List<String> erreursDeTraitement) {
		try {
			return this.clientDataGouv.telechargerCodesPostaux();
		} catch (Exception e) {
			// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return new HashMap<>();
		}
	}

	/**
	 * Chargement du référentiel des SIRETs à partir des données DATA.GOUV.FR
	 *
	 * @param token               Token de l'API INSEE
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private List<CommuneNaissanceDto> telechargerLesCommunesDeNaissance(Oauth2TokenDto token, List<String> erreursDeTraitement) {
		try {
			return this.clientOidcInsee.telechargerLesCommunesDeNaissance(token);
		} catch (Exception e) {
			// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return new ArrayList<>();
		}
	}

	private Map<String, PaysEtNationaliteDto> telechargerPays(List<String> erreursDeTraitement) {
		Map<String, PaysEtNationaliteDto> nationalites = null;
		try {
			nationalites = this.autre.telechargerReferentielPaysEtANationalites();
		} catch (Exception e) {
			LOGGER.error("Erreur au chargement du référentiel des nationalités", e);
			erreursDeTraitement.add(e.getMessage());
		}
		return nationalites;
	}

	/**
	 * Chargement du référentiel des protections de communes à partir des données DATA.GOUV.FR
	 *
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private Map<String, ProtectionDeCommuneDto> telechargerProtectionsDeCommune(List<String> erreursDeTraitement) {
		try {
			return this.clientDataGouv.telechargerProtectionsDeCommune();
		} catch (Exception e) {
			// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return new HashMap<>();
		}
	}

	/**
	 * Chargement du référentiel des SIRETs à partir des données DATA.GOUV.FR
	 *
	 * @param erreursDeTraitement La liste des erreurs (on va le plus loin possible méme si une API plante, on tente la suivante.
	 */
	private Collection<InformationSiretDto> telechargerSirets(List<String> erreursDeTraitement) {
		try {
			return this.clientDataGouv.telechargerSirets();
		} catch (Exception e) {
			// La stacktrace ne sera pas disponible plus tard. Donc on loggue maintenant
			LOGGER.error("Erreur à l'initialisation des données depuis le référentiel INSEE", e);
			erreursDeTraitement.add(e.getMessage());
			return new ArrayList<>();
		}
	}
}
