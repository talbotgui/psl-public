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
package com.github.talbotgui.psl.socle.dbnotification.client;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.MessageSauvegardeNotificationEmailDto;
import com.github.talbotgui.psl.socle.dbnotification.apiclient.exception.NotificationException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailClientImpl implements EmailClient {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailClientImpl.class);

	/** Flag d'activation du bouchon SP */
	@Value("${email.bouchonActif:false}")
	private boolean bouchonActif;

	/** Bean spring d'envoi de mail. */
	@Autowired
	private JavaMailSender composantEnvoiMail;

	/** Email utilisé comme emetteur. */
	@Value("${email.emetteur}")
	private String emailEmetteur;

	@Override
	public void envoyerEmail(MessageSauvegardeNotificationEmailDto demande) {

		// Si le bouchon est actif
		if (this.bouchonActif) {
			// Cette log ne contrevient pas à la règle sur les données personnelles dans un log autre que TRACE (le bouchon ne sera jamais actif sur un environnement de production)
			LOGGER.info("Bouchon actif : pas d'envoi du mail {}", demande.getObjet());
		}

		// Création du message multipart avec le composant Spring d'envoi de mail
		MimeMessage message;
		try {
			message = this.composantEnvoiMail.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			// Remplissage du DTO
			helper.setFrom(this.emailEmetteur);
			helper.setTo(demande.getDestinataires().toArray(new String[demande.getDestinataires().size()]));
			if (demande.getDestinatairesCopie() != null && !demande.getDestinatairesCopie().isEmpty()) {
				helper.setCc(demande.getDestinatairesCopie().toArray(new String[demande.getDestinatairesCopie().size()]));
			}
			if (demande.getDestinatairesCopieCachee() != null && !demande.getDestinatairesCopieCachee().isEmpty()) {
				helper.setBcc(demande.getDestinatairesCopieCachee().toArray(new String[demande.getDestinatairesCopieCachee().size()]));
			}
			helper.setSubject(demande.getObjet());
			helper.setText(demande.getContenu(), demande.getContenuHtml());

			// Ajout des éventuelles pièces jointes
			if (demande.getPiecesJointes() != null) {
				for (final Triple<String, byte[], String> pj : demande.getPiecesJointes()) {
					helper.addAttachment(pj.getLeft(), new ByteArrayDataSource(pj.getMiddle(), pj.getRight()));
				}
			}

			// Envoi du mail
			this.composantEnvoiMail.send(message);

		} catch (final MessagingException e) {
			throw new NotificationException(NotificationException.NOTIFICATION_NON_ENVOYEE, e);
		}
	}
}
