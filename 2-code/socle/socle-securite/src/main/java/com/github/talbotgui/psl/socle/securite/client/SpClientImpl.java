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
package com.github.talbotgui.psl.socle.securite.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpAdresseDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpCompteDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpTelephoneDto;
import com.github.talbotgui.psl.socle.securite.client.dto.InformationSpUsagerDto;
import com.github.talbotgui.psl.socle.securite.client.dto.TypeDeCompteSP;

import io.micrometer.tracing.Tracer;

@Component
public class SpClientImpl extends AbstractClientHttp implements SpClient {

	/** Données du bouchon. */
	private static final InformationSpCompteDto DONNEES_BOUCHON_CHARGER_DONNEES_COMPTE = new InformationSpCompteDto("etabage-0159@yopmail.com",
			"uuid-xxxx-yyyy-zzzz", TypeDeCompteSP.particulier, true);

	/** Données du bouchon. */
	private static final InformationSpUsagerDto DONNEES_BOUCHON_CHARGER_DONNEES_PERSONNELLES = new InformationSpUsagerDto("nom", "prenom1 prenom2", 1,
			new Date(),
			new InformationSpAdresseDto("voie", "lieuDit", "batiment", "appartement", "Amiens", "France", "80000", "80021", "complément", "Somme"),
			"M", "80021", "12345", "Amiens", "Somme", DONNEES_BOUCHON_CHARGER_DONNEES_COMPTE.getEmail(), "NOM", "France", "Célibataire",
			new InformationSpTelephoneDto("0123456789", "+33"), new InformationSpTelephoneDto("0623456789", "+33"));

	/** Flag d'activation du bouchon SP */
	private final boolean bouchonActif;

	/** URL de l'API des informations du compte connecté. */
	private final String urlInfoCompte;

	/** URL de l'API des informations de l'usager. */
	private final String urlInfoUsager;

	public SpClientImpl(Tracer traceur, //
			@Value("${oidc.urls.infousager}") String urlInfoUsager, @Value("${oidc.urls.infocompte}") String urlInfoCompte,
			@Value("${sp.desactiverSSL:false}") boolean desactiverSSL, @Value("${oidc.proxy.hoteDuProxy:#{null}}") String hoteDuProxy,
			@Value("${oidc.proxy.portDuProxy:0}") int portDuProxy, @Value("${oidc.proxy.nomUtilisateur:#{null}}") String nomUtilisateur,
			@Value("${oidc.proxy.motDePasse:#{null}}") String motDePasse, @Value("${oidc.bouchon:#{false}}") boolean bouchonActif) {
		super(traceur, "", desactiverSSL, hoteDuProxy, portDuProxy, nomUtilisateur, motDePasse);
		this.urlInfoUsager = urlInfoUsager;
		this.urlInfoCompte = urlInfoCompte;
		this.bouchonActif = bouchonActif;
	}

	@Override
	public InformationSpCompteDto chargerDonneesCompte(String clientId, String clientSecret, String accessTokenOIDC) {
		// log
		this.logger.info("Chargement des données techniques du compte SP");

		// Si le bouchon est actif
		if (this.bouchonActif) {
			this.logger.warn("Attention, le bouchon SP est actif !");
			return DONNEES_BOUCHON_CHARGER_DONNEES_COMPTE;
		}

		// Création de la liste des entêtes
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + accessTokenOIDC);

		// Exécution de la requête
		return super.executerRequeteGet(this.urlInfoCompte, new TypeReference<InformationSpCompteDto>() {
		}, null, entetes);
	}

	@Override
	public InformationSpUsagerDto chargerDonneesPersonnelles(String clientId, String clientSecret, String accessTokenOIDC) {
		// log
		this.logger.info("Chargement des données personnelles du compte SP");

		// Si le bouchon est actif
		if (this.bouchonActif) {
			this.logger.warn("Attention, le bouchon SP est actif !");
			return DONNEES_BOUCHON_CHARGER_DONNEES_PERSONNELLES;
		}

		// Création de la liste des entêtes
		List<String> entetes = Arrays.asList(HttpHeaders.AUTHORIZATION, JwtSecuriteFilter.BEARER + accessTokenOIDC);

		// Exécution de la requête
		return super.executerRequeteGet(this.urlInfoUsager, new TypeReference<InformationSpUsagerDto>() {
		}, null, entetes);
	}

	// Cette méthode traite bien de données personnelles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

}
