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
package com.github.talbotgui.psl.socle.soumission.dto;

import java.util.HashMap;
import java.util.Map;

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.soumission.apiclient.dto.DonneesDeSoumissionDto;

/**
 * Classe contenant la structure complète des données nécessaires à la gestion de la soumission d'un télé-dossier.
 *
 * Toutes les données config/calcul/saisie/génération sont toutes ici.
 */
public class TeledossierDto {

	/** Configuration interne de la démarche */
	private ConfigurationInterneDemarcheDto configurationInterneDemarche;

	/** Configuration de la démarche ayant donné lieu à la saisie présente dans donneesDeSoumission. */
	private ConfigurationPubliqueDemarcheDto configurationPubliqueDemarche;

	/** Données saisies par l'usager et envoyées au service REST de soumission */
	private DonneesDeSoumissionDto donneesDeSoumission;

	/** Référence du point d'entrée utilisé à la soumission */
	private Map<String, String> listeParametresDuPointEntreeUtilise = new HashMap<>();

	/** Numéro du télé-dossier. */
	private String numero;

	/** Token PSL utilisé lors de la soumission. */
	private String tokenPSL;

	public TeledossierDto() {
		super();
	}

	public TeledossierDto(DonneesDeSoumissionDto donneesDeSoumission, ConfigurationPubliqueDemarcheDto configurationPubliqueDemarche) {
		super();
		this.donneesDeSoumission = donneesDeSoumission;
		this.configurationPubliqueDemarche = configurationPubliqueDemarche;
	}

	public ConfigurationInterneDemarcheDto getConfigurationInterneDemarche() {
		return this.configurationInterneDemarche;
	}

	public ConfigurationPubliqueDemarcheDto getConfigurationPubliqueDemarche() {
		return this.configurationPubliqueDemarche;
	}

	public DonneesDeSoumissionDto getDonneesDeSoumission() {
		return this.donneesDeSoumission;
	}

	public Map<String, String> getListeParametresDuPointEntreeUtilise() {
		return this.listeParametresDuPointEntreeUtilise;
	}

	public String getNumero() {
		return this.numero;
	}

	public String getTokenPSL() {
		return this.tokenPSL;
	}

	public void setConfigurationInterneDemarche(ConfigurationInterneDemarcheDto configurationInterneDemarche) {
		this.configurationInterneDemarche = configurationInterneDemarche;
	}

	public void setConfigurationPubliqueDemarche(ConfigurationPubliqueDemarcheDto configurationPubliqueDemarche) {
		this.configurationPubliqueDemarche = configurationPubliqueDemarche;
	}

	public void setDonneesDeSoumission(DonneesDeSoumissionDto donneesDeSoumission) {
		this.donneesDeSoumission = donneesDeSoumission;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setTokenPSL(String tokenPSL) {
		this.tokenPSL = tokenPSL;
	}
}
