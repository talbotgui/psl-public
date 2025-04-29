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
package com.github.talbotgui.psl.socle.dbnotification.apiclient.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/** Classe d'erreur d'envoi de notification. */
public class NotificationException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Impossible d'utiliser une API d'administration depuis le client car aucun code Java n'a à appeler les APIs d'administration. */
	public static final ExceptionId API_ADMIN_NON_DISPONIBLE = new ExceptionId("API_ADMIN_NON_DISPONIBLE", NiveauException.WARNING, 400,
			"Impossible d'utiliser une API d'administration depuis le client car aucun code Java n'a à appeler les APIs d'administration");

	/** Impossible d'enregistrer la demande de notification */
	public static final ExceptionId NOTIFICATION_NON_ENREGISTREE = new ExceptionId("NOTIFICATION_NON_ENREGISTREE", NiveauException.ERROR, 500,
			"La demande de notification n'a pas été enregistree");

	/** Impossible d'envoyer la demande de notification */
	public static final ExceptionId NOTIFICATION_NON_ENVOYEE = new ExceptionId("NOTIFICATION_NON_ENVOYEE", NiveauException.ERROR, 500,
			"La demande de notification n'a pas été envoyée");

	/** Impossible de supprimer la demande de notification. */
	public static final ExceptionId NOTIFICATION_NON_SUPPRIMEE = new ExceptionId("NOTIFICATION_NON_SUPPRIMEE", NiveauException.ERROR, 400,
			"La demande de notification n'a pas été supprimée (absente ou déjà envoyée)");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public NotificationException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public NotificationException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public NotificationException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public NotificationException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}