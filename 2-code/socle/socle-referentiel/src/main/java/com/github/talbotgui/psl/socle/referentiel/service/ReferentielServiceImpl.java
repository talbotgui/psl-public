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
package com.github.talbotgui.psl.socle.referentiel.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.CommuneUgleDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.DepartementActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysActuelDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.PaysNaissanceDto;
import com.github.talbotgui.psl.socle.referentiel.apiclient.dto.RegionActuelleDto;
import com.github.talbotgui.psl.socle.referentiel.service.indexeur.IndexeurService;

@Service
public class ReferentielServiceImpl implements ReferentielService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferentielServiceImpl.class);

	@Autowired
	private IndexeurService indexeur;

	// pas de valeur par défaut pour forcer la présence d'un fichier de configuration 'application.properties'
	@Value("${controleur.nbResultatsMax}")
	private int nbMaxResultats;

	@SuppressWarnings("unchecked")
	@Override
	public List<CommuneNaissanceDto> rechercherCommuneNaissance(String chaine) {
		LOGGER.debug("Recherche de CommuneNaissance avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<CommuneNaissanceDto>) this.indexeur.rechercher(CommuneNaissanceDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommuneActuelleDto> rechercherCommunesActuelles(String chaine) {
		LOGGER.debug("Recherche de CommuneActuelle avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<CommuneActuelleDto>) this.indexeur.rechercher(CommuneActuelleDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommuneUgleDto> rechercherCommuneUgle(String chaine) {
		LOGGER.debug("Recherche de CommuneUgle avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<CommuneUgleDto>) this.indexeur.rechercher(CommuneUgleDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DepartementActuelDto> rechercherDepartementsActuels(String chaine) {
		LOGGER.debug("Recherche de DepartementActuel avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<DepartementActuelDto>) this.indexeur.rechercher(DepartementActuelDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaysActuelDto> rechercherNationalitesDePaysActuel(String chaine) {
		LOGGER.debug("Recherche de nationalités de pays actuel avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<PaysActuelDto>) this.indexeur.rechercher(PaysActuelDto.class, chaine, this.nbMaxResultats, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaysActuelDto> rechercherPaysActuels(String chaine) {
		LOGGER.debug("Recherche de PaysActuel avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<PaysActuelDto>) this.indexeur.rechercher(PaysActuelDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaysNaissanceDto> rechercherPaysNaissance(String chaine) {
		LOGGER.debug("Recherche de PaysNaissance avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<PaysNaissanceDto>) this.indexeur.rechercher(PaysNaissanceDto.class, chaine, this.nbMaxResultats, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegionActuelleDto> rechercherRegionsActuelles(String chaine) {
		LOGGER.debug("Recherche de RegionActuelle avec nbMaxResultats={} et la chaine {}", this.nbMaxResultats, chaine);
		return (List<RegionActuelleDto>) this.indexeur.rechercher(RegionActuelleDto.class, chaine, this.nbMaxResultats, false);
	}
}
