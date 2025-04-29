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
package com.github.talbotgui.psl.socle.dbnotification.apiclient.dto;

/**
 * DTO de demande de création/modification d'une notification.
 */
public class DemandeNotificationSpDto {

	/**
	 * Constructeur pour le cas d'une création de notification/modification de brouillon/soumission dans un espace associatif (utilisation du token
	 * OIDC de l'usager).
	 *
	 * @param accessToken                Access token OIDC (expiré ou pas).
	 * @param refreshToken               Refresh token OIDC (pour traiter le cas de l'accessToken expiré).
	 * @param uuidEspaceAssociation      UUID de l'espace associatif.
	 * @param codeDemarche               Code de la démarche.
	 * @param libelleDemarche            Libelle de la démarche.
	 * @param idExterneNotification      Numéro du télédossier.
	 * @param nombreJoursAvantExpiration Durée de vie de la notification (en jours).
	 * @param messageAafficher           Message à afficher.
	 * @param statut                     Statut de la demande (@see
	 *                                   com.github.talbotgui.psl.socle.dbnotification.service.NotificationSpServiceImpl#STATUT_NOTIFICATION_BROUILLON par
	 *                                   exemple)
	 * @param urlReprise                 URL de reprise du brouillon (le libellé est fixe côté SP).
	 */
	public static DemandeNotificationSpDto creerDemandePourNotificationBrouillonDemarcheAssociative(String accessToken, String refreshToken,
			String uuidEspaceAssociation, String codeDemarche, String libelleDemarche, String idExterneNotification, int nombreJoursAvantExpiration,
			String messageAafficher, String statut, String urlReprise) {
		return new DemandeNotificationSpDto(accessToken, codeDemarche, null, null, libelleDemarche, messageAafficher, nombreJoursAvantExpiration,
				idExterneNotification, refreshToken, statut, null, null, urlReprise, uuidEspaceAssociation);
	}

	/**
	 * DTO pour le cas d'une création/modification de notification de brouillon/soumission dans un espace particulier (utilisation du token OIDC de
	 * l'usager).
	 *
	 * @param accessToken                Access token OIDC (expiré ou pas).
	 * @param refreshToken               Refresh token OIDC (pour traiter le cas de l'accessToken expiré).
	 * @param codeDemarche               Code de la démarche.
	 * @param libelleDemarche            Libelle de la démarche.
	 * @param idExterneNotification      Numéro du télédossier.
	 * @param nombreJoursAvantExpiration Durée de vie de la notification (en jours).
	 * @param messageAafficher           Message à afficher.
	 * @param statut                     Statut de la demande (@see
	 *                                   com.github.talbotgui.psl.socle.dbnotification.service.NotificationSpServiceImpl#STATUT_NOTIFICATION_BROUILLON par
	 *                                   exemple)
	 * @param urlReprise                 URL de reprise du brouillon (le libellé est fixe côté SP).
	 */
	public static DemandeNotificationSpDto creerDemandePourNotificationBrouillonDemarcheParticuliere(String accessToken, String refreshToken,
			String codeDemarche, String libelleDemarche, String idExterneNotification, int nombreJoursAvantExpiration, String messageAafficher,
			String statut, String urlReprise) {
		return new DemandeNotificationSpDto(accessToken, codeDemarche, null, null, libelleDemarche, messageAafficher, nombreJoursAvantExpiration,
				idExterneNotification, refreshToken, statut, null, null, urlReprise, null);
	}

	/**
	 * Constructeur pour le cas d'une modification de notification après la soumission (utilisation du token technique).
	 *
	 * @param uuidEspace                 UUID de l'espace (particulier ou associatif).
	 * @param codeDemarche               Code de la démarche.
	 * @param libelleDemarche            Libelle de la démarche.
	 * @param idExterneNotification      Numéro du télédossier.
	 * @param nombreJoursAvantExpiration Durée de vie de la notification (en jours).
	 * @param messageAafficher           Message à afficher.
	 * @param urlReprise                 URL de reprise du brouillon (le libellé est fixe côté SP).
	 * @param libelleBouton1             Libellé d'un bouton supplémentaire.
	 * @param urlBouton1                 URL associée au premier bouton supplémentaire.
	 * @param libelleBouton2             Libellé d'un bouton supplémentaire.
	 * @param urlBouton2                 URL associée au second bouton supplémentaire.
	 *
	 */
	public static DemandeNotificationSpDto creerDemandePourNotificationPostSoumission(String uuidEspace, String codeDemarche, String libelleDemarche,
			String idExterneNotification, int nombreJoursAvantExpiration, String messageAafficher, String urlReprise, String libelleBouton1,
			String urlBouton1, String libelleBouton2, String urlBouton2) {
		return new DemandeNotificationSpDto(null, codeDemarche, libelleBouton1, libelleBouton2, libelleDemarche, messageAafficher,
				nombreJoursAvantExpiration, idExterneNotification, null, null, urlBouton1, urlBouton2, urlReprise, uuidEspace);
	}

	/** Access token OIDC (expiré ou pas). */
	private String accessToken;

	/** Code de la démarche. */
	private String codeDemarche;

	/** Numéro du télédossier ou ID du brouillon. */
	private String idExterneNotification;

	/** Identifiant dans les logs de la création et/ou modification (ajout par concaténation séparée par un espace) de cet objet. */
	private String idTrace;

	/** Libellé d'un bouton supplémentaire. */
	private String libelleBouton1;

	/** Libellé d'un bouton supplémentaire. */
	private String libelleBouton2;

	/** Libellé de la démarche. */
	private String libelleDemarche;

	/** Message à afficher. */
	private String messageAafficher;

	/** Durée de vie de la notification (en jours). */
	private int nombreJoursAvantExpiration;

	/** Refresh token OIDC (pour traiter le cas de l'accessToken expiré). */
	private String refreshToken;

	/**
	 * Statut du télédossier
	 *
	 * @see com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI#STATUT_NOTIFICATION_BROUILLON par exemple
	 */
	private String statutTeledossier;

	/** URL associée au premier bouton supplémentaire. */
	private String urlBouton1;

	/** URL associée au second bouton supplémentaire. */
	private String urlBouton2;
	/** URL de reprise du brouillon (le libellé est fixe côté SP). */
	private String urlReprise;
	/** UUID de l'espace (particulier ou associatif). */
	private String uuidEspace;

	/** Constructeur pour Jackson. */
	public DemandeNotificationSpDto() {
		super();
	}

	/** Constructeur privé avec tous les membres. */
	protected DemandeNotificationSpDto(String accessToken, String codeDemarche, String libelleBouton1, String libelleBouton2, String libelleDemarche,
			String messageAafficher, int nombreJoursAvantExpiration, String idExterneNotification, String refreshToken, String statutTeledossier,
			String urlBouton1, String urlBouton2, String urlReprise, String uuidEspace) {
		super();
		this.accessToken = accessToken;
		this.codeDemarche = codeDemarche;
		this.libelleBouton1 = libelleBouton1;
		this.libelleBouton2 = libelleBouton2;
		this.libelleDemarche = libelleDemarche;
		this.messageAafficher = messageAafficher;
		this.nombreJoursAvantExpiration = nombreJoursAvantExpiration;
		this.idExterneNotification = idExterneNotification;
		this.refreshToken = refreshToken;
		this.statutTeledossier = statutTeledossier;
		this.urlBouton1 = urlBouton1;
		this.urlBouton2 = urlBouton2;
		this.urlReprise = urlReprise;
		this.uuidEspace = uuidEspace;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getCodeDemarche() {
		return this.codeDemarche;
	}

	public String getIdExterneNotification() {
		return this.idExterneNotification;
	}

	public String getIdTrace() {
		return this.idTrace;
	}

	public String getLibelleBouton1() {
		return this.libelleBouton1;
	}

	public String getLibelleBouton2() {
		return this.libelleBouton2;
	}

	public String getLibelleDemarche() {
		return this.libelleDemarche;
	}

	public String getMessageAafficher() {
		return this.messageAafficher;
	}

	public int getNombreJoursAvantExpiration() {
		return this.nombreJoursAvantExpiration;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public String getStatutTeledossier() {
		return this.statutTeledossier;
	}

	public String getUrlBouton1() {
		return this.urlBouton1;
	}

	public String getUrlBouton2() {
		return this.urlBouton2;
	}

	public String getUrlReprise() {
		return this.urlReprise;
	}

	public String getUuidEspace() {
		return this.uuidEspace;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setCodeDemarche(String codeDemarche) {
		this.codeDemarche = codeDemarche;
	}

	public void setIdExterneNotification(String idExterneNotification) {
		this.idExterneNotification = idExterneNotification;
	}

	public void setIdTrace(String idTrace) {
		this.idTrace = idTrace;
	}

	public void setLibelleBouton1(String libelleBouton1) {
		this.libelleBouton1 = libelleBouton1;
	}

	public void setLibelleBouton2(String libelleBouton2) {
		this.libelleBouton2 = libelleBouton2;
	}

	public void setLibelleDemarche(String libelleDemarche) {
		this.libelleDemarche = libelleDemarche;
	}

	public void setMessageAafficher(String messageAafficher) {
		this.messageAafficher = messageAafficher;
	}

	public void setNombreJoursAvantExpiration(int nombreJoursAvantExpiration) {
		this.nombreJoursAvantExpiration = nombreJoursAvantExpiration;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setStatutTeledossier(String statutTeledossier) {
		this.statutTeledossier = statutTeledossier;
	}

	public void setUrlBouton1(String urlBouton1) {
		this.urlBouton1 = urlBouton1;
	}

	public void setUrlBouton2(String urlBouton2) {
		this.urlBouton2 = urlBouton2;
	}

	public void setUrlReprise(String urlReprise) {
		this.urlReprise = urlReprise;
	}

	public void setUuidEspace(String uuidEspace) {
		this.uuidEspace = uuidEspace;
	}
}
