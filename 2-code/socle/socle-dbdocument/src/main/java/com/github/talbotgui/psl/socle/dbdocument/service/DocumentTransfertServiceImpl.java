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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.dbdocument.apiclient.api.StatutDocumentTransfert;
import com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageSauvegardeDocumentDto;
import com.github.talbotgui.psl.socle.dbdocument.dao.DocumentDao;
import com.github.talbotgui.psl.socle.dbdocument.dao.PieceJointeDao;
import com.github.talbotgui.psl.socle.dbdocument.dao.TransfertDao;

@Service
public class DocumentTransfertServiceImpl implements DocumentTransfertService {

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private PieceJointeDao pjDao;

	@Autowired
	private TransfertDao transfertDao;

	@Override
	public void notifierEchecDuTraitementDunDocumentDeTransfert(String idDocumentTransfert) {
		this.transfertDao.modifierEtatDuTransfert(idDocumentTransfert, StatutDocumentTransfert.EN_ERREUR);
	}

	@Override
	public void purgerUnTeledossierApresTransfert(String numeroTeledossier) {
		this.documentDao.supprimerDocumentsDuTeledossierSansLeDocumentDeTransfert(numeroTeledossier);
		this.pjDao.supprimerPiecesJointesDuTeledossier(numeroTeledossier);
	}

	@Override
	public MessageSauvegardeDocumentDto rechercherDocumentOuPieceJointeDuTeledossier(String idDocument) {
		return this.transfertDao.rechercherDocumentOuPieceJointeDuTeledossier(idDocument);
	}

	@Override
	public String rechercherEtVerrouillerUnDocumentDeTransfertAtraiter() {
		return this.transfertDao.rechercherEtVerrouillerUnDocumentDeTransfertAtraiter();
	}

}
