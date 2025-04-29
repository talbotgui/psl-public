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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.talbotgui.psl.socle.commun.securite.exception.CommunException;

public class ChiffrementUtils {

	/** Type de clef générée. */
	private static final String BLOWFISH = "Blowfish";

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChiffrementUtils.class);

	/** Suffixe à la fin des chaînes pour les détectées */
	private static final String SUFFIX_DES_CHAINES_CHIFFREES = "$$0@";

	/**
	 * Chiffrement d'une chaine avec un secret spécifique.
	 *
	 * @param donneesAchiffrer Données à chiffrer.
	 * @param secretEnBase64   Clef à utiliser.
	 * @return
	 */
	public static String chiffrerChaineDeCaracteres(String donneesAchiffrer, String secretEnBase64) {
		try {
			byte[] secret = Base64.getDecoder().decode(secretEnBase64);

			// Chiffrement de la chaîne
			Key clef = new SecretKeySpec(secret, BLOWFISH);
			Cipher cipher = Cipher.getInstance(BLOWFISH);
			cipher.init(Cipher.ENCRYPT_MODE, clef);
			byte[] donneesChiffrees = cipher.doFinal(donneesAchiffrer.getBytes(StandardCharsets.UTF_8));

			// Ajout du suffixe
			donneesChiffrees = contatenerTableaux(donneesChiffrees, SUFFIX_DES_CHAINES_CHIFFREES.getBytes(StandardCharsets.UTF_8));

			// Encodage de la chaîne en base 64 et échapement des caractères spéciaux HTTP
			return Base64.getEncoder().encodeToString(donneesChiffrees);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.error("Erreur durant le chiffrement d'un token", e);
			// Aucune information spécifique ne doit remontée dans cette exception.
			throw new CommunException(CommunException.ERREUR_GENERIQUE);
		}
	}

	/**
	 * Merge les deux tableaux en arguments.
	 *
	 * @see https://www.devnotebook.fr/java
	 *
	 * @param premier Premier tableau
	 * @param second  Second tableau
	 * @return
	 */
	private static byte[] contatenerTableaux(byte[] premier, byte[] second) {
		byte[] resultat = Arrays.copyOf(premier, premier.length + second.length);
		System.arraycopy(second, 0, resultat, premier.length, second.length);
		return resultat;
	}

	/**
	 * Déchiffre la chaîne en argument en utilisant la clé fournie.
	 *
	 * @param donneesEnBase64 Chaîne chiffrée + le suffixe, en base 64
	 * @param secretEnBase64  Clé de déchiffrement
	 * @return la chaîne déchiffrée
	 */
	public static String dechiffrerChaineDeCaracteres(String donneesEnBase64, String secretEnBase64) {
		try {
			// Décodage de la chaîne en base 64
			byte[] donnees = Base64.getDecoder().decode(donneesEnBase64);
			byte[] secret = Base64.getDecoder().decode(secretEnBase64);

			// Suppression du suffixe
			donnees = retirerFinDuTableau(donnees, SUFFIX_DES_CHAINES_CHIFFREES.getBytes(StandardCharsets.UTF_8).length);

			// Déchiffrement de la chaîne
			Key clef = new SecretKeySpec(secret, BLOWFISH);
			Cipher cipher = Cipher.getInstance(BLOWFISH);
			cipher.init(Cipher.DECRYPT_MODE, clef);
			return new String(cipher.doFinal(donnees));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.error("Erreur durant le chiffrement d'un token", e);
			// Aucune information spécifique ne doit remontée dans cette exception.
			throw new CommunException(CommunException.ERREUR_GENERIQUE);
		}

	}

	/**
	 * Tronque le tableau de byte en arguments.
	 *
	 * @see https://www.devnotebook.fr/java
	 *
	 * @param tableau          Tableau à tronquer
	 * @param longueurAretirer Taille maximale du tableau à conserver
	 * @return le tableau tronqué
	 */
	private static byte[] retirerFinDuTableau(byte[] tableau, int longueurAretirer) {
		int nouvelleTaille = Math.min(tableau.length, tableau.length - longueurAretirer);
		byte[] nouveauTableau = new byte[nouvelleTaille];
		System.arraycopy(tableau, 0, nouveauTableau, 0, tableau.length - longueurAretirer);
		return nouveauTableau;
	}

	/**
	 * Vérification que la chaine passée en paramètre est une données chiffrée.
	 *
	 * @param donneesChiffreesEnBase64 La données à vérifier
	 * @return TRUE si c'est une donnée chiffrée.
	 */
	public static boolean verifierChaineEstChiffree(String donneesChiffreesEnBase64) {
		try {
			byte[] donneesChiffrees = Base64.getDecoder().decode(donneesChiffreesEnBase64.getBytes(StandardCharsets.UTF_8));
			byte[] suffixe = SUFFIX_DES_CHAINES_CHIFFREES.getBytes(StandardCharsets.UTF_8);
			int debut = donneesChiffrees.length - suffixe.length;
			return 0 == Arrays.compare(donneesChiffrees, debut, donneesChiffrees.length, suffixe, 0, suffixe.length);
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	private ChiffrementUtils() {
		// Constructeur bloquant les instanciations car cette classe est une classe utilitaire.
	}
}
