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
package com.github.talbotgui.psl.socle.soumission.exception;

import java.io.Serializable;

import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId.NiveauException;

/**
 * Cette classe d'erreur définit des codes non compréhensible de l'usager mais utilisable en période de test (en ayant la liste des codes et
 * l'explication associée).
 */
public class SoumissionException extends com.github.talbotgui.psl.socle.commun.exception.AbstractException {

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Aucune donnée soumise */
	public static final String CODE_ERREUR_ADS = "ads";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Aucun tokenPSL fourni alors que le point d'entrée demande une authentification. */
	public static final String CODE_ERREUR_ATFAQLPEDUA = "atfaqlpedua";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Bouton de soumission non atteint. */
	public static final String CODE_ERREUR_BDSNA = null;

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Code de démarche absent */
	public static final String CODE_ERREUR_CDDA = "cdda";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Données nulles */
	public static final String CODE_ERREUR_DN = "dn";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Exception à l'évaluation de la condition '{}' avec le message : {}" */
	public static final String CODE_ERREUR_EALEDLC = "ealedlc";

	/**
	 * Code d'erreur non compréhensible renvoyé vers l'usager : Erreur de configuration : le type de génération '{}' paramétré pour le document '{}'
	 * n'existe pas
	 */
	public static final String CODE_ERREUR_EDCLTDGPPLDNP = "edcltdgppldnp";

	/** Code d'erreur non compréhensible renvoyé vers l'usager :Erreur d'evaluation Velocity pour le document {} */
	public static final String CODE_ERREUR_EDEVPLD = "edevpld";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Erreur de génération du document {} */
	public static final String CODE_ERREUR_EDGDD = "edgdd";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Echec de validation du contenu pour la clef */
	public static final String CODE_ERREUR_EDVDC = "edvdc";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Mauvais type retourné par l'évaluation de la condition '{}' */
	public static final String CODE_ERREUR_MTRPLEDLC = "mtrpledlc";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Présence de clefs en trop dans les données soumises */
	public static final String CODE_ERREUR_PDCETDLDS = "pdcetdlds";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Pas de document de TRANSFERT configuré. */
	public static final String CODE_ERREUR_PDT = "pdt";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Pas de point d'entrée trouvé */
	public static final String CODE_ERREUR_PPET = "ppet";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Type de contenu inconnu */
	public static final String CODE_ERREUR_TDCI = "tdci";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Type de validation inconnu */
	public static final String CODE_ERREUR_TDVI = "tdvi";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : TokenPSL et données soumises incohérentes */
	public static final String CODE_ERREUR_TEDSI = "tedsi";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : TokenPSL fourni invalide. */
	public static final String CODE_ERREUR_TI = "ti";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Trop d'occurences pour le bloc dynamique */
	public static final String CODE_ERREUR_TOPLBD = "toplbd";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Trop de points d'entrée trouvés */
	public static final String CODE_ERREUR_TPET = "tpet";

	/** Code d'erreur non compréhensible renvoyé vers l'usager : Version de configuration absente */
	public static final String CODE_ERREUR_VDCA = "vdca";

	/** Exception si les données du captcha sont invalides pour une soumission d'un usager non connecté */
	public static final ExceptionId ERREUR_CAPTCHA = new ExceptionId("ERREUR_DE_CAPTCHA", NiveauException.INFORMATION, 400,
			"Valeur du captcha invalide");

	/** Exception si un appel à une API interne échouesans plus de précision pour éviter de guider un éventuel fraudeur/hacker */
	public static final ExceptionId ERREUR_GENERIQUE = new ExceptionId("ERREUR_GENERIQUE", NiveauException.ERROR, 500,
			"Erreur d'exécution du serveur");

	/** Exception si les données ne sont pas conformes à l'attendu mais sans plus de précision pour éviter de guider un éventuel fraudeur/hacker */
	public static final ExceptionId ERREUR_REQUETE_AVEC_CODE = new ExceptionId("ERREUR_REQUETE_AVEC_CODE", NiveauException.ERROR, 400,
			"Les données soumises ne sont pas conformes à l'attendu ({})");

	/** Exception si les données ne sont pas conformes à l'attendu mais sans plus de précision pour éviter de guider un éventuel fraudeur/hacker */
	public static final ExceptionId ERREUR_SERVEUR_AVEC_CODE = new ExceptionId("ERREUR_SERVEUR_AVEC_CODE", NiveauException.ERROR, 500,
			"Le traitement des données soumises s'est mal terminé ({})");

	private static final long serialVersionUID = 1L;

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SoumissionException(ExceptionId id) {
		super(id);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SoumissionException(ExceptionId id, Serializable... parametres) {
		super(id, parametres);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SoumissionException(ExceptionId exceptionId, Throwable nestedException) {
		super(exceptionId, nestedException);
	}

	/** @see com.github.talbotgui.psl.socle.commun.exception.AbstractException */
	public SoumissionException(ExceptionId id, Throwable nestedException, Serializable... parametres) {
		super(id, nestedException, parametres);
	}
}