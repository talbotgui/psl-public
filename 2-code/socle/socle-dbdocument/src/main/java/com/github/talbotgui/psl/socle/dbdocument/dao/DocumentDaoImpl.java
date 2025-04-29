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
package com.github.talbotgui.psl.socle.dbdocument.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.commundb.dao.AbstractMongoDao;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.DocumentTransfertAPI;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;

@Service
public class DocumentDaoImpl extends AbstractMongoDao implements DocumentDao {

	@Autowired
	public DocumentDaoImpl(@Value("${spring.data.mongodb.database}") String baseDeDonneeMongoDB,
			@Value("${spring.data.mongodb.host}") String hoteMongoDB, @Value("${spring.data.mongodb.port}") int portMongoDB) {
		super(baseDeDonneeMongoDB, hoteMongoDB, portMongoDB);
	}

	private Query creerRequeteRechercheDunDocumentDeTeledossier(String numeroTeledossier, String codeDocument) {
		Criteria criteres = Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_DOCUMENT).is(codeDocument));
		Query requete = Query.query(criteres);
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_ORDRE_PRESENTATION));
		requete.fields().exclude("id");
		return requete;
	}

	@Override
	public List<MessageMetadonneesDocumentDto> rechercherDocumentsVisibleDuTeledossierSansContenuOuDonneesDeTelechargement(String numeroTeledossier) {
		Criteria criteres = Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier)//
				.andOperator(Criteria.where(ATTRIBUT_DOCUMENT_PRESENTE_A_LUSAGER).is(true));
		Query requete = Query.query(criteres);
		requete.with(Sort.by(Direction.ASC, ATTRIBUT_ORDRE_PRESENTATION));
		requete.fields().exclude("id").exclude("contenuDocument").exclude("checksumMd5").exclude("clefUniqueTelechargement")
				.exclude("tempsLimiteDeValiditeDeLaClefDeTelechargement");
		return super.rechercherListe(requete, MessageMetadonneesDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);
	}

	@Override
	public MessageSauvegardeDocumentDto rechercherDocumentVisibleDuTeledossier(String numeroTeledossier, String codeDocument) {
		Query requete = this.creerRequeteRechercheDunDocumentDeTeledossier(numeroTeledossier, codeDocument);
		return super.rechercherUn(requete, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT);
	}

	@Override
	public void sauvegarderClefDansMessageMetadonneesDocumentDto(String numeroTeledossier, String codeDocument, String clef, Long temps) {
		Update update = new Update();
		update.set(ATTRIBUT_CLEF_TELECHARGEMENT, clef);
		update.set(ATTRIBUT_TEMPS_LIMITE_CLEF_TELECHARGEMENT, temps);

		Query requete = this.creerRequeteRechercheDunDocumentDeTeledossier(numeroTeledossier, codeDocument);
		super.modifierPremierElementTrouve(requete, MessageMetadonneesDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT, update);
	}

	@Override
	public String sauvegarderDocumentGenereDeTeledossier(MessageSauvegardeDocumentDto message) {
		return super.inserer(message, COLLECTION_MONGODB_POUR_DOCUMENT).getId();
	}

	@Override
	public void supprimerClefDansMessageMetadonneesDocumentDto(String numeroTeledossier, String codeDocument) {
		Update update = new Update();
		update.unset(ATTRIBUT_CLEF_TELECHARGEMENT);
		update.unset(ATTRIBUT_TEMPS_LIMITE_CLEF_TELECHARGEMENT);

		Query requete = this.creerRequeteRechercheDunDocumentDeTeledossier(numeroTeledossier, codeDocument);
		super.modifierPremierElementTrouve(requete, MessageSauvegardeDocumentDto.class, COLLECTION_MONGODB_POUR_DOCUMENT, update);
	}

	@Override
	public void supprimerDocumentsDuTeledossierAvecLeDocumentDeTransfert(String numeroTeledossier) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier));
		super.supprimerParRequete(requete, COLLECTION_MONGODB_POUR_DOCUMENT);
	}

	@Override
	public void supprimerDocumentsDuTeledossierSansLeDocumentDeTransfert(String numeroTeledossier) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_NUMERO_TELEDOSSIER).is(numeroTeledossier)//
				.andOperator(Criteria.where(ATTRIBUT_CODE_DOCUMENT).ne(DocumentTransfertAPI.CODE_DOCUMENT_TRANSFERT)));
		super.supprimerParRequete(requete, COLLECTION_MONGODB_POUR_DOCUMENT);
	}

}
