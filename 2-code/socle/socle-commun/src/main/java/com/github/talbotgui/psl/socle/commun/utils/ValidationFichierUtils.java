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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.talbotgui.psl.socle.commun.utils.exception.ValidationException;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * Classe utilitaire permettant de valider des documents en fonction d'un format ou d'un schéma.
 */
public class ValidationFichierUtils {

	/**
	 * Validation de la structure JSON du document fourni.
	 *
	 * @param json JSON à valider
	 */
	public static void validerDocumentJson(byte[] json) {
		try {
			new ObjectMapper().readTree(json);
		} catch (IOException e) {
			throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION, e);
		}
	}

	/**
	 * Validation d'un document JSON à partir d'un shéma
	 *
	 * @see https://www.baeldung.com/introduction-to-json-schema-in-java
	 * @param json   JSON à valider (obligatoire)
	 * @param schema Schéma à valider (obligatoire)
	 */
	public static void validerDocumentJsonAvecSchema(byte[] json, byte[] schema) {
		try (ByteArrayInputStream baisJson = new ByteArrayInputStream(json); ByteArrayInputStream baisSchema = new ByteArrayInputStream(schema)) {

			// Validation
			ObjectMapper mapper = new ObjectMapper();
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
			Set<ValidationMessage> erreurs = factory.getSchema(baisSchema).validate(mapper.readTree(baisJson));

			// Gestion des erreurs
			if (!erreurs.isEmpty()) {
				String message = erreurs.stream().map(ValidationMessage::getMessage).reduce("", (a, b) -> a + " / " + b);
				throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION_AVEC_MESSAGE, message);
			}
		}
		// En cas d'erreur d'exécution (lecture de fichier, ... ou validation)
		catch (Exception e) {
			throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION, e);
		}
	}

	/**
	 * Validation d'un document XML vis-à-vis de sa structure.
	 *
	 * @param xml Document XML.
	 */
	public static void validerDocumentXml(byte[] xml) {
		try (ByteArrayInputStream baisXml = new ByteArrayInputStream(xml)) {
			// Initialisation de l'erreur
			Collection<String> erreurs = new ArrayList<>();

			// Gestionnaire d'erreur
			DefaultHandler gestionnaireErreur = new DefaultHandler() {
				@Override
				public void error(SAXParseException e) throws SAXException {
					super.error(e);
					erreurs.add(e.getMessage());
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					super.fatalError(e);
					erreurs.add(e.getMessage());
				}

				@Override
				public void warning(SAXParseException e) throws SAXException {
					super.warning(e);
					erreurs.add(e.getMessage());
				}
			};

			// Les document à valider sont générés par la PSL. DOnc pas de limitation liée à la sécurité (java:S2755)
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse(baisXml, gestionnaireErreur);

			// Gestion des erreurs
			if (!erreurs.isEmpty()) {
				String message = erreurs.stream().reduce("", (a, b) -> a + " / " + b);
				throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION_AVEC_MESSAGE, message);
			}
		}
		// En cas d'erreur de validation
		catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION, e);
		}
	}

	/**
	 * Validation du document XML avec une XSD.
	 *
	 * @param xml Contenu du document XML (contenu obligatoire).
	 * @param xsd Contenu du document XSD (contenu obligatoire).
	 * @throws ValidationException En cas d'erreur de lecture des flux ou d'erreur de validation.
	 */
	public static void validerDocumentXmlAvecUnXsd(byte[] xml, byte[] xsd) {
		try (ByteArrayInputStream baisXml = new ByteArrayInputStream(xml); ByteArrayInputStream baisXsd = new ByteArrayInputStream(xsd)) {
			// Les document à valider sont générés par la PSL. DOnc pas de limitation liée à la sécurité (java:S2755)
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Validator validator = factory.newSchema(new StreamSource(baisXsd)).newValidator();
			validator.validate(new StreamSource(baisXml));
		}
		// En cas d'erreur de validation
		catch (SAXException | IOException e) {
			throw new ValidationException(ValidationException.ERREUR_TRAITEMENT_VALIDATION, e);
		}
	}

	/** Constructeur privé bloquant l'instanciation */
	private ValidationFichierUtils() {
		// rien à faire
	}
}
