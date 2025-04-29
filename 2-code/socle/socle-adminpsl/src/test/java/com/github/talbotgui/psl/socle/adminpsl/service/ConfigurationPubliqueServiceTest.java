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

import java.util.Collection;

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
class ConfigurationPubliqueServiceTest extends AbstractAdminpslServiceTest {

	@Autowired
	private ConfigurationPubliqueService service;

	@Test
	void test01ListerCodesDemarche01BaseVide() {
		//
		//
		Collection<String> codes = this.service.listerLesCodesDeDemarche();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertEquals(0, codes.size());
	}

	@Test
	void test01ListerCodesDemarche02DeuxDemarches() {
		//
		Document document = Document.parse(this.contenuFichierPublic1);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		document = Document.parse(this.contenuFichierPublic2);
		this.mongoTemplateConfiguration.save(document, AbstractMongoDao.COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE);
		//
		Collection<String> codes = this.service.listerLesCodesDeDemarche();
		//
		Assertions.assertNotNull(codes);
		Assertions.assertEquals(2, codes.size());
	}
}
