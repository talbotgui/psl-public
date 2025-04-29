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
package com.github.talbotgui.psl.socle.commun.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.feign.PslFeignException;

public class AbstractToutesExceptionsTest {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractToutesExceptionsTest.class);

	@Test
	void testPresenceLibellesEnToutesLanguesPourToutesLesException() throws IOException, IllegalArgumentException, IllegalAccessException {
		// Pour chaque racine de classpatch
		for (final Path root : ReflectionUtils.getAllClasspathRootDirectories()) {

			// Recherche des exceptions du projet
			// sauf PslFeignException qui n'est pas soumise à l'i18n
			// sauf AbstractException qui est abstraite
			List<Class<?>> listeClassesDexception = ReflectionSupport.findAllClassesInClasspathRoot(//
					root.toUri(), //
					t -> AbstractException.class.isAssignableFrom(t) && !AbstractException.class.equals(t) && !PslFeignException.class.equals(t), //
					name -> name.endsWith("Exception"));

			// Pour chaque classe,
			for (final Class<?> classeException : listeClassesDexception) {
				LOGGER.info("Vérification des libellés de la classe {}", classeException.getCanonicalName());

				// Charger fichier de properties associé à l'exception
				String nomFichierPourCetteClasse = "classpath:/" + classeException.getCanonicalName().replace('.', '/') + ".properties";
				Resource[] r = ResourcePatternUtils.getResourcePatternResolver(null).getResources(nomFichierPourCetteClasse);
				if ((r == null) || (r.length == 0)) {
					Assertions.fail("Pas de fichier '" + nomFichierPourCetteClasse + "' pour la classe '"
							+ classeException.getClass().getCanonicalName() + "'");
				}
				String contenuFichier = IOUtils.toString(r[0].getInputStream(), StandardCharsets.UTF_8);

				// Lister les langues présentes dans le fichier
				List<String> clefs = new ArrayList<>();
				Set<String> langues = Arrays.asList(contenuFichier.replace("\r", "").split("\n")).stream()//
						.filter(ligne -> ligne.contains("=") && !ligne.startsWith("#"))//
						.map(ligne -> {
							clefs.add(ligne.split("=")[0]);

							int debut = ligne.indexOf('.');
							int fin = ligne.indexOf("=", debut);
							if ((debut != -1) && (fin != -1) && (debut < fin)) {
								String langue = ligne.substring(debut + 1, fin);
								Assertions.assertTrue(langue.toUpperCase().equals(langue),
										"Un suffixe de langue est en minuscule dans la ligne " + ligne);
								return langue;
							} else {
								return null;
							}
						})//
						.collect(Collectors.toSet());
				LOGGER.info("Langues détectées : {}", langues);
				LOGGER.info("Clefs présentes : {}", clefs);

				// Recherche de doublon
				Assertions.assertEquals((new HashSet<>(clefs)).size(), clefs.size(),
						"Le fichier '" + nomFichierPourCetteClasse + "' contient au moins un doublon");

				// recherche des ExceptionId
				List<Field> exceptionIds = Arrays.asList(classeException.getFields()).stream()//
						.filter(membre -> ExceptionId.class.equals(membre.getType())) //
						.toList();

				// Pour chaque ExceptionId
				for (final Field exceptionId : exceptionIds) {
					String id = ((ExceptionId) exceptionId.get(null)).getId();

					// Vérification de la présence de la clef pour chaque langue
					for (final String langue : langues) {
						String suffixe = (langue == null) ? "" : "." + langue;
						LOGGER.info("Contrôle de la présence de la clef : {}{}", id, suffixe);
						Assertions.assertTrue(clefs.contains(id + suffixe),
								"Le fichier '" + nomFichierPourCetteClasse + "' ne contient pas la clef '" + id + suffixe);
					}
				}
			}
		}
	}
}
