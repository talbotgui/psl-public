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
package com.github.talbotgui.psl.socle.dbnotification.dao;

import java.util.List;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.StatutDocumentNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.LigneStatistiqueComptageParStatut;

public interface NotificationDao {

	/**
	 * Compte les emails et les notifications SP avec un "groupby" par statut.
	 */
	List<LigneStatistiqueComptageParStatut> compterToutParStatut();

	/**
	 * Modification de l'état de la notification.
	 * 
	 * @param doc      document à modifier.
	 * @param enErreur Statut à placer.
	 */
	void modifierEtatNotification(MessageSauvegardeNotification doc, StatutDocumentNotification enErreur);

	/**
	 * Recherche d'un document décrivant une notification à envoyer et pose d'un verrou
	 * <ul>
	 * <li>au statut "A_FAIRE" sans contrainte de date</li>
	 * <li>au statut "EN_ERREUR" avec une date de traitement de plus de 24h</li>
	 * </ul>
	 * 
	 * @return L'identifiant du premier document trouvé et verrouillé.
	 */
	MessageSauvegardeNotification rechercherEtVerrouillerUneNotificationAenvoyer();

	/**
	 * Sauvegarde de la demande de notification Email.
	 * 
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message);

	/**
	 * Sauvegarde de la demande de notification SP.
	 * 
	 * @param message Détails de la demande.
	 * @return ID dans MongoDB
	 */
	String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message);

	/**
	 * Suppression d'une demande de notification tant qu'elle n'est pas envoyee.
	 * 
	 * @param idNotification Identifiant de la demande.
	 */
	void supprimerNotificationAvantTraitement(String idNotification);

}