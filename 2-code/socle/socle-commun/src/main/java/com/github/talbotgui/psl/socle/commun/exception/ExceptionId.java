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
package com.github.talbotgui.psl.socle.commun.exception;

import java.io.Serializable;

/**
 * Identifiant d'une exception : chaque exception doit disposer d'un identifiant. Cet identifiant est utilisé pour obtenir, lors de sa transformation
 * en erreur HTTP, le code HTTP à retourner et le contenu de la réponse.
 */
public class ExceptionId implements Serializable {

	/** Niveaux de l'exception utilisé pour les logs */
	public enum NiveauException {
		ERROR, FATAL, INFORMATION, WARNING
	}

	private static final long serialVersionUID = 1L;

	/** Code HTTP utilisé si l'exception est renvoyée par une API REST */
	private int codeHttp;

	/** Code unique du type d'exception */
	private String id;

	/** Message par défaut intégré dans la réponse HTTP et loggué */
	private String messageParDefaut;

	/** Niveau de log utilisé pour cette exception */
	private NiveauException niveau;

	/**
	 * Déclaration d'un nouvel identifiant d'exception.
	 *
	 * @param id               Code unique du type d'exception.
	 * @param niveau           Niveau de log utilisé pour cette exception.
	 * @param codeHttp         Code HTTP utilisé si l'exception est renvoyée par une API REST.
	 * @param messageParDefaut Message par défaut intégré dans la réponse HTTP et loggué.
	 */
	public ExceptionId(String id, NiveauException niveau, int codeHttp, String messageParDefaut) {
		super();
		this.id = id;
		this.niveau = niveau;
		this.codeHttp = codeHttp;
		this.messageParDefaut = messageParDefaut;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ExceptionId && obj.hashCode() == this.hashCode();
	}

	public int getCodeHttp() {
		return this.codeHttp;
	}

	public String getId() {
		return this.id;
	}

	public String getMessageParDefaut() {
		return this.messageParDefaut;
	}

	public NiveauException getNiveau() {
		return this.niveau;
	}

	@Override
	public int hashCode() {
		if (this.id == null) {
			return super.hashCode();
		} else {
			return this.id.hashCode();
		}
	}

}
