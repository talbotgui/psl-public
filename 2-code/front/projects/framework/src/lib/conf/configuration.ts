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
/**
 * Classe de configuration interne au framework
 * (pour limiter la duplication dans les fichiers d'environnement de chaque démarche)
 */
export const configuration = {

  // Paramètes OIDC
  oidc: {
    // URL de l'API /auth
    url: 'https://xxx.service-public.fr/realms/service-public/protocol/openid-connect/auth',
    // Client réel (nécessaire aux échanges entre l'application et la page de connexion du fournisseur OIDC)
    clientId: 'xxxxxxxxx',
    // Scopes échangés avec le fournisseur OIDC
    scopes: 'openid profile email particulier',
    // Délai entre deux raffraichissement du token PSL en ms (480000=8mn avec un refreshToken SP de 30mn mais un accessToken PSL de 10mn)
    refreshTimeout: 480000
  },
};
