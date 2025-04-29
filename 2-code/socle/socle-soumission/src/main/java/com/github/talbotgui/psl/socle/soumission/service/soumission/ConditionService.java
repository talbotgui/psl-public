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
package com.github.talbotgui.psl.socle.soumission.service.soumission;

import java.util.Map;

import javax.script.ScriptContext;

public interface ConditionService {

	/**
	 * Calcul de la conditionCondition à calculer.
	 *
	 * @param condition       Condition à calculer.
	 * @param donnees         Données de contexte
	 * @param valeurParDefaut Valeur par défaut.
	 * @return TRUE si la condition est valide et respectée.
	 */
	boolean calculerCondition(String condition, Map<String, String> donnees, boolean valeurParDefaut);

	/**
	 * Calcul de la condition.
	 *
	 * @param condition       Condition à calculer.
	 * @param contexte        Contexte contenant les données précédemment traitées.
	 * @param valeurParDefaut Valeur par défaut.
	 * @return TRUE si la condition est valide et respectée.
	 */
	boolean calculerConditionAvecUnContexteEnrichiAuFurEtAMesure(String condition, ScriptContext contexte, boolean valeurParDefaut);

}