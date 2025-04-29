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
package com.github.talbotgui.psl.socle.commun.application.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.commun.securite.RejetRequeteSansTokenJwtEndpoint;

/**
 * Configuration de la sécurité de l'application. Les APIs de monitoring ACTUATOR étant ouvertes, elles sont sécurisées
 */
@Configuration
// Pour l'authentification anonyme avec JWT
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	/** Le filtre de sécurité bloquant les requêtes ne contenant pas un token JWT valide */
	@Autowired
	private JwtSecuriteFilter jwtSecuriteFilter;

	/** Mot de passe que l'application ADMIN doit utiliser pour appeler les APIs Actuator exposées par l'application */
	@Value("${spring.boot.admin.client.instance.metadata.user.password}")
	private String motDePassePourActuator;

	/** Endpoint générant l'erreur 401 */
	@Autowired
	private RejetRequeteSansTokenJwtEndpoint rejetRequeteSansTokenJwt;

	/** URL de la documentation automatique SpringDoc */
	@Value("${springdoc.api-docs.path:/v3/api-docs}")
	private String urlDocumentationRest;

	/** Login que l'application ADMIN doit utiliser pour appeler les APIs Actuator exposées par l'application */
	@Value("${spring.boot.admin.client.instance.metadata.user.name}")
	private String utilisateurPourActuator;

	/** Définition du login/mdp pour la partie authentifiée de l'application */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(this.utilisateurPourActuator).password("{noop}" + this.motDePassePourActuator).roles("USER");
	}

	/**
	 * Configuration de base laissant les APIs libres d'accès mais protégeant les APIs Actuator avec un login/mdp configuré.
	 *
	 * @see https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html#use-new-requestmatchers
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				// Définition des accès
				.authorizeHttpRequests((authz) -> authz

						// Libre accès aux APIs de sécurité du socle
						.requestMatchers(JwtSecuriteFilter.URI_DE_BASE_SECURITE + "/**").permitAll()

						// Libre accès aux APIs de sécurité du socle
						.requestMatchers(JwtSecuriteFilter.URI_DE_BASE_SECURITE + "/**").permitAll()

						// Libre accès à l'API de connexion de l'AdminPsl
						.requestMatchers(JwtSecuriteFilter.URI_CONNEXION_ADMINPSL).permitAll()

						// Libre accès au Swagger en JSON
						.requestMatchers(this.urlDocumentationRest).permitAll()

						// Libre accès aux requêtes "preflight" des navigateurs avec le type OPTIONS
						.requestMatchers(HttpMethod.OPTIONS).permitAll()

						// Mais sécurisation du reste : les APIs du socle et les APIs Actuator
						.anyRequest().authenticated())

				// Authentification basique
				.httpBasic(Customizer.withDefaults())

				// Pour ignorer le CSRF
				.csrf(AbstractHttpConfigurer::disable)

				// Définition du enpoint qui génère les erreurs 401
				.exceptionHandling(config -> config.authenticationEntryPoint(this.rejetRequeteSansTokenJwt))

				// Pas de session HTTP dans les applications
				.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Ajout du filtre pour valider les token JWT
				.addFilterBefore(this.jwtSecuriteFilter, UsernamePasswordAuthenticationFilter.class);

		// renvoi du bean
		return http.build();
	}
}
