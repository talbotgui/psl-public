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
package com.github.talbotgui.psl.socle.servicegateway.application.filtrageentete;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

class FiltrageEntetesGatewayFilterFactoryTest {

	@Test
	void test01FiltrageDesEntetes() {
		//
		ConfigurationFiltrageEntete config = new ConfigurationFiltrageEntete("a,b,c");
		FiltrageEntetesGatewayFilterFactory filtre = new FiltrageEntetesGatewayFilterFactory();
		HttpHeaders entetes = new HttpHeaders();
		entetes.add("a", "valeurA");
		entetes.add("b", "valeurB");
		entetes.add("y", "valeurY");
		entetes.add("z", "valeurZ1");
		entetes.add("z", "valeurZ2");
		ServerWebExchange swe = MockServerWebExchange.builder(MockServerHttpRequest.get("").headers(entetes)).build();
		//
		List<String> entetesAutorises = filtre.creerListeDesEntetesAutorisesEnMajuscules(config.getListeEntetesAutorises());
		ServerWebExchange resultat = filtre.supprimerLesEntetesNonAutorises(swe, entetesAutorises);
		//
		Assertions.assertEquals(2, resultat.getRequest().getHeaders().size());
		Assertions.assertEquals(1, resultat.getRequest().getHeaders().get("a").size());
		Assertions.assertEquals("valeurA", resultat.getRequest().getHeaders().get("a").get(0));
		Assertions.assertEquals(1, resultat.getRequest().getHeaders().get("b").size());
		Assertions.assertEquals("valeurB", resultat.getRequest().getHeaders().get("b").get(0));
	}
}
