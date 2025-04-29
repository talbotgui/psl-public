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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.adminpsl.dao.ConfigurationDao;
import com.github.talbotgui.psl.socle.adminpsl.dto.ElementConfigurationAjouterPourAdministration;
import com.github.talbotgui.psl.socle.adminpsl.exception.AdminpslException;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final String CLEF_CODE_DEMARCHE_DANS_CONFIGURATION_MINIMALE = "CODE_DEMARCHE";

	/** Configuration interne minimale. */
	private static final String CONFIGURATION_INTERNE_MINIMALE = "{\"codeDemarche\": \"CODE_DEMARCHE\",\"versionConfiguration\": \"0.0.0\",\"documentsAgenerer\": [],\"notifications\": {\"brouillon\": [],\"soumission\": []}}";

	/** Configuration publique minimale. */
	private static final String CONFIGURATION_PUBLIQUE_MINIMALE = "{\"versionConfiguration\": \"0.0.0\",\"codeDemarche\": \"CODE_DEMARCHE\",\"titre\": \"\",\"pointsEntree\": [],\"valeursInitiales\": {},\"piecesJointesAssociees\": [],\"fonctionnalites\": {\"modeObligatoireParDefaut\": true,\"deuil\": true,\"brouillon\": true},\"pages\": []}";

	@Autowired
	private ConfigurationDao dao;

	@Autowired
	private JwtService jwtService;

	@Override
	public String chargerVersionDeConfiguration(boolean publique, String codeDemarche, String id) {
		Object configuration = this.dao.chargerVersionDeConfiguration(publique, codeDemarche, id);
		return configuration.toString();
	}

	@Override
	public void creerNouvelleDemarche(String codeDemarche, String token) {

		// Vérification absence de configuration déjà existante
		if (!this.dao.listerLesVersionsDeConfiguration(true, codeDemarche).isEmpty()) {
			throw new AdminpslException(AdminpslException.ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_PUBLIQUE);
		}
		if (!this.dao.listerLesVersionsDeConfiguration(false, codeDemarche).isEmpty()) {
			throw new AdminpslException(AdminpslException.ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_INTERNE);
		}

		// Création d'une configuration publique vide
		String configurationPublique = CONFIGURATION_PUBLIQUE_MINIMALE.replace(CLEF_CODE_DEMARCHE_DANS_CONFIGURATION_MINIMALE, codeDemarche);
		this.sauvegarderVersionDeConfiguration(true, codeDemarche, null, configurationPublique, token);

		// Création d'une configuration interne vide
		String configurationInterne = CONFIGURATION_INTERNE_MINIMALE.replace(CLEF_CODE_DEMARCHE_DANS_CONFIGURATION_MINIMALE, codeDemarche);
		this.sauvegarderVersionDeConfiguration(false, codeDemarche, null, configurationInterne, token);
	}

	@Override
	public Map<String, String> listerLesVersionsDeConfiguration(boolean publique, String codeDemarche) {

		// Recherche
		List<ElementConfigurationAjouterPourAdministration> liste = this.dao.listerLesVersionsDeConfiguration(publique, codeDemarche);

		// Transformation
		return liste.stream().collect(Collectors.toMap(//
				ElementConfigurationAjouterPourAdministration::getId, //
				ElementConfigurationAjouterPourAdministration::creerLibelle //
		));
	}

	@Override
	public String sauvegarderVersionDeConfiguration(boolean publique, String codeDemarche, String id, String configuration, String tokenJwt) {

		// Extraction du login de l'utilisateur connecté
		String utilisateur = null;
		if (tokenJwt != null) {
			utilisateur = this.jwtService.extraireEtDechiffrerClaimsDuToken(tokenJwt).getSubject();
		}

		// Parse en Document et vérification du code de démarche
		Document document = this.dao.verifierCoherenceDuCodeDeDemarche(codeDemarche, id, configuration);

		// Ajout de la date
		document.append(AbstractMongoDao.ATTRIBUT_DATE_CREATION, new Date());
		document.append(AbstractMongoDao.ATTRIBUT_CREATEUR, utilisateur);

		// Pour une insertion
		if (id == null) {
			return this.dao.insererVersionDeConfiguration(publique, document);
		}
		// Pour une MaJ
		else {
			return this.dao.mettreAjourVersionDeConfiguration(publique, id, document);
		}
	}
}
