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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.StatutDocumentNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;
import com.github.talbotgui.psl.socle.dbnotification.client.EmailClient;
import com.github.talbotgui.psl.socle.dbnotification.dao.NotificationDao;

import io.micrometer.tracing.Tracer;

@Service
public class NotificationServiceImpl implements NotificationService {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired
	private NotificationDao dao;
	@Autowired
	private EmailClient emailService;
	@Autowired
	private NotificationSpService notificationSpService;
	@Autowired(required = false)
	private Tracer traceur;

	@Override
	public StatistiquesNotificationDto calculerStatistiques() {
		// Création du DTO
		StatistiquesNotificationDto stats = new StatistiquesNotificationDto();

		// Alimentation
		stats.setNombreParStatut(this.dao.compterToutParStatut());

		// Adaptation du contenu :
		// les status vides sont des demandes de notification en attente de traitement
		stats.getNombreParStatut().forEach(nbParS -> {
			if (nbParS.getStatut() == null) {
				nbParS.setStatut("EN_ATTENTE_DE_TRAITEMENT");
			}
		});

		// Calculs de données
		int total = stats.getNombreParStatut().stream().map(nbParS -> nbParS.getNombre()).reduce(0, (a, b) -> a + b);
		stats.setNombreTotal(total);

		// Renvoi du DTO
		return stats;
	}

	@Override
	public void rechercherEtTraiterUneNotification() {
		// Ce log est en débug pour ne pas générer des centaines de lignes de log inutiles si aucune notification n'est traitée
		LOGGER.debug("Début de la recherche/traitement d'une notification à envoyer");

		// Initialisation des variables à null en dehors du TRY pour les avoir à disposition dans le catch et réaliser des compensations (pas de
		// transactions dans un système de micro-services)
		MessageSauvegardeNotification notificationAenvoyer = null;

		try {
			// Recherche d'une notification à envoyer
			notificationAenvoyer = this.dao.rechercherEtVerrouillerUneNotificationAenvoyer();

			// Si pas d'identifiant, on ressort de la méthode tout simplement
			if (notificationAenvoyer == null) {
				LOGGER.debug("Aucune notification à envoyer");
				return;
			}

			// Log de début du traitement de cette notification
			LOGGER.info("Début d'envoi de la notification '{}'", notificationAenvoyer.getId());

			// Envoi de la notification
			if (notificationAenvoyer instanceof MessageSauvegardeNotificationEmailDto msned) {
				this.emailService.envoyerEmail(msned);
			} else if (notificationAenvoyer instanceof MessageSauvegardeNotificationSpDto msned) {
				this.notificationSpService.creerOuModifierNotificationPourTeledossier(msned);
			} else {
				LOGGER.error("Type de demande inconnu : '{}'", notificationAenvoyer.getClass());
				return;
			}

			// Modification du statut
			this.dao.modifierEtatNotification(notificationAenvoyer, StatutDocumentNotification.TRAITE);

			// Log de fin du traitement
			LOGGER.info("Fin de l'envoi");
		}

		// Gestion d'erreur : si une erreur est remontée à un quelconque moment,
		catch (Exception e) {
			LOGGER.error("Erreur durant la recherche et l'envoi d'une notification. ", e);
			// la notification passe au statut EN_ERREUR
			if (notificationAenvoyer != null) {
				LOGGER.info("Compensation pour la notification {} : passage au statut EN_ERREUR", notificationAenvoyer.getId());
				this.dao.modifierEtatNotification(notificationAenvoyer, StatutDocumentNotification.EN_ERREUR);
			}
		}

		// Ce log est en débug pour ne pas générer des centaines de lignes de log inutiles si aucun télé-dossier n'est traité
		LOGGER.debug("Fin de la recherche/traitement d'une notification à envoyer");
	}

	@Override
	public String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message) {
		LOGGER.info("Sauvegarde d'une notification par mail");

		// Si le message n'est pas cohérent, erreur
		if (!message.verifierMessageEstComplet()) {
			throw new NotificationException(NotificationException.NOTIFICATION_NON_ENREGISTREE);
		}

		// Initialisation de la date
		message.setDateCreation(new Date());
		if (this.traceur.currentSpan() != null && this.traceur.currentSpan().context() != null) {
			message.setIdTrace(this.traceur.currentSpan().context().toString());
		}

		// Sinon, sauvegarde
		return this.dao.sauvegarderNotificationEmail(message);
	}

	@Override
	public String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message) {
		LOGGER.info("Sauvegarde d'une notification SP");

		// Si le message n'est pas cohérent, erreur
		if (!message.verifierMessageEstComplet()) {
			LOGGER.warn("La demande de notification SP n'est pas complète (détails en niveau TRACE).");
			LOGGER.trace("La demande est : {}", message);
			throw new NotificationException(NotificationException.NOTIFICATION_NON_ENREGISTREE);
		}

		// Initialisation de la date
		message.setDateCreation(new Date());
		if (this.traceur.currentSpan() != null && this.traceur.currentSpan().context() != null) {
			message.setIdTrace(this.traceur.currentSpan().context().toString());
		}

		// Sinon, sauvegarde
		return this.dao.sauvegarderNotificationSp(message);
	}

	@Override
	public void supprimerNotificationAvantTraitement(String idNotification) {
		LOGGER.info("Suppression de la notification '{}'", idNotification);

		try {
			// Tentative de suppression
			this.dao.supprimerNotificationAvantTraitement(idNotification);
		} catch (MongodbException e) {
			// Si zéro demande supprimée, erreur spécifique
			if (AbstractException.equals(e, MongodbException.ERREUR_DOCUMENT_NON_EXISTANT)) {
				throw new NotificationException(NotificationException.NOTIFICATION_NON_SUPPRIMEE);
			}
		}
	}
}
