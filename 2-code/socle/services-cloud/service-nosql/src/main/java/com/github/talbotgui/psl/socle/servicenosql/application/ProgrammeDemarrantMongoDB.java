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
package com.github.talbotgui.psl.socle.servicenosql.application;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.beust.jcommander.JCommander;
import com.github.talbotgui.psl.socle.commundb.test.ProgrammeDemarrantMongoDBDansLesTests;

public class ProgrammeDemarrantMongoDB extends ProgrammeDemarrantMongoDBDansLesTests {

	/**
	 * Méthode de démarrage
	 */
	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
		// Parse des paramètres
		ParametresDemarrageDto parametres = new ParametresDemarrageDto();
		JCommander.newBuilder().addObject(parametres).build().parse(args);

		// Démarrage
		if (demarrerUneBaseDeDonneesMongoDB(parametres.getRepertoirePidEtLog(), parametres.getFichierConfiguration())) {
			// Attente
			attendreSignalArret(parametres.getRepertoirePidEtLog());
		}
	}
}
