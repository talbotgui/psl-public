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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

public class ChecksumUtils {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChecksumUtils.class);

	/**
	 * Génération du MD5 à partir d'un contenu de fichier
	 *
	 * @param contenuDuFichier
	 * @return
	 */
	public static String creerChecksumMd5(byte[] contenuDuFichier) {
		return DigestUtils.md5DigestAsHex(contenuDuFichier).toUpperCase();
	}

	/**
	 * Génération du SHA1 d'un fichier.
	 *
	 * @param cheminDuCache Chemin du fichier
	 * @return
	 */
	public static String creerChecksumSha256(File cheminDuCache) {
		try (FileInputStream fis = new FileInputStream(cheminDuCache)) {
			return org.apache.commons.codec.digest.DigestUtils.sha256Hex(fis);
		} catch (IOException e) {
			LOGGER.error("Impossible de générer le SHA1 du fichier '{}'", cheminDuCache, e);
			return null;
		}
	}

	private ChecksumUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}
}
