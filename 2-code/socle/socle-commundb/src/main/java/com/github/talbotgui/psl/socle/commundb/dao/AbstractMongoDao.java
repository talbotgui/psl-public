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
package com.github.talbotgui.psl.socle.commundb.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.util.StringUtils;

import com.github.talbotgui.psl.socle.commundb.exception.MongodbException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.UpdateResult;

public abstract class AbstractMongoDao {

	// Warning Javadoc normal, la classe est dans le projet socle-dbdocument
	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#numeroTeledossier */
	public static final String ATTRIBUT_CLEF_TELECHARGEMENT = "clefUniqueTelechargement";

	// Warning Javadoc normal, la classe est dans le projet socle-dbconfiguration
	/** @see ConfigurationInterneDemarcheDto.codeDemarche, ConfigurationPubliqueDemarcheDto.codeDemarche et PieceJointe.codeDemarche */
	public static final String ATTRIBUT_CODE_DEMARCHE = "codeDemarche";

	// Warning Javadoc normal, la classe est dans le projet socle-dbdocument
	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#codeDocument */
	public static final String ATTRIBUT_CODE_DOCUMENT = "codeDocument";

	// Warning Javadoc normal, la classe est dans le projet socle-dbdocument
	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.PieceJointe.codePieceJointe */
	public static final String ATTRIBUT_CODE_PIECE_JOINTE = "codePieceJointe";

	// Warning Javadoc normal, la classe est dans le projet socle-adminpsl
	/** @see com.github.talbotgui.psl.socle.adminpsl.dto.AdminConfigurationPubliqueDemarcheDto#commentaireCreation */
	public static final String ATTRIBUT_COMMENTAIRE_CREATION = "commentaireCreation";

	// Warning Javadoc normal, la classe est dans le projet socle-adminps
	/** @see com.github.talbotgui.psl.socle.adminpsl.dto.AdminConfigurationPubliqueDemarcheDto#createur */
	public static final String ATTRIBUT_CREATEUR = "createur";

	/**
	 * @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#dateCreation
	 * @see com.github.talbotgui.psl.socle.adminpsl.dto.AdminConfigurationPubliqueDemarcheDto#dateCreation
	 */
	public static final String ATTRIBUT_DATE_CREATION = "dateCreation";

	/** Cet attribut est lié à un attribut JAVA */
	public static final String ATTRIBUT_DATECREATION_POUR_DOCUMENT_NOTIFICATION = "dateCreation";

	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#documentPresenteAuTelechargementEnFinDeDemarche */
	public static final String ATTRIBUT_DOCUMENT_PRESENTE_A_LUSAGER = "documentPresenteAuTelechargementEnFinDeDemarche";

	/** Attribut généré par MongoDB */
	public static final String ATTRIBUT_ID = "_id";

	/** Cet attribut est lié à com.github.talbotgui.psl.socle.dbnotification.apiclient.dto.statistiques.LigneStatistiqueComptageParStatut#nombre */
	public static final String ATTRIBUT_NOMBRE = "nombre";

	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#numeroTeledossier */
	public static final String ATTRIBUT_NUMERO_TELEDOSSIER = "numeroTeledossier";

	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#ordrePresentation */
	public static final String ATTRIBUT_ORDRE_PRESENTATION = "ordrePresentation";

	/** Cet attribut n'est pas lié à un attribut JAVA */
	public static final String ATTRIBUT_STATUT_POUR_DOCUMENT_NOTIFICATION = "statut";

	/** Cet attribut n'est pas lié à un attribut JAVA */
	public static final String ATTRIBUT_STATUT_POUR_DOCUMENT_TRANSFERT = "statut";

	/** @see com.github.talbotgui.psl.socle.dbdocument.apiclient.dto.MessageMetadonneesDocumentDto#numeroTeledossier */
	public static final String ATTRIBUT_TEMPS_LIMITE_CLEF_TELECHARGEMENT = "tempsLimiteDeValiditeDeLaClefDeTelechargement";

	/**
	 * @see com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationInterneDemarcheDto#versionConfiguration
	 * @see com.github.talbotgui.psl.socle.dbconfiguration.apiclient.dto.ConfigurationPubliqueDemarcheDto#versionConfiguration
	 */
	public static final String ATTRIBUT_VERSION_CONFIGURATION = "versionConfiguration";

