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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto;

import java.util.List;
import java.util.Map;

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.DocumentsAgenererDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.NotificationDto;

/**
 * Classe décrivant les données de configuration interne d'une démarche (chargée uniquement par le service de soumission pour générer les éléments du
 * télé-dossier à destination des partenaires).
 */
public class ConfigurationInterneDemarcheDto {

	/** Code de la démarche */
	private String codeDemarche;

	/** Liste des documents à générer durant la soumission. */
	private List<DocumentsAgenererDto> documentsAgenerer;

	/** Liste des notifications possibles dans la démarche. */
	private Map<String, List<NotificationDto>> notifications;

	/** Version de la configuration */
	private String versionConfiguration;

	public ConfigurationInterneDemarcheDto() {
		super();
	}

	public ConfigurationInterneDemarcheDto(String codeDemarche, String versionConfiguration, List<DocumentsAgenererDto> documentsAgenerer,
			Map<String, List<NotificationDto>> notifications) {
		super();
		this.codeDemarche = codeDemarche;
		this.versionConfiguration = versionConfiguration;
		this.documentsAgenerer = documentsAgenerer;
		this.notifications = notifications;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public List<DocumentsAgenererDto> getDocumentsAgenerer() {
		return this.documentsAgenerer;
	}

	public Map<String, List<NotificationDto>> getNotifications() {
		return this.notifications;
	}

	public String getVersionConfiguration() {
		return this.versionConfiguration;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setDocumentsAgenerer(List<DocumentsAgenererDto> documentsAgenerer) {
		this.documentsAgenerer = documentsAgenerer;
	}

	public void setNotifications(Map<String, List<NotificationDto>> notifications) {
		this.notifications = notifications;
	}

	public void setVersionConfiguration(String versionConfiguration) {
		this.versionConfiguration = versionConfiguration;
	}

}