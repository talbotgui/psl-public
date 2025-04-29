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
package com.github.talbotgui.psl.socle.serviceredis.application;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/** Paramètres de démarrage de REDIS */
@Parameters(separators = "=")
public class ParametresDemarrageDto {
	/** Chemin de l'exécutable de REDIS par défaut. */
	private static final String CHEMIN_EXECUTABLE_PAR_DEFAUT = "redis-server.exe";

	/** Port de démarrage par défaut de REDIS. */
	private static final int PORT_PAR_DEFAUT = 6369;

	@Parameter(required = false, names = "--cheminExecutable", description = "Chemin de l'exécutable")
	private String cheminExecutable = CHEMIN_EXECUTABLE_PAR_DEFAUT;

	/** Paramètres utilisé pour tous les micro-services PSL même s'il n'a pas de sens ici car il ne peut y avoir qu'un seul service de bdd */
	@Parameter(required = false, names = "--indexInstance", description = "Chemin du répertoire des PID et logs")
	private Integer indexInstance;

	@Parameter(required = false, names = "--port", description = "Port d'écoute de REDIS")
	private Integer port = PORT_PAR_DEFAUT;

	@Parameter(required = false, names = "--repertoirePidEtLog", description = "Chemin du répertoire des PID et logs")
	private String repertoirePidEtLog;

	public ParametresDemarrageDto() {
		super();
	}

	public String getCheminExecutable() {
		return this.cheminExecutable;
	}

	public Integer getIndexInstance() {
		return this.indexInstance;
	}

	public Integer getPort() {
		return this.port;
	}

	public String getRepertoirePidEtLog() {
		return this.repertoirePidEtLog;
	}

	public void setCheminExecutable(String cheminExecutable) {
		this.cheminExecutable = cheminExecutable;
	}

	public void setIndexInstance(Integer indexInstance) {
		this.indexInstance = indexInstance;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setRepertoirePidEtLog(String repertoirePidEtLog) {
		this.repertoirePidEtLog = repertoirePidEtLog;
	}

	@Override
	public String toString() {
		return "ParametresDemarrageDto [repertoirePidEtLog=" + this.repertoirePidEtLog + "]";
	}
}
