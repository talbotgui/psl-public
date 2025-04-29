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

/**
 * Cette classe regroupe les expressions régulières utilisables pour valider une chaîne de caractère et notamment celles en entrée des APIs du socle.
 */
public class RegexUtils {

	public static final String ALPHABETIQUE = "^[A-Za-z]*$";
	public static final String ALPHABETIQUE_ETENDUE = "^[A-Za-zéèà\\\\-_,;' ]*$";
	public static final String ALPHANUMERIQUE = "^[A-Za-z0-9]*$";
	public static final String ALPHANUMERIQUE_ETENDUE = "^[A-Za-z0-9éèà\\-_,;' ]*$";
	/** Tout le contenu du clavier. */
	public static final String ALPHANUMERIQUE_TRES_ETENDUE = "^[A-Za-z0-9&éê~\"#'{(\\[\\-|è`_\\\\ç^âà@)\\]=}£$%ù*µ,?\\.;:/!§<> \"\n]*$";
	// source : https://stackoverflow.com/questions/475074/regex-to-parse-or-validate-base64-data
	public static final String BASE64 = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";
	public static final String CHECKSUM = "^[A-Fa-f0-9]{32}$";
	public static final String CLEF_UNIQUE_TELECHARGEMENT = "^[A-Fa-f0-9]{8}(?:-[A-Fa-f0-9]{4}){4}[A-Fa-f0-9]{8}$";
	public static final String CODE_DEMARCHE = "^[a-z]*$";
	public static final String CODE_DOCUMENT = ALPHANUMERIQUE;
	public static final String CODE_INSEE = "^[0-9]*$";
	public static final String DONNEE_SOUMISE_CLEF = "^[a-zA-Z\\._0-9]*$";
	public static final String DONNEE_SOUMISE_VALEUR = ALPHANUMERIQUE_TRES_ETENDUE;
	public static final String EMAIL = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
	public static final String ID = "^[a-z0-9\\-]*$";
	public static final String ID_TRACE = "^[0-9]*$";
	public static final String JSON = ALPHANUMERIQUE_TRES_ETENDUE;
	public static final String LANGUE = "^[A-Z]{2}$";
	public static final String NOMBRE = "^[0-9]*$";
	public static final String NUMERO_TELEDOSSIER = "^[A-Z]{3}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$";
	public static final String OIDC_PARAM_CLEF = "^(client_id|client_secret|code|code_verifier|grant_type|redirect_uri|refresh_token)$";
	public static final String OIDC_PARAM_VALEUR = ALPHANUMERIQUE_ETENDUE;
	public static final String RECHERCHE = ALPHANUMERIQUE_ETENDUE;
	public static final String VERSION_CONFIGURATION = "^[0-9\\-\\.]*$";

	/** Constructeur privé d'une classe utilitaire. */
	private RegexUtils() {
		// Rien à faire
	}
}
