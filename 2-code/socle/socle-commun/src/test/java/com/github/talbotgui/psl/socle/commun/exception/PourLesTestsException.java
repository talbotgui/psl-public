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

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Exception développée spécifiquement pour tester AbstractException. */
public class PourLesTestsException extends AbstractException {

	public static final ExceptionId ERREUR_DE_TEST = new ExceptionId("ERREUR_DE_TEST", NiveauException.ERROR, 400,
			"Message avec une  String '{}', un array '{}' et une collection '{}'");

	public static final ExceptionId ERREUR_DE_TEST_SIMPLE = new ExceptionId("ERREUR_DE_TEST_SIMPLE", NiveauException.ERROR, 400, "Message");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public PourLesTestsException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public PourLesTestsException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public PourLesTestsException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public PourLesTestsException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}