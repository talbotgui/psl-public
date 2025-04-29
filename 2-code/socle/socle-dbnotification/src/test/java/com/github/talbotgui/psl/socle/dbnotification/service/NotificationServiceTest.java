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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationSpDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.StatutDocumentNotification;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.StatistiquesNotificationDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;
import com.github.talbotgui.psl.socle.dbnotification.application.SocleDbnotificationApplication;
import com.github.talbotgui.psl.socle.dbnotification.client.EmailClient;
import com.googlecode.catchexception.CatchException;
import com.mongodb.client.MongoClients;

/** Classe de test de ConfigurationService. */
@SpringBootTest(classes = SocleDbnotificationApplication.class)
//Sans le PER_CLASS car les @BeforeAll et @AfterAll ne fonctionnent pas (Spring démarre avant l'exécution du @BeforeAll qui démarre la BDD)
//@TestInstance(Lifecycle.PER_CLASS)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest extends AbstractMongoTest {

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@MockitoBean
	private EmailClient bouchonEmailService;

	/**
	 * Bouchon de service permettant de tester les actions du service
	 * rechercherEtTraiterUneNotification.
	 */
	// Implémentation déclarée pour les besoins de Mockito
	@MockitoBean
	private NotificationSpService bouchonNotificationSpService;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	private MongoTemplate mongoTemplate;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private NotificationService service;

	/** Avant chaque test MAIS AUSSI A LA FIN DE LA CLASSE. */
	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la méthode parente
		super.avantChaqueTest(testInfo);

		// Création du mongoTemplate si ce n'est pas déjà fait
		if (this.mongoTemplate == null) {
			String chaineConnexion = "mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB;
			this.mongoTemplate = new MongoTemplate(MongoClients.create(chaineConnexion), this.baseDeDonneeMongoDB);
		}
		// Destruction/recréation des collections
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);

		// Reset des bouchons
		Mockito.reset(this.bouchonEmailService, this.bouchonNotificationSpService);
	}

	@Test
	void test01SauvegarderEmail() {
		MessageSauvegardeNotificationEmailDto message = ObjectMother.creerNotificationEmail();
		//
		String idDansMongo = this.service.sauvegarderNotificationEmail(message);
		//
		Query requeteNbEmails = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_ID).ne(null));
		Assertions.assertEquals(1, this.mongoTemplate.count(requeteNbEmails, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
		Assertions.assertNotNull(idDansMongo);
	}

	@Test
	void test02SauvegardeNotification() {
		MessageSauvegardeNotificationSpDto message = ObjectMother.creerNotificationSpAvecToken();
		//
		String idDansMongo = this.service.sauvegarderNotificationSp(message);
		//
		Query requeteNbEmails = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_ID).ne(null));
		Assertions.assertEquals(1, this.mongoTemplate.count(requeteNbEmails, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
		Assertions.assertNotNull(idDansMongo);
	}

	@Test
	void test03rechercherEtTraiterUneNotification01aucunDocumentDisponible() {
		//
		//
		this.service.rechercherEtTraiterUneNotification();
		//
		Mockito.verifyNoInteractions(this.bouchonEmailService, this.bouchonNotificationSpService);
	}

	// Test un peu particulier car il contient deux ACT. C'est voulu ! Le cas de
	// plusieurs documents doit exister. Autant tester le cas de chaque type
	// de message et le cas des multiples messages en même temps.
	@Test
	void test03rechercherEtTraiterUneNotification02deuxNotificationsEnvoyeesAvecSuccess() {
		//
		this.mongoTemplate.insert(ObjectMother.creerNotificationEmail(), AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);
		this.mongoTemplate.insert(ObjectMother.creerNotificationSpAvecUUID(), AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);
		Mockito.doNothing().when(this.bouchonEmailService).envoyerEmail(Mockito.any());
		Mockito.doNothing().when(this.bouchonNotificationSpService).creerOuModifierNotificationPourTeledossier(Mockito.any());
		//
		this.service.rechercherEtTraiterUneNotification();
		this.service.rechercherEtTraiterUneNotification();
		//
		Query requeteNbDocuments = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).is(StatutDocumentNotification.TRAITE));
		Assertions.assertEquals(2, this.mongoTemplate.count(requeteNbDocuments, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
	}

	@Test
	void test03rechercherEtTraiterUneNotification03uneNotificationEnEchec() {
		this.mongoTemplate.insert(ObjectMother.creerNotificationEmail(), AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);
		Mockito.doThrow(new RuntimeException("boum")).when(this.bouchonEmailService).envoyerEmail(Mockito.any());
		//
		this.service.rechercherEtTraiterUneNotification();
		//
		Query requeteNbDocuments = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).is(StatutDocumentNotification.EN_ERREUR));
		Assertions.assertEquals(1, this.mongoTemplate.count(requeteNbDocuments, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
	}

	@Test
	void test03rechercherEtTraiterUneNotification04uneNotificationAvecUnEchecPuisUnSuccess() {
		this.mongoTemplate.insert(ObjectMother.creerNotificationEmail(), AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION);
		Mockito.doThrow(new RuntimeException("boumAuPremierAppel")).doNothing().when(this.bouchonEmailService).envoyerEmail(Mockito.any());
		this.service.rechercherEtTraiterUneNotification();
		//
		this.service.rechercherEtTraiterUneNotification();
		//
		Query requeteNbDocuments = Query
				.query(Criteria.where(AbstractMongoDao.ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION).is(StatutDocumentNotification.TRAITE));
		Assertions.assertEquals(1, this.mongoTemplate.count(requeteNbDocuments, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
	}

	@Test
	void test04SuppressionNotification01Absente() {
		//
		String idDansMongo = "absent";
		//
		CatchException.catchException(() -> this.service.supprimerNotificationAvantTraitement(idDansMongo));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(NotificationException.equals(CatchException.caughtException(), NotificationException.NOTIFICATION_NON_SUPPRIMEE),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test04SuppressionNotification02CasNominal() {
		//
		String idDansMongo = this.service.sauvegarderNotificationSp(ObjectMother.creerNotificationSpAvecToken());
		//
		this.service.supprimerNotificationAvantTraitement(idDansMongo);
		//
		Query requeteNbDocuments = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_ID).is(idDansMongo));
		Assertions.assertEquals(0, this.mongoTemplate.count(requeteNbDocuments, AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION));
	}

	@Test
	void test04SuppressionNotification03InterditCarNotificationDejaTraitee() {
		String idDansMongo = this.mongoTemplate
				.insert(ObjectMother.creerNotificationSpAvecToken(), AbstractMongoDao.COLLECTION_MONGODB_POUR_NOTIFICATION).getId();
		Mockito.doNothing().when(this.bouchonNotificationSpService).creerOuModifierNotificationPourTeledossier(Mockito.any());
		this.service.rechercherEtTraiterUneNotification();
		//
		CatchException.catchException(() -> this.service.supprimerNotificationAvantTraitement(idDansMongo));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(NotificationException.equals(CatchException.caughtException(), NotificationException.NOTIFICATION_NON_SUPPRIMEE),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test05Statistiques01baseVide() {
		//
		//
		StatistiquesNotificationDto stats = this.service.calculerStatistiques();
		//
		Assertions.assertNotNull(stats);
		Assertions.assertEquals(0, stats.getNombreParStatut().size());
	}

	@Test
	void test05Statistiques02baseNonVide() {
		this.service.sauvegarderNotificationSp(ObjectMother.creerNotificationSpAvecToken());
		//
		StatistiquesNotificationDto stats = this.service.calculerStatistiques();
		//
		Assertions.assertNotNull(stats);
		Assertions.assertEquals(1, stats.getNombreParStatut().size());
		Assertions.assertEquals(1, stats.getNombreParStatut().get(0).getNombre());
	}
}
