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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.talbotgui.psl.socle.commun.utils.ChecksumUtils;
import com.github.talbotgui.psl.socle.commun.utils.LogUtils;
import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.api.ConfigurationAPI;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.BlocDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.ContenuDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PageDto;
import com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.configurationpubliquedemarche.PieceJointeAssocieeDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe;
import com.github.talbotgui.psl.socle.dbdocument.clientantivirus.ClamavClient;
import com.github.talbotgui.psl.socle.dbdocument.dao.PieceJointeDao;
import com.github.talbotgui.psl.socle.dbdocument.exception.DocumentException;

@Service
public class PieceJointeServiceImpl implements PieceJointeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PieceJointeServiceImpl.class);

	/** Nombre d'octets dans 1Mo */
	private static final long TAILLE_UN_MO = 1024L * 1024L;

	private static final String TYPE_DOCUMENT_UPLOAD_DOCUMENT = "uploadDocument";

	@Value("${antivirus.actif:true}")
	private boolean antivirusActif;

	/** Instance de client de l'antivirus. */
	@Autowired
	private ClamavClient clientAntivirus;

	/** Client d'appel de l'API des configurations */
	@Autowired
	private ConfigurationAPI clientConfigurationAPI;

	/** DAO MongoDB pour les pièces jointes. */
	@Autowired
	private PieceJointeDao dao;

	@Value("${antivirus.indisponibiliteNonBloquante:false}")
	private boolean indisponibiliteAntivirusNonBloquante;

	/** Instance de TIKA pour détecter le type d'un document */
	private Tika tika = new Tika();

	@Override
	public void associerPieceJointeAunTeledossier(String idPieceJointe, String numeroTeledossier) {
		this.dao.associerPieceJointeAunTeledossier(idPieceJointe, numeroTeledossier);
	}

	/**
	 * Méthode contrôlant la pièce jointe.
	 *
	 * @param codeDemarche    Le code de la démarche (obligatoire).
	 * @param codePieceJointe le code de la pièce jointe associée à la démarche (obligatoire).
	 * @param fichier         la pièce jointe uploadée.
	 */
	private void controlerLaPieceJointe(String codeDemarche, String codePieceJointe, MultipartFile fichier, byte[] contenuFichier) {

		// Contrôle de la cohérence de la taille affichée et de la taille réelle
		if (fichier.getSize() != contenuFichier.length) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 1);
		}

		// Chargement de la configuration
		ConfigurationPubliqueDemarcheDto configurationPublique = this.clientConfigurationAPI
				.rechercherDerniereConfigurationPubliqueDeDemarche(codeDemarche);

		// Si aucune pj autorisée, erreur
		if (configurationPublique == null || configurationPublique.getPiecesJointesAssociees() == null
				|| configurationPublique.getPiecesJointesAssociees().isEmpty()) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 2);
		}

		// Si aucune pj autorisée avec ce code, erreur
		PieceJointeAssocieeDto pjAssociee = configurationPublique.getPiecesJointesAssociees().stream()//
				.filter(pj -> codePieceJointe.equals(pj.codePieceJointe()))//
				.findFirst()//
				.orElseThrow(() -> {
					this.genererLogSiIncoherencePjEtContenu(configurationPublique);
					return new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 3);
				});

		// contrôle du "type déclaré" de document et de la taille max
		if (fichier.getSize() > pjAssociee.tailleMaximaleAutorisee() * TAILLE_UN_MO
				|| !pjAssociee.typesDeContenuAutorises().contains(fichier.getContentType())) {
			LOGGER.error("Fichier refusé : {}/{} et {}/{}", fichier.getSize(), pjAssociee.tailleMaximaleAutorisee(),
					pjAssociee.typesDeContenuAutorises(), fichier.getContentType());
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 4);
		}

		// contrôle du type réel de document
		String typeReelDuFichier = this.tika.detect(contenuFichier);
		if (!pjAssociee.typesDeContenuAutorises().contains(typeReelDuFichier)) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 6);
		}

		// contrôle antivirus
		if (this.antivirusActif) {
			try {
				if (!this.clientAntivirus.analyserFichier(contenuFichier)) {
					// Si l'analyse remonte que le fichier est un virus, on renvoi le code 7
					throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_NON_AUTORISEE, 7);
				} else {
					LOGGER.trace("Analyse virale ok pour le document '{}' de la démarche '{}'", codePieceJointe, codeDemarche);
				}
			} catch (DocumentException e) {
				// En cas d'erreur technique (comme une indisponibilité), l'erreur est remontée ou logguée en fonction de la clef de configuration
				// "antivirus.indisponibiliteNonBloquante"
				if (!this.indisponibiliteAntivirusNonBloquante) {
					throw e;
				} else {
					LOGGER.error("Erreur durant l'analyse antivirale d'une pièce jointe", e);
				}
			}
		} else {
			LOGGER.warn("Analyse virale désactivée (document '{}' de la démarche '{}')", LogUtils.nettoyerDonneesAvantDeLogguer(codePieceJointe),
					LogUtils.nettoyerDonneesAvantDeLogguer(codeDemarche));
		}
	}

	@Override
	public void desassocierPiecesJointesDuTeledossier(String numeroTeledossier) {
		this.dao.desassocierPiecesJointesDuTeledossier(numeroTeledossier);
	}

	/**
	 * Méthode appelée si une PJ au code inconnu est uploadée.
	 *
	 * Mais, cela peut venir d'une incohérence de la configuration publique qui décrit une PJ avec un code précis et un contenu de type
	 * "uploadDocument" dont la clef ne correspond pas au code.
	 *
	 * @param configurationPublique La configuration publique.
	 */
	private void genererLogSiIncoherencePjEtContenu(ConfigurationPubliqueDemarcheDto configurationPublique) {

		// Création de la liste de tous les codes de contenu
		List<String> codeDeContenus = new ArrayList<>();
		List<String> clefDeContenusUpload = new ArrayList<>();
		for (PageDto page : configurationPublique.getPages()) {
			for (BlocDto bloc : page.blocs()) {
				for (ContenuDto contenu : bloc.obtenirTousLesContenusDuBlocEtDeSesSousBlocs()) {
					codeDeContenus.add(contenu.getClef());
					if (TYPE_DOCUMENT_UPLOAD_DOCUMENT.equals(contenu.getType())) {
						clefDeContenusUpload.add(contenu.getClef());
					}
				}
			}
		}

		// Pour chaque PJ déclarée, vérification qu'un code de contenu correspond
		List<String> codeDePJ = new ArrayList<>();
		for (PieceJointeAssocieeDto pj : configurationPublique.getPiecesJointesAssociees()) {
			codeDePJ.add(pj.codePieceJointe());
			if (!codeDeContenus.contains(pj.codePieceJointe())) {
				LOGGER.warn(
						"Erreur de configuration de la démarche '{}' car il n'existe pas de contenu de type '{}' cohérent avec la pièce jointe '{}'",
						configurationPublique.getCodeDemarche(), TYPE_DOCUMENT_UPLOAD_DOCUMENT, pj.codePieceJointe());
			}
		}

		// Pour chaque contenu de type uploadDocument, vérification qu'une pièce jointe existe
		for (String clef : clefDeContenusUpload) {
			if (!codeDePJ.contains(clef)) {
				LOGGER.warn(
						"Erreur de configuration de la démarche '{}' car il n'existe pas de pièce jointe cohérente avec le contenu  de type '{}' et de clef '{}'",
						configurationPublique.getCodeDemarche(), TYPE_DOCUMENT_UPLOAD_DOCUMENT, clef);
			}
		}
	}

	@Override
	public String sauvegarderPieceJointe(String codeDemarche, String codePieceJointe, MultipartFile fichier) {

		// Lecture du contenu du fichier
		byte[] contenuFichier;
		try {
			contenuFichier = fichier.getBytes();
		} catch (IOException e) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_DOCUMENT, e);
		}

		// Contrôles vis-à-vis de la configuration publique de la démarche
		this.controlerLaPieceJointe(codeDemarche, codePieceJointe, fichier, contenuFichier);

		// Recherche d'une éventuelle PJ existante
		List<PieceJointe> pjsExistantes = this.dao.rechercherPiecesJointes(codeDemarche, codePieceJointe);

		// Calcul du MD5 du contenu
		String md5 = ChecksumUtils.creerChecksumMd5(contenuFichier);

		// Création du DTO complet à sauvegarder
		PieceJointe pieceJointe = new PieceJointe(codeDemarche, codePieceJointe, fichier.getOriginalFilename(), fichier.getSize(),
				fichier.getContentType(), contenuFichier, md5);

		// Sauvegarde de la nouvelle PJ
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Sauvegarde de la pièce jointe {}", LogUtils.nettoyerDonneesAvantDeLogguer(pieceJointe.toString()));
		}
		String idPjGeneree = this.dao.sauvegarderPieceJointe(pieceJointe);

		// Si une ancienne PJ existe,
		if (pjsExistantes != null && !pjsExistantes.isEmpty()) {
			try {
				// on les supprime
				for (PieceJointe pjExistante : pjsExistantes) {
					this.dao.supprimerPieceJointeNonSoumise(pjExistante.getId());
				}
			}
			// Mais, en cas d'erreur de la suppression de la PJ précédente, on supprime la nouvelle PJ enregistrée plus haut
			// pour n'en garder qu'une unique
			catch (Exception e) {
				// Pas de "nested exception" pour ne pas donner trop de détails à un éventuel attaquant
				throw new DocumentException(DocumentException.SAUVEGARDE_IMPOSSIBLE);
			}
		}

		// Renvoi de l'ID de la nouvelle PJ
		return idPjGeneree;
	}

	@Override
	public void supprimerPieceJointeNonSoumise(String idPieceJointe) {
		this.dao.supprimerPieceJointeNonSoumise(idPieceJointe);
	}
}
