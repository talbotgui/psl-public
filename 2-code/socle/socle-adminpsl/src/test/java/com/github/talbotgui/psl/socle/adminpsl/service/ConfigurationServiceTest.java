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
package com.github.talbotgui.psl.socle.adminpsl.service;

import java.util.Map;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.talbotgui.psl.socle.adminpsl.application.SocleAdminPslApplication;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;

/** Classe de test de ConfigurationPubliqueService. */
@SpringBootTest(classes = SocleAdminPslApplication.class)
//Activation de Mockito dans ce test qui ne génére qu'une unique instance de la classe de test pour l'ensemble des méthodes de la classe
//Sans le PER_CLASS car les @BeforeAll et @AfterAll ne fonctionnent pas (Spring démarre avant l'exécution du @BeforeAll qui démarre la BDD)
//@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
//Héritage de AbstractMongoTest pour démarrer/arrêter MongoDB
class ConfigurationServiceTest extends AbstractAdminpslServiceTest {

	@Autowired
	private ConfigurationService service;

	@Test
	void test01ListerVersionsPubliques01BaseVide() {
		//
		//
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertEquals(0, versions.size());
	}

	@Test
	void test01ListerVersionsPubliques02UneVersion() {
		//
		Document document = Document.parse(this.contenuFichierPublic1);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		//
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertEquals(1, versions.size());
	}

	@Test
	void test02ChargerConfigurationPublique() {
		//
		Document document = Document.parse(this.contenuFichierPublic1);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL);
		//
		String idAcharger = versions.entrySet().iterator().next().getKey();
		Object configuration = this.service.chargerVersionDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL, idAcharger);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertTrue(configuration.toString().contains(CODE_DEMARCHE_ETATCIVIL));
	}

	@Test
	void test03SauvegarderConfigurationPublique01insertion() {
		//
		int nbVersionsAvant = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL).size();
		//
		this.service.sauvegarderVersionDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL, null, this.contenuFichierPublic1, null);
		//
		int nbVersionsApres = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL).size();
		Assertions.assertEquals(nbVersionsAvant + 1, nbVersionsApres);
	}

	@Test
	void test03SauvegarderConfigurationPublique02maj() {
		//
		int nbVersionsAvant = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL).size();
		String id1 = this.service.sauvegarderVersionDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL, null, this.contenuFichierPublic1, null);
		Object contenuFichier1AvecId = this.service.chargerVersionDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL, id1);
		//
		String id2 = this.service.sauvegarderVersionDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL, id1, contenuFichier1AvecId.toString(), null);
		//
		int nbVersionsApres = this.service.listerLesVersionsDeConfiguration(true, CODE_DEMARCHE_ETATCIVIL).size();
		Assertions.assertEquals(nbVersionsAvant + 1, nbVersionsApres);
		Assertions.assertEquals(id1, id2);
	}

	@Test
	void test04ListerVersionsInternes01BaseVide() {
		//
		//
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertEquals(0, versions.size());
	}

	@Test
	void test04ListerVersionsInternes02UneVersion() {
		//
		Document document = Document.parse(this.contenuFichierInterne1);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		//
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL);
		//
		Assertions.assertNotNull(versions);
		Assertions.assertEquals(1, versions.size());
	}

	@Test
	void test05ChargerConfigurationInterne() {
		//
		Document document = Document.parse(this.contenuFichierInterne1);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE);
		Map<String, String> versions = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL);
		//
		String idAcharger = versions.entrySet().iterator().next().getKey();
		Object configuration = this.service.chargerVersionDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL, idAcharger);
		//
		Assertions.assertNotNull(configuration);
		Assertions.assertTrue(configuration.toString().contains(CODE_DEMARCHE_ETATCIVIL));
	}

	@Test
	void test06SauvegarderConfigurationInterne01insertion() {
		//
		int nbVersionsAvant = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL).size();
		//
		this.service.sauvegarderVersionDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL, null, this.contenuFichierInterne1, null);
		//
		int nbVersionsApres = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL).size();
		Assertions.assertEquals(nbVersionsAvant + 1, nbVersionsApres);
	}

	@Test
	void test06SauvegarderConfigurationInterne02maj() {
		//
		int nbVersionsAvant = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL).size();
		String id1 = this.service.sauvegarderVersionDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL, null, this.contenuFichierInterne1, null);
		Object contenuFichier1AvecId = this.service.chargerVersionDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL, id1);
		//
		String id2 = this.service.sauvegarderVersionDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL, id1, contenuFichier1AvecId.toString(),
				null);
		//
		int nbVersionsApres = this.service.listerLesVersionsDeConfiguration(false, CODE_DEMARCHE_ETATCIVIL).size();
		Assertions.assertEquals(nbVersionsAvant + 1, nbVersionsApres);
		Assertions.assertEquals(id1, id2);
	}
}
