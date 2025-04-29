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
package com.github.talbotgui.psl.socle.adminpsl.controleur;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import com.github.talbotgui.psl.socle.adminpsl.apiclient.api.AdminPslAPI;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.RequeteRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.apiclient.dto.ResultatRechercheTransfertsDto;
import com.github.talbotgui.psl.socle.adminpsl.exception.SecuriteException;
import com.github.talbotgui.psl.socle.adminpsl.service.ConfigurationPubliqueService;
import com.github.talbotgui.psl.socle.adminpsl.service.ConfigurationService;
import com.github.talbotgui.psl.socle.adminpsl.service.SecuriteService;
import com.github.talbotgui.psl.socle.adminpsl.service.TransfertService;
import com.github.talbotgui.psl.socle.commun.apiclient.dto.Page;
import com.github.talbotgui.psl.socle.commun.securite.TokenJwtUtils;

/**
 * Principal controleur REST du projet
 */
@RestController
public class AdminPslControleur implements AdminPslAPI {

	/** Service des configurations publiques. */
	@Autowired
	private ConfigurationPubliqueService configurationPubliqueService;

	/** Service des configurations publiques. */
	@Autowired
	private ConfigurationService configurationService;

	/** Service de sécurité. */
	@Autowired
	private SecuriteService securiteService;

	/** Service des transferts. */
	@Autowired
	private TransfertService transfertService;

	@Override
	public Object chargerDetailsDunTransfert(String idTransert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object chargerVersionDeConfigurationInterne(String codeDemarche, String idConfiguration) {
		return this.configurationService.chargerVersionDeConfiguration(false, codeDemarche, idConfiguration);
	}

	@Override
	public Object chargerVersionDeConfigurationPublique(String codeDemarche, String idConfiguration) {
		return this.configurationService.chargerVersionDeConfiguration(true, codeDemarche, idConfiguration);
	}

	@Override
	public String connexion(String nomUtilisateur, String motDePasse) {
		// Validation des paramètres
		if (!StringUtils.hasLength(nomUtilisateur) || !StringUtils.hasLength(motDePasse)) {
			throw new SecuriteException(SecuriteException.CONNEXION_NON_AUTORISEE);
		}

		// Tentative de connexion au LDAP et création d'un token
		return this.securiteService.seConnecterEtCreerJeton(nomUtilisateur, motDePasse);
	}

	@Override
	public void creerNouvelleDemarche(String codeDemarche) {

		// Récupération du token de l'usager.
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Appel au service
		this.configurationService.creerNouvelleDemarche(codeDemarche, tokenJwt);
	}

	@Override
	public String creerVersionDeConfigurationInterne(String codeDemarche, String configuration) {
		return this.modifierVersionDeConfigurationInterne(codeDemarche, null, configuration);
	}

	@Override
	public String creerVersionDeConfigurationPublique(String codeDemarche, String configuration) {
		return this.modifierVersionDeConfigurationPublique(codeDemarche, null, configuration);
	}

	@Override
	public void genererDesTeledossiers(int nbTransferts) {
		this.transfertService.genererDesTeledossiers(nbTransferts);
	}

	@Override
	public Collection<String> listerLesDemarches() {
		return this.configurationPubliqueService.listerLesCodesDeDemarche();
	}

	@Override
	public Map<String, String> listerLesVersionsDeConfigurationInterne(String codeDemarche) {
		return this.configurationService.listerLesVersionsDeConfiguration(false, codeDemarche);
	}

	@Override
	public Map<String, String> listerLesVersionsDeConfigurationPublique(String codeDemarche) {
		return this.configurationService.listerLesVersionsDeConfiguration(true, codeDemarche);
	}

	@Override
	public String modifierVersionDeConfigurationInterne(String codeDemarche, String idConfiguration, String configuration) {

		// Récupération du token de l'usager.
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Sauvegarde de la configuration
		String id = this.configurationService.sauvegarderVersionDeConfiguration(false, codeDemarche, idConfiguration, configuration, tokenJwt);
		return "\"" + id + "\"";
	}

	@Override
	public String modifierVersionDeConfigurationPublique(String codeDemarche, String idConfiguration, String configuration) {

		// Récupération du token de l'usager.
		String tokenJwt = TokenJwtUtils.obtenirTokenJwtDepuisSecuriteSpring();

		// Sauvegarde de la configuration
		String id = this.configurationService.sauvegarderVersionDeConfiguration(true, codeDemarche, idConfiguration, configuration, tokenJwt);
		return "\"" + id + "\"";
	}

	@Override
	public Page<ResultatRechercheTransfertsDto> rechercherDesTeledossiers(RequeteRechercheTransfertsDto parametresRecherche) {
		return this.transfertService.rechercherDesTeledossiers(parametresRecherche);
	}
}
