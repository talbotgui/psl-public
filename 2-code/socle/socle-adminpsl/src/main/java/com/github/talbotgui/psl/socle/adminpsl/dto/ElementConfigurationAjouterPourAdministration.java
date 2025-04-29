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
package com.github.talbotgui.psl.socle.adminpsl.dto;

import java.util.Date;

public interface ElementConfigurationAjouterPourAdministration {

	/**
	 * Création du libellé de la version.
	 *
	 * @return
	 */
	String creerLibelle();

	String getCommentaireCreation();

	String getCreateur();

	Date getDateCreation();

	String getId();

	void setCommentaireCreation(String commentaireCreation);

	void setCreateur(String createur);

	void setDateCreation(Date dateCreation);

	void setId(String id);

}