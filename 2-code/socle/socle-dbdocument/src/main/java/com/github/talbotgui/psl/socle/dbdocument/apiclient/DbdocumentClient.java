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
package com.github.talbotgui.psl.socle.dbdocument.apiclient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp;
import com.github.talbotgui.psl.socle.commun.securite.JwtSecuriteFilter;
import com.github.talbotgui.psl.socle.commun.utils.ChecksumUtils;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

import io.micrometer.tracing.Tracer;

/** Classe fournissant les méthodes d'appel aux APIs exposées par ce micro-service */
// Deux des interfaces ne sont pas implémentées car certaines signatures (manipulant des fichiers) n'ont pas de sens dans un client (MultipartFile ou ResponseEntity)
public class DbdocumentClient extends AbstractClientHttp /* implements , PieceJointeAPI, DocumentAPI */ {

	/** Parametre d'une URL pour le code de document. */
	private static final String PARAMETRE_CODE_DOCUMENT = "{codeDocument}";
	/** Parametre d'une URL pour le numéro de télédossier. */
	private static final String PARAMETRE_NUMERO_TELEDOSSIER = "{numeroTeledossier}";

	/** URL de base du micro-service */
	public static final String URI_DE_BASE = "/socle/document";
	/** URL spécifique à la demande de clef de téléchargement d'un document généré et visible d'un télé-dossier */
	public static final String URI_DOCUMENT_DISPONIBLE_DU_TELEDOSSIER = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ "teledossier/{numeroTeledossier}/document/{codeDocument}";
	/** URL spécifique à la recherche des documents générés et visibles d'un télé-dossier */
	public static final String URI_DOCUMENTS_DISPONIBLES_DU_TELEDOSSIER = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_PUBLIC
			+ "teledossier/{numeroTeledossier}/document";
	/** URL spécifique au téléchargement d'un document d'un télé-dossier */
	public static final String URI_INTERNE_DOCUMENT = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE + "teledossier/{idDocument}";
	/** URL spécifique à la purge/suppression ou modification d'un transfert de télé-dossier */
	public static final String URI_INTERNE_TRANSFERT_PRECIS = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE + "transfert/{idDocumentTransfert}";
	/** URL spécifique de recherche et modification d'un document de transfert */
	public static final String URI_INTERNE_TRANSFERT_RECHERCHE = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE + "transfert";
	/** URL spécifique à la purge des documents et pièces jointes d'un télédossier */
	public static final String URI_PURGE_TELEDOSSIER = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE + "teledossier/{numeroTeledossier}";
	/** URL spécifique à la sauvegarde d'un document généré de télé-dossier */
	public static final String URI_SAUVEGARDE_DOCUMENT_GENERE_DE_TELEDOSSIER = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE + "document";
	/** URL spécifique de création d'une pièce jointe */
	public static final String URI_SAUVEGARDE_PIECEJOINTE = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "piecejointe";
	/** URL spécifique de suppression ou modification d'une pièce jointe */
	public static final String URI_SUPPRESSION_OU_MAJ_PIECEJOINTE = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_PUBLIC + "piecejointe/{idPieceJointe}";
	/** URL spécifique de suppression des pièces jointes d'un télé-dossier */
	public static final String URI_SUPPRESSION_PIECEJOINTE_DU_TELEDOSSIER = DbdocumentClient.URI_DE_BASE + PREFIXE_URI_INTERNE
			+ "teledossier/{numeroTeledossier}/piecejointe";

	/** @see com.github.talbotgui.psl.socle.commun.apiclient.AbstractClientHttp */
	public DbdocumentClient(Tracer traceur, String urlDeBase, boolean desactiverSSL) {
		super(traceur, urlDeBase, desactiverSSL);
	}

