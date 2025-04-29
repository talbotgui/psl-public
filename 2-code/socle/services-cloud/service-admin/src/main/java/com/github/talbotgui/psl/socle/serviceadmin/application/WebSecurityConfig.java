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
package com.github.talbotgui.psl.socle.serviceadmin.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

/**
 * Configuration de la sécurité du serveur AdminServer
 */
@Configuration
public class WebSecurityConfig {

	/** Role du LDAP nécessaire à la connexion. */
	private static final String ROLE_ADMIN_SUPERVISION = "ROLE_ADMIN_SUPERVISION";

	/** URL du logout (répétée plusieurs fois) */
	private static final String URL_LOGOUT = "/logout";

	/** Configuration de l'application */
	private final AdminServerProperties adminServer;

	@Value("${ldap.baseDn:dc=psl,dc=talbotgui,dc=github,dc=com}")
	private String baseDn;

	@Value("${ldap.urls:ldap://localhost:1389}")
	private String ldapUrl;

	/**
	 * Constructeur avec injection des configurations
	 *
	 * @param adminServer Configuration de l'application
	 */
	public WebSecurityConfig(AdminServerProperties adminServer) {
		this.adminServer = adminServer;
	}

	/**
	 * Authentification en tentant de se connecter au LDAP avec les paramètres
	 * soumis par l'utilisateur.
	 *
	 * Le DN doit être au format dn: "uid=LE_LOGIN_SAISI,ou=people," suivi du baseDN
	 *
	 * @see https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/ldap.html#servlet-authentication-ldap-bind
	 */
	@Bean
	public AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource,
			LdapAuthoritiesPopulator authorities) {
		LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
		factory.setUserDnPatterns("uid={0},ou=people," + this.baseDn);
		factory.setUserSearchFilter(this.baseDn);
		factory.setLdapAuthoritiesPopulator(authorities);
		return factory.createAuthenticationManager();
	}

	/**
	 * Composant de chargement des autorisations/groupes de chaque utilisateur.
	 *
	 * Les groupes doivent être des 'groupOfNames' (@see
	 * https://www.vincentliefooghe.net/content/ldap-les-types-groupes) et être
	 * déclarés dans "ou=groups,"+baseDn.
	 *
	 * Chaque "member" doit être le DN complet.
	 */
	@Bean
	public LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
		String groupSearchBase = "ou=groups," + this.baseDn;
		DefaultLdapAuthoritiesPopulator authorities = new DefaultLdapAuthoritiesPopulator(contextSource,
				groupSearchBase);
		// le {0} est le DN de l'utilisateur
		authorities.setGroupSearchFilter("member= {0}");
		return authorities;
	}

	/** Connexion au LDAP */
	@Bean
	public ContextSource contextSource() {
		return new DefaultSpringSecurityContextSource(this.ldapUrl);
	}

	/**
	 * Configuration de base.
	 *
	 * @see https://codecentric.github.io/spring-boot-admin/current/#_securing_spring_boot_admin_server
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// Si l'usager réussi à se connecter, redirection vers /
		SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		loginSuccessHandler.setTargetUrlParameter("redirectTo");
		loginSuccessHandler.setDefaultTargetUrl(this.adminServer.path("/"));

		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests//
				// Accès aux requêtes OPTIONS
				.requestMatchers(HttpMethod.OPTIONS, "/*").permitAll()//
				// Accès aux ressources et aux pages login et logout
				.requestMatchers(this.adminServer.path("/assets/**")).permitAll()//
				.requestMatchers(this.adminServer.path("/login")).permitAll()//
				.requestMatchers(this.adminServer.path(URL_LOGOUT)).permitAll()//
				// Le reste est limité aux utilisateurs connectés avec le rôle
				// ROLE_ADMIN_SUPERVISION
				.requestMatchers(this.adminServer.path("/*")).hasAuthority(ROLE_ADMIN_SUPERVISION)//
				.anyRequest().authenticated())//

				// Définition des pages de login et logout
				// @see
				// https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html#jc-logout-success-handler
				.formLogin(formLogin -> formLogin.loginPage(this.adminServer.path("/login"))
						.successHandler(loginSuccessHandler))
				.logout(logout -> logout.logoutUrl(this.adminServer.path(URL_LOGOUT)))

				.httpBasic(Customizer.withDefaults())

				// CSRF désactivé sur plusieurs URL
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.ignoringRequestMatchers(
								// Autorisation pour les requêtes OPTIONS
								new AntPathRequestMatcher(this.adminServer.path("/*"), HttpMethod.OPTIONS.toString()),
								// Autorisation pour la déconnexion
								new AntPathRequestMatcher(this.adminServer.path(URL_LOGOUT),
										HttpMethod.POST.toString()),
								// Autorisation pour modifier les niveaux de log depuis l'IHM
								new AntPathRequestMatcher(this.adminServer.path("/instances"),
										HttpMethod.POST.toString()),
								// Autorisation pour raffraîchir le contexte depuis l'IHM
								new AntPathRequestMatcher(this.adminServer.path("/instances/**"),
										HttpMethod.POST.toString()),
								// Autorisation pour exécuter un shutdown depuis l'IHM
								new AntPathRequestMatcher(this.adminServer.path("/applications/**"),
										HttpMethod.POST.toString()),
								// Autorisation pour retirer l'application depuis l'IHM
								new AntPathRequestMatcher(this.adminServer.path("/applications/**"),
										HttpMethod.DELETE.toString())));

		return http.build();
	}
}
