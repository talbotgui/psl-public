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
package com.github.talbotgui.psl.socle.commun.feign;

import com.github.talbotgui.psl.socle.commun.exception.AbstractException;
import com.github.talbotgui.psl.socle.commun.exception.ExceptionId;

/**
 * Classe d'exception utilisée par RetreiveMessageErrorDecoder pour regénérer une exception Java à partir du JSON venant d'un micro-service et reçue
 * par Feign.
 */
public class PslFeignException extends AbstractException {

	private static final long serialVersionUID = 1L;

	protected PslFeignException(ExceptionId id) {
		super(id);
	}
}
