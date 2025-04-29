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
package com.github.talbotgui.psl.socle.referentiel.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe de démarrage de l'application.
 *
 * Aucune configuration spécifique ne doit être dévelopée ici car il existe plusieurs classes de démarrage.
 */
@SpringBootApplication(scanBasePackages = { "com.github.talbotgui.psl.socle.referentiel", "com.github.talbotgui.psl.socle.commun" })
// Pour activer le scheduling (cf. classe CronManager)
@EnableScheduling
public class SocleReferentielApplication {

	/**
	 * Méthode de démarrage.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SocleReferentielApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		springApplication.run(args);
	}

}
