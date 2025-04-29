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
package com.github.talbotgui.psl.socle.referentiel.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

public class ReferentielException extends AbstractException {

	/** En cas d'erreur d'appel à une API de référentiel */
	public static final ExceptionId ERREUR_APPEL_EXTERNE = new ExceptionId("ERREUR_APPEL_EXTERNE", NiveauException.ERROR, 500,
			"Impossible d'obtenir les données de référence");

	/** En cas d'erreur de création du cache */
	public static final ExceptionId ERREUR_CREATION_CACHE = new ExceptionId("ERREUR_CREATION_CACHE", NiveauException.ERROR, 500,
			"Impossible de créer le cache dans '{}'");

	/** En cas d'erreur de lecture du cache */
	public static final ExceptionId ERREUR_LECTURE_CACHE = new ExceptionId("ERREUR_LECTURE_CACHE", NiveauException.ERROR, 500,
			"Impossible de lire le cache dans '{}'");

	/** En cas d'erreur de suppression du cache */
	public static final ExceptionId ERREUR_SUPPRESSION_CACHE = new ExceptionId("ERREUR_SUPPRESSION_CACHE", NiveauException.ERROR, 500,
			"Impossible de supprimer le cache dans '{}'");

	private static final long serialVersionUID = 1L;

	public ReferentielException(ExceptionId id) {
		super(id);
	}

	public ReferentielException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	public ReferentielException(ExceptionId id, Throwable nestedException) {
		super(id, nestedException);
	}

	public ReferentielException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}

}
