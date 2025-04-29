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
package com.github.talbotgui.psl.socle.adminpsl.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.dao.BrouillonDao;
import com.github.talbotgui.psl.socle.adminpsl.dao.TransfertDao;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;
import com.github.talbotgui.psl.socle.soumission.apiclient.api.SoumissionAPI;
import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;

@Service
public class TransfertServiceImpl implements TransfertService {

	/** Copie de la constante ValidationSoumissionServiceImpl.CLEF_DANS_DONNEES_SOUMISES_EMAIL_UTILISATEUR_CONNECTE. */
	private static final String CLEF_DANS_DONNEES_SOUMISES_EMAIL_UTILISATEUR_CONNECTE = "utilisateur_email";

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(TransfertServiceImpl.class);

	/** Instance de DAO pour consulter les brouillons. */
	@Autowired
	private BrouillonDao brouillonDao;

	/** Instance de client d'appel au micro-service de soumission (initialisé dans le @PostConstruct). */
	@Autowired
	private SoumissionAPI clientSoumission;

	/** Instance de DAO pour manipuler les transfert. */
	@Autowired
	private TransfertDao transfertDao;

	/**
	 * Transformation des DTO.
	 *
	 * @param b Données d'un brouillon.
	 * @return Données à soumettre.
	 */
	private DonneesDeSoumissionDto creerDonneesDeSoumissionDepuisBrouillon(BrouillonDto b) {
		return new DonneesDeSoumissionDto(b.getCodeDemarche(), b.getVersionConfiguration(), DonneesDeSoumissionDto.LANGUE_FR, b.getDonnees());
		// A ne surtout pas décommenter au risque de détruire les brouillons utilisés pour la génération des télé-dossiers
		// dto.setIdBrouillon(b.getId());
	}

	@Override
	public void genererDesTeledossiers(int nbTransferts) {
		// LOG
		LOGGER.info("Génération de {} télédossiers pour chaque brouillon présent en base de données", nbTransferts);

		// Recherche de tous les brouillons disponibles (sans passer par le micro-service dédié qui n'a pas le droit de faire des accès complets)
		Collection<BrouillonDto> listeBrouillons = this.brouillonDao.listerTousLesBrouillons();
		LOGGER.info("{} brouillons disponibles", listeBrouillons.size());

		// Pour chaque brouillon
		int comptageSuccess = 0;
		for (BrouillonDto b : listeBrouillons) {

			// gestion d'erreur au dessus la boucle car resoumettre plusieurs fois le même télédossier donnera le même résultat
			try {

				// Modification des données soumises pour coller avec le token de l'administrateur connecté
				String emailAdministrateur = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
				b.getDonnees().put(CLEF_DANS_DONNEES_SOUMISES_EMAIL_UTILISATEUR_CONNECTE, emailAdministrateur);

				// Pour les X transferts à générer
				for (int i = 0; i < nbTransferts; i++) {

					// Tentative de soumission du brouillon plusieurs fois
					LOGGER.info("Soumission du brouillon '{}'", b.getId());
					this.clientSoumission.soumettreUnTeledossier(this.creerDonneesDeSoumissionDepuisBrouillon(b));

					// Comptage
					comptageSuccess++;
				}
			} catch (Exception e) {
				LOGGER.info("Impossible de soumettre les {} télé-dossiers avec les données du brouillon '{}' à cause de l'erreur '{}'", nbTransferts,
						b.getId(), e.getMessage());
			}
		}

		// Logs de résumé des actions réalisées
		LOGGER.info("{} soumissions réussies pour {}x{} soumissions potentielles", comptageSuccess, nbTransferts, listeBrouillons.size());
	}

	@Override
	public Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(RequeteRechercheTransfertsDto requete) {
		return this.transfertDao.rechercherDesTeledossiers(requete);
	}
}