	/**
	 * Association d'une pièce jointe à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 * @param idTeledossier ID du télé-dossier
	 */
	public void associerPieceJointeAunTeledossier(String idPieceJointe, String idTeledossier) {
		super.executerRequetePut(URI_SUPPRESSION_OU_MAJ_PIECEJOINTE.replace("{idPieceJointe}", idPieceJointe), idTeledossier,
				MediaType.TEXT_PLAIN_VALUE, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	// Cette méthode ne traite pas de données personnelles ou sensibles
	@Override
	public boolean clientDapiTraitantDesDonneesSensiblesOuPersonnelles() {
		return false;
	}

	/**
	 * Génération d'une clef unique de téléchargement de document.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @param codeDocument      Code de document.
	 * @return Clef générée.
	 */
	public String demanderClefDeTelechargement(String numeroTeledossier, String codeDocument) {
		return super.executerRequetePost(URI_DOCUMENT_DISPONIBLE_DU_TELEDOSSIER.replace(PARAMETRE_NUMERO_TELEDOSSIER, numeroTeledossier)
				.replace(PARAMETRE_CODE_DOCUMENT, codeDocument), new TypeReference<String>() {
				}, null, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Suppression des pièces jointes d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 */
	public void desassocierPiecesJointesDuTeledossier(String numeroTeledossier) {
		super.executerRequeteDelete(URI_SUPPRESSION_PIECEJOINTE_DU_TELEDOSSIER.replace(PARAMETRE_NUMERO_TELEDOSSIER, numeroTeledossier),
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Rechercher les documents disponibles à l'usager d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 * @return Documents trouvés
	 */
	public List<MessageMetadonneesDocumentDto> rechercherDocumentsVisibleDunTeledossier(String numeroTeledossier) {
		return super.executerRequeteGet(URI_DOCUMENTS_DISPONIBLES_DU_TELEDOSSIER.replace(PARAMETRE_NUMERO_TELEDOSSIER, numeroTeledossier),
				new TypeReference<List<MessageMetadonneesDocumentDto>>() {
				}, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Sauvegarde d'un document généré pour un télé-dossier.
	 *
	 * @param codeDemarche                                    Code de la démarche.
	 * @param numeroTeledossier                               Numéro du télé-dossier.
	 * @param codeDocument                                    Code du document (code unique présent dans la configuration).
	 * @param libelleDocument                                 Libellé du document à afficher à l'usager.
	 * @param nomDocument                                     Nom final du document.
	 * @param documentPresenteAuTelechargementEnFinDeDemarche Flag indiquant si le document est à présenter à l'usager.
	 * @param ordrePresentationDuDocument                     Ordre de présentation du document.
	 * @param contentType                                     Type de contenu (MimeType)
	 * @param contenu                                         Contenu du document.
	 */
	public String sauvegarderDocumentGenereDeTeledossier(String codeDemarche, String numeroTeledossier, String codeDocument, String libelleDocument,
			String nomDocument, boolean documentPresenteAuTelechargementEnFinDeDemarche, Integer ordrePresentationDuDocument, String contentType,
			byte[] contenu) {
		String md5 = ChecksumUtils.creerChecksumMd5(contenu);
		String contenuBase64 = Base64.getEncoder().encodeToString(contenu);
		MessageSauvegardeDocumentDto message = new MessageSauvegardeDocumentDto(codeDemarche, numeroTeledossier, codeDocument, libelleDocument,
				nomDocument, documentPresenteAuTelechargementEnFinDeDemarche, ordrePresentationDuDocument, contentType, contenu.length, contenuBase64,
				md5);
		return super.executerRequetePost(URI_SAUVEGARDE_DOCUMENT_GENERE_DE_TELEDOSSIER, new TypeReference<String>() {
		}, message, null, Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Sauvegarde d'une pièce jointe pour une démarche
	 *
	 * @param codeDemarche    Code de la démarche.
	 * @param codePieceJointe Code de la pièce jointe
	 * @param path            Chemin du fichier
	 * @return ID du document généré
	 */
	public String sauvegarderPieceJointe(String codeDemarche, String codePieceJointe, Path path) {
		String contentDisposition = "form-data; name=\"fichier\"; filename=\"" + path.getFileName() + "\"";

		Map<String, Object> corps = new HashMap<>();
		corps.put("codeDemarche", codeDemarche);
		corps.put("codePieceJointe", codePieceJointe);
		corps.put("fichier", path);

		return super.executerRequetePost(URI_SAUVEGARDE_PIECEJOINTE, new TypeReference<String>() {
		}, corps, MediaType.MULTIPART_FORM_DATA_VALUE,
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt, HttpHeaders.CONTENT_DISPOSITION, contentDisposition));
	}

	/**
	 * Suppression des documents générés d'un télé-dossier.
	 *
	 * @param numeroTeledossier Numéro du télé-dossier.
	 */

	public void supprimerDocumentsDunTeledossier(String numeroTeledossier) {
		super.executerRequeteDelete(URI_DOCUMENTS_DISPONIBLES_DU_TELEDOSSIER.replace(PARAMETRE_NUMERO_TELEDOSSIER, numeroTeledossier),
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Supression d'une pièce jointe tant qu'elle n'est pas associée à un télé-dossier soumis.
	 *
	 * @param idPieceJointe ID de la pièce jointe
	 */
	public void supprimerPieceJointeNonSoumise(String idPieceJointe) {
		super.executerRequeteDelete(URI_SUPPRESSION_OU_MAJ_PIECEJOINTE.replace("{idPieceJointe}", idPieceJointe),
				Arrays.asList(HttpHeaders.AUTHORIZATION, this.tokenJwt));
	}

	/**
	 * Téléchargement du document.
	 *
	 * @param numeroTeledossier          Numéro du télé-dossier.
	 * @param codeDocument               Code de document.
	 * @param clefUniqueDeTelechargement Clef unique de téléchargement
	 * @return Document demandé.
	 */
	public String telechargerDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument, String clefUniqueDeTelechargement) {
		String debutUrl = URI_DOCUMENT_DISPONIBLE_DU_TELEDOSSIER.replace(PARAMETRE_NUMERO_TELEDOSSIER, numeroTeledossier)
				.replace(PARAMETRE_CODE_DOCUMENT, codeDocument);

		// La clef unique de téléchargement est une donnée chiffrée et encodé en base64.
		// Or cet encodage peut générer des + et des / dans la valeur.
		// Et les + sont interprétés comme des espaces dans une URL
		if (clefUniqueDeTelechargement != null && clefUniqueDeTelechargement.contains("+")) {
			clefUniqueDeTelechargement = URLEncoder.encode(clefUniqueDeTelechargement, StandardCharsets.UTF_8);
		}

		// Appel
		return super.executerRequeteGet(debutUrl + "?" + JwtSecuriteFilter.PARAMETRE_CLEF + "=" + clefUniqueDeTelechargement, null, null,
				Arrays.asList());
	}
}
