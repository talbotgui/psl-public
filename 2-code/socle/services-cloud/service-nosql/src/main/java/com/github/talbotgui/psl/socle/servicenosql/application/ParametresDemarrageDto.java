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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/** Paramètres de démarrage de la bdd MongoDB */
@Parameters(separators = "=")
public class ParametresDemarrageDto {

	@Parameter(required = false, names = "--fichierConfiguration", description = "Chemin du fichier de configuration de MongoDB")
	private String fichierConfiguration;

	/** Paramètres utilisé pour tous les micro-services PSL même s'il n'a pas de sens ici car il ne peut y avoir qu'un seul service de bdd */
	@Parameter(required = false, names = "--indexInstance", description = "Chemin du répertoire des PID et logs")
	private Integer indexInstance;

	@Parameter(required = false, names = "--repertoirePidEtLog", description = "Chemin du répertoire des PID et logs")
	private String repertoirePidEtLog;

	public ParametresDemarrageDto() {
		super();
	}

	public String getFichierConfiguration() {
		return this.fichierConfiguration;
	}

	public Integer getIndexInstance() {
		return this.indexInstance;
	}

	public String getRepertoirePidEtLog() {
		return this.repertoirePidEtLog;
	}

	public void setFichierConfiguration(String fichierConfiguration) {
		this.fichierConfiguration = fichierConfiguration;
	}

	public void setIndexInstance(Integer indexInstance) {
		this.indexInstance = indexInstance;
	}

	public void setRepertoirePidEtLog(String repertoirePidEtLog) {
		this.repertoirePidEtLog = repertoirePidEtLog;
	}

	@Override
	public String toString() {
		return "ParametresDemarrageDto [repertoirePidEtLog=" + this.repertoirePidEtLog + "]";
	}
}
