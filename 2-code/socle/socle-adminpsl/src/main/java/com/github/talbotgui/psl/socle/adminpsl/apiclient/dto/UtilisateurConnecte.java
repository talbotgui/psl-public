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
package com.github.talbotgui.psl.socle.adminpsl.apiclient.dto;

import java.util.ArrayList;
import java.util.Collection;

/** Informations de l'utilisateur connecté. */
public class UtilisateurConnecte {

	/** Groupes */
	private Collection<String> groupes = new ArrayList<>();

	/** Nom */
	private String nom;

	/** Login */
	private String nomUtilisateur;

	/** Constructeur sans paramètre. */
	public UtilisateurConnecte() {
		super();
	}

	/**
	 * Constructeur avec tous les champs
	 *
	 * @param nomUtilisateur Login.
	 * @param nom            Nom.
	 * @param groupes        Groupes.
	 */
	public UtilisateurConnecte(String nomUtilisateur, String nom, Collection<String> groupes) {
		super();
		this.nomUtilisateur = nomUtilisateur;
		this.nom = nom;
		this.groupes = groupes;
	}

	public Collection<String> getGroupes() {
		return this.groupes;
	}

	public String getNom() {
		return this.nom;
	}

	public String getNomUtilisateur() {
		return this.nomUtilisateur;
	}

	public void setGroupes(Collection<String> groupes) {
		this.groupes = groupes;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setNomUtilisateur(String nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}

	@Override
	public String toString() {
		return "UtilisateurConnecte [nomUtilisateur=" + this.nomUtilisateur + ", nom=" + this.nom + ", groupes=" + this.groupes + "]";
	}
}
