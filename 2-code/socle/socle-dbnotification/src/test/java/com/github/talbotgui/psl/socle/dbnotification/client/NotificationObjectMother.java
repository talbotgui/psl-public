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
package com.github.talbotgui.psl.socle.dbnotification.client;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.DemandeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationTeledossierAvecPremiereNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteNotificationAction;

/**
 * Classe créant des jeux de données.
 * 
 * /!\ Tous les DTO sont des record. Donc pas de réutilisation des méthodes entre elles.
 */
public class NotificationObjectMother {

	public static final String CODE_DEMARCHE = "arnaqueInternet";
	public static final String LIBELLE_DEMARCHE = "Arnaque Internet";
	public static final String MESSAGE = "Vous avez sauvegarder un brouillon dans ArnaqueInternet";
	public static final int NOMBRE_JOUR_AVANT_EXPIRATION = 45;
	public static final String ORIGINE = "INFORMATIONS ADMINISTRATIVES ET LÉGALES";
	public static final String URL_REPRISE = "https://rienDuTout";

	/** Création d'un DemandeNotificationSpDto */
	public static DemandeNotificationSpDto creerDemandeNotificationSpDeBrouillonDemarcheParticuliere(String accessToken, String refreshToken,
			String numeroTeledossier, String statut) {
		return DemandeNotificationSpDto.creerDemandePourNotificationBrouillonDemarcheParticuliere(accessToken, refreshToken, CODE_DEMARCHE,
				LIBELLE_DEMARCHE, numeroTeledossier, NOMBRE_JOUR_AVANT_EXPIRATION, MESSAGE, statut, URL_REPRISE);
	}

	/**
	 * Requete de création d'un télédossier avec un numéro aléatoire (sur le même modèle que Postman).
	 * 
	 * @return
	 */
	public static RequeteCreationTeledossierAvecPremiereNotification creerRequeteCreationTeledossierAvecPremiereNotification() {
		Date date = new Date();
		String id = genererNumeroTeledossier();
		String statut = NotificationAPI.STATUT_NOTIFICATION_BROUILLON;
		List<RequeteNotificationAction> actions = null;
		RequeteCreationNotification statutInitial = new RequeteCreationNotification(date, MESSAGE, id, statut, ORIGINE, URL_REPRISE, actions);

		return new RequeteCreationTeledossierAvecPremiereNotification(CODE_DEMARCHE, LIBELLE_DEMARCHE, id, NOMBRE_JOUR_AVANT_EXPIRATION,
				statutInitial);
	}

	/**
	 * Requête de création d'un second statut dans le fil d'activité.
	 * 
	 * @return
	 */
	public static RequeteCreationNotification creerRequeteSecondeNotification(String idDemarche) {
		Date date = new Date();
		String idDemarcheComplementaire = idDemarche;
		String statut = NotificationAPI.STATUT_NOTIFICATION_BROUILLON;
		List<RequeteNotificationAction> actions = null;
		return new RequeteCreationNotification(date, MESSAGE, idDemarcheComplementaire, statut, ORIGINE, URL_REPRISE, actions);
	}

	/**
	 * Méthode de génération d'un numéro de télédossier.
	 * 
	 * @return
	 */
	public static String genererNumeroTeledossier() {
		return "SP2-AI-" + UUID.randomUUID().toString().substring(0, 8) + "-GTAL";
	}

}
