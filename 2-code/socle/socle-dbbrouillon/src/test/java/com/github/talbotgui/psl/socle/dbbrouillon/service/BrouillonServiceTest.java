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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.github.talbotgui.psl.socle.commun.test.JwtPourLesTestsUtils;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.commundb.test.AbstractMongoTest;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;
import com.github.talbotgui.psl.socle.dbbrouillon.application.SocleDbbrouillonApplication;
import com.github.talbotgui.psl.socle.dbbrouillon.exception.BrouillonException;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.NotificationDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationinterne.TypeNotificationEnum;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.FonctionnalitesDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PointEntreeDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.api.NotificationAPI;
import com.googlecode.catchexception.CatchException;
import com.mongodb.client.MongoClients;

/** Classe de test de BrouillonService. */
@SpringBootTest(classes = SocleDbbrouillonApplication.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
//Sans le PER_CLASS car les @BeforeAll et @AfterAll ne fonctionnent pas (Spring démarre avant l'exécution du @BeforeAll qui démarre la BDD)
//@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class BrouillonServiceTest extends AbstractMongoTest {

	/** Données du bouchon de configuration */
	private static final ConfigurationInterneDemarcheDto CONF_INTERNE_AVEC_NOTIFICATION = new ConfigurationInterneDemarcheDto("code", "version", null,
			Map.of(NotificationDto.CLEF_NOTIFICATIONS_BROUILLON, Arrays.asList(//
					// Un email complet
					new NotificationDto(TypeNotificationEnum.email, "<div>test email ${data1}</div>", true, "objet ${data1}",
							"test1@mail.com,${email1}"), //
					// Une notification SP complète
					new NotificationDto(TypeNotificationEnum.notificationSP, "<div>test email ${data1}</div>", "http://aze", 30), //
					// Un email sans contenu
					new NotificationDto(TypeNotificationEnum.email, "", true, "objet ${data1}", "test1@mail.com,${email1}"), //
					// Une notification SP sans contenu
					new NotificationDto(TypeNotificationEnum.notificationSP, "", "http://aze", 30), //
					// Un email incomplet
					new NotificationDto(TypeNotificationEnum.email, "<div>test email ${data1}</div>", true, "", "test1@mail.com,${email1}") //
			)));

	/** Données du bouchon de configuration */
	private static final ConfigurationInterneDemarcheDto CONF_INTERNE_SANS_NOTIFICATION = new ConfigurationInterneDemarcheDto("code", "version", null,
			Map.of(NotificationDto.CLEF_NOTIFICATIONS_BROUILLON, Arrays.asList()));

	/** Données du bouchon de configuration */
	private static final ConfigurationPubliqueDemarcheDto CONF_PUBLIQUE_BROUILLON_ACTIF = new ConfigurationPubliqueDemarcheDto("code", "version",
			new FonctionnalitesDto(true, true));

	/** Données du bouchon de configuration */
	private static final ConfigurationPubliqueDemarcheDto CONF_PUBLIQUE_BROUILLON_ACTIF_AVEC_POINT_ENTREE = new ConfigurationPubliqueDemarcheDto(
			"code", "version", new FonctionnalitesDto(true, true), Arrays.asList(new PointEntreeDto("auth", null)));

	/** Données du bouchon de configuration */
	private static final ConfigurationPubliqueDemarcheDto CONF_PUBLIQUE_BROUILLON_INACTIF = new ConfigurationPubliqueDemarcheDto("code", "version",
			new FonctionnalitesDto(false, false));

	@Value("${spring.data.mongodb.database}")
	private String baseDeDonneeMongoDB;

	/** Bouchon du client d'appel à l'API Configuration présent dans le service. */
	@MockitoBean
	private ConfigurationAPI bouchonClientConfiguration;

	/** Bouchon du client d'appel à l'API Notification présent dans le service. */
	@MockitoBean
	private NotificationAPI bouchonClientNotification;

	@Value("${spring.data.mongodb.host}")
	private String hoteMongoDB;

	private MongoTemplate mongoTemplate;

	@Value("${spring.data.mongodb.port:0}")
	private int portMongoDB;

	/** Le secret utilisé pour chiffrer les tokens JWT. */
	@Value("${jwt.secretJWT}")
	protected String secret;

	/**
	 * Cette variable n'est là que pour se faire injecter ses membres bouchonnés.
	 */
	@Autowired
	private BrouillonService service;

	@Override
	@BeforeEach
	protected void avantChaqueTest(TestInfo testInfo) {
		// Appel à la méthode parente
		super.avantChaqueTest(testInfo);
		// Gestion des bouchons
		Mockito.reset(this.bouchonClientConfiguration, this.bouchonClientNotification);
		// Création du mongoTemplate si ce n'est pas déjà fait
		if (this.mongoTemplate == null) {
			String chaineConnexion = "mongodb://" + this.hoteMongoDB + ":" + this.portMongoDB;
			this.mongoTemplate = new MongoTemplate(MongoClients.create(chaineConnexion), this.baseDeDonneeMongoDB);
		}
		// Destruction/recréation des collections
		this.mongoTemplate.dropCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		this.mongoTemplate.createCollection(AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
	}

	@Test
	void test01SauvegardeBrouillon01PremiereFois() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		//
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_DEMARCHE).is(brouillon.getCodeDemarche()));
		BrouillonDto trouve = this.mongoTemplate.findOne(requete, BrouillonDto.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		Assertions.assertNotNull(trouve);
		Assertions.assertEquals(id, trouve.getId());
	}

	@Test
	void test01SauvegardeBrouillon02SecondeFois() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>(Map.of("C1", "V1")));
		//
		String id1 = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		brouillon.getDonnees().entrySet().iterator().next().setValue("valeurModifiée");
		String id2 = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		Assertions.assertEquals(id1, id2);
		Query requete = Query.query(Criteria.where(AbstractMongoDao.ATTRIBUT_CODE_DEMARCHE).is(brouillon.getCodeDemarche()));
		BrouillonDto trouve = this.mongoTemplate.findOne(requete, BrouillonDto.class, AbstractMongoDao.COLLECTION_MONGODB_POUR_BROUILLON);
		Assertions.assertNotNull(trouve);
		Assertions.assertEquals(id1, trouve.getId());
	}

	@Test
	void test01SauvegardeBrouillon03FonctionnaliteInactive() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_INACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_INACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_INACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_INACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_INACTIF.getVersionConfiguration(), 2, new HashMap<>(Map.of("C1", "V1")));
		//
		CatchException.catchException(() -> this.service.sauvegarderBrouillon(tokenJwt, brouillon));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(BrouillonException.equals(CatchException.caughtException(), BrouillonException.FONCTIONNALITE_INACTIVE),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02TelechargerBrouillon01avecUnMotDePasse() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		BrouillonDto dtoTrouve = this.service.rechercherBrouillon(tokenJwt, brouillon.getCodeDemarche(), id);
		//
		Assertions.assertNotNull(dtoTrouve);
		Assertions.assertEquals(dtoTrouve.getCodeDemarche(), brouillon.getCodeDemarche());
		Assertions.assertEquals(dtoTrouve.getVersionConfiguration(), brouillon.getVersionConfiguration());
	}

	@Test
	void test02TelechargerBrouillon02avecOIDC() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTest(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		BrouillonDto dtoTrouve = this.service.rechercherBrouillon(tokenJwt, brouillon.getCodeDemarche(), id);
		//
		Assertions.assertNotNull(dtoTrouve);
		Assertions.assertEquals(dtoTrouve.getCodeDemarche(), brouillon.getCodeDemarche());
		Assertions.assertEquals(dtoTrouve.getVersionConfiguration(), brouillon.getVersionConfiguration());
	}

	@Test
	void test03SupprimerBrouillon01Nominal() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		this.service.supprimerBrouillon(tokenJwt, CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), id);
		//
	}

	@Test
	void test03SupprimerBrouillon02Absent() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		String id = "idInexistant";
		//
		CatchException.catchException(() -> this.service.supprimerBrouillon(tokenJwt, CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), id));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(MongodbException.equals(CatchException.caughtException(), MongodbException.ERREUR_DOCUMENT_NON_EXISTANT),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test04NotificationDeSauvegarde01PremiereFois() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_AVEC_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		//
		this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		Mockito.verify(this.bouchonClientNotification).sauvegarderNotificationEmail(Mockito.any());
	}

	@Test
	void test05ObtenirAuthentification01NominalSansPointEntree() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF).when(this.bouchonClientConfiguration).rechercherConfigurationPubliqueDeDemarche(
				CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		String authentification = this.service.obtenirAuthentificationDunBrouillon(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), id);
		//
		Assertions.assertEquals("", authentification);
	}

	@Test
	void test05ObtenirAuthentification02NominalAvecPointEntree() {
		//
		String tokenJwt = JwtPourLesTestsUtils.genererTokenJwtPourUnTestAvecUnMotDePasse(this.secret);
		Mockito.doReturn(CONF_INTERNE_SANS_NOTIFICATION).when(this.bouchonClientConfiguration)
				.rechercherDerniereConfigurationInterneDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche());
		Mockito.doReturn(CONF_PUBLIQUE_BROUILLON_ACTIF_AVEC_POINT_ENTREE).when(this.bouchonClientConfiguration)
				.rechercherConfigurationPubliqueDeDemarche(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
						CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration());
		BrouillonDto brouillon = new BrouillonDto(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(),
				CONF_PUBLIQUE_BROUILLON_ACTIF.getVersionConfiguration(), 2, new HashMap<>());
		String id = this.service.sauvegarderBrouillon(tokenJwt, brouillon);
		//
		String authentification = this.service.obtenirAuthentificationDunBrouillon(CONF_PUBLIQUE_BROUILLON_ACTIF.getCodeDemarche(), id);
		//
		Assertions.assertEquals("auth", authentification);
	}
}
