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
package com.github.talbotgui.psl.socle.referentiel.service.indexeur;

import java.util.Collection;
import java.util.List;

import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.ITransformableEnDocumentLuceneDto;

public interface IndexeurService {

	public static final String NOM_CHAMP_LUCENE_CONTENU_JSON = "contenuJson";
	public static final String NOM_CHAMP_LUCENE_RECHERCHE = "champDeRecherche";
	public static final String NOM_CHAMP_LUCENE_RECHERCHE2 = "champDeRecherche2";
	public static final String NOM_CHAMP_LUCENE_TYPE_JAVA = "type";

	/**
	 * Création d'un index de données avec la liste fournie en paramètre.
	 * 
	 * @param objetsAindexer Objets à indexer.
	 */
	void initialiserIndex(Collection<? extends ITransformableEnDocumentLuceneDto> objetsAindexer);

	/**
	 * Exécution d'une recherche.
	 * 
	 * @param type                               Type de données à rechercher.
	 * @param recherche                          Chaîne à rechercher.
	 * @param limiteDeResultats                  Limite de résultats.
	 * @param rechercheSurSecondChampDeRecherche Défini si la recherche doit se faire sur le champ principal ou secondaire (nom du pays ou nationalité
	 *                                           par exemple)
	 * @return Les résultats.
	 */
	List<?> rechercher(Class<? extends ITransformableEnDocumentLuceneDto> type, String recherche, int limiteDeResultats,
			boolean rechercheSurSecondChampDeRecherche);

	/** Purge des indexes. */
	void viderLesIndexes();
}
