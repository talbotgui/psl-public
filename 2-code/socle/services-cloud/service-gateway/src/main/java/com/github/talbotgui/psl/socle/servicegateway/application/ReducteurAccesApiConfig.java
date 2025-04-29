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
package com.github.talbotgui.psl.socle.servicegateway.application;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

/**
 * Ce composant est associé aux configurations "RequestRateLimiter" et "redis-rate-limiter.*" présentes dans le fichier de configuration SpringBoot et
 * participe à la création d'une limite maximale d'appels aux APIs du socle.
 * 
 * <ul>
 * <li>redis-rate-limiter.replenishRate est le nombre de jetons autorisées par seconde</li>
 * <li>redis-rate-limiter.burstCapacity est le nombre de jetons autorisées par seconde en cas de pic (burst). Cette valeur peut être suppérieure à la
 * précédente mais, du coup, toutes les requêtes des secondes suivantes seront bloquées jusqu'à revenir à la normal (repla=5 + burst=10 => si 10
 * requêtes tombent en 1s, elles sont traitées mais aucune ne sera acceptée la seconde d'après)</li>
 * <li>redis-rate-limiter.requestedTokens est le nombre de jetons correspondant à chaque requête</li>
 * </ul>
 */
@Configuration
public class ReducteurAccesApiConfig {

	/**
	 * Ce composant KeyResolver identifie le client d'appel aux APIs. Par défaut, le PrincipalNameKeyResolver utiliserait le login de l'utilisateur
	 * connecté. Mais, dans notre cas, la limite d'appel s'applique à tout le monde et globalement car ce n'est qu'une mécanique de protection de
	 * l'infrastructure en amont et non une mécanique pour faire payer des abonnés à un service.
	 */
	@Bean
	public KeyResolver keyResolver() {
		return e -> Mono.just("1");
	}
}
