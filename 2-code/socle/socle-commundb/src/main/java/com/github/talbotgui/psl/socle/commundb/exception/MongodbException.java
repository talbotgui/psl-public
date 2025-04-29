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
package com.github.talbotgui.psl.socle.commundb.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/**
 * Exception de base pour la gestion de document dans MongoDB. (rappel : les exceptions ne doivent pas donner de détails pour des raisons de sécurité.
 * Mais les logs doivent en donner)
 */
public class MongodbException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Exception si le document à sauvegarder est un doublon */
	public static final ExceptionId ERREUR_DOCUMENT_EN_DOUBLE = new ExceptionId("ERREUR_DOCUMENT_EN_DOUBLE", NiveauException.ERROR, 400,
			"Document déjà existant pour ce numéro de télé-dossier et ce code ({},{})");

	/** Exception si aucun document n'est trouvé durant une recherche */
	public static final ExceptionId ERREUR_DOCUMENT_NON_EXISTANT = new ExceptionId("ERREUR_DOCUMENT_NON_EXISTANT", NiveauException.ERROR, 404,
			"Aucun document trouvé");

	/** Exception si le document à sauvegarder est un doublon */
	public static final ExceptionId ERREUR_SAUVEGARDE_DOCUMENT = new ExceptionId("ERREUR_SAUVEGARDE_DOCUMENT", NiveauException.ERROR, 400,
			"Impossible de sauvegarder ce document ou document altérée durant le transfert");

	/** Exception si le document n'est pas autorisé */
	public static final ExceptionId ERREUR_SAUVEGARDE_NON_AUTORISEE = new ExceptionId("ERREUR_SAUVEGARDE_NON_AUTORISEE", NiveauException.ERROR, 400,
			"Document non autorisé dans cette démarche (erreur {})");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public MongodbException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public MongodbException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public MongodbException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public MongodbException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}