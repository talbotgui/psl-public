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
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

public class DemandeNotificationEmailDto {
	/** Le contenu du mail. */
	private String contenu;

	/** Un flag indiquant si le contenu du mail est HTML ou pur texte. */
	private Boolean contenuHtml;
	/** La liste des destinataires. */
	private Collection<String> destinataires;
	/** La liste des destinataires en copie. */
	private Collection<String> destinatairesCopie;
	/** La liste des destinataires en copie cachée. */
	private Collection<String> destinatairesCopieCachee;
	/** Identifiant dans les logs de la création et/ou modification (ajout par concaténation séparée par un espace) de cet objet. */
	private String idTrace;
	/** L'objet du mail. */
	private String objet;
	/** La liste des pièces jointes avec le nom du fichier; le flux et le MimeType. */
	private List<Triple<String, byte[], String>> piecesJointes;

	/** Constructeur pour Jackson. */
	public DemandeNotificationEmailDto() {
		super();
	}

	public DemandeNotificationEmailDto(String contenu, Boolean contenuHtml, Collection<String> destinataires, Collection<String> destinatairesCopie,
			Collection<String> destinatairesCopieCachee, String objet, List<Triple<String, byte[], String>> piecesJointes) {
		super();
		this.contenu = contenu;
		this.contenuHtml = contenuHtml;
		this.destinataires = destinataires;
		this.destinatairesCopie = destinatairesCopie;
		this.destinatairesCopieCachee = destinatairesCopieCachee;
		this.objet = objet;
		this.piecesJointes = piecesJointes;
	}

	public String getContenu() {
		return this.contenu;
	}

	public Boolean getContenuHtml() {
		return this.contenuHtml;
	}

	public Collection<String> getDestinataires() {
		return this.destinataires;
	}

	public Collection<String> getDestinatairesCopie() {
		return this.destinatairesCopie;
	}

	public Collection<String> getDestinatairesCopieCachee() {
		return this.destinatairesCopieCachee;
	}

	public String getIdTrace() {
		return this.idTrace;
	}

	public String getObjet() {
		return this.objet;
	}

	public List<Triple<String, byte[], String>> getPiecesJointes() {
		return this.piecesJointes;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public void setContenuHtml(Boolean contenuHtml) {
		this.contenuHtml = contenuHtml;
	}

	public void setDestinataires(Collection<String> destinataires) {
		this.destinataires = destinataires;
	}

	public void setDestinatairesCopie(Collection<String> destinatairesCopie) {
		this.destinatairesCopie = destinatairesCopie;
	}

	public void setDestinatairesCopieCachee(Collection<String> destinatairesCopieCachee) {
		this.destinatairesCopieCachee = destinatairesCopieCachee;
	}

	public void setIdTrace(String idTrace) {
		this.idTrace = idTrace;
	}

	public void setObjet(String objet) {
		this.objet = objet;
	}

	public void setPiecesJointes(List<Triple<String, byte[], String>> piecesJointes) {
		this.piecesJointes = piecesJointes;
	}
}
