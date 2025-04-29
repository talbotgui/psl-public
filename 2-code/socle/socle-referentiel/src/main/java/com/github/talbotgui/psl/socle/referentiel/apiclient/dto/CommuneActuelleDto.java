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
package com.github.talbotgui.psl.socle.referentiel.apiclient.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.talbotgui.psl.socle.referentiel.client.dtointerne.DonneesComedec;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

//Jackson est utilisé pour stocker les données dans le cache et pour renvoyer un objet en sortie de l'API.
//Pour éviter que Jackson ne boucle entre commune->departement->region->commune, @JsonIdentityInfo est utilisée
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "codeInsee")
//pour le "libelleLong" indexé et donc renvoyé par le client d'API
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommuneActuelleDto extends ProtectionDeCommuneDto implements ITransformableEnDocumentLuceneDto {

	/** Code INSEE. */
	private String codeInsee;

	/** Code INSEE du département (non retourné dans le JSON). */
	private String codeInseeDepartement;

	/** Premier des codes postaux (utile quand on ne veut pas traiter le cas des multiples codes postaux). */
	private String codePostal;

	/** Code postal (issu du référentiel DataGouv). */
	private List<String> codesPostaux = new ArrayList<>();

	/** DTO des données comedec (pour ne jamais avoir à vérifier que l'objet est null ou pas). */
	private DonneesComedec comedec = new DonneesComedec();

	/** Liste des communes déléguées. */
	private Collection<CommuneDelegueeActuelleDto> communesDeleguees;

	/** Coordonnées de la gendarmerie (si la commune est protégée par une gendarmerie. */
	private AdresseEtHorairesGendarmerieDto coordonneesGendarmerie;

	/** Liste des codes de démarches connectés. */
	private List<String> demarchesConnectes = new ArrayList<>();

	/** DTO du département associé au membre 'codeInseeDepartement'. */
	private DepartementActuelDto departement;

	/** Libellé de la commune. */
	private String libelle;

	/** Siret (issu du référentiel DataGouv). */
	private String siret;

	public CommuneActuelleDto() {
		super();
	}

	public CommuneActuelleDto(String codeInsee, String libelle, String codeInseeDepartement) {
		super();
		this.codeInsee = codeInsee;
		this.libelle = libelle;
		this.codeInseeDepartement = codeInseeDepartement;
	}

	public String getCodeInsee() {
		return this.codeInsee;
	}

	public String getCodeInseeDepartement() {
		return this.codeInseeDepartement;
	}

	public String getCodePostal() {
		return this.codePostal;
	}

	public List<String> getCodesPostaux() {
		return this.codesPostaux;
	}

	public DonneesComedec getComedec() {
		return this.comedec;
	}

	public Collection<CommuneDelegueeActuelleDto> getCommunesDeleguees() {
		return this.communesDeleguees;
	}

	public AdresseEtHorairesGendarmerieDto getCoordonneesGendarmerie() {
		return this.coordonneesGendarmerie;
	}

	public List<String> getDemarchesConnectes() {
		return this.demarchesConnectes;
	}

	public DepartementActuelDto getDepartement() {
		return this.departement;
	}

	public String getLibelle() {
		return this.libelle;
	}

	/** Pour disposer d'un libellé long affichable */
	public String getLibelleLong() {
		String informationComplementaire = "";
		if (!this.codesPostaux.isEmpty()) {
			informationComplementaire = " (" + String.join(", ", this.codesPostaux) + ")";
		} else if (StringUtils.hasLength(this.codeInseeDepartement)) {
			informationComplementaire = "(" + this.codeInseeDepartement + ")";
		}
		return this.libelle + informationComplementaire;
	}

	public String getSiret() {
		return this.siret;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public void setCodeInseeDepartement(String codeInseeDepartement) {
		this.codeInseeDepartement = codeInseeDepartement;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	/**
	 * SETTER /!\ la liste n'est jamais nulle
	 *
	 * @param codesPostaux
	 */
	public void setCodesPostaux(List<String> codesPostaux) {
		this.codesPostaux = codesPostaux;
		if (codesPostaux == null) {
			this.codesPostaux = new ArrayList<>();
		} else if (!codesPostaux.isEmpty()) {
			this.codePostal = this.codesPostaux.get(0);
		}
	}

	public void setComedec(DonneesComedec comedec) {
		this.comedec = comedec;
	}

	public void setCommunesDeleguees(Collection<CommuneDelegueeActuelleDto> communesDeleguees) {
		this.communesDeleguees = communesDeleguees;
	}

	public void setCoordonneesGendarmerie(AdresseEtHorairesGendarmerieDto coordonneesGendarmerie) {
		this.coordonneesGendarmerie = coordonneesGendarmerie;
	}

	public void setDemarchesConnectes(List<String> demarchesConnectes) {
		this.demarchesConnectes = demarchesConnectes;
	}

	public void setDepartement(DepartementActuelDto departement) {
		this.departement = departement;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	public String toJson() {
		// La solution est simple partout sauf ici.
		// Donc on reste sur la méthode toJson custom
		StringBuilder communesDelegueesJSON = new StringBuilder();
		communesDelegueesJSON.append('[');
		if (this.communesDeleguees != null) {
			List<String> listeJsons = this.communesDeleguees.stream().map(CommuneDelegueeActuelleDto::toJson).toList();
			communesDelegueesJSON.append(String.join(",", listeJsons));
		}
		communesDelegueesJSON.append(']');
		String demarchesConnectesJSON = ("[\"" + String.join("\",\"", this.demarchesConnectes) + "\"]").replaceFirst("\"\"", "");
		String codePostauxJSON = ("[\"" + String.join("\",\"", this.codesPostaux) + "\"]").replaceFirst("\"\"", "");

		String coordonnees = "";
		if (this.coordonneesGendarmerie != null) {
			coordonnees = ", \"coordonneesGendarmerie\":" + this.coordonneesGendarmerie.toJson();
		}

		return "{\"libelle\":\"" + this.libelle + "\", \"codeInsee\":\"" + this.codeInsee + "\", \"siret\":\"" + this.siret + "\", \"codePostal\":\""
				+ this.codePostal + "\", \"codesPostaux\":" + codePostauxJSON + ",\"typeProtection\":\"" + this.getTypeProtection()
				+ "\",\"nomProtecteur\":\"" + this.getNomProtecteur() + "\", \"communesDeleguees\":" + communesDelegueesJSON
				+ ",\"demarchesConnectes\":" + demarchesConnectesJSON + ", \"departement\":" + this.departement.toJson() + coordonnees + "}";
	}

	public String toJsonSimple() {
		String codePostauxJSON = ("[\"" + String.join("\",\"", this.codesPostaux) + "\"]").replaceFirst("\"\"", "");
		return "{\"libelle\":\"" + this.libelle + "\", \"codeInsee\":\"" + this.codeInsee + "\", \"siret\":\"" + this.siret + "\", \"codePostal\":\""
				+ this.codePostal + "\", \"codesPostaux\":" + codePostauxJSON + ",\"typeProtection\":\"" + this.getTypeProtection()
				+ "\",\"nomProtecteur\":\"" + this.getNomProtecteur() + "\"}";
	}

	@Override
	public String toString() {
		String codePostauxJSON = ("[" + String.join("\",\"", this.codesPostaux) + "]").replaceFirst("\"\"", "");
		return "CommuneActuelleDto [libelle=" + this.libelle + ", codeInsee=" + this.codeInsee + ", siret=" + this.siret + ", codesPostaux="
				+ codePostauxJSON + ", typeProtection()=" + this.getTypeProtection() + ", nomProtecteur()=" + this.getNomProtecteur() + "]";
	}

	@Override
	public Document transformerObjetEnDocument() {
		Document document = new Document();
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_RECHERCHE, this.codesPostaux + " " + this.libelle, Field.Store.YES));
		document.add(new TextField(IndexeurService.NOM_CHAMP_LUCENE_TYPE_JAVA, this.getClass().getSimpleName(), Field.Store.YES));
		document.add(new StoredField(IndexeurService.NOM_CHAMP_LUCENE_CONTENU_JSON, this.toJson()));
		return document;
	}

}
