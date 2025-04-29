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
package com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {

	/** Clef utilisée dans les JSON des configurations internes pour décrire les notifications de brouillon. */
	public static final String CLEF_NOTIFICATIONS_BROUILLON = "brouillon";
	/** Clef utilisée dans les JSON des configurations internes pour décrire les notifications de soumission. */
	public static final String CLEF_NOTIFICATIONS_SOUMISSION = "soumission";
	/** Clef utilisée dans les JSON des configurations internes pour décrire les notifications de transfert (envoi au partenaire). */
	public static final String CLEF_NOTIFICATIONS_TRANSFERT = "transfert";

	/** EMAIL : true si le contenu est HTML */
	private Boolean contenuHtml;
	/** NOTIFICATION_SP : nombre de jours avant expiration de la notification. */
	private Integer nombreJoursAvantExpiration;
	/** COMMUN : contenu de la notification (mail ou notificationSP) */
	private String templateContenu;
	/** EMAIL : template des destinataires */
	private String templateDestinataires;
	/** NOTIFICATION_SP : lien de reprise du brouillon */
	private String templateLienReprise;
	/** EMAIL : template d'objet du mail */
	private String templateObjet;
	/** COMMUN : MAIL ou NOTIFICATION_SP */
	private TypeNotificationEnum type;

	/** Constructeur pour Jackson. */
	public NotificationDto() {
		super();
	}

	public NotificationDto(TypeNotificationEnum type, String templateContenu, Boolean contenuHtml, String templateObjet,
			String templateDestinataires) {
		super();
		this.type = type;
		this.templateContenu = templateContenu;
		this.contenuHtml = contenuHtml;
		this.templateObjet = templateObjet;
		this.templateDestinataires = templateDestinataires;
	}

	public NotificationDto(TypeNotificationEnum type, String templateContenu, String templateLienReprise, Integer nombreJoursAvantExpiration) {
		super();
		this.type = type;
		this.templateContenu = templateContenu;
		this.templateLienReprise = templateLienReprise;
		this.nombreJoursAvantExpiration = nombreJoursAvantExpiration;
	}

	public Boolean getContenuHtml() {
		return this.contenuHtml;
	}

	public Integer getNombreJoursAvantExpiration() {
		return this.nombreJoursAvantExpiration;
	}

	public String getTemplateContenu() {
		return this.templateContenu;
	}

	public String getTemplateDestinataires() {
		return this.templateDestinataires;
	}

	public String getTemplateLienReprise() {
		return this.templateLienReprise;
	}

	public String getTemplateObjet() {
		return this.templateObjet;
	}

	public TypeNotificationEnum getType() {
		return this.type;
	}

	public void setContenuHtml(Boolean contenuHtml) {
		this.contenuHtml = contenuHtml;
	}

	public void setNombreJoursAvantExpiration(Integer nombreJoursAvantExpiration) {
		this.nombreJoursAvantExpiration = nombreJoursAvantExpiration;
	}

	public void setTemplateContenu(String templateContenu) {
		this.templateContenu = templateContenu;
	}

	public void setTemplateDestinataires(String templateDestinataires) {
		this.templateDestinataires = templateDestinataires;
	}

	public void setTemplateLienReprise(String templateLienReprise) {
		this.templateLienReprise = templateLienReprise;
	}

	public void setTemplateObjet(String templateObjet) {
		this.templateObjet = templateObjet;
	}

	public void setType(TypeNotificationEnum type) {
		this.type = type;
	}

}
