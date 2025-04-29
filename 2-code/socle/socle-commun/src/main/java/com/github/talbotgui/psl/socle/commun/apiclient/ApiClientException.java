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
package com.github.talbotgui.psl.socle.commun.apiclient;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Exception renvoyées par tout client d'appel à une API REST du projet. */
public class ApiClientException extends AbstractException {

	public static final ExceptionId ERREUR_APPEL = new ExceptionId("ERREUR_APPEL", NiveauException.ERROR, 500,
			"Erreur durant l'appel à l'API \"{}\"");

	public static final ExceptionId ERREUR_CODE_APPEL = new ExceptionId("ERREUR_CODE_APPEL", NiveauException.ERROR, 500,
			"Erreur dans le code de l'appel d'une API");

	public static final ExceptionId ERREUR_CODE_RETOUR = new ExceptionId("ERREUR_CODE_RETOUR", NiveauException.ERROR, 500,
			"Erreur durant l'appel à l'API \"{}\". Le code de retour est \"{}\".");

	public static final ExceptionId ERREUR_CORP_INVALIDE = new ExceptionId("ERREUR_CORP_INVALIDE", NiveauException.ERROR, 500,
			"Erreur durant l'appel à une API car le corps est invalide");

	public static final ExceptionId ERREUR_PREPARATION_APPEL = new ExceptionId("ERREUR_PREPARATION_APPEL", NiveauException.ERROR, 500,
			"Erreur durant la préparation de l'appel à l'API \"{}\"");

	public static final ExceptionId ERREUR_PREPARATION_APPEL_SSL = new ExceptionId("ERREUR_PREPARATION_APPEL_SSL", NiveauException.ERROR, 500,
			"Erreur durant la préparation SSL d'un appel REST");

	public static final ExceptionId ERREUR_TRAITEMENT_REPONSE = new ExceptionId("ERREUR_TRAITEMENT_VALIDATION", NiveauException.ERROR, 500,
			"Erreur durant le traitement de la réponse de l'appel à l'API \"{}\"");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public ApiClientException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public ApiClientException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public ApiClientException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public ApiClientException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}