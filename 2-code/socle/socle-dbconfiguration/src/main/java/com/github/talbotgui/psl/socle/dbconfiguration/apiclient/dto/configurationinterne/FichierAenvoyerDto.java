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

/** Ce DTO fait partie de la structure de donnée décrivant le(s) transfert(s) à réaliser pour un télé-dossier. */
public class FichierAenvoyerDto {
	/**
	 * Code du document généré ou de la pièce jointe (pour faire le lien avec les configurations internes et publiques) String code, /** ID du fichier
	 * dans le stockage de document généré ou des pièces jointes.
	 */
	private String codeFichierAenvoyer;
	/** Indique si le document est une pièce jointe ou un document généré. */
	private Boolean estPieceJointe;
	/** ID de fichier stocké */
	private String idFichierStocke;
	/** Type MIME du fichier */
	private String mimeType;
	/** Nom du fichier à envoyer au partenaire */
	private String nomFichier;
	/** Taille du fichier */
	private Long taille;

	/** Constructeur pour Jackson */
	public FichierAenvoyerDto() {
		super();
	}

	public FichierAenvoyerDto(Boolean estPieceJointe, String codeFichierAenvoyer, String nomFichier, Long taille, String mimeType) {
		super();
		this.estPieceJointe = estPieceJointe;
		this.codeFichierAenvoyer = codeFichierAenvoyer;
		this.nomFichier = nomFichier;
		this.taille = taille;
		this.mimeType = mimeType;
	}

	public FichierAenvoyerDto(Boolean estPieceJointe, String codeFichierAenvoyer, String nomFichier, Long taille, String mimeType,
			String idFichierStocke) {
		this(estPieceJointe, codeFichierAenvoyer, nomFichier, taille, mimeType);
		this.idFichierStocke = idFichierStocke;
	}

	public String getCodeFichierAenvoyer() {
		return this.codeFichierAenvoyer;
	}

	public Boolean getEstPieceJointe() {
		return this.estPieceJointe;
	}

	public String getIdFichierStocke() {
		return this.idFichierStocke;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public String getNomFichier() {
		return this.nomFichier;
	}

	public Long getTaille() {
		return this.taille;
	}

	public void setCodeFichierAenvoyer(String codeFichierAenvoyer) {
		this.codeFichierAenvoyer = codeFichierAenvoyer;
	}

	public void setEstPieceJointe(Boolean estPieceJointe) {
		this.estPieceJointe = estPieceJointe;
	}

	public void setIdFichierStocke(String idFichierStocke) {
		this.idFichierStocke = idFichierStocke;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}

	public void setTaille(Long taille) {
		this.taille = taille;
	}

}
