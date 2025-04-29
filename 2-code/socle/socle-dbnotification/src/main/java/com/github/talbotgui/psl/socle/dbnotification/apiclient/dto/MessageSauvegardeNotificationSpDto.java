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
package com.github.talbotgui.psl.socle.dbnotification.apiclient.dto;

import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * Classe contenant les métadonnées et le contenu d'un document.
 */
public class MessageSauvegardeNotificationSpDto extends DemandeNotificationSpDto implements MessageSauvegardeNotification {

	/** Date création du message dans MongoDB */
	private Date dateCreation;

	/** ID Mongodb. */
	private String id;

	/** Constructeur pour Jackson. */
	public MessageSauvegardeNotificationSpDto() {
		super();
	}

	/** Constructeur hérité de la classe parente. */
	public MessageSauvegardeNotificationSpDto(String accessToken, String codeDemarche, String libelleBouton1, String libelleBouton2,
			String libelleDemarche, String messageAafficher, int nombreJoursAvantExpiration, String idExterneNotification, String refreshToken,
			String statutTeledossier, String urlBouton1, String urlBouton2, String urlReprise, String uuidEspace) {
		super(accessToken, codeDemarche, libelleBouton1, libelleBouton2, libelleDemarche, messageAafficher, nombreJoursAvantExpiration,
				idExterneNotification, refreshToken, statutTeledossier, urlBouton1, urlBouton2, urlReprise, uuidEspace);
	}

	public Date getDateCreation() {
		return this.dateCreation;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MessageSauvegardeNotificationSpDto [dateCreation=" + this.dateCreation + ", id=" + this.id + ", getAccessToken()=***"
				+ ", getCodeDemarche()=" + this.getCodeDemarche() + ", getIdExterneNotification()=" + this.getIdExterneNotification()
				+ ", getIdTrace()=" + this.getIdTrace() + ", getLibelleBouton1()=" + this.getLibelleBouton1() + ", getLibelleBouton2()="
				+ this.getLibelleBouton2() + ", getLibelleDemarche()=" + this.getLibelleDemarche() + ", getMessageAafficher()="
				+ this.getMessageAafficher() + ", getNombreJoursAvantExpiration()=" + this.getNombreJoursAvantExpiration() + ", getRefreshToken()=***"
				+ ", getStatutTeledossier()=" + this.getStatutTeledossier() + ", getUrlBouton1()=" + this.getUrlBouton1() + ", getUrlBouton2()="
				+ this.getUrlBouton2() + ", getUrlReprise()=" + this.getUrlReprise() + ", getUuidEspace()=***]";
	}

	/**
	 * @see com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification#verifierMessageEstComplet()
	 */
	@Override
	public boolean verifierMessageEstComplet() {
		return
		// Appel avec un token OIDC d'usager
		(StringUtils.hasLength(this.getAccessToken()) && StringUtils.hasLength(this.getRefreshToken())
				// Appel avec le token technique
				|| StringUtils.hasLength(this.getUuidEspace()))
				// dans tous les cas
				&& StringUtils.hasLength(this.getCodeDemarche()) && StringUtils.hasLength(this.getLibelleDemarche())//
				&& StringUtils.hasLength(this.getStatutTeledossier()) && StringUtils.hasLength(this.getIdExterneNotification())//
				&& StringUtils.hasLength(this.getMessageAafficher())//
				&& this.getNombreJoursAvantExpiration() > 0;
	}

}
