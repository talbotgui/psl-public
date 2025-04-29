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
package com.github.talbotgui.psl.socle.adminpsl.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;

/** DTO héritant de ConfigurationPubliqueDemarcheDto pour y ajouter les données cachées dans l'API publique exposée sur Internet. */
public class AdminConfigurationInterneDemarcheDto extends ConfigurationInterneDemarcheDto implements ElementConfigurationAjouterPourAdministration {

	/** Commentaire du créateur de la version de la configuration. */
	private String commentaireCreation;

	/** Créateur de la version de la configuration. */
	private String createur;

	/** Date de création. */
	private Date dateCreation;

	/** ID dans mongoDB. */
	private String id;

	/** Formatteur des date de création. */
	// surtout pas static car SimpleDateFormat n'est pas Thread Safe
	// Le @Transient force MongoDB à ignore cet attribut sinon il hurle
	@Transient
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/**
	 * Création du libellé de la version.
	 *
	 * @return
	 */
	@Override
	public String creerLibelle() {
		// Création du libellé
		String libelle = "";
		if (super.getVersionConfiguration() != null) {
			libelle += super.getVersionConfiguration();
			libelle += " - ";
		}
		if (this.getDateCreation() != null) {
			libelle += this.sdf.format(this.getDateCreation());
			libelle += " - ";
		}
		if (this.createur != null) {
			libelle += this.createur;
			libelle += " - ";
		}
		if (this.commentaireCreation != null) {
			libelle += this.commentaireCreation;
			libelle += " - ";
		}

		// Si vide, valeur par défaut
		if (libelle.length() == 0) {
			libelle = "Version par défaut";
		}
		return libelle;
	}

	@Override
	public String getCommentaireCreation() {
		return this.commentaireCreation;
	}

	@Override
	public String getCreateur() {
		return this.createur;
	}

	@Override
	public Date getDateCreation() {
		return this.dateCreation;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setCommentaireCreation(String commentaireCreation) {
		this.commentaireCreation = commentaireCreation;
	}

	@Override
	public void setCreateur(String createur) {
		this.createur = createur;
	}

	@Override
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

}
