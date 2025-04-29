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
package com.github.talbotgui.psl.socle.dbnotification.declenchement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.talbotgui.psl.socle.commun.securite.JwtServicePourAppelsInternes;
import com.github.talbotgui.psl.socle.dbnotification.service.NotificationService;

/**
 * Composant déclenchant les appels au service de traitement d'une notification.
 * 
 * Ce composant ne sert qu'au déclenchement. Il serait possible de poser les annotations sur NotificationService mais, alors, les tests auraient été
 * plus complexes.
 * 
 */
@Component
public class DeclenchementComposant {

	/** Instance du service JWT interne. */
	@Autowired
	private JwtServicePourAppelsInternes jwtServiceInterne;

	/** Instance du service de transfert. */
	@Autowired
	private NotificationService notificationService;

	/**
	 * Méthode de déclenchement du service toutes les 3 secondes (@Scheduled) avec possibilité de travail en parallèle (@Async).
	 * 
	 * /!\ le nombre de traitement en // est limité par la clef de configuration spring.task.scheduling.pool.size.
	 */
	@Scheduled(fixedDelayString = "${declenchement.delaiEntreDeuxRecherchesDeNotification}", initialDelayString = "${declenchement.delaiInitialAvantRecherchesDeNotification}")
	@Async
	public void declencherAppelAuServiceDeTraitementDunTransfert() {
		// Recréation d'un token anonyme pour chaque appel au service
		this.jwtServiceInterne.genererEtSauvegarderNouveauTokenInterne();

		// Appel au service
		this.notificationService.rechercherEtTraiterUneNotification();
	}
}
