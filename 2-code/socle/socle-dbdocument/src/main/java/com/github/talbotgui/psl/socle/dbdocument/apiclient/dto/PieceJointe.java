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
package com.github.talbotgui.psl.socle.dbdocument.apiclient.dto;

public class PieceJointe {
	/** Code de la démarche associé. */
	private String codeDemarche;
	/** Code de la pièce jointe dans la démarche. */
	private String codePieceJointe;
	/** Contenu binaire du fichier. */
	private byte[] contenu;
	/** Id du document une fois inséré */
	private String id;
	/** MD5 du contenu. */
	private String md5;
	/** Nom du fichier. */
	private String nom;
	/** Taille du fichier. */
	private Long taille;
	/** Type de contenu. */
	private String typeDeContenu;

	/**
	 * Constructeur.
	 *
	 * @param codeDemarche    Code de la démarche associé.
	 * @param codePieceJointe Code de la pièce jointe dans la démarche.
	 * @param nom             Nom du fichier.
	 * @param taille          Taille du fichier.
	 * @param typeDeContenu   Type de contenu.
	 * @param contenu         Contenu binaire du fichier.
	 * @param md5             MD5 du contenu.
	 */
	public PieceJointe(String codeDemarche, String codePieceJointe, String nom, Long taille, String typeDeContenu, byte[] contenu, String md5) {
		super();
		this.codeDemarche = codeDemarche;
		this.codePieceJointe = codePieceJointe;
		this.nom = nom;
		this.taille = taille;
		this.typeDeContenu = typeDeContenu;
		this.contenu = contenu;
		this.md5 = md5;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public String getCodePieceJointe() {
		return this.codePieceJointe;
	}

	public byte[] getContenu() {
		return this.contenu;
	}

	public String getId() {
		return this.id;
	}

	public String getMd5() {
		return this.md5;
	}

	public String getNom() {
		return this.nom;
	}

	public Long getTaille() {
		return this.taille;
	}

	public String getTypeDeContenu() {
		return this.typeDeContenu;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setCodePieceJointe(String codePieceJointe) {
		this.codePieceJointe = codePieceJointe;
	}

	public void setContenu(byte[] contenu) {
		this.contenu = contenu;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setTaille(Long taille) {
		this.taille = taille;
	}

	public void setTypeDeContenu(String typeDeContenu) {
		this.typeDeContenu = typeDeContenu;
	}

	@Override
	public String toString() {
		return "PieceJointe [codeDemarche=" + this.codeDemarche + ", codePieceJointe=" + this.codePieceJointe + ", nom=" + this.nom + ", taille="
				+ this.taille + ", typeDeContenu=" + this.typeDeContenu + ", md5=" + this.md5 + "]";
	}
}
