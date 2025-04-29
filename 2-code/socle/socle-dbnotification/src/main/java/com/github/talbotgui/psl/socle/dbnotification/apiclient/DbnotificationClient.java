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
package com.github.talbotgui.psl.socle.dbnotification.apiclient;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class DbnotificationClient extends AbstractClientHttp implements NotificationAPI {

	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/notification";
	/** URL de l'API des emails. */
	public static final String URI_SAUVEGARDE_EMAIL = URI_DE_BASE + PREFIXE_URI_INTERNE + "email";
	/** URL de l'API des notifications. */
	public static final String URI_SAUVEGARDE_NOTIFICATION = URI_DE_BASE + PREFIXE_URI_INTERNE + "notification";
	/** URL de l'API des statistiques d'administration. */
	public static final String URI_STATISTIQUES = URI_DE_BASE + PREFIXE_URI_ADMIN + "statistiques";
	/** URL de l'API d'une notification (pour DELETE ou GET). */
	public static final String URI_UNE_NOTIFICATION_PAR_ID = URI_DE_BASE + PREFIXE_URI_INTERNE + "{idNotification}";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public DbnotificationClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	@Override
	public StatistiquesNotificationDto calculerStatistiques() {
		throw new NotificationException(NotificationException.API_ADMIN_NON_DISPONIBLE);
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	@Override
	public String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message) {
		return super.executerRequetePost(URI_SAUVEGARDE_EMAIL, new TypeReference<String>() {
		}, message, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message) {
		return super.executerRequetePost(URI_SAUVEGARDE_NOTIFICATION, new TypeReference<String>() {
		}, message, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public void supprimerNotificationAvantTraitement(String idNotification) {
		String url = URI_UNE_NOTIFICATION_PAR_ID.replace("{idNotification}", idNotification);
		super.executerRequeteDelete(url, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}
}
