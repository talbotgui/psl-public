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
package com.github.talbotgui.psl.socle.dbnotification.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.DbnotificationClient;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-dbnotification", contextId = "NotificationApi")
public interface NotificationAPI {

	/** Statut à utiliser dans la demande de création de notification en fonction du cas d'usage : brouillon */
	static final String STATUT_NOTIFICATION_BROUILLON = "enregistre_brouillon";
	/** Statut à utiliser dans la demande de création de notification en fonction du cas d'usage : post soumission */
	static final String STATUT_NOTIFICATION_POST_SOUMISSION = "envoye_transmis";

	/**
	 * ADMIN !! Récupération de statistiques.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping(value = DbnotificationClient.URI_STATISTIQUES, produces = MediaType.APPLICATION_JSON_VALUE)
	StatistiquesNotificationDto calculerStatistiques();

	/**
	 * Sauvegarde de la demande de notification Email.
	 *
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbnotificationClient.URI_SAUVEGARDE_EMAIL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String sauvegarderNotificationEmail(@Valid @RequestBody MessageSauvegardeNotificationEmailDto message);

	/**
	 * Sauvegarde de la demande de notification SP.
	 *
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping(value = DbnotificationClient.URI_SAUVEGARDE_NOTIFICATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	String sauvegarderNotificationSp(@Valid @RequestBody MessageSauvegardeNotificationSpDto message);

	/**
	 * Suppression de la demande de notification avant son traitement.
	 *
	 * @param idNotification ID dans MongoDB.
	 */
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping(value = DbnotificationClient.URI_UNE_NOTIFICATION_PAR_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	void supprimerNotificationAvantTraitement(
			@Pattern(regexp = RegexUtils.ID) @PathVariable(name = "idNotification", required = true) String idNotification);
}