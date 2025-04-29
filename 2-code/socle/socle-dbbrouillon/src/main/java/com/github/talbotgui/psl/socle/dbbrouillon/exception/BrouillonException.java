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
package com.github.talbotgui.psl.socle.dbbrouillon.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

public class BrouillonException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Exception si l'authentification liée à la requête est incohérente. */
	public static final ExceptionId AUTHENTIFICATION_INCOHERENTE = new ExceptionId("AUTHENTIFICATION_INCOHERENTE", NiveauException.WARNING, 403,
			"L'authentification est incohérente.");

	/** Exception si la requête est incohérente. */
	public static final ExceptionId DONNEE_INCOHERENTE = new ExceptionId("DONNEE_INCOHERENTE", NiveauException.WARNING, 400,
			"Les données sont incohérentes.");

	/** Exception si un appel à une API interne échoue sans plus de précision pour éviter de guider un éventuel fraudeur/hacker */
	// Niveau Warning car un log d'erreur est codé systématiquement
	public static final ExceptionId FONCTIONNALITE_INACTIVE = new ExceptionId("FONCTIONNALITE_INACTIVE", NiveauException.WARNING, 400,
			"La sauvegarde de brouillon n'est pas active dans la démarche {}.");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public BrouillonException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public BrouillonException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public BrouillonException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public BrouillonException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}