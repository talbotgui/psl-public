#!/usr/bin/env bash

####################################################################################################
# Script de démarrage des services et micro-services sur un poste de développement.
#
# A SAVOIR : 
#   les chemins passés dans "-cp" ne peuvent être absolu dans une ligne de commande GIT sous Windows
#
####################################################################################################

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

#####################
# Gestion du keystore
#####################
OPTIONS_KEYSTORE="-DcheminKeystore=`pwd`/${cheminRelatifKeystore} -DmotdepasseKeystore=${motdepasseKeystore}"

####################
# Paramètres mémoire
####################
# 80m pour les petites JVM
OPTIONS_MEM="-Xmx80m -Xms80m"
# par défaut pour les autres
OPTIONS_MEM_LARGE=""

################
# Paramètres CDS
################
# Pour bloquer le Class Data Sharing
OPTIONS_CDS="-Xshare:off"

##################
# Gestion du debug
##################
DEBUG_PARAMETER=""
if [ "$2" == "DEBUG" ] ; then
	OPTIONS_MEM="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9999"
	DEBUG_PARAMETER="--debug"
fi

# Création du répertoire ${repertoirePidEtLog} si besoin et export pour les besoins de certains programmes
export repertoirePidEtLog="${repertoirePidEtLog}"
if test ! -d "${repertoirePidEtLog}"; then
	mkdir ${repertoirePidEtLog}
fi
PARAMETRES_COMMUN="--repertoirePidEtLog=${repertoirePidEtLog}"
PARAMETRES_EUREKA="--EUREKA_URI=${eurekaUri} --MACHINE_HOSTNAME=${machineHostname}"

######################
# Gestion des services
######################
if [ "$1" == "S_ADMIN" ] || [ "$1" == "SA" ] || [ "$1" == "service-admin" ] ; then
	NOM_EXACTE="service-admin"
	EXECUTABLE="-jar ${cheminExecutableServiceAdmin}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA} --spring.config.location=`pwd`/services-cloud/service-admin/src/test/resources/application-specifique.properties"

elif [ "$1" == "S_CONFIG" ] || [ "$1" == "SC" ] || [ "$1" == "service-config" ] ; then
	NOM_EXACTE="service-config"
	EXECUTABLE="-jar ${cheminExecutableServiceConfig}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} -Dcheminapp=`pwd`/socle-communtest -Dcheminkeystorechiffrement=`pwd`/socle-communtest/src/main/resources/keystoreconfig"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA} --spring.config.location=`pwd`/services-cloud/service-config/src/test/resources/application-specifique.properties"

elif [ "$1" == "S_GATEWAY" ] || [ "$1" == "SG" ] || [ "$1" == "service-gateway" ] ; then
	NOM_EXACTE="service-gateway"
	EXECUTABLE="-jar ${cheminExecutableServiceGateway}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM} -Dreactor.netty.http.server.accessLogEnabled=true -Dlogging.config=`pwd`/services-cloud/service-gateway/src/test/resources/logback.xml"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA} --spring.config.location=`pwd`/services-cloud/service-gateway/src/test/resources/application-specifique.properties"

elif [ "$1" == "S_REDIS" ] || [ "$1" == "SRE" ] || [ "$1" == "service-redis" ] ; then
	NOM_EXACTE="service-redis"
	EXECUTABLE="-jar ${cheminExecutableServiceRedis}"
	OPTIONS="--enable-preview ${OPTIONS_CDS} ${OPTIONS_MEM} -Dlogback.configurationFile=`pwd`/services-cloud/service-redis/src/test/resources/logback.xml"
	PARAMETRES="${PARAMETRES_COMMUN} --port=6379 --cheminExecutable=`pwd`/services-cloud/service-redis/redis-server.exe"

elif [ "$1" == "S_MONGODB" ] || [ "$1" == "SDB" ] || [ "$1" == "service-nosql" ] ; then
	NOM_EXACTE="service-nosql"
	EXECUTABLE="-jar ${cheminExecutableServiceNosql}"
	OPTIONS="--enable-preview ${OPTIONS_CDS} ${OPTIONS_MEM} -Dlogback.configurationFile=`pwd`/services-cloud/service-nosql/src/test/resources/logback.xml"
	PARAMETRES="${PARAMETRES_COMMUN} --fichierConfiguration=`pwd`/socle-communtest/src/main/resources/config/mongodb.properties"

elif [ "$1" == "S_LDAP" ] || [ "$1" == "SL" ] || [ "$1" == "service-ldap" ] ; then
	NOM_EXACTE="service-ldap"
	EXECUTABLE="-jar ${cheminExecutableServiceLdap}"
	OPTIONS="--enable-preview ${OPTIONS_CDS} ${OPTIONS_MEM} -Dlogback.configurationFile=`pwd`/services-cloud/service-ldap/src/test/resources/logback.xml"
	PARAMETRES="${PARAMETRES_COMMUN} --ldapPort=1389 --baseDn=dc=psl,dc=talbotgui,dc=github,dc=com --adminUtilisateur=${ldapAdminUtilisateur} --adminMdp=${ldapAdminMdp} --cheminLdif=`pwd`/services-cloud/service-ldap/src/main/resources/server.ldif"
	
elif [ "$1" == "S_REGISTRY" ] || [ "$1" == "SR" ] || [ "$1" == "service-registry" ] ; then
	NOM_EXACTE="service-registry"
	EXECUTABLE="-jar ${cheminExecutableServiceRegistre}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA} --spring.config.location=`pwd`/services-cloud/service-registry/src/test/resources/application-specifique.properties"

############################
# Gestion des micro-services
############################
elif [ "$1" == "BROUILLON" ] || [ "$1" == "B" ] || [ "$1" == "socle-dbbrouillon" ] ; then
	NOM_EXACTE="socle-dbbrouillon"
	EXECUTABLE="-jar ${cheminExecutableSocleDbbrouillon}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "CONFIGURATION" ] || [ "$1" == "C" ] || [ "$1" == "socle-dbconfiguration" ] ; then
	NOM_EXACTE="socle-dbconfiguration"
	EXECUTABLE="-jar ${cheminExecutableSocleDbconfiguration}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "DOCUMENT" ] || [ "$1" == "D" ] || [ "$1" == "socle-dbdocument" ] ; then
	NOM_EXACTE="socle-dbdocument"
	EXECUTABLE="-jar ${cheminExecutableSocleDbdocument}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"


elif [ "$1" == "NOTIFICATION" ] || [ "$1" == "N" ] || [ "$1" == "socle-dbnotification" ] ; then
	NOM_EXACTE="socle-dbnotification"
	EXECUTABLE="-jar ${cheminExecutableSocleDbnotification}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "REFERENTIEL" ] || [ "$1" == "REF" ] || [ "$1" == "socle-referentiel" ] ; then
	NOM_EXACTE="socle-referentiel"
	EXECUTABLE="-jar ${cheminExecutableSocleReferentiel}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM_LARGE}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "REFERENTIEL_EXTERNE" ] || [ "$1" == "REFEX" ] || [ "$1" == "socle-referentielexterne" ] ; then
	NOM_EXACTE="socle-referentielexterne"
	EXECUTABLE="-jar ${cheminExecutableSocleReferentielExterne}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "SECURITE" ] || [ "$1" == "SECU" ] || [ "$1" == "socle-securite" ] ; then
	NOM_EXACTE="socle-securite"
	EXECUTABLE="-jar ${cheminExecutableSocleSecurite}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "SOUMISSION" ] || [ "$1" == "S" ] || [ "$1" == "socle-soumission" ] ; then
	NOM_EXACTE="socle-soumission"
	EXECUTABLE="-jar ${cheminExecutableSocleSoumission}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM} -Dpolyglot.engine.WarnInterpreterOnly=false"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "TRANSFERT" ] || [ "$1" == "T" ] || [ "$1" == "socle-transfert" ] ; then
	NOM_EXACTE="socle-transfert"
	EXECUTABLE="-jar ${cheminExecutableSocleTransfert}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

elif [ "$1" == "ADMINPSL" ] || [ "$1" == "A" ] || [ "$1" == "socle-adminpsl" ] ; then
	NOM_EXACTE="socle-adminpsl"
	EXECUTABLE="-jar ${cheminExecutableSocleAdminpsl}"
	OPTIONS="--enable-preview ${OPTIONS_KEYSTORE} ${OPTIONS_CDS} ${OPTIONS_MEM}"
	PARAMETRES="${PARAMETRES_COMMUN} ${PARAMETRES_EUREKA}"

#################################
# Documentation si erreur d'appel
#################################
else
	printf 'Option "%s" invalide. Les options sont :\n' $1
	printf '    services : S_ADMIN (SA), S_CONFIG (SC), S_GATEWAY (SG), S_MONGODB (SDB), S_REGISTRY (SR), S_REDIS (SRE), S_LDAP (SL)\n'
	printf '    micro-services avec BDD : BROUILLON (B), CONFIGURATION (C), DOCUMENT (D), NOTIFICATION (N), ADMINPSL (A)\n'
	printf '    micro-services sans BDD : REFERENTIEL (REF), REFERENTIEL_EXTERNE (REFEX), SECURITE (SECU) et SOUMISSION (S)\n'
	return
fi

###################################################
# Gestion de l'index de l'instance du micro-service
###################################################
INDEX_INSTANCE=1
while [ -f "${repertoirePidEtLog}pid_${NOM_EXACTE}-${INDEX_INSTANCE}.pid" ] ; do
	INDEX_INSTANCE=$(($INDEX_INSTANCE+1))
done
PARAMETRES="--indexInstance=${INDEX_INSTANCE} ${PARAMETRES}"

# Les services n'ont pas le droit de démarrer plusieurs fois
if [[ "$NOM_EXACTE" == "service"* ]] && [ "${INDEX_INSTANCE}" != "1" ] ; then
	printf "  Service %s déjà démarré et multi-instance interdit en local pour les services.\n" "$NOM_EXACTE"
	return
fi

###########
# Démarrage
###########
FICHIER_BOOT="${repertoirePidEtLog}log_${NOM_EXACTE}-${INDEX_INSTANCE}.boot"
echo "nohup java ${OPTIONS} ${EXECUTABLE} ${PARAMETRES} ${DEBUG_PARAMETER} >>${FICHIER_BOOT} 2>&1 &"
nohup java ${OPTIONS} ${EXECUTABLE} ${PARAMETRES} ${DEBUG_PARAMETER} >>${FICHIER_BOOT} 2>&1 &

######################################################
# Bloquage du script si le 3ème paramètre est BLOQUANT
######################################################
echo "    attente de la fin du démarrage (max 20 secondes)"
# Attente de max 20 secondes
i=0
FICHIER_PID="${repertoirePidEtLog}pid_${NOM_EXACTE}-${INDEX_INSTANCE}.pid"
while [ ! -f $FICHIER_PID ] && [ $i -lt 100 ] ; do
  sleep 0.2
  i=$(($i+1))
done
# Log final
if [ -f $FICHIER_PID ] ; then
	echo "    démarré"
else
	echo "    erreur de démarrage"
fi
