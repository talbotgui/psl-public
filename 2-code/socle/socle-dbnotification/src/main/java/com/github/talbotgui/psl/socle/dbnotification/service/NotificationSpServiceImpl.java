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
package com.github.talbotgui.psl.socle.dbnotification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.oidc.OidcClient;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.DemandeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;
import com.github.talbotgui.psl.socle.dbnotification.client.SpClient;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteCreationTeledossierAvecPremiereNotification;
import com.github.talbotgui.psl.socle.dbnotification.client.model.RequeteNotificationAction;

@Service
public class NotificationSpServiceImpl implements NotificationSpService {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSpServiceImpl.class);

	@Value("${oidc.clientId}")
	private String clientId;

	@Autowired
	private OidcClient clientOidc;

	@Value("${oidc.clientSecret}")
	private String clientSecret;

	@Autowired
	private SpClient clientSp;

	/** Libellé du système demandant la création de la notification. */
	@Value("${sp.origineDemandeNotificationSp}")
	private String origineDemandeNotificationSp;

	@Override
	public void creerOuModifierNotificationPourTeledossier(DemandeNotificationSpDto demande) {

		// Identification du type d'authentification à utiliser pour appeler SP
		boolean oidcCompteTechnique;
		if (StringUtils.hasText(demande.getUuidEspace()) && !StringUtils.hasText(demande.getAccessToken())
				&& !StringUtils.hasText(demande.getRefreshToken())) {
			oidcCompteTechnique = true;
		} else if (!StringUtils.hasText(demande.getUuidEspace()) && StringUtils.hasText(demande.getAccessToken())
				&& StringUtils.hasText(demande.getRefreshToken())) {
			oidcCompteTechnique = false;
		} else {
			LOGGER.warn("Erreur de code : l'appel SP doit être cohérent : OIDC avec un compte usager ou OIDC avec un compte technique");
			throw new NotificationException(NotificationException.NOTIFICATION_NON_ENVOYEE);
		}

		// Récupération du token en fonction du type d'authentification
		String tokenAutiliser;
		if (oidcCompteTechnique) {
			tokenAutiliser = this.clientSp.creerTokenOidcTechnique();
		} else {
			tokenAutiliser = this.clientOidc.raffraichirAccessTokenSiNecessaire(this.clientId, this.clientSecret, demande.getAccessToken(),
					demande.getRefreshToken());
		}

		// Créer le premier statut pour ce télédossier
		try {
			this.creerTeledossierDansFilDactualite(tokenAutiliser, demande);
		} catch (Exception e) {
			this.modifierTeledossierDansFilDactualite(tokenAutiliser, demande);
		}

	}

	/**
	 * Méthode déclarant le télédossier dans le fil d'actualité de SP.
	 *
	 * Le télédossier peut déjà exister !! Cela renvoie une exception.
	 *
	 * @param tokenAutiliser Token OIDC à utiliser
	 * @param demande        Détails de la demande
	 */
	private void creerTeledossierDansFilDactualite(String tokenAutiliser, DemandeNotificationSpDto demande) {
		// Création des DTO
		List<RequeteNotificationAction> actions = new ArrayList<>();
		if (StringUtils.hasLength(demande.getLibelleBouton1()) && StringUtils.hasLength(demande.getUrlBouton1())) {
			actions.add(new RequeteNotificationAction(demande.getLibelleBouton1(), demande.getUrlBouton1()));
		}
		if (StringUtils.hasLength(demande.getLibelleBouton2()) && StringUtils.hasLength(demande.getUrlBouton2())) {
			actions.add(new RequeteNotificationAction(demande.getLibelleBouton2(), demande.getUrlBouton2()));
		}
		RequeteCreationNotification statut = new RequeteCreationNotification(new Date(), demande.getMessageAafficher(),
				demande.getIdExterneNotification(), demande.getStatutTeledossier(), this.origineDemandeNotificationSp, demande.getUrlReprise(),
				actions);
		RequeteCreationTeledossierAvecPremiereNotification requeteCreationTeledossier = new RequeteCreationTeledossierAvecPremiereNotification(
				demande.getCodeDemarche(), demande.getLibelleDemarche(), demande.getIdExterneNotification(), demande.getNombreJoursAvantExpiration(),
				statut);

		try {
			// Appel à SP pour créer le télédossier
			this.clientSp.creerTeledossierParticulierEtPremiereNotification(tokenAutiliser, requeteCreationTeledossier);

		} catch (ApiClientException e) {
			// Si c'est une erreur 400, c'est que le télédossier existe déjà
			if (e.getParametres() != null && e.getParametres().length == 2 && "400".equals(e.getParametres()[1])) {
				// Tentative de MaJ du statut existant
				LOGGER.info("La notification du télé-dossier '{}' semble déjà exister. Mise à jour du statut.", demande.getIdExterneNotification());
				this.clientSp.creerNouvelleNotificationDansUnTeledossierExistantDejaDansTdbParticulier(tokenAutiliser, statut);
			}

			// Sinon c'est un plantage
			else {
				throw e;
			}
		}
	}

	/**
	 * Méthode modifiant le télédossier dans le fil d'actualité de SP.
	 *
	 * Le télédossier doit déjà exister !!
	 *
	 * @param tokenAutiliser Token OIDC à utiliser
	 * @param demande        Détails de la demande
	 */
	private void modifierTeledossierDansFilDactualite(String tokenAutiliser, DemandeNotificationSpDto demande) {
		// Création des DTO
		List<RequeteNotificationAction> actions = new ArrayList<>();
		if (StringUtils.hasLength(demande.getLibelleBouton1()) && StringUtils.hasLength(demande.getUrlBouton1())) {
			actions.add(new RequeteNotificationAction(demande.getLibelleBouton1(), demande.getUrlBouton1()));
		}
		if (StringUtils.hasLength(demande.getLibelleBouton2()) && StringUtils.hasLength(demande.getUrlBouton2())) {
			actions.add(new RequeteNotificationAction(demande.getLibelleBouton2(), demande.getUrlBouton2()));
		}
		RequeteCreationNotification statut = new RequeteCreationNotification(new Date(), demande.getMessageAafficher(),
				demande.getIdExterneNotification(), demande.getStatutTeledossier(), this.origineDemandeNotificationSp, demande.getUrlReprise(),
				actions);

		// Tentative de MaJ du statut existant
		this.clientSp.creerNouvelleNotificationDansUnTeledossierExistantDejaDansTdbParticulier(tokenAutiliser, statut);
	}
}
