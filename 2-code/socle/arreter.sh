#!/usr/bin/env bash

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

######################################################
# Fonction d'arrêt d'un applicatif par son fichier PID
# @param $1 le code exacte du micro-servie
######################################################
arreter() {
	# Création du répertoire ${repertoirePidEtLog} si inexistant
	if [ ! -d "$repertoirePidEtLog" ] ; then
		mkdir $repertoirePidEtLog
	fi

	# Lecture des paramètres
	app=$1
	index=$2
	mode=$3

	# Log
	printf "Arrêt de l'application %s (index instance=%s et mode=%s).\n" "$app" "$index" "$mode"

	# Si l'index a été fourni, on le recherche
	if [ "$index" == "" ] ; then
		index="`ls ${repertoirePidEtLog}pid_$app*.pid 2>/dev/null | tail -1 | sed 's#.*/##g' | sed 's/[^0-9]//g'`"
		printf "Instance '%s' trouvée\n" "$index"
	fi

	# Définition du fichier PID à arrêter
	NOM_PID_A_ARRETER="${repertoirePidEtLog}pid_$app-$index.pid"

	# Vérification du fichier PID
	if [ ! -f "${NOM_PID_A_ARRETER}" ] ; then
		echo "  Aucun fichier PID trouvé pour arrêter pour cette instance"
		return
	fi

	# A moins que l'arrêt brutal soit demandé, sinon on tente le soft
	ok="false"
	if [ "$mode" != "HARD" ] ; then
		httpPort=`curl -k -s -u ${apiRegistreUtilisateur}:${apiRegistreMotdepasse} ${apiRegistreURL}/service/registry/eureka/apps/ | awk "/${app}-${index}/,/port/" | grep port | sed 's/[^0-9\.]*//g'`
		if [ "$httpPort" != "" ] ; then
			printf "  arrêt de %s via API sur le port %s\n" "${NOM_PID_A_ARRETER}" "$httpPort"
			resultat=`curl -k -s -X POST -u "${apiActuatorUtilisateur}:${apiActuatorMotdepasse}" ${apiActuatorURLsansPort}:${httpPort}/actuator/shutdown`
			if [ "$resultat" == '{"message":"Shutting down, bye..."}' ] ; then
				echo "  arrêt réussi"
				ok="true"
			else
				echo "  arrêt échoué (résultat de l'appel à /shutdown : $resultat)"
			fi
		else
			echo "  arrêt échoué (résultat de la recherche du port : $httpPort)"
		fi
	fi

	# Si l'arrêt HARD est demandé ou nécessaire
	if [ "$ok" != "true" ] ; then
		# Lecture du PID
		pid=`cat ${NOM_PID_A_ARRETER}`
		printf "  arrêt de %s par un kill de %s.\n" "${NOM_PID_A_ARRETER}" "$pid"

		# Arrêt du PID
		taskkill //F //PID $pid
	fi
	
	# Suppression du fichier PID (Spring, avec /shutdown, supprime le fichier pid mais pas assez vite)
	rm "$NOM_PID_A_ARRETER"

	# Si plus rien ne tourne, purge du répertoire ${repertoireTomcat}
	if [ `ls ${repertoirePidEtLog}/*.pid 2>/dev/null | wc -l` -eq 0 ] && [ -d ${repertoireTomcat} ] ; then
		echo ""
		echo "Plus aucune application ne fonctionne, donc suppression du répertoire '${repertoireTomcat}'"
		rm -rf ${repertoireTomcat}
	fi
}

# Lecture des paramètres
app="$1"
index="$2"
mode="$3"

# Gestion des paramètres optionnels
if [ "$index" == "HARD" ] || [ "$index" == "SOFT" ] ; then
  mode="$index"
  index="1"
fi
re='^[0-9]+$'
if [ "$index" != "" ] && ! [[ $index =~ $re ]] ; then
   echo "L'index '$index' n'est pas un nombre"
   return
fi

######################
# Gestion des services
######################
if [ "$app" == "S_ADMIN" ] || [ "$app" == "SA" ] || [ "$app" == "service-admin" ] ; then
	arreter "service-admin" "$index" "$mode"

elif [ "$app" == "S_CONFIG" ] || [ "$app" == "SC" ] || [ "$app" == "service-config" ] ; then
	arreter "service-config" "$index" "$mode"

elif [ "$app" == "S_GATEWAY" ] || [ "$app" == "SG" ] || [ "$app" == "service-gateway" ] ; then
	arreter "service-gateway" "$index" "$mode"

elif [ "$app" == "S_REDIS" ] || [ "$app" == "SRE" ] || [ "$app" == "service-redis" ] ; then
	if test -f "${fichierPidRedis}"; then
		echo ""
		echo "Arrêt du serveur REDIS par suppression du fichier ${fichierPidRedis}"
		rm ${fichierPidRedis}
	fi

elif [ "$app" == "S_MONGODB" ] || [ "$app" == "SDB" ] || [ "$app" == "service-nosql" ] ; then
	if test -f "${fichierPidNosql}"; then
		echo ""
		echo "Arrêt de la base de données MongoDB par suppression du fichier ${fichierPidNosql}"
		rm ${fichierPidNosql}
	fi

elif [ "$app" == "S_LDAP" ] || [ "$app" == "SL" ] || [ "$app" == "service-ldap" ] ; then
	if test -f "${fichierPidLdap}"; then
		echo ""
		echo "Arrêt du LDAP par suppression du fichier ${fichierPidLdap}"
		rm ${fichierPidLdap}
	fi

elif [ "$app" == "S_REGISTRY" ] || [ "$app" == "SR" ] || [ "$app" == "service-registry" ] ; then
	arreter "service-registry" "$index" "$mode"

############################
# Gestion des micro-services
############################
elif [ "$app" == "BROUILLON" ] || [ "$app" == "B" ] || [ "$app" == "socle-dbbrouillon" ] ; then
	arreter "socle-dbbrouillon" "$index" "$mode"

elif [ "$app" == "CONFIGURATION" ] || [ "$app" == "C" ] || [ "$app" == "socle-dbconfiguration" ] ; then
	arreter "socle-dbconfiguration" "$index" "$mode"

elif [ "$app" == "DOCUMENT" ] || [ "$app" == "D" ] || [ "$app" == "socle-dbdocument" ] ; then
	arreter "socle-dbdocument" "$index" "$mode"

elif [ "$app" == "NOTIFICATION" ] || [ "$app" == "N" ] || [ "$app" == "socle-dbnotification" ] ; then
	arreter "socle-dbnotification" "$index" "$mode"

elif [ "$app" == "REFERENTIEL" ] || [ "$app" == "REF" ] || [ "$app" == "socle-referentiel" ] ; then
	arreter "socle-referentiel" "$index" "$mode"

elif [ "$app" == "REFERENTIEL_EXTERNE" ] || [ "$app" == "REFEX" ] || [ "$app" == "socle-referentielexterne" ] ; then
	arreter "socle-referentielexterne" "$index" "$mode"

elif [ "$app" == "SECURITE" ] || [ "$app" == "SECU" ] || [ "$app" == "socle-securite" ] ; then
	arreter "socle-securite" "$index" "$mode"

elif [ "$app" == "SOUMISSION" ] || [ "$app" == "S" ] || [ "$app" == "socle-soumission" ] ; then
	arreter "socle-soumission" "$index" "$mode"

elif [ "$app" == "TRANSFERT" ] || [ "$app" == "T" ] || [ "$app" == "socle-transfert" ] ; then
	arreter "socle-transfert" "$index" "$mode"

elif [ "$app" == "ADMINPSL" ] || [ "$app" == "A" ] || [ "$app" == "socle-adminpsl" ] ; then
	arreter "socle-adminpsl" "$index" "$mode"

#################################
# Documentation si erreur d'appel
#################################
else
	printf 'Option "%s" invalide. Les options sont :\n' $app
	printf '    services : S_ADMIN (SA), S_CONFIG (SC), S_GATEWAY (SG), S_MONGODB (SDB), S_REGISTRY (SR), S_REDIS (SRE), S_LDAP (SL)\n'
	printf '    micro-services avec BDD : BROUILLON (B), CONFIGURATION (C), DOCUMENT (D), NOTIFICATION (N), ADMINPSL (A)\n'
	printf '    micro-services sans BDD : REFERENTIEL (REF), REFERENTIEL_EXTERNE (REFEX), SECURITE (SECU) et SOUMISSION (S)\n'
fi
