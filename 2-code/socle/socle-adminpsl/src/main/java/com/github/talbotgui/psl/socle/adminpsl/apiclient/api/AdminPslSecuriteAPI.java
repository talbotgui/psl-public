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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import com.github.talbotgui.psl.socle.commun.utils.RegexUtils;

import jakarta.validation.constraints.Pattern;

public interface AdminPslSecuriteAPI {

	/** URI de connexion. */
	public static final String URI_SECURITE_CONNEXION = AdminPslAPI.URI_BASE + "/connexion";

	@PostMapping(value = AdminPslSecuriteAPI.URI_SECURITE_CONNEXION, produces = MediaType.APPLICATION_JSON_VALUE)
	String connexion(@Pattern(regexp = RegexUtils.EMAIL) @RequestPart(value = "nomUtilisateur", required = true) String nomUtilisateur,
			@Pattern(regexp = RegexUtils.ALPHANUMERIQUE_TRES_ETENDUE) @RequestPart(value = "motDePasse", required = true) String motDePasse);

}