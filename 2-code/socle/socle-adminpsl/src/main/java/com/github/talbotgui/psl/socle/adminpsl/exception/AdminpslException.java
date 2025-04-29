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
package com.github.talbotgui.psl.socle.adminpsl.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/**
 * Classe d'exception du micro-service Adminpsl
 */
public class AdminpslException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Exception si le code de démarche fourni en paramètre existe déjà dans une configuration interne */
	public static final ExceptionId ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_INTERNE = new ExceptionId(
			"ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_INTERNE", NiveauException.WARNING, 400,
			"Code de démarche fourni en paramètre incohérent car déjà utilisé dans une configuration interne");

	/** Exception si le code de démarche fourni en paramètre existe déjà dans une configuration publique */
	public static final ExceptionId ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_PUBLIQUE = new ExceptionId(
			"ERREUR_CODE_DEMARCHE_EXISTANT_DANS_CONFIGURATION_PUBLIQUE", NiveauException.WARNING, 400,
			"Code de démarche fourni en paramètre incohérent car déjà utilisé dans une configuration publique");

	/** Exception si le code de démarche fourni en paramètre n'est pas cohérent avec le reste des données */
	public static final ExceptionId ERREUR_CODE_DEMARCHE_INCOHERENT = new ExceptionId("ERREUR_CODE_DEMARCHE_INCOHERENT", NiveauException.WARNING, 400,
			"Code de démarche fourni en paramètre n'est pas cohérent avec le reste des données");

	/** Exception si l'ID fourni en paramètre n'est pas cohérent avec le reste des données */
	public static final ExceptionId ERREUR_ID_INCOHERENT = new ExceptionId("ERREUR_ID_INCOHERENT", NiveauException.WARNING, 400,
			"ID fourni en paramètre n'est pas cohérent avec le reste des données");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public AdminpslException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public AdminpslException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public AdminpslException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public AdminpslException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}