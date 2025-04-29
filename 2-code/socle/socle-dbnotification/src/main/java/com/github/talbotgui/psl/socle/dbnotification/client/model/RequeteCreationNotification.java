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
package com.github.talbotgui.psl.socle.dbnotification.client.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Partie du DTO d'appel aux APIs SP
 */
public record RequeteCreationNotification(
		/** Date du jour */
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date date,
		/** Message à envoyer. */
		String message,
		/** Numéro de télé-dossier. */
		@JsonInclude(Include.NON_NULL) String idDemarcheComplementaire,
		/**
		 * Statut du télédossier
		 * 
		 * @see com.github.talbotgui.psl.socle.dbnotification.service.NotificationSpServiceImpl#STATUT_NOTIFICATION_BROUILLON par exemple
		 */
		String statut,
		/** SI envoyant la demande. */
		String origine,
		/** URL de reprise du brouillon. */
		@JsonInclude(Include.NON_NULL) String url,
		/** URLs des boutons supplémentaires. */
		@JsonInclude(Include.NON_NULL) List<RequeteNotificationAction> actions) {

}
