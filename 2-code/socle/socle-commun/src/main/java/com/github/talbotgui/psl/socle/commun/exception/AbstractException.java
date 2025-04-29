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
package com.github.talbotgui.psl.socle.commun.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import com.github.talbotgui.psl.socle.commun.utils.SleuthUtils;

/**
 * Classe de base de toutes les exceptions. Cette exception est RUNTIME pour réduire le volume de code. Ainsi plus de throws inutiles dans les
 * signatures de méthodes car, dans tous les cas, la gestion des erreurs est réalisée par le premier appelant (cas de l'appel à un client d'API) ou
 * par un gestionnaire d'erreur (cas des APIs REST exposées avec Spring).
 */
public abstract class AbstractException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Méthode permettant de vérifier l'ID d'une exception si celle-ci hérite de AbstractException.
	 *
	 * @param e  Exception à tester.
	 * @param id Identifiant attendu.
	 * @return TRUE si l'exception fournie hérite de AbstractException et que l'identifiant fourni correspond à celui de l'exception.
	 */
	public static boolean equals(final Exception e, final ExceptionId id) {
		if (!(e instanceof AbstractException)) {
			return false;
		}
		return ((AbstractException) e).getId().equals(id);
	}

	/**
	 * Intégration des paramètres dans le message là où sont les {}.
	 *
	 * @param messageAutiliser    Message à utiliser.
	 * @param parametresAutiliser Paramètre à intégrer.
	 * @return Messages avec les paramètres placés.
	 */
	public static String integrerParametresDansMessage(String messageAutiliser, Serializable[] parametresAutiliser) {
		if (messageAutiliser != null && parametresAutiliser != null) {
			for (Object parametre : parametresAutiliser) {
				// Récupération du paramètre sous forme de string
				String s = transformerValeurParametreEnString(parametre);
				// Traitement des caractères spéciaux comme un $ qui serait considéré comme un pattern invalide dans String.replaceFirst
				s = Matcher.quoteReplacement(s);
				// Replace de la valeur
				messageAutiliser = messageAutiliser.replaceFirst("\\{\\}", s);
			}
		}
		return messageAutiliser;
	}

	/**
	 * Transforme la valeur du parametre en string
	 *
	 * @param valeurParametre la valeur du paramètre à traiter
	 * @return la String
	 */
	private static String transformerValeurParametreEnString(Object valeurParametre) {
		String valeur = "null";
		if (valeurParametre != null) {
			if (valeurParametre.getClass().isArray()) {
				List<String> valeurs = new ArrayList<>();
				for (final Object p : (Object[]) valeurParametre) {
					valeurs.add(p.toString());
				}
				valeur = valeurs.toString();
			} else {
				valeur = valeurParametre.toString();
			}
		}
		return valeur;
	}

	/** Identifiant de l'exception. */
	private final ExceptionId id;

	/** Paramètres du message par défaut (utilisable dans un message personnalisé). */
	private final Serializable[] parametres;

	/** Référence unique permettant d'investiguer sur la source de l'exception ("TraceId-SpanId" de Sleuth). */
	private final String referencePourInvestigation;

	/**
	 * Constructeur
	 *
	 * @param id Identifiant de l'exception.
	 */
	protected AbstractException(final ExceptionId id) {
		super();
		this.id = id;
		this.parametres = null;
		this.referencePourInvestigation = SleuthUtils.calculerReferencePourInvestigation();
	}

	/**
	 * Constructor.
	 *
	 * @param id         Identifiant de l'exception.
	 * @param parametres Message parametres.
	 */
	protected AbstractException(final ExceptionId id, final Serializable... parametres) {
		this.id = id;
		this.parametres = parametres;
		this.referencePourInvestigation = SleuthUtils.calculerReferencePourInvestigation();
	}

	/**
	 * Constructor.
	 *
	 * @param id              Identifiant de l'exception.
	 * @param nestedException Exception source du problème.
	 */
	protected AbstractException(final ExceptionId id, final Throwable nestedException) {
		super(nestedException);
		this.id = id;
		this.parametres = null;
		this.referencePourInvestigation = SleuthUtils.calculerReferencePourInvestigation();
	}

	/**
	 * Constructor.
	 *
	 * @param id              Identifiant de l'exception.
	 * @param parametres      Message parametres.
	 * @param nestedException Exception source du problème.
	 */
	protected AbstractException(final ExceptionId id, final Throwable nestedException, final Serializable... parametres) {
		super(nestedException);
		this.id = id;
		this.parametres = parametres;
		this.referencePourInvestigation = SleuthUtils.calculerReferencePourInvestigation();
	}

	public ExceptionId getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String result;
		if (this.id == null) {
			result = super.getMessage();
		} else {
			result = integrerParametresDansMessage(this.id.getMessageParDefaut(), this.parametres);
		}
		return result;
	}

	/**
	 * GETTER pour objet immutable.
	 *
	 * @return Message parameters.
	 */
	public Serializable[] getParametres() {
		if (this.parametres != null) {
			return Arrays.copyOf(this.parametres, this.parametres.length);
		}
		return new Serializable[0];
	}

	public String getReferencePourInvestigation() {
		return this.referencePourInvestigation;
	}
}
