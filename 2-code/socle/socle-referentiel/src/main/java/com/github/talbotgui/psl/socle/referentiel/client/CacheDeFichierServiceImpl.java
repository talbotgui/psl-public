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
package com.github.talbotgui.psl.socle.referentiel.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.referentiel.exception.ReferentielException;

@Service
public class CacheDeFichierServiceImpl implements CacheDeFichierService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheDeFichierServiceImpl.class);

	/** Flag d'activation du cache. */
	@Value("${cache.activer:false}")
	private boolean activation;

	/** Chemin du répertoire de basedu cache. */
	@Value("${cache.chemin:../.cache/}")
	private String cheminRepertoireDuCache;

	/**
	 * Recherche du répertoire de base du cache.
	 */
	private Path calculerCheminRepertoireDuCache() {

		// Recherche du chemin par défaut
		Path chemin = Paths.get(this.cheminRepertoireDuCache);
		if (chemin.toFile().exists() && chemin.toFile().isDirectory()) {
			return chemin;
		}

		// Si besoin, recherche du niveau au dessus
		Path cheminParent = Paths.get("..", this.cheminRepertoireDuCache);
		if (cheminParent.toFile().exists() && cheminParent.toFile().isDirectory()) {
			return cheminParent;
		}

		// Sinon création du chemin par défaut
		if (chemin.toFile().mkdir()) {
			return chemin;
		}

		// Si ce n'est pas possible, erreur
		throw new ReferentielException(ReferentielException.ERREUR_CREATION_CACHE, chemin.toFile().getAbsolutePath());
	}

	@Override
	public Path definirNomFichierDunCache(String codeDuCache) {
		return this.calculerCheminRepertoireDuCache().resolve(codeDuCache);
	}

	@Override
	public byte[] obtenirContenuDuCacheSiActifEtDisponible(String codeDuCache) {

		// Si le cache n'est pas actif, on ne renvoie rien
		if (!this.activation) {
			return new byte[0];
		}

		// Calcul du chemin du fichier à partir du code
		Path cheminFichierCache = this.definirNomFichierDunCache(codeDuCache);

		// Si le fichier n'existe pas, on ne renvoie rien
		if (!cheminFichierCache.toFile().exists()) {
			return new byte[0];
		}

		// Si quelque chose existe mais n'est pas un fichier
		if (!cheminFichierCache.toFile().isFile()) {
			throw new ReferentielException(ReferentielException.ERREUR_LECTURE_CACHE, cheminFichierCache.toFile().getAbsolutePath());
		}

		// Renvoi du contenu du fichier
		try {
			LOGGER.info("  Lecture d'un cache dans le fichier '{}'", cheminFichierCache);
			return Files.readAllBytes(cheminFichierCache);
		} catch (IOException e) {
			throw new ReferentielException(ReferentielException.ERREUR_LECTURE_CACHE, e, cheminFichierCache.toFile().getAbsolutePath());
		}
	}

	@Override
	public void reinitialiserLesCaches(List<String> codes) {

		// Si le cache n'est pas actif, on ne renvoie rien
		if (!this.activation) {
			return;
		}

		// Calcul du chemin du fichier à partir du code
		Path repertoireCache = this.calculerCheminRepertoireDuCache();
		for (String code : codes) {
			File cheminFichierCache = repertoireCache.resolve(code).toFile();
			if (cheminFichierCache.exists() && !cheminFichierCache.delete()) {
				throw new ReferentielException(ReferentielException.ERREUR_SUPPRESSION_CACHE, cheminFichierCache.getAbsolutePath());
			}
		}
	}

	@Override
	public void sauvegarderContenuDuCache(String codeDuCache, byte[] contenu) {

		// Calcul du chemin du fichier à partir du code
		Path cheminFichierCache = this.definirNomFichierDunCache(codeDuCache);

		// Si le cache n'est pas actif, on ne fait que vérifier si le fichier existe pour le supprimer
		if (!this.activation) {
			File fichierCache = cheminFichierCache.toFile();
			if (fichierCache.exists() && !fichierCache.delete()) {
				LOGGER.error("Le cache n'est pas actif mais le fichier {} est présent et non supprimable", fichierCache.getAbsoluteFile());
				// Pas d'exception pour ne pas arrêter le traitement en cours
			}
			return;
		}

		// Si le fichier existe, on tente de le supprimer
		if (cheminFichierCache.toFile().exists() && !cheminFichierCache.toFile().delete()) {
			throw new ReferentielException(ReferentielException.ERREUR_CREATION_CACHE, cheminFichierCache.toFile().getAbsolutePath());
		}

		// Sauvegarde du contenu
		try {
			LOGGER.info("Sauvegarde d'un cache dans le fichier '{}'", cheminFichierCache);
			Files.write(cheminFichierCache, contenu, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new ReferentielException(ReferentielException.ERREUR_CREATION_CACHE, e, cheminFichierCache.toFile().getAbsolutePath());
		}
	}

	@Override
	public boolean verifierActivation() {
		return this.activation;
	}
}