	/** Collection MongoDB contenant les brouillons. */
	public static final String COLLECTION_MONGODB_POUR_BROUILLON = "Brouillon";

	/** Collection MongoDB contenant les configurations internes. */
	public static final String COLLECTION_MONGODB_POUR_CONFIGURATION_INTERNE = "ConfigurationInterne";

	/** Collection MongoDB contenant les configurations publiques. */
	public static final String COLLECTION_MONGODB_POUR_CONFIGURATION_PUBLIQUE = "Configuration";

	/** Collection MongoDB contenant les documents. */
	public static final String COLLECTION_MONGODB_POUR_DOCUMENT = "Document";

	/** Collection MongoDB contenant les notifications SP. */
	public static final String COLLECTION_MONGODB_POUR_NOTIFICATION = "Notification";

	/** Collection MongoDB contenant les pièces jointes. */
	public static final String COLLECTION_MONGODB_POUR_PIECE_JOINTE = "PieceJointe";

	/** @see https://docs.mongodb.com/manual/core/index-unique/ */
	public static final Bson INDEX_COLLECTION_DOCUMENT = BasicDBObject
			.parse("{" + ATTRIBUT_NUMERO_TELEDOSSIER + ":1," + ATTRIBUT_CODE_DOCUMENT + ":1}");

