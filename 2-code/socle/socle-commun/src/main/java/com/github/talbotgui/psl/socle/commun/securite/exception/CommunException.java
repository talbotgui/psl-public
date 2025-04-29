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
package com.github.talbotgui.psl.socle.commun.securite.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Classe d'exception du socle commun. */
public class CommunException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Exception en cas de problème de sécurité (sans plus de détails pour ne pas donner d'indication à un attaquant. */
	public static final ExceptionId ERREUR_GENERIQUE = new ExceptionId("ERREUR_GENERIQUE", NiveauException.ERROR, 500,
			"Erreur d'exécution du serveur");

	/** Exception si les données ne sont pas conformes à l'attendu mais sans plus de précision pour éviter de guider un éventuel fraudeur/hacker */
	public static final ExceptionId ERREUR_GENERIQUE_APPELANT = new ExceptionId("ERREUR_GENERIQUE_APPELANT", NiveauException.ERROR, 400,
			"Erreur d'exécution de l'appelant");

	/** Exception en cas de micro-service manquant */
	public static final ExceptionId ERREUR_GENERIQUE_MICROSERVICE_ABSENT = new ExceptionId("ERREUR_GENERIQUE_MICROSERVICE_ABSENT",
			NiveauException.ERROR, 404, "Erreur d'exécution car microservice manquant");

	/**
	 * Exception si les données fournies en entrées sont techniquement invalides mais sans plus de précision pour éviter de guider un éventuel
	 * fraudeur/hacker
	 */
	public static final ExceptionId ERREUR_VALIDATION_DONNEES = new ExceptionId("ERREUR_VALIDATION_DONNEES", NiveauException.WARNING, 400,
			"Les données envoyées ne sont pas valides");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public CommunException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public CommunException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public CommunException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public CommunException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}