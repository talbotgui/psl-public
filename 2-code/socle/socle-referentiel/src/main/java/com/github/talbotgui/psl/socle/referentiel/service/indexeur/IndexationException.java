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
package com.github.talbotgui.psl.socle.referentiel.service.indexeur;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Exception générée par l'indexation des données. */
public class IndexationException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	public static final ExceptionId ERREUR_INDEXATION = new ExceptionId("ERREUR_INDEXATION", NiveauException.ERROR, 500, "Erreur LUCENE");

	public static final ExceptionId ERREUR_INDEXATION_DOCUMENT = new ExceptionId("ERREUR_INDEXATION_DOCUMENT", NiveauException.ERROR, 500,
			"{} erreurs de transformation des {} objets en document : {}");

	public static final ExceptionId ERREUR_INITIALISATION_INDEXES = new ExceptionId("ERREUR_INITIALISATION_INDEXES", NiveauException.ERROR, 500,
			"{} erreurs de mise à jour des indexes : {}");

	public static final ExceptionId ERREUR_RECHERCHE = new ExceptionId("ERREUR_RECHERCHE", NiveauException.ERROR, 500,
			"Erreur lors de la recherche ({},{},{})");

	public static final ExceptionId ERREUR_VIDANGE = new ExceptionId("ERREUR_VIDANGE", NiveauException.ERROR, 500,
			"Erreur durant la vidange des indexes");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public IndexationException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public IndexationException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public IndexationException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public IndexationException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}