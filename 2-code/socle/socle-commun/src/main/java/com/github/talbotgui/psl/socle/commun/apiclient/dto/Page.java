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
package com.github.talbotgui.psl.socle.commun.apiclient.dto;

import java.security.InvalidParameterException;
import java.util.Collection;

public class Page<T> {

	private static final int PAR_DEFAULT_NUMERO_PAGE = 1;

	public static final int PAR_DEFAULT_TAILLE_PAGE = 50;

	public static <T> Page<T> defaultPage() {
		return new Page<>(PAR_DEFAULT_NUMERO_PAGE, PAR_DEFAULT_TAILLE_PAGE);
	}

	private long nombreTotalResultats;

	private final int numeroPage;

	private Collection<T> resultats;

	private final int taillePage;

	/**
	 * Constructeur à l'usage exclusif de Jackson
	 */
	public Page() {
		super();
		this.taillePage = PAR_DEFAULT_TAILLE_PAGE;
		this.numeroPage = PAR_DEFAULT_NUMERO_PAGE;
	}

	public Page(int numeroPage, int taillePage) {
		super();
		if (numeroPage < 0) {
			throw new InvalidParameterException("numeroPage doit être positif");
		}
		if (taillePage <= 0) {
			throw new InvalidParameterException("taillePage doit être strictement positif");
		}
		this.numeroPage = numeroPage;
		this.taillePage = taillePage;
	}

	/**
	 * Création de la réponse à partir de la requête en entrée (dont le type paramétré peut être différent).
	 *
	 * @param numeroPage           Index de la page.
	 * @param taillePage           Nombre d'éléments par page.
	 * @param nombreTotalResultats Nombre total de résultats disponibles.
	 * @param resultats            Resultats trouvés.
	 */
	public Page(int numeroPage, int taillePage, long nombreTotalResultats, Collection<T> resultats) {
		super();
		this.taillePage = taillePage;
		this.numeroPage = numeroPage;
		this.resultats = resultats;
		this.nombreTotalResultats = nombreTotalResultats;
	}

	public int calculerIndexPremierResultat() {
		return (this.getNumeroPage() - 1) * this.getTaillePage();
	}

	public long getNombreTotalResultats() {
		return this.nombreTotalResultats;
	}

	public int getNumeroPage() {
		return this.numeroPage;
	}

	public Collection<?> getResultats() {
		return this.resultats;
	}

	public int getTaillePage() {
		return this.taillePage;
	}
}
