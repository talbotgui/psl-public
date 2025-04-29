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
package com.github.talbotgui.psl.socle.referentiel.apiclient.dto;

public record InformationSiretDto(String siret, String nom, String codePostal, String codeInsee) {

	/**
	 * Comparaison entre deux DTO pour vérifier qu'il porte sur la même commune : vérification du codeInsee et (du codePostal exacte OU (du début du
	 * code postal et du nom)).
	 * 
	 * @param c La commune à tester.
	 * @return TRUE si ça correspond.
	 */
	public boolean calculerCorrespondanceAvecUneCommune(CommuneActuelleDto c) {

		// Si les codes INSEE sont différents, c'est perdu
		if (this.codeInsee == null || !this.codeInsee.equals(c.getCodeInsee())) {
			return false;
		}

		// Si les codes postaux sont identiques, c'est gagné
		if (c.getCodesPostaux() != null && c.getCodesPostaux().contains(this.codePostal())) {
			return true;
		}

		// Si les codes INSEE sont identiques mais pas les codes postaux (on est arrivé jusqu'ici)

		// Protection de la suite du code
		if (this.codePostal == null || c.getCodesPostaux() == null || c.getCodesPostaux().isEmpty() || this.nom == null || c.getLibelle() == null) {
			return false;
		}

		// on teste le code des départements et le nom
		String cCodeDepartement = c.getCodesPostaux().get(0).substring(0, 2);
		String codeDepartement = this.codePostal().substring(0, 2);
		return cCodeDepartement.equals(codeDepartement) && this.normaliserNom(this.nom).equals(this.normaliserNom(c.getLibelle()));
	}

	/** Méthode retirant tous les caractères spéciaux (notamment les accents). */
	private String normaliserNom(String nom) {
		return nom.toUpperCase().replaceAll("[ÈÉÊË]", "E").replaceAll("[ÛÙ]", "U").replaceAll("[ÏÎ]", "I").replaceAll("[ÀÂÃ]", "A")
				.replaceAll("[ÔÕ]", "O").replaceAll("[Ç]", "C");
	}
}
