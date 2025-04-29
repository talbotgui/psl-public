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

import java.util.List;

// Cette classe ne peut pas être un "record" car ces attributs peuvent être modifiées après instanciation
public class ConfigurationTransfertDto {

	/** Code de la démarche. */
	private String codeDemarche;
	/** Liste des destinataires */
	private List<DestinataireDto> destinataires;
	/** Liste des documents disponibles à l'envoi des destinataires (la répartition par destinataire se fait dans DestinataireDto.codeDesDocuments) */
	private List<FichierAenvoyerDto> documents;
	/** Email de l'usager. */
	private String emailUsager;
	/** Identifiant de l'éventuel brouillon. */
	private String idBrouillon;
	/** Nom de l'usager. */
	private String nomUsager;
	/** Numéro de télé-dossier. */
	private String numeroTeledossier;
	/** Prénom de l'usager. */
	private String prenomUsager;
	/** UUID de l'espace SP de l'usager (particulier ou associatif). */
	private String uuidEspace;

	/** Constructeur pour Jackson */
	public ConfigurationTransfertDto() {
		super();
	}

	public ConfigurationTransfertDto(String codeDemarche, String numeroTeledossier, String emailUsager, String nomUsager, String prenomUsager,
			List<FichierAenvoyerDto> documents, List<DestinataireDto> destinataires) {
		super();
		this.codeDemarche = codeDemarche;
		this.numeroTeledossier = numeroTeledossier;
		this.documents = documents;
		this.destinataires = destinataires;
		this.nomUsager = nomUsager;
		this.prenomUsager = prenomUsager;
		this.emailUsager = emailUsager;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public List<DestinataireDto> getDestinataires() {
		return this.destinataires;
	}

	public List<FichierAenvoyerDto> getDocuments() {
		return this.documents;
	}

	public String getEmailUsager() {
		return this.emailUsager;
	}

	public String getIdBrouillon() {
		return this.idBrouillon;
	}

	public String getNomUsager() {
		return this.nomUsager;
	}

	public String getNumeroTeledossier() {
		return this.numeroTeledossier;
	}

	public String getPrenomUsager() {
		return this.prenomUsager;
	}

	public String getUuidEspace() {
		return this.uuidEspace;
	}

	public FichierAenvoyerDto rechercherUnDocumentAenvoyerParCode(String code) {

		// Recherche du code de document dans les documents envoyés à tous les destinataires
		if (this.documents != null) {
			for (FichierAenvoyerDto doc : this.documents) {
				if (code.equals(doc.getCodeFichierAenvoyer())) {
					return doc;
				}
			}
		}

		// Par défaut, renvoi de null
		return null;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setDestinataires(List<DestinataireDto> destinataires) {
		this.destinataires = destinataires;
	}

	public void setDocuments(List<FichierAenvoyerDto> documents) {
		this.documents = documents;
	}

	public void setEmailUsager(String emailUsager) {
		this.emailUsager = emailUsager;
	}

	public void setIdBrouillon(String idBrouillon) {
		this.idBrouillon = idBrouillon;
	}

	public void setNomUsager(String nomUsager) {
		this.nomUsager = nomUsager;
	}

	public void setNumeroTeledossier(String numeroTeledossier) {
		this.numeroTeledossier = numeroTeledossier;
	}

	public void setPrenomUsager(String prenomUsager) {
		this.prenomUsager = prenomUsager;
	}

	public void setUuidEspace(String uuidEspace) {
		this.uuidEspace = uuidEspace;
	}

	@Override
	public String toString() {
		return "ConfigurationTransfertDto [" + (this.codeDemarche != null ? "codeDemarche=" + this.codeDemarche + ", " : "")
				+ (this.numeroTeledossier != null ? "numeroTeledossier=" + this.numeroTeledossier + ", " : "")
				+ (this.emailUsager != null ? "emailUsager=" + this.emailUsager + ", " : "")
				+ (this.nomUsager != null ? "nomUsager=" + this.nomUsager + ", " : "")
				+ (this.prenomUsager != null ? "prenomUsager=" + this.prenomUsager + ", " : "")
				+ (this.destinataires != null ? "destinataires=" + this.destinataires + ", " : "")
				+ (this.documents != null ? "documents=" + this.documents : "") + "]";
	}
}