	/** @see https://docs.mongodb.com/manual/core/index-unique/ */
	public static final Bson INDEX_COLLECTIONS_CONFIGURATION = BasicDBObject
			.parse("{" + ATTRIBUT_CODE_DEMARCHE + ":1," + ATTRIBUT_VERSION_CONFIGURATION + ":1}");

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoDao.class);

	/** Connexion MongoDB */
	private MongoTemplate mongoTemplate;

	/**
	 * Création du mongoTemplate au démarrage de l'application
	 */
	@Autowired
	protected AbstractMongoDao(String baseDeDonneeMongoDB, String hoteMongoDB, int portMongoDB) {
		String chaineConnexion = "mongodb://" + hoteMongoDB + ":" + portMongoDB;
		if (StringUtils.hasLength(hoteMongoDB) && StringUtils.hasLength(baseDeDonneeMongoDB)) {
			this.mongoTemplate = new MongoTemplate(MongoClients.create(chaineConnexion), baseDeDonneeMongoDB);
		}
	}

	/**
	 * Méthode spécialisée pour le comptage de documents depuis une requête
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#count
	 */
	protected <T> Long compterElementsParRequete(Query requete, Class<T> classeEntite, String nomCollection) {
		return this.mongoTemplate.count(requete, classeEntite, nomCollection);
	}

	/**
	 * Méthode spécialisée pour la gestion des erreurs
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#insert
	 */
	protected <T> T inserer(T objet, String nomCollection) {
		try {
			return this.mongoTemplate.insert(objet, nomCollection);
		} catch (DuplicateKeyException e) {
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_EN_DOUBLE, e);
		} catch (Exception e) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_DOCUMENT, e);
		}
	}

	/**
	 * Renvoi l'ID d'un document qu'il soit un attribut de type String ou ObjectId.
	 *
	 * @param d document à traiter.
	 * @return ID du document
	 */
	public String lireId(Document d) {
		Object id = d.get(ATTRIBUT_ID);
		if (id == null) {
			return null;
		} else if (id instanceof ObjectId o) {
			return o.toString();
		} else {
			return (String) id;
		}
	}

	/**
	 * Méthode de recherche des valeurs distincte d'un attribut unique.
	 */
	protected <T> Set<T> listerDistinct(String nomAttribut, Class<T> classeEntite, String nomCollection) {
		return this.mongoTemplate
				// Récupération de la collection
				.getCollection(nomCollection)
				// liste distincte d'un attribut
				.distinct(nomAttribut, classeEntite)
				// Transformée en liste
				.into(new HashSet<>());
	}

	/**
	 * Méthode spécialisée pour la gestion des erreurs
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#findAndReplace
	 */
	protected <T> T mettreAjour(String id, T objet, String nomCollection) {
		try {
			Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(id));
			return this.mongoTemplate.findAndReplace(requete, objet, nomCollection);
		} catch (Exception e) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_DOCUMENT, e);
		}
	}

	/**
	 * Méthode spécialisée pour la gestion des erreurs
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#findAndModify
	 */
	protected <T> T mettreAjourUnAttribut(String id, Update modifications, Class<T> classe, String nomCollection) {
		try {
			Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(id));
			return this.mongoTemplate.findAndModify(requete, modifications, classe, nomCollection);
		} catch (Exception e) {
			throw new MongodbException(MongodbException.ERREUR_SAUVEGARDE_DOCUMENT, e);
		}
	}

	/**
	 * Méthode spécialisée pour la mise à jour d'attributs particuliers.
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#updateFirst
	 */
	protected <T> void modifierPremierElementTrouve(Query requeteRecherche, Class<T> classe, String nomCollection, Update modifications) {
		UpdateResult resultat = this.mongoTemplate.updateFirst(requeteRecherche, modifications, classe, nomCollection);
		if (1 != resultat.getMatchedCount() || 1 != resultat.getModifiedCount()) {
			// Log utile (en plus du log d'erreur) car requête présente
			LOGGER.warn("Aucun document correspondant à mettre à jour avec la requête {}", requeteRecherche);
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
	}

	/**
	 * Méthode spécialisée pour la mise à jour d'attributs particuliers.
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#updateMulti
	 */
	protected <T> void modifierTousLesElementsTrouves(Query requeteRecherche, Class<T> classe, String nomCollection, Update modifications) {
		this.mongoTemplate.updateMulti(requeteRecherche, modifications, classe, nomCollection);
	}

	/**
	 * Comptage de données.
	 *
	 * @param <T>                    Type de retour.
	 * @param agregation             Agrégation des données.
	 * @param filtresSurLesDocuments Filtres sur les documents.
	 * @param classeEntite
	 * @return
	 */
	protected <T> List<T> rechercherAvecAgregation(GroupOperation agregation, MatchOperation filtresSurLesDocuments, Class<T> classeEntite,
			String collection) {
		// Création de l'agrégation
		Aggregation aggregation;
		if (filtresSurLesDocuments != null) {
			aggregation = Aggregation.newAggregation(filtresSurLesDocuments, agregation);
		} else {
			aggregation = Aggregation.newAggregation(agregation);
		}

		// Exécution de la requête
		AggregationResults<T> resultat = this.mongoTemplate.aggregate(aggregation, collection, classeEntite);

		// Renvoi des résultats
		return resultat.getMappedResults();
	}

	/**
	 * Méthode spécialisée pour la recherche d'un document
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#find
	 */
	protected <T> List<T> rechercherListe(Query requete, Class<T> classeEntite, String nomCollection) {
		return this.mongoTemplate.find(requete, classeEntite, nomCollection);
	}

	/**
	 * Méthode de recherche du premier document correspondant aux critères, renvoi du dit document (cf. options) et modification.
	 *
	 * @param criteresFiltre Critères de recherche du document
	 * @param modifications  Modification à réaliser sur le premier trouvé
	 * @param classeEntite   Type d'objet à rechercher
	 * @param nomCollection  Nom de la collection à utiliser
	 * @return document (selon les options)
	 */
	public <T> T rechercherPremierDocumentEtModifier(Query criteresFiltre, UpdateDefinition modifications, Class<T> classeEntite,
			String nomCollection) {
		return this.mongoTemplate.findAndModify(criteresFiltre, modifications, classeEntite, nomCollection);
	}

	/**
	 * Méthode spécialisée pour la recherche d'un document
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#findOne
	 */
	protected <T> T rechercherUn(Query requete, Class<T> classeEntite, String nomCollection) {
		T resultat = this.mongoTemplate.findOne(requete, classeEntite, nomCollection);
		if (resultat == null) {
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
		return resultat;
	}

	/**
	 * Méthode spécialisée pour la suppression d'un document
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#remove
	 */
	protected void supprimerParId(String id, String nomCollection) {
		Query requete = Query.query(Criteria.where(ATTRIBUT_ID).is(id));
		this.supprimerParRequete(requete, nomCollection);
	}

	/**
	 * Méthode spécialisée pour la suppression de document par requête
	 *
	 * @see org.springframework.data.mongodb.core.MongoTemplate#remove
	 */
	protected void supprimerParRequete(Query requete, String nomCollection) {
		long nbSuppression = this.mongoTemplate.remove(requete, nomCollection).getDeletedCount();
		if (nbSuppression == 0) {
			throw new MongodbException(MongodbException.ERREUR_DOCUMENT_NON_EXISTANT);
		}
	}
}
