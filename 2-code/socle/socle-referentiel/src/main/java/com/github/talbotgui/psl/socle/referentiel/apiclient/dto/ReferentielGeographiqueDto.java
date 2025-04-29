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
package com.github.talbotgui.psl.socle.referentiel.apiclient.dto;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.DonneeReferentielComedec;

/** Simple objet de transport contenant tout le référentiel géographique */
public class ReferentielGeographiqueDto {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReferentielGeographiqueDto.class);

	/** Map des communes et communes déléguées (par code INSEE). */
	private Map<String, CommuneActuelleDto> communesActuelles = new HashMap<>();

	/** Map des départements (par code INSEE). */
	private Map<String, DepartementActuelDto> departementsActuels = new HashMap<>();

	/** Map des pays (par code INSEE). */
	private Map<String, PaysActuelDto> paysActuels = new HashMap<>();

	/** Map des régions (par code INSEE). */
	private Map<String, RegionActuelleDto> regionsActuelles = new HashMap<>();

	/**
	 * Intégration des abonnés actifs HUBEE.
	 *
	 * @param abonnesHubeeActifs Abonnés.
	 */
	public void ajouterAbonnesHubee(Map<String, List<AbonneHubeeDto>> abonnesHubeeActifs) {
		int compteur = 0;
		// Pour chaque commune avec un SIRET, on recherche un abonnement
		for (CommuneActuelleDto commune : this.communesActuelles.values()) {
			if (StringUtils.hasLength(commune.getSiret())) {
				List<AbonneHubeeDto> abonnes = abonnesHubeeActifs.get(commune.getSiret());
				if (abonnes != null) {
					for (AbonneHubeeDto abonne : abonnes) {
						// abonnement dont on extrait le code démarche pour l'ajouter dans les démarches connectées de la commune
						commune.getDemarchesConnectes().add(abonne.codeDemarche());
						// Incrément du compteur
						compteur++;
					}
				}
			}
		}
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty()) {
			int nbAbonnements = abonnesHubeeActifs.values().stream().map(l -> l == null ? 0 : l.size()).reduce(0, Integer::sum);
			int pourcentageRenseigne = 100 * compteur / this.communesActuelles.size();
			int pourcentageUtilise = 100 * compteur / nbAbonnements;
			LOGGER.info(
					"  {} abonnements intégrés ({}%) dans les {} communes ({}% communes renseignées) sur les {} abonnements obtenus de l'API"
							+ " (PS : certains abonnés ne sont pas des communes donc le pourcentage d'abonnements utilisés peut être faible)",
					compteur, pourcentageUtilise, this.communesActuelles.size(), pourcentageRenseigne, nbAbonnements);
		}
	}

	/**
	 * Ajout des codes postaux dans les communes.
	 *
	 * @param codesInseeVsPostaux Map de CodeInsee, CodesPostaux
	 */
	public void ajouterLesCodesPostaux(Map<String, List<String>> codesInseeVsPostaux) {
		// Au cas où
		if (codesInseeVsPostaux == null) {
			return;
		}

		// Intégration
		int compteur = 0;
		for (CommuneActuelleDto commune : this.communesActuelles.values()) {
			List<String> codesPostaux = codesInseeVsPostaux.get(commune.getCodeInsee());
			if (codesPostaux != null) {
				commune.setCodesPostaux(codesPostaux);
				compteur++;
			}
		}

		// Log de l'intégration de ce référentiel dans le référentiel géographique
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty()) {
			int pourcentageRenseigne = 100 * compteur / this.communesActuelles.size();
			int pourcentageUtilise = 100 * compteur / codesInseeVsPostaux.size();
			LOGGER.info(
					"  Intégration de {} ({}% de communes renseignées et {}% de codes utilisés) codes postaux dans les {} communes actuelles du référentiel",
					compteur, pourcentageRenseigne, pourcentageUtilise, this.communesActuelles.size());
		}
	}

	/**
	 * Intégration des données COMEDEC dans le référentiel géographique).
	 *
	 * @param donneesComedec Donnnées COMEDEC à intégrer.
	 */
	public void ajouterLesDonneesComedec(List<DonneeReferentielComedec> donneesComedec) {
		// Au cas où
		if (donneesComedec == null) {
			return;
		}

		// Utilitaire de comparaison
		Collator collator = Collator.getInstance(Locale.FRENCH);
		collator.setStrength(Collator.PRIMARY);

		// Intégration en croisant les données COMEDEC avec les communes
		int compteur = 0;
		for (DonneeReferentielComedec donneeComedec : donneesComedec) {
			boolean correspond = false;
			String nomSimplifieCommuneDonneesComedec = this.simplifierNomCommunePourComparaison(donneeComedec.nomCommune());
			for (CommuneActuelleDto commune : this.communesActuelles.values()) {
				String nomSimplifieCommuneActuelle = this.simplifierNomCommunePourComparaison(commune.getLibelle());

				// Calcul de la correspondance des données pivot
				correspond = commune.getCodeInsee().equals(donneeComedec.codeInsee())//
						&& collator.equals(nomSimplifieCommuneActuelle, nomSimplifieCommuneDonneesComedec);

				// Si cela correspond
				if (correspond) {
					commune.setComedec(donneeComedec.comedec());
					compteur++;
					break;
				}
				// Si ce n'est que partiel
				else if (commune.getCodeInsee().equals(donneeComedec.codeInsee())
						|| collator.equals(nomSimplifieCommuneActuelle, nomSimplifieCommuneDonneesComedec)) {
					LOGGER.debug("  Correspondance partielle de la commune {} ({}) avec données COMEDEC {}", commune.getLibelle(),
							commune.getCodeInsee(), donneeComedec);
				}
			}
			if (!correspond) {
				LOGGER.debug("  Intégration impossible des données COMEDEC : {}", donneeComedec);
			}
		}

		// Log de l'intégration de ce référentiel dans le référentiel géographique
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty()) {
			int pourcentageRenseigne = 100 * compteur / this.communesActuelles.size();
			int pourcentageUtilise = 100 * compteur / donneesComedec.size();
			LOGGER.info(
					"  Intégration de {} ({}% de communes renseignées et {}% de codes utilisés) lignes COMEDEC dans les {} communes actuelles du référentiel",
					compteur, pourcentageRenseigne, pourcentageUtilise, this.communesActuelles.size());
		}

	}

	/**
	 * Insertion des nationalités dans le référentiel géographique
	 *
	 * @param nationalites Map des nationalités
	 */
	public void ajouterLesNationalites(Map<String, PaysEtNationaliteDto> nationalites) {
		int compteur = 0;
		for (PaysActuelDto pays : this.paysActuels.values()) {
			PaysEtNationaliteDto nationaliteTrouvee = nationalites.get(pays.getCode2caracteres());
			if (nationaliteTrouvee != null) {
				if (nationaliteTrouvee.getNationalite() != null) {
					pays.setNationalite(nationaliteTrouvee.getNationalite().toUpperCase());
				}
				if (nationaliteTrouvee.getCapitale() != null) {
					pays.setCapitale(nationaliteTrouvee.getCapitale().toUpperCase());
				}
				compteur++;
			}
		}
		if (LOGGER.isInfoEnabled() && !this.paysActuels.isEmpty()) {
			int pourcentageRenseigne = 100 * compteur / this.paysActuels.size();
			int pourcentageUtilise = 100 * compteur / nationalites.size();
			LOGGER.info(
					"  Intégration de {} ({}% de pays renseignés et {}% de nationalités utilisées) nationalités et capitales dans les {} pays du référentiel",
					compteur, pourcentageRenseigne, pourcentageUtilise, this.paysActuels.size());
		}
	}

	/**
	 * Ajout des protections dans les communes ainsi que des coordonnées associées.
	 *
	 * @param codesInseeVsProtectionsDeCommunes Map de CodeInsee, ProtectionDeCommune
	 * @param idGendarmerieVsAdresseEtHoraires  Map de CodeInsee, AdresseEtHorairesGendarmerieDto
	 */
	public void ajouterLesProtectionsDeCommune(Map<String, ProtectionDeCommuneDto> codesInseeVsProtectionsDeCommunes,
			Map<String, AdresseEtHorairesGendarmerieDto> idGendarmerieVsAdresseEtHoraires) {
		int compteur1 = 0;
		int compteur2 = 0;
		Set<String> idProtectionsUtilisees = new HashSet<>();
		Set<String> idHorairesUtilisees = new HashSet<>();
		// Pour chaque commune
		for (CommuneActuelleDto commune : this.communesActuelles.values()) {
			// Recherche de la protection
			ProtectionDeCommuneDto protection = codesInseeVsProtectionsDeCommunes.get(commune.getCodeInsee());
			// S'il y en a une
			if (protection != null) {
				// Sauvegarde des informations du protecteur
				commune.setNomProtecteur(protection.getNomProtecteur());
				commune.setTypeProtection(protection.getTypeProtection());
				compteur1++;
				idProtectionsUtilisees.add(commune.getCodeInsee());
				// Sauvegarde de l'adresse et des horaires s'ils sont disponibles
				AdresseEtHorairesGendarmerieDto adresse = idGendarmerieVsAdresseEtHoraires.get(protection.getId());
				if (adresse != null) {
					commune.setCoordonneesGendarmerie(adresse);
					compteur2++;
					idHorairesUtilisees.add(protection.getId());
				}
			}
		}
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty()) {
			int pourcentageRenseigne1 = 100 * compteur1 / this.communesActuelles.size();
			int pourcentageUtilise1 = 100 * idProtectionsUtilisees.size() / codesInseeVsProtectionsDeCommunes.size();
			int pourcentageRenseigne2 = 100 * compteur2 / this.communesActuelles.size();
			int pourcentageUtilise2 = 100 * idHorairesUtilisees.size() / idGendarmerieVsAdresseEtHoraires.size();
			LOGGER.info(
					"  Intégration de {} protections ({}% des communes renseignées et {}% des codes utilisés) et {} horaires ({}% des communes renseignées et {}% des codes utilisés) dans les {} communes actuelles du référentiel",
					compteur1, pourcentageRenseigne1, pourcentageUtilise1, compteur2, pourcentageRenseigne2, pourcentageUtilise2,
					this.communesActuelles.size());
		}
	}

	/**
	 * Intégration des sirets dans la liste des communes.
	 *
	 * @param sirets SIRETS à intégrer
	 */
	public void ajouterLesSiretsDeCommune(Collection<InformationSiretDto> sirets) {
		LOGGER.info("  Intégration des SIRETs dans le référentiel géographique");
		// AtomicInteger pour faire l'incrément dans un Consumer de forEach
		AtomicInteger compteur = new AtomicInteger(0);
		// Pour chaque SIRET
		sirets.parallelStream().forEach(siret -> this.communesActuelles.values().stream()
				// Recherche de commune correspondante
				.filter(c -> c.getSiret() == null && siret.calculerCorrespondanceAvecUneCommune(c))
				// Sauvegarde du siret et incrément du compteur
				.forEach(c -> {
					c.setSiret(siret.siret());
					compteur.getAndIncrement();
				}));
		// Log
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty() && !sirets.isEmpty()) {
			int pourcentageRenseigne = 100 * compteur.get() / this.communesActuelles.size();
			int pourcentageUtilise = 100 * compteur.get() / sirets.size();
			LOGGER.info(
					"  Intégration de {} SIRET(s) ({}% de communes renseignées et {}% de sirets utilisés) dans les {} communes actuelles du référentiel avec {} SIRETs en entrée",
					compteur, pourcentageRenseigne, pourcentageUtilise, this.communesActuelles.size(), sirets.size());
		} else {
			LOGGER.info("  Intégration de {} SIRET(s) dans les {} communes actuelles du référentiel avec {} SIRETs en entrée", compteur,
					this.communesActuelles.size(), sirets.size());
		}
	}

	/**
	 * Création des liens commune->departement departement->region et pour les chefs lieu de département et de région.
	 */
	public void crerLesLiensEntreLesElements() {
		// Tous les attributs derrière les SETTER appelés ici doivent avoir l'annotation suivante :
		// Pour éviter la boucle infinie et une taille énorme à la sérialisation en JSON pour stocker les données en cache
		// @JsonIgnore

		// Création des liens pour la région
		for (RegionActuelleDto region : this.regionsActuelles.values()) {
			region.setCommuneChefLieu(this.communesActuelles.get(region.getCodeInseeCommuneChefLieu()));
		}
		// Création des liens pour le département
		for (DepartementActuelDto departement : this.departementsActuels.values()) {
			departement.setCommuneChefLieu(this.communesActuelles.get(departement.getCodeInseeCommuneChefLieu()));
			departement.setRegion(this.regionsActuelles.get(departement.getCodeInseeRegion()));
		}
		// Création des liens pour la commune
		for (CommuneActuelleDto commune : this.communesActuelles.values()) {
			commune.setDepartement(this.departementsActuels.get(commune.getCodeInseeDepartement()));
		}
	}

	public Map<String, CommuneActuelleDto> getCommunesActuelles() {
		return this.communesActuelles;
	}

	public Map<String, DepartementActuelDto> getDepartementsActuels() {
		return this.departementsActuels;
	}

	public Map<String, PaysActuelDto> getPaysActuels() {
		return this.paysActuels;
	}

	public Map<String, RegionActuelleDto> getRegionsActuelles() {
		return this.regionsActuelles;
	}

	/**
	 * Méthode recherchant des anomalies et les traçant en WARN.
	 */
	public void rechercherDesAnomalies() {
		LOGGER.info("Recherche des anomalies dans le référentiel");

		// Initialisation de la liste des problèmes (on ne recrée pas la liste à chaque itération, on la vide)
		List<String> champsManquants = new ArrayList<>();
		List<String> incoherences = new ArrayList<>();

		// Parcours des communes
		int compteur = 0;
		for (CommuneActuelleDto commune : this.communesActuelles.values()) {
			// Contrôles de champs manquants
			if (commune.getCodeInsee() == null) {
				champsManquants.add("codeInsee");
			}
			if (commune.getCodeInseeDepartement() == null) {
				champsManquants.add("codeInseeDepartement");
			}
			if (commune.getCodesPostaux() == null || commune.getCodesPostaux().isEmpty()) {
				champsManquants.add("codesPostaux");
			}
			if (commune.getDepartement() == null) {
				champsManquants.add("departement");
			}
			if (commune.getSiret() == null) {
				champsManquants.add("SIRET");
			}
			if (commune.getTypeProtection() == null) {
				champsManquants.add("typeProtection");
			}
			// Contrôles d'incohérence du code INSEE et du code postal (si le siret est présent c'est qu'un SIRET existe pour cette combinaison
			// postal-insee donc pas de soucis)
			if (commune.getSiret() == null && commune.getCodeInsee() != null && commune.getCodesPostaux() != null) {
				for (String codePostal : commune.getCodesPostaux()) {
					if (!commune.getCodeInsee().startsWith(codePostal.substring(0, 1))) {
						incoherences.add("codePostal-codeInsee");
					}
				}
			}
			// Log et purge de la liste
			if (!champsManquants.isEmpty() || !incoherences.isEmpty()) {
				compteur++;
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("  Incohérence(s) {} et/ou manque de {} dans la commune : {}", incoherences, champsManquants, commune.toJson());
				}
				champsManquants.clear();
				incoherences.clear();
			}
		}

		// Log
		if (LOGGER.isInfoEnabled() && !this.communesActuelles.isEmpty()) {
			int pourcentage = 100 * compteur / this.communesActuelles.size();
			LOGGER.info("  {} ({}%) communes avec au moins une anomalie sur les {} communes du référentiel", compteur, pourcentage,
					this.communesActuelles.size());
		}
	}

	/**
	 * Méthode simplifiant le nom d'une commune en la passant en majuscule, retirant les articles (LE, LA et LES) et retirant les espaces et tirets.
	 *
	 * @param nomCommune Nom de la commune.
	 * @return Nom de la commune simplifié.
	 */
	private String simplifierNomCommunePourComparaison(String nomCommune) {
		return nomCommune.toUpperCase().replaceAll("(L' |LE |LA |LES )", "").replaceAll("[ \\-]", "");
	}
}
