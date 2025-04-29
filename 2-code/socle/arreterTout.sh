#!/usr/bin/env bash

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

###########################################################
# Fonction d'arrêt de toutes les instances du micro-service
# @param $1 le code exacte du micro-servie
###########################################################
arreterTout() {
	app=$1
	mode=$2
	while compgen -G "${repertoirePidEtLog}pid_$1-*.pid" > /dev/null ; do
		echo ""
		index=`ls ${repertoirePidEtLog}pid_${app}-*.pid | sed 's/.*-//' | sed 's/\.pid//'`
		. ./arreter.sh $app $index $mode
	done
}

# Possibilité d'arrêter tout en HARD mais le mode SOFT est celui par défaut
mode="SOFT"
if [ "$1" == "HARD" ] ; then
	mode=$1
fi

# Arrêt des applicatifs Java
arreterTout "socle-adminpsl" "$mode"
arreterTout "socle-dbbrouillon" "$mode"
arreterTout "socle-dbconfiguration" "$mode"
arreterTout "socle-dbdocument" "$mode"
arreterTout "socle-dbnotification" "$mode"
arreterTout "socle-referentielexterne" "$mode"
arreterTout "socle-referentiel" "$mode"
arreterTout "socle-securite" "$mode"
arreterTout "socle-soumission" "$mode"
arreterTout "socle-transfert" "$mode"

# Arrêt des services Java
arreterTout "service-admin" "HARD"
arreterTout "service-config" "HARD"
arreterTout "service-registry" "HARD"
arreterTout "service-gateway" "HARD"
arreterTout "service-redis" "HARD"

# Arrêt des autres applications (bdd, ...)
arreterTout "service-nosql" "HARD"
arreterTout "service-ldap" "HARD"
