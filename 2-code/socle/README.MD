# Règles de développement

* Nommage
** l'ensemble du code est en français
** les méthodes commencent par un verbe à l'infinitif
** le suffixe IMPL s'applique dès qu'une classe implémente un service du même nom
** les suffixes Utils, Service, Dao, Controller sont issus de Design Pattern et donc en anglais

* Usage des frameworks
** les loggeurs sont basés sur SLF4J et en variable statique

* Conception
** Tout projet exposant des APIs fournit son client d'API
** Le répertoire src/main/resources ne doit jamais contenir d'élément propre à un environnement (ni application.properties ni autre fichier de template ou autre)
** Toutes les classes utilisées par un client d'API REST (com.github.talbotgui.xxx.clientapi) doivent être dans le package com.github.talbotgui.xxx.clientapi.dto (attention au packaging du JAR dédié aux clients d'API REST)

* Documentation
** La javadoc n'est pas obligatoire si la signature de la méthode est TOTALEMENT explicite
** La javadoc est interdite sur les GETTER et SETTER

* Sécurité
** Si la méthode traite des données personnelles et qu'il est nécessaire de logguer ces données dans des cas de débug, les données doivent être logguées avec le niveau TRACE et un log doit s'afficher au démarrage de l'application (@see com.github.talbotgui.psl.socle.soumission.service.SoumissionServiceImpl.avertissementPourLesLogs()

* Test
** Tous les controleurs REST doivent être testés avec le service bouchonné et en utilisant le client
** Tous les services métiers doivent être testés avec un contexte Spring non bouchonné sauf pour la source de données (base de donnée IN MEMORY par exemple). Les DAOs sont donc testés avec les services métiers
** Tout autre type de composant que les controleurs REST, les services métiers et les DAO doivent être testés.
** Tous les projets doivent disposer d'un launcher permettant d'exécuter tous les tests JUNIT

# Aide

Au démarrage d'un test mettant en jeu une MongoDB embarquée, "de.flapdoodle.embed" tente de télécharger un binaire depuis Internet. Si cela échoue, exécuter les étapes suivantes :
* récupérer l'adresse du binaire (https://downloads.mongodb.org/win32/mongodb-win32-x86_64-3.5.5.zip par exemple)
* créer le répertoire local C:\Users\<Username>\.embedmongo\win32\
* télécharger le binaire et le placer dans le répertoire nouvellement créé
* redémarrer le test
