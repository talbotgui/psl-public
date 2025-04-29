#!/usr/bin/env bash

# Si un paramètre, affichage de la doc
if [ "$1" != "" ] ; then
  echo "Programme démarrant tout. Equivalent de . ./demarrerMulti.sh SR SC SRE SG SL SA REF REFEX SECU S SDB B C D N A T"
  return
fi

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

echo ""
echo "Execution du script d'arrêt total pour ne pas tenter de démarrer un applicatif déjà démarré"
. ./arreterTout.sh
echo "  done"

echo ""
echo "Démarrage des services de base"
echo ""
. ./demarrer.sh "S_REGISTRY"
echo ""
. ./demarrer.sh "S_CONFIG"
echo ""
. ./demarrer.sh "S_REDIS"
echo ""
. ./demarrer.sh "S_GATEWAY"
echo ""
. ./demarrer.sh "S_LDAP"

echo ""
echo "Démarrage des services et applicatifs Java ne nécessitant pas MongoDB"
echo ""
. ./demarrer.sh "S_ADMIN"
echo ""
. ./demarrer.sh "REFERENTIEL"
echo ""
. ./demarrer.sh "REFERENTIEL_EXTERNE"
echo ""
. ./demarrer.sh "SECURITE"
echo ""
. ./demarrer.sh "SOUMISSION"
echo ""
. ./demarrer.sh "TRANSFERT"

echo ""
echo "Démarrage de la base de données MONGO"
echo ""
. ./demarrer.sh "S_MONGODB"

echo ""
echo "Démarrage des applicatifs pouvant avoir besoin de la base de données MONGO dès leur démarrage"
echo ""
. ./demarrer.sh "BROUILLON"
echo ""
. ./demarrer.sh "CONFIGURATION"
echo ""
. ./demarrer.sh "DOCUMENT"
echo ""
. ./demarrer.sh "NOTIFICATION"
echo ""
. ./demarrer.sh "ADMINPSL"

echo ""
if test -f "${fichierPidNosql}"; then
	echo "  MongoDB est bien démarré"
	echo ""
	. ./outils.sh PS
else
	echo ""
	echo "  MongoDB n'a pas généré son fichier PID (cf. logs ci-dessous). /!\ Une partie des applicatifs sont néanmoins démarrés /!\ "
	echo "  Le PID de la base est affiché dans les logs mongo.log et disponible avec la commande suivante : . ./outils.sh PS"
	echo "  Sous Windows, pour tuer la tâche : taskkill /F /PID xxxx"
	echo ""

fi

