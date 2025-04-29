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
package com.github.talbotgui.psl.socle.securite.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Classe d'erreur de ce micro-service. */
public class SecuriteException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Impossible d'enregistrer l'accessToken */
	public static final ExceptionId ACCESSTOKEN_NON_ENREGISTRABLE = new ExceptionId("ACCESSTOKEN_NON_ENREGISTRABLE", NiveauException.INFORMATION, 400,
			"Le token PSL ou l'accessToken OIDC sont invalides ou incohérents entre eux ({})");

	/** Impossible de créer un token avec un mot de passe à cause des données fournies */
	public static final ExceptionId DONNEES_AUTHENTIFICATION_INVALIDE = new ExceptionId("DONNEES_AUTHENTIFICATION_INVALIDE",
			NiveauException.INFORMATION, 400, "Les données de connexion fournies ne permettent pas la création d'un token ({})");

	/** Impossible de charger les données de l'usager */
	public static final ExceptionId DONNEES_USAGER_INDISPONIBLES = new ExceptionId("DONNEES_USAGER_INDISPONIBLES", NiveauException.INFORMATION, 401,
			"Les données de l'usager sont indisponibles ({})");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SecuriteException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SecuriteException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SecuriteException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SecuriteException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}