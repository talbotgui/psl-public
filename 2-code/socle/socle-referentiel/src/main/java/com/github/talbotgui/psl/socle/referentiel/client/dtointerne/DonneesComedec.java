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
package com.github.talbotgui.psl.socle.referentiel.client.dtointerne;

/** Statut de connexion de la commune au système COMEDEC. */
public class DonneesComedec {
	private boolean mairieDeces;
	private boolean mairieMariage;
	private boolean mairieNaissance;
	private boolean notaireDeces;
	private boolean notaireMariage;
	private boolean notaireNaissance;
	private boolean tesNaissance;

	public boolean isMairieDeces() {
		return this.mairieDeces;
	}

	public boolean isMairieMariage() {
		return this.mairieMariage;
	}

	public boolean isMairieNaissance() {
		return this.mairieNaissance;
	}

	public boolean isNotaireDeces() {
		return this.notaireDeces;
	}

	public boolean isNotaireMariage() {
		return this.notaireMariage;
	}

	public boolean isNotaireNaissance() {
		return this.notaireNaissance;
	}

	public boolean isTesNaissance() {
		return this.tesNaissance;
	}

	public void setMairieDeces(boolean mairieDeces) {
		this.mairieDeces = mairieDeces;
	}

	public void setMairieMariage(boolean mairieMariage) {
		this.mairieMariage = mairieMariage;
	}

	public void setMairieNaissance(boolean mairieNaissance) {
		this.mairieNaissance = mairieNaissance;
	}

	public void setNotaireDeces(boolean notaireDeces) {
		this.notaireDeces = notaireDeces;
	}

	public void setNotaireMariage(boolean notaireMariage) {
		this.notaireMariage = notaireMariage;
	}

	public void setNotaireNaissance(boolean notaireNaissance) {
		this.notaireNaissance = notaireNaissance;
	}

	public void setTesNaissance(boolean tesNaissance) {
		this.tesNaissance = tesNaissance;
	}

	@Override
	public String toString() {
		return "DonneesComedec [mairieDeces=" + this.mairieDeces + ", mairieMariage=" + this.mairieMariage + ", mairieNaissance="
				+ this.mairieNaissance + ", notaireDeces=" + this.notaireDeces + ", notaireMariage=" + this.notaireMariage + ", notaireNaissance="
				+ this.notaireNaissance + ", tesNaissance=" + this.tesNaissance + "]";
	}

}
