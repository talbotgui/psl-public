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
package com.github.talbotgui.psl.socle.dbbrouillon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commun.apiclient.ApiClientException;
import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;
import com.github.talbotgui.psl.socle.commun.utils.MotDePasseUtils;
import com.github.talbotgui.psl.socle.commun.utils.VelocityUtils;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;
import com.github.talbotgui.psl.socle.dbbrouillon.dao.BrouillonDao;
import com.github.talbotgui.psl.socle.dbbrouillon.exception.BrouillonException;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.NotificationDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.TypeNotificationEnum;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PointEntreeDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.utils.PointEntreeUtils;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;

import feign.FeignException;
import io.jsonwebtoken.Claims;

@Service
public class BrouillonServiceImpl implements BrouillonService {
	/**
	 * Clef de donnée de brouillon dans laquelle est stockée le hash du nom d'utilisateur et du mot de passe si la connexion s'est faite par mot de
	 * passe.
	 */
	private static final String CLEF_DONNEE_HASH_AUTHENTIFICATION_PAR_MOT_DE_PASSE = "CLEF_DONNEE_HASH_AUTHENTIFICATION_PAR_MOT_DE_PASSE";

	/** Clef de donnée de brouillon dans laquelle est stockée l'UUID de l'usager connecté à SP/FC. */
	private static final String CLEF_DONNEE_UUID_AUTHENTIFICATION_PAR_OIDC = "CLEF_DONNEE_UUID_AUTHENTIFICATION_PAR_OIDC";

	private static final Logger LOGGER = LoggerFactory.getLogger(BrouillonServiceImpl.class);

	/** Client d'appel de l'API des configurations */
	@Autowired
	private ConfigurationAPI clientDocumentPourConfiguration;

	@Autowired
	private NotificationAPI clientNotification;

	@Autowired
	private BrouillonDao dao;

	/** Service de manipulation de token. */
	@Autowired
	private JwtService jwtService;

	/**
	 * Chargement de la configuration interne utilisée par la démarche
	 *
	 * @param codeDemarche Code de la démarche
	 * @return Configuration
	 */
	private ConfigurationInterneDemarcheDto chargerConfigurationInterne(String codeDemarche) {
		try {
			LOGGER.trace("Chargement de la configuration interne de la démarche {}", codeDemarche);
			ConfigurationInterneDemarcheDto configuration = this.clientDocumentPourConfiguration
					.rechercherDerniereConfigurationInterneDeDemarche(codeDemarche);
			LOGGER.trace("Configuration interne chargée : {}", configuration);
			return configuration;
		} catch (ApiClientException | FeignException e) {
			throw new BrouillonException(BrouillonException.FONCTIONNALITE_INACTIVE, e, codeDemarche);
		}
	}

	/**
	 * Chargement de la configuration publique utilisée par la démarche
	 *
	 * @param codeDemarche         Code de la démarche
	 * @param versionConfiguration Version de la configuration
	 * @return Configuration
	 */
	private ConfigurationPubliqueDemarcheDto chargerConfigurationPublique(String codeDemarche, String versionConfiguration) {
		try {
			LOGGER.trace("Chargement de la configuration publique de la démarche {}-{}", codeDemarche, versionConfiguration);
			ConfigurationPubliqueDemarcheDto configuration = this.clientDocumentPourConfiguration
					.rechercherConfigurationPubliqueDeDemarche(codeDemarche, versionConfiguration);
			LOGGER.trace("Configuration publique chargée : {}", configuration);
			return configuration;
		} catch (ApiClientException | FeignException e) {
			throw new BrouillonException(BrouillonException.FONCTIONNALITE_INACTIVE, e, codeDemarche + "-" + versionConfiguration);
		}
	}

	/**
	 * Sauvegarde des données d'authentification (OIDC ou par mot de passe) dans les données du brouillon
	 *
	 * @param tokenJwt Token JWT
	 * @param dto      Donnée du brouillon
	 */
	private void enregistrerDonneesAuthentificationDansLeBrouillon(String tokenJwt, BrouillonDto dto) {
		// Extraction des claims du token
		Claims claims = this.jwtService.extraireEtDechiffrerClaimsDuToken(tokenJwt);

		// Stockage des paramètres de connexion (mode mot de passe)
		String hashConnexionEventuel = MotDePasseUtils.hasherNomUtilisateurEtMotDePasse(claims.getSubject(),
				(String) claims.get(JwtService.CLEF_CLAIMS_MOT_DE_PASSE));
		if (StringUtils.hasLength(hashConnexionEventuel)) {
			dto.getDonnees().put(CLEF_DONNEE_HASH_AUTHENTIFICATION_PAR_MOT_DE_PASSE, hashConnexionEventuel);
		}

		// Stockage de l'identifiant du compte (mode OIDC)
		else if (StringUtils.hasLength(claims.getSubject())) {
			dto.getDonnees().put(CLEF_DONNEE_UUID_AUTHENTIFICATION_PAR_OIDC, claims.getSubject());
		}

		// Si aucun des deux, on a un bug
		else {
			throw new BrouillonException(BrouillonException.AUTHENTIFICATION_INCOHERENTE);
		}
	}

	/**
	 * Méthode générant la demande de notification (email ou SP).
	 *
	 * /!\ Code très semblable à com.github.talbotgui.psl.socle.soumission.service.SoumissionServiceImpl#envoyerNotification et
	 * com.github.talbotgui.psl.socle.transfert.service.TransfertServiceImpl#envoyerNotification sans la mécanique de compensation car le brouillon doit être
	 * sauvegardé avant la notification.
	 *
	 * @param tokenJwt    Token JWT de l'usager.
	 * @param dto         DTO contenant les données du brouillon.
	 * @param idBrouillon ID du brouillon créé.
	 */
	private void envoyerNotification(String tokenJwt, BrouillonDto dto, String idBrouillon) {

		// Récupération de la configuration interne de la démarche
		ConfigurationInterneDemarcheDto configuration = this.chargerConfigurationInterne(dto.getCodeDemarche());
		if (configuration == null) {
			LOGGER.warn("Aucune configuration interne existante pour la démarche {}", LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
			throw new BrouillonException(BrouillonException.FONCTIONNALITE_INACTIVE, dto.getCodeDemarche());
		}

		// Récupération des notifications de brouillon s'il en existe
		if ((configuration.getNotifications() == null)
				|| !configuration.getNotifications().containsKey(NotificationDto.CLEF_NOTIFICATIONS_BROUILLON)) {
			LOGGER.warn("Aucune notification de brouillon n'est prévue pour la démarche {}",
					LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
			return;
		}
		List<NotificationDto> configurationDesNotifications = configuration.getNotifications()
				.get(NotificationDto.CLEF_NOTIFICATIONS_BROUILLON);

		// Extraction des tokens (si soumission connectée)
		String accessToken = null;
		String refreshToken = null;
		if (StringUtils.hasLength(tokenJwt)) {
			accessToken = this.jwtService.extraireClaimDuToken(tokenJwt, JwtService.CLEF_CLAIMS_REFRESH_TOKEN_OIDC);
			refreshToken = this.jwtService.extraireClaimDuToken(tokenJwt, JwtService.CLEF_CLAIMS_ACCESS_TOKEN_OIDC);
		}

		// Création du contexte Velocity avec les données du brouillon
		VelocityContext contexte = VelocityUtils.creerContexteVelocityAvecEchapementXmlSystematique(//
				Map.of("idBrouillon", idBrouillon, "codeDemarche", dto.getCodeDemarche()), //
				dto.getDonnees());

		// Résolution des templates de contenu et création des messages à envoyer
		List<MessageSauvegardeNotification> messagesAenvoyer = new ArrayList<>();
		for (final NotificationDto notificationDto : configurationDesNotifications) {

			// Résolution du contenu
			String contenu = VelocityUtils.resoudreTemplate(contexte, notificationDto.getTemplateContenu(), dto.getCodeDemarche(), idBrouillon);

			// Si KO ou vide, on ignore la notification (gain de performance que de ne pas résoudre les autres templates)
			if (!StringUtils.hasLength(contenu)) {
				continue;
			}

			// Résolution des autres templates
			String objet = VelocityUtils.resoudreTemplate(contexte, notificationDto.getTemplateObjet(), dto.getCodeDemarche(), idBrouillon);
			String destinataires = VelocityUtils.resoudreTemplate(contexte, notificationDto.getTemplateDestinataires(), dto.getCodeDemarche(),
					idBrouillon);
			String lienReprise = VelocityUtils.resoudreTemplate(contexte, notificationDto.getTemplateLienReprise(), dto.getCodeDemarche(),
					idBrouillon);

			// Création du message correspondant
			if (TypeNotificationEnum.email.equals(notificationDto.getType())) {
				List<String> listeDestinataires = Arrays.asList((destinataires != null ? destinataires : "").split("[,;]"));
				messagesAenvoyer.add(new MessageSauvegardeNotificationEmailDto(contenu, notificationDto.getContenuHtml(), listeDestinataires, null,
						null, objet, null));
			} else if (TypeNotificationEnum.notificationSP.equals(notificationDto.getType())) {
				messagesAenvoyer.add(new MessageSauvegardeNotificationSpDto(accessToken, dto.getCodeDemarche(), null, null, dto.getCodeDemarche(),
						contenu, notificationDto.getNombreJoursAvantExpiration(), idBrouillon, refreshToken,
						NotificationAPI.STATUT_NOTIFICATION_BROUILLON, null, null, lienReprise, null));
			} else {
				LOGGER.warn("Email sans type reconnu dans la configuration de la démarche {}",
						LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
			}
		}

		// Filtrage des notifications par la condition d'activation
		messagesAenvoyer = messagesAenvoyer.stream().filter(m -> m.verifierMessageEstComplet()).toList();
		if (messagesAenvoyer.isEmpty()) {
			LOGGER.warn("Aucun message de notification ne sera envoyé pour le brouillon '{}' de la démarche {}",
					LogUtils.nettoyerDonneesAvantDeLogguer(dto.getId()), LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
		}

		// Création des demandes de notification
		for (final MessageSauvegardeNotification messageAenvoyer : messagesAenvoyer) {
			if (messageAenvoyer instanceof final MessageSauvegardeNotificationEmailDto msned) {
				this.clientNotification.sauvegarderNotificationEmail(msned);
			} else {
				this.clientNotification.sauvegarderNotificationSp((MessageSauvegardeNotificationSpDto) messageAenvoyer);
			}
		}

		// Log
		LOGGER.info("{} notifications envoyées pour le brouillon '{}'", messagesAenvoyer.size(), LogUtils.nettoyerDonneesAvantDeLogguer(dto.getId()));
	}

	@Override
	public String obtenirAuthentificationDunBrouillon(String codeDemarche, String idBrouillon) {
		// Chargement du brouillon pour récupérer ses données
		BrouillonDto brouillon = this.dao.rechercherBrouillon(codeDemarche, idBrouillon);

		// Vérification de la demande
		if ((codeDemarche == null) || !codeDemarche.equals(brouillon.getCodeDemarche())) {
			throw new BrouillonException(BrouillonException.DONNEE_INCOHERENTE);
		}

		// Chargement de la configuration associée
		ConfigurationPubliqueDemarcheDto configuration = this.clientDocumentPourConfiguration
				.rechercherConfigurationPubliqueDeDemarche(codeDemarche, brouillon.getVersionConfiguration());

		String typeAuthentification;

		// Si des points d'entrée existent, il faut en trouver un correspondant aux données
		if ((configuration.getPointsEntree() != null) && !configuration.getPointsEntree().isEmpty()) {
			// Recherche du type d'authentification
			PointEntreeDto pointEntreeCorrespondant = PointEntreeUtils.rechercherPointEntreeCorrespondant(configuration.getPointsEntree(),
					brouillon.getDonnees());
			if (pointEntreeCorrespondant == null) {
				// Erreur si aucun trouvé
				throw new BrouillonException(BrouillonException.DONNEE_INCOHERENTE);
			} else {
				// Sauvegarde de l'authentification associée
				typeAuthentification = pointEntreeCorrespondant.authentification();
			}
		}

		// Sans point d'entrée, l'authentification est anonyme par défaut
		else {
			typeAuthentification = "";
		}

		// renvoi du résultat
		return typeAuthentification;
	}

	@Override
	public BrouillonDto rechercherBrouillon(String tokenJwt, String codeDemarche, String idBrouillon) {
		// Chargement du brouillon
		BrouillonDto dto = this.dao.rechercherBrouillon(codeDemarche, idBrouillon);

		// Vérification des données du brouillon vis-à-vis de l'utilisateur connecté
		this.verifierAuthentificationAvantChargementDuBrouillon(tokenJwt, dto);

		// Retrait des informations liées à l'authentification (inutile de les faire remonter)
		this.supprimerDonneesAuthentificationDuBrouillon(dto);

		// renvoi du brouillon
		return dto;
	}

	@Override
	public String sauvegarderBrouillon(String tokenJwt, BrouillonDto dto) {
		// au cas où
		if (dto == null) {
			return null;
		}

		// Log utilisé dans la pile Elastic. A ne pas modifier !
		if (StringUtils.hasLength(dto.getId())) {
			LOGGER.info("Sauvegarde d'un brouillon déjà existant pour la démarche {}.",
					LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
		} else {
			LOGGER.info("Sauvegarde d'un brouillon pour la démarche {}.", LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
		}

		// Vérification de l'activation explicite de la fonctionnalité
		ConfigurationPubliqueDemarcheDto configuration = this.chargerConfigurationPublique(dto.getCodeDemarche(),
				dto.getVersionConfiguration());
		if ((configuration.getFonctionnalites() == null) || (configuration.getFonctionnalites().brouillon() == null)
				|| !configuration.getFonctionnalites().brouillon().booleanValue()) {
			LOGGER.warn("Pas de brouillon actif dans la configuration de la démarche {}",
					LogUtils.nettoyerDonneesAvantDeLogguer(dto.getCodeDemarche()));
			throw new BrouillonException(BrouillonException.FONCTIONNALITE_INACTIVE, dto.getCodeDemarche());
		}

		// Sauvegarde des données d'authentification (OIDC ou par mot de passe) dans les données du brouillon
		this.enregistrerDonneesAuthentificationDansLeBrouillon(tokenJwt, dto);

		// Sauvegarde
		String idBrouillon = this.dao.sauvegarderBrouillon(dto);

		// Création de la notification de mail
		// sans mécanique de compensation car le brouillon doit être sauvegardé avant la notification.
		this.envoyerNotification(tokenJwt, dto, idBrouillon);

		// renvoi de l'ID du brouillon
		return idBrouillon;
	}

	@Override
	public void supprimerBrouillon(String tokenJwt, String codeDemarche, String idBrouillon) {

		// Chargement du brouillon
		BrouillonDto dto = this.dao.rechercherBrouillon(codeDemarche, idBrouillon);

		// Vérification des données du brouillon vis-à-vis de l'utilisateur connecté
		this.verifierAuthentificationAvantChargementDuBrouillon(tokenJwt, dto);

		// Suppression du brouillon
		this.dao.supprimerBrouillon(codeDemarche, idBrouillon);
	}

	/**
	 * Retrait des informations liées à l'authentification (inutile de les faire remonter)
	 *
	 * @param dto Données du brouillon
	 */
	private void supprimerDonneesAuthentificationDuBrouillon(BrouillonDto dto) {
		dto.getDonnees().remove(CLEF_DONNEE_HASH_AUTHENTIFICATION_PAR_MOT_DE_PASSE);
		dto.getDonnees().remove(CLEF_DONNEE_UUID_AUTHENTIFICATION_PAR_OIDC);
	}

	/**
	 * Vérification des données du brouillon vis-à-vis de l'utilisateur connecté.
	 *
	 * Aucune distinction entre les erreurs ne doit être possible pour assurer la sécurité de l'application.
	 *
	 * @param tokenJwt Token JWT
	 * @param dto      Donnée du brouillon
	 */
	private void verifierAuthentificationAvantChargementDuBrouillon(String tokenJwt, BrouillonDto dto) {

		// Lecture des données d'authentification du token actuel
		Claims claims = this.jwtService.extraireEtDechiffrerClaimsDuToken(tokenJwt);
		String nomUtilisateur = claims.getSubject();
		String motDePasse = (String) claims.get(JwtService.CLEF_CLAIMS_MOT_DE_PASSE);

		// Lecture des données dans le brouillon
		String hashConnexionDuBrouillon = dto.getDonnees().get(CLEF_DONNEE_HASH_AUTHENTIFICATION_PAR_MOT_DE_PASSE);
		String uuidUsagerDuBrouillon = dto.getDonnees().get(CLEF_DONNEE_UUID_AUTHENTIFICATION_PAR_OIDC);

		// Contrôle si authentification par mot de passe au moment de la sauvegarde du brouillon
		if (StringUtils.hasLength(hashConnexionDuBrouillon)) {
			if (!MotDePasseUtils.verifierCorrespondance(hashConnexionDuBrouillon, nomUtilisateur, motDePasse)) {
				throw new BrouillonException(BrouillonException.AUTHENTIFICATION_INCOHERENTE);
			} else {
				LOGGER.debug("Authentification par mot de passe vérifiée au chargement du brouillon");
			}
		}
		// Contrôle si authentification OIDC au moment de la sauvegarde du brouillon
		else if (StringUtils.hasLength(uuidUsagerDuBrouillon)) {
			if (!uuidUsagerDuBrouillon.equals(nomUtilisateur)) {
				throw new BrouillonException(BrouillonException.AUTHENTIFICATION_INCOHERENTE);
			} else {
				LOGGER.debug("Authentification OIDC vérifiée au chargement du brouillon");
			}
		}
		// Si aucun cas ne passe
		else {
			throw new BrouillonException(BrouillonException.AUTHENTIFICATION_INCOHERENTE);
		}

	}
}
