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
package com.github.talbotgui.psl.socle.dbbrouillon.apiclient;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.api.BrouillonAPI;
import com.github.talbotgui.psl.socle.dbbrouillon.apiclient.dto.BrouillonDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
public class DbbrouillonClient extends AbstractClientHttp implements BrouillonAPI {

	/** URL spécifique de récupération de l'authentification d'un brouillon */
	public static final String URI_AUTHENTIFICATION_BROUILLON = DbbrouillonClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ "brouillon/{codeDemarche}/{idBrouillon}/authentification";

	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/brouillon";

	/** URL spécifique de téléchargement d'un brouillon */
	public static final String URI_LECTURE_BROUILLON = DbbrouillonClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "brouillon/{codeDemarche}/{idBrouillon}";

	/** URL spécifique à la lecture de la configuration */
	public static final String URI_SAUVEGARDE_BROUILLON = DbbrouillonClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "brouillon";

	/** URL spécifique de suppression d'un brouillon */
	public static final String URI_SUPPRESSION_BROUILLON = URI_LECTURE_BROUILLON;

	/** @see AbstractClientHttp */
	public DbbrouillonClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	// Cette méthode traite bien de données personnelles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return true;
	}

	/**
	 * Obtenir l'authentification nécessaire pour un brouillon donné (son identifiant).
	 *
	 * @param codeDemarche Code de la démarche.
	 * @param idBrouillon  Identifiant du brouillon
	 * @return Le type d'authentification nécessaire
	 */
	@Override
	public String obtenirAuthentificationDunBrouillon(String codeDemarche, String idBrouillon) {
		return super.executerRequeteGet(URI_AUTHENTIFICATION_BROUILLON.replace("{codeDemarche}", codeDemarche).replace("{idBrouillon}", idBrouillon),
				new TypeReference<String>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Sauvegarde d'un brouillon.
	 *
	 * @param brouillon Données du brouillon.
	 */
	@Override
	public String sauvegarderBrouillon(BrouillonDto brouillon) {
		return super.executerRequetePost(URI_SAUVEGARDE_BROUILLON, new TypeReference<String>() {
		}, brouillon, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	@Override
	public void supprimerBrouillon(String codeDemarche, String idBrouillon) {
		super.executerRequeteDelete(URI_SUPPRESSION_BROUILLON.replace("{codeDemarche}", codeDemarche).replace("{idBrouillon}", idBrouillon),
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Lecture d'un brouillon.
	 *
	 * @param codeDemarche Code de la démarche.
	 * @param idBrouillon  Identifiant du brouillon
	 * @return Brouillon trouvé
	 */
	@Override
	public BrouillonDto telechargerBrouillon(String codeDemarche, String idBrouillon) {
		return super.executerRequeteGet(URI_LECTURE_BROUILLON.replace("{codeDemarche}", codeDemarche).replace("{idBrouillon}", idBrouillon),
				new TypeReference<BrouillonDto>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

}
