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
package com.github.talbotgui.psl.socle.commun.utils;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.talbotgui.psl.socle.commun.utils.exception.ValidationException;
import com.googlecode.catchexception.CatchException;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ValidationFichierUtilsTest {

	/** JSONschema valide */
	private static final String MINI_SCHEMA_JSON_VALIDE = "{\"$schema\": \"http://json-schema.org/draft-04/schema#\",\"type\": \"object\",\"properties\": {\"attribut1\": {\"type\": \"string\"},\"tableau\": {\"type\": \"array\",\"items\": [{\"type\": \"object\",\"properties\": {\"att2\": {\"type\": \"string\"}},\"required\": [\"att2\"]},{\"type\": \"object\",\"properties\": {\"att2\": {\"type\": \"string\"}},\"required\": [\"att2\"]}]}},\"required\": [\"attribut1\",\"tableau\"]}";

	/** XSD valide */
	private static final String MINI_XSD_VALIDE = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"html\"><xs:complexType><xs:sequence><xs:element name=\"head\"><xs:complexType><xs:sequence><xs:element type=\"xs:string\" name=\"title\"/></xs:sequence></xs:complexType></xs:element><xs:element type=\"xs:string\" name=\"body\"/></xs:sequence></xs:complexType></xs:element></xs:schema>";

	@Test
	void test01validationXml01versusXsdOk() {
		//
		String xml = "<html><head><title>titre</title></head><body>coucou</body></html>";
		String xsd = MINI_XSD_VALIDE;
		//
		ValidationFichierUtils.validerDocumentXmlAvecUnXsd(xml.getBytes(StandardCharsets.UTF_8), xsd.getBytes(StandardCharsets.UTF_8));
		// rien à faire
	}

	@Test
	void test01validationXml02versusXsdKo() {
		//
		String xml = "<html></html>";
		String xsd = MINI_XSD_VALIDE;
		//
		CatchException.catchException(
				() -> ValidationFichierUtils.validerDocumentXmlAvecUnXsd(xml.getBytes(StandardCharsets.UTF_8), xsd.getBytes(StandardCharsets.UTF_8)));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ValidationException.equals(CatchException.caughtException(), ValidationException.ERREUR_TRAITEMENT_VALIDATION),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test01validationXml03versusStructureOk() {
		//
		String xml = "<html><head><title>titre</title></head><body>coucou</body></html>";
		//
		ValidationFichierUtils.validerDocumentXml(xml.getBytes(StandardCharsets.UTF_8));
		// rien à faire
	}

	@Test
	void test01validationXml04versusStructureKo() {
		//
		String xml = "html >>>";
		//
		CatchException.catchException(() -> ValidationFichierUtils.validerDocumentXml(xml.getBytes(StandardCharsets.UTF_8)));

		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ValidationException.equals(CatchException.caughtException(), ValidationException.ERREUR_TRAITEMENT_VALIDATION),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02validationJson01versusSchemaOk() {
		//
		String json = "{\"attribut1\":\"valeur1\",\"tableau\":[{\"att2\":\"val2\"},{\"att2\":\"val2\"}]}";
		String schema = MINI_SCHEMA_JSON_VALIDE;
		//
		ValidationFichierUtils.validerDocumentJsonAvecSchema(json.getBytes(StandardCharsets.UTF_8), schema.getBytes(StandardCharsets.UTF_8));
		// rien à faire
	}

	@Test
	void test02validationJson02versusSchemaKo() {
		//
		String json = "";
		String schema = MINI_SCHEMA_JSON_VALIDE;
		//
		CatchException.catchException(() -> ValidationFichierUtils.validerDocumentJsonAvecSchema(json.getBytes(StandardCharsets.UTF_8),
				schema.getBytes(StandardCharsets.UTF_8)));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ValidationException.equals(CatchException.caughtException(), ValidationException.ERREUR_TRAITEMENT_VALIDATION),
				CatchException.caughtException().getMessage());
	}

	@Test
	void test02validationJson03versusStructureOk() {
		//
		String json = "{\"attribut1\":\"valeur1\",\"tableau\":[{\"att2\":\"val2\"},{\"att2\":\"val2\"}]}";
		//
		ValidationFichierUtils.validerDocumentJson(json.getBytes(StandardCharsets.UTF_8));
		// rien à faire
	}

	@Test
	void test02validationJson04versusStructureKo() {
		//
		String json = "qsd";
		//
		CatchException.catchException(() -> ValidationFichierUtils.validerDocumentJson(json.getBytes(StandardCharsets.UTF_8)));
		//
		Assertions.assertNotNull(CatchException.caughtException());
		Assertions.assertTrue(ValidationException.equals(CatchException.caughtException(), ValidationException.ERREUR_TRAITEMENT_VALIDATION),
				CatchException.caughtException().getMessage());
	}
}
