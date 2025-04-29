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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import jakarta.validation.constraints.Pattern;

public class MessageSauvegardeNotificationEmailDto extends DemandeNotificationEmailDto implements MessageSauvegardeNotification {

	/** Date création du message dans MongoDB */
	private Date dateCreation;

	/** ID Mongodb. */
	@Pattern(regexp = RegexUtils.ID)
	private String id;

	/** Constructeur pour Jackson. */
	public MessageSauvegardeNotificationEmailDto() {
		super();
	}

	/** Constructeur hérité. */
	public MessageSauvegardeNotificationEmailDto(String contenu, Boolean contenuHtml, Collection<String> destinataires,
			Collection<String> destinatairesCopie, Collection<String> destinatairesCopieCachee, String objet,
			List<Triple<String, byte[], String>> piecesJointes) {
		super(contenu, contenuHtml, destinataires, destinatairesCopie, destinatairesCopieCachee, objet, piecesJointes);
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

	/**
	 * @see com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification#verifierMessageEstComplet()
	 */
	@Override
	public boolean verifierMessageEstComplet() {
		return StringUtils.hasLength(this.getContenu()) && this.getContenuHtml() != null //
				&& this.getDestinataires() != null && !this.getDestinataires().isEmpty()//
				&& StringUtils.hasLength(this.getDestinataires().iterator().next())//
				&& StringUtils.hasLength(this.getObjet());
	}
}
