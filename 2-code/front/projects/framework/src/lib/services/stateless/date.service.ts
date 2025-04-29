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
import { DatePipe } from '@angular/common';
import { Injectable } from '@angular/core';

/** Utilitaire pour la gestion des dates */
@Injectable({ providedIn: 'root' })
export class DateService {

    /** Constructeur pour injection des dépendances */
    public constructor(private datePipe: DatePipe) { }

    /** Transformation de date en string */
    public transformerDateEnString(laDate: Date, format = 'dd/MM/yyyy'): string | undefined {
        const resultat = this.datePipe.transform(laDate, format);
        if (resultat === null) {
            return undefined;
        } else {
            return resultat;
        }
    }

    /**
     * /!\ Cette méthode est statique. Inutile d'ajouter la dépendance au service pour l'utiliser !
     * Cette méthode doit rester statique pour les besoins du LocalDateAdapter
     */
    public static transformerStringEnDate(laDate: any): Date | null {
        // @see https://stackoverflow.com/questions/44201050/how-to-implement-md-date-formats-for-datepicker
        if (typeof laDate === 'string' && laDate.length === 10 && laDate.indexOf('/') === 2 && laDate.indexOf('/', 4) === 5) {
            const str = laDate.split('/');
            const date = new Date(Number(str[2]), Number(str[1]) - 1, Number(str[0]), 12);
            date.setHours(0, 0, 0, 0);
            return date;
        }
        const timestamp = typeof laDate === 'number' ? laDate : Date.parse(laDate);
        return isNaN(timestamp) ? null : new Date(timestamp);
    }
}