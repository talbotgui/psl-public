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
package com.github.talbotgui.psl.socle.dbdocument.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commun.securite.JwtService;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;
import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.dao.DocumentDao;

import io.jsonwebtoken.Claims;
import io.micrometer.tracing.Tracer;

@Service
public class DocumentServiceImpl implements DocumentService {

	/** 10 secondes de validité de la clef de téléchargement. */
	private static final long DELAI_VALIDITE_CLEF_TELECHARGEMENT = 10000L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Autowired
	private DocumentDao dao;

	@Autowired
	private JwtService jwtService;

	@Autowired(required = false)
	private Tracer traceur;

	@Override
	public String genererEtEnregistrerClefUniqueDeTelechargement(String tokenJwt, String numeroTeledossier, String codeDocument) {

		// Génération de la clef unique valable 10 secondes
		String clefUnique = UUID.randomUUID().toString();
		String clefTemporaire = this.jwtService.genererClefTemporaireSurTokenJwt(tokenJwt, clefUnique, DELAI_VALIDITE_CLEF_TELECHARGEMENT,
				Map.of(AbstractMongoDao.ATTRIBUT_NUMERO_TELEDOSSIER, numeroTeledossier, AbstractMongoDao.ATTRIBUT_CODE_DOCUMENT, codeDocument));

		// Description de la mise à jour
		this.dao.sauvegarderClefDansMessageMetadonneesDocumentDto(numeroTeledossier, codeDocument, clefUnique,
				new Date().getTime() + DELAI_VALIDITE_CLEF_TELECHARGEMENT);

		// Renvoi de la clef générée
		return clefTemporaire;
	}

	@Override
	public List<MessageMetadonneesDocumentDto> rechercherDocumentsVisibleDuTeledossier(String numeroTeledossier) {
		return this.dao.rechercherDocumentsVisibleDuTeledossierSansContenuOuDonneesDeTelechargement(numeroTeledossier);
	}

	@Override
	public MessageSauvegardeDocumentDto rechercherDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument) {

		// Vérification des données d'authentification validables avant d'appeller la base de données
		// @see com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter#doFilterInternal
		if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication().getCredentials() == null) {
			LOGGER.warn("Tentative de téléchargement du document '{}' du télé-dossier '{}' sans authentification",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		Object cred = SecurityContextHolder.getContext().getAuthentication().getCredentials();
		if (!(cred instanceof String)) {
			LOGGER.warn("Tentative de téléchargement du document '{}' du télé-dossier '{}' sans paramètre 'clef'",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		Claims claims = this.jwtService.validerClefTemporaireSurTokenJwt((String) cred);
		if (claims == null) {
			LOGGER.error("Tentative de téléchargement du document '{}' du télé-dossier '{}' avec un paramètre 'clef' invalide",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		String valeurUnique = (String) claims.get(JwtService.CLEF_CLAIMS_VALEUR_UNIQUE);

		// Recherche du document
		MessageSauvegardeDocumentDto message = this.dao.rechercherDocumentVisibleDuTeledossier(numeroTeledossier, codeDocument);

		// Validations en fonction des données présentes dans les méta-données
		// @see com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter#doFilterInternal
		if (valeurUnique == null) {
			LOGGER.error("Tentative de téléchargement du document '{}' du télé-dossier '{}' sans clef dans la requête",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		if (message.getClefUniqueTelechargement() == null || message.getTempsLimiteDeValiditeDeLaClefDeTelechargement() == null) {
			LOGGER.error("Tentative de téléchargement du document '{}' du télé-dossier '{}' sans clef dans le document",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		if (new Date().getTime() > message.getTempsLimiteDeValiditeDeLaClefDeTelechargement()) {
			LOGGER.warn("Tentative de téléchargement du document '{}' du télé-dossier '{}' avec une clef expirée",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		if (!valeurUnique.equals(message.getClefUniqueTelechargement())) {
			LOGGER.error(
					"Tentative de téléchargement du document '{}' du télé-dossier '{}' avec une clef dans la requête ne correspondant pas à celle du document",
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDocument), LogUtils.nettoyerDonneesAvantDeLogguer(numeroTeledossier));
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}

		// Suppression de la clef
		this.dao.supprimerClefDansMessageMetadonneesDocumentDto(numeroTeledossier, codeDocument);

		// Renvoi du document
		return message;

	}

	@Override
	public String sauvegarderDocumentGenereDeTeledossier(MessageSauvegardeDocumentDto message) {
		message.setDateCreation(new Date());
		if (this.traceur.currentSpan() != null && this.traceur.currentSpan().context() != null) {
			message.setIdTrace(this.traceur.currentSpan().context().toString());
		}
		return this.dao.sauvegarderDocumentGenereDeTeledossier(message);
	}

	@Override
	public void supprimerDocumentsDuTeledossier(String numeroTeledossier) {
		this.dao.supprimerDocumentsDuTeledossierAvecLeDocumentDeTransfert(numeroTeledossier);
	}

}
