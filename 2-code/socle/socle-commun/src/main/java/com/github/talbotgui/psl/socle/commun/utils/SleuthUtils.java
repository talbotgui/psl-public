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

import org.slf4j.MDC;

/**
 * Classe utilitaire pour récupérer les données de Sleuth.
 */
public class SleuthUtils {

	/**
	 * Méthode définissant une valeur de referencePourInvestigation à partir des données de Sleuth ("TraceId-SpanId").
	 * 
	 * @return Une référence
	 */
	public static String calculerReferencePourInvestigation() {
		String traceId = MDC.get("traceId");
		String spanId = MDC.get("spanId");
		if (traceId != null && spanId != null) {
			return traceId + "-" + spanId;
		} else {
			return null;
		}
	}

	/** Constructeur bloquant l'instanciation. */
	private SleuthUtils() {
	}
}
