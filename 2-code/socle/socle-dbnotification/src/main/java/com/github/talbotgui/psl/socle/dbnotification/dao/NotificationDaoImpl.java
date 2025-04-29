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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.StatutDocumentNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.LigneStatistiqueComptageParStatut;

@Service
public class NotificationDaoImpl extends AbstractMongoDao implements NotificationDao {

	@Autowired
	public NotificationDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	@Override
	public List<LigneStatistiqueComptageParStatut> compterToutParStatut() {
		// Définition du "groupBy"
		GroupOperation agregation = Aggregation.group(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).count().as(ATTRIBUT_NOMBRE);

		// Ajout éventuel d'un filtre (juste pour l'exemple)
		MatchOperation filtres = null;

		// Exécution de la requête
		return super.rechercherAvecAgregation(agregation, filtres, LigneStatistiqueComptageParStatut.class, COLLECTION_MONGODB_POUR_NOTIFICATION);
	}

	@Override
	public void modifierEtatNotification(MessageSauvegardeNotification document, StatutDocumentNotification statut) {
		// La modification à réaliser
		Update modifications = new Update();
		modifications.set(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION, statut.name());

		// Déclenchement
		super.mettreAjourUnAttribut(document.getId(), modifications, document.getClass(), COLLECTION_MONGODB_POUR_NOTIFICATION);
	}

	@Override
	public MessageSauvegardeNotification rechercherEtVerrouillerUneNotificationAenvoyer() {

		// Recherche d'un document de notification jamais traité
		MessageSauvegardeNotification doc = this.rechercherEtVerrouillerUneNotificationAenvoyer(null);

		// Si aucun trouvé
		if (doc == null) {
			// Recheche d'un document de notification au statut EN_ERREUR
			doc = this.rechercherEtVerrouillerUneNotificationAenvoyer(StatutDocumentNotification.EN_ERREUR);
		}

		// Renvoi de l'éventuel document trouvé
		return doc;
	}

	/**
	 * Recherche du premier document décrivant une notification à envoyer (tri par date de création descendant) et pose d'un verrou.
	 *
	 * @param statut Statut du document à rechercher (null si le document ne doit pas avoir de statut).
	 * @return l'ID du document.
	 * @see https://www.mongodb.com/community/forums/t/preventing-concurrent-updates-to-ensure-every-thread-gets-a-unique-id-value/14378/4
	 */
	private MessageSauvegardeNotification rechercherEtVerrouillerUneNotificationAenvoyer(StatutDocumentNotification statut) {
		// La modification à réaliser
		Update modifications = new Update();
		modifications.set(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION, StatutDocumentNotification.EN_COURS.name());

		// Création de la requête en fonction du statut fourni
		Criteria criteresFiltre;
		if (statut != null) {
			criteresFiltre = Criteria.where(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).is(statut.name());
		} else {
			criteresFiltre = Criteria.where(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).isNull();
		}

		// Ajout d'une contrainte de temps pour ne pas traiter trop vite les demandes et laisser le temps de supprimer la notification en cas d'erreur
		Date maintenantMoins10secondes = Date.from(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
		criteresFiltre.andOperator(Criteria.where(ATTRIBUT_DATE_CREATION).lt(maintenantMoins10secondes));

		// Création de la requête avec le tri
		Query requete = Query.query(criteresFiltre);
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_DATE_CREATION));

		// recherche & modification puis return
		return super.rechercherPremierDocumentEtModifier(requete, modifications, MessageSauvegardeNotification.class,
				COLLECTION_MONGODB_POUR_NOTIFICATION);
	}

	@Override
	public String sauvegarderNotificationEmail(MessageSauvegardeNotificationEmailDto message) {
		return super.inserer(message, COLLECTION_MONGODB_POUR_NOTIFICATION).getId();
	}

	@Override
	public String sauvegarderNotificationSp(MessageSauvegardeNotificationSpDto message) {
		return super.inserer(message, COLLECTION_MONGODB_POUR_NOTIFICATION).getId();
	}

	@Override
	public void supprimerNotificationAvantTraitement(String idNotification) {
		Criteria criteresFiltre = Criteria.where(ATTRIBUT_ID).is(idNotification)//
				.andOperator(Criteria.where(ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).isNull());
		Query requete = Query.query(criteresFiltre);
		super.supprimerParRequete(requete, COLLECTION_MONGODB_POUR_NOTIFICATION);
	}
}
