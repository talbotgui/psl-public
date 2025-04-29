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
package com.github.talbotgui.psl.socle.serviceldap.application;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/** Paramètres de démarrage du LDAP */
@Parameters(separators = "=")
public class ParametresDemarrageDto {

	@Parameter(required = true, names = "--adminMdp", description = "Mot de passe d'administration", password = true)
	private String adminMdp;

	@Parameter(required = true, names = "--adminUtilisateur", description = "Utilisateur d'administration")
	private String adminUtilisateur;

	@Parameter(required = true, names = "--baseDn", description = "BaseDN du LDAP")
	private String baseDn;

	@Parameter(required = true, names = "--cheminLdif", description = "Chemin du LDIF chargé au démarrage")
	private String cheminLdif;

	/** Paramètres utilisé pour tous les micro-services PSL même s'il n'a pas de sens ici car il ne peut y avoir qu'un seul service de bdd */
	@Parameter(required = false, names = "--indexInstance", description = "Chemin du répertoire des PID et logs")
	private Integer indexInstance;

	@Parameter(required = true, names = "--ldapPort", description = "Port du LDAP")
	private Integer ldapPort;

	@Parameter(required = false, names = "--repertoirePidEtLog", description = "Chemin du répertoire des PID et logs")
	private String repertoirePidEtLog;

	public ParametresDemarrageDto() {
		super();
	}

	public String getAdminMdp() {
		return this.adminMdp;
	}

	public String getAdminUtilisateur() {
		return this.adminUtilisateur;
	}

	public String getBaseDn() {
		return this.baseDn;
	}

	public String getCheminLdif() {
		return this.cheminLdif;
	}

	public Integer getIndexInstance() {
		return this.indexInstance;
	}

	public Integer getLdapPort() {
		return this.ldapPort;
	}

	public String getRepertoirePidEtLog() {
		return this.repertoirePidEtLog;
	}

	public void setAdminMdp(String adminMdp) {
		this.adminMdp = adminMdp;
	}

	public void setAdminUtilisateur(String adminUtilisateur) {
		this.adminUtilisateur = adminUtilisateur;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public void setCheminLdif(String cheminLdif) {
		this.cheminLdif = cheminLdif;
	}

	public void setIndexInstance(Integer indexInstance) {
		this.indexInstance = indexInstance;
	}

	public void setLdapPort(Integer ldapPort) {
		this.ldapPort = ldapPort;
	}

	public void setRepertoirePidEtLog(String repertoirePidEtLog) {
		this.repertoirePidEtLog = repertoirePidEtLog;
	}

	@Override
	public String toString() {
		return "ParametresDemarrageDto [ldapPort=" + this.ldapPort + ", baseDn=" + this.baseDn + ", adminMdp=***"
				+ ", adminUtilisateur=***" + ", cheminLdif=" + this.cheminLdif + ", repertoirePidEtLog=" + this.repertoirePidEtLog + "]";
	}

}
