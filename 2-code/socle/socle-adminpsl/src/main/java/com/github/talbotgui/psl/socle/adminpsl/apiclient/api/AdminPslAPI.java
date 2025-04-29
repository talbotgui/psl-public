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
package com.github.talbotgui.psl.socle.adminpsl.apiclient.api;

import org.springframework.cloud.openfeign.FeignClient;

import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;

/** Interface commune au contrôleur REST et à l'interface FeignClient. */
//"name" est le nom de l'application déclarée dans Eureka et "contextId" est un identifiant unique (le nom de l'interface) pour permettre d'avoir plusieurs @FeignClient dans le même projet
@FeignClient(name = "socle-adminpsl", contextId = "AdminPslAPI")
public interface AdminPslAPI
		extends AdminPslSecuriteAPI, AdminPslConfigPubliqueAPI, AdminPslConfigInterneAPI, AdminPslTransfertAPI, AdminPslListeDemarchesAPI {

	/** URL de base du micro-service */
	public static final String URI_BASE = JwtSecuriteFilter.URI_BASE_ADMINPSL;
}