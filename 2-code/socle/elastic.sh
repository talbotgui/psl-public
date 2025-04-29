#!/usr/bin/env bash

###########################################################
# Script d'installation et de démarrage des briques Elastic
###########################################################

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

# Chemins nécessaires à la suite du script et aux fichiers de configuration (d'où l'export)
export versionElastic=${versionElastic}
export CHEMIN_SOCLE=`pwd`/
CHEMIN_ARCHIVE=${CHEMIN_SOCLE}.elastic/zip/
CHEMIN_BIN=${CHEMIN_SOCLE}.elastic/bin/
CHEMIN_LOG=${CHEMIN_SOCLE}.elastic/logs/
CHEMIN_DATA=${CHEMIN_SOCLE}.elastic/data/
CHEMIN_TMP_ELAS=${CHEMIN_SOCLE}.elastic/tmp/
FB_PATH_CONF=${CHEMIN_SOCLE}services-cloud/service-elastic/filebeat/
# sans / à la fin de ES_PATH_CONF
export ES_PATH_CONF=${CHEMIN_SOCLE}services-cloud/service-elastic/elasticsearch
export KBN_PATH_CONF=${CHEMIN_SOCLE}services-cloud/service-elastic/kibana

# Création des chemins des logs et des PID
CHEMIN_LOG_ELAS=${CHEMIN_LOG}elasticsearch/
ELAS_LOG_BOOT=${CHEMIN_LOG_ELAS}boot.log
ELAS_PID=${CHEMIN_LOG_ELAS}elastic.pid
CHEMIN_LOG_FB=${CHEMIN_LOG}filebeat/
FB_LOG_BOOT=${CHEMIN_LOG_FB}boot.log
FB_PID=${CHEMIN_LOG_FB}filebeat.pid
CHEMIN_LOG_KIBANA=${CHEMIN_LOG}kibana/
KIBANA_LOG_BOOT=${CHEMIN_LOG_KIBANA}boot.log
KIBANA_PID=${CHEMIN_LOG_KIBANA}kibana.pid

#############################################
# Fonctions communes utilisées plusieurs fois
#############################################
arreterKibana()
{
	taskkill //F //PID `cat ${KIBANA_PID}`
	rm ${KIBANA_PID}
}
demarrerKibana()
{
	# Lecture du mdp et passage en variable
	export MDP_KIBANA_SYSTEM="`cat ${CHEMIN_DATA}elasticsearch/mdp-kibana_system`"
	
	# Démarrage
	nohup ${CHEMIN_BIN}kibana-${versionElastic}/bin/kibana.bat >>${KIBANA_LOG_BOOT} 2>&1 &

	# Attente et validation du démarrage
	etat="`curl -s -o /dev/null -w "%{http_code}" --user "kibana_system:${MDP_KIBANA_SYSTEM}"  http://localhost:5601/api/status`"
	while [ "${etat}" != "200" ] ; do
		sleep 1
		etat="`curl -s -o /dev/null -w "%{http_code}" --user "kibana_system:${MDP_KIBANA_SYSTEM}" http://localhost:5601/api/status`"
	done
}

######################
# Désinstallation
######################
if [ "$1" == "DESINSTALLATION" ] ; then
	echo "Désinstallation de l'ensemble de la pile elastic"
	rm -rf ${CHEMIN_SOCLE}./.elastic

######################
# Installation
######################
elif [ "$1" == "INSTALLATION" ] ; then

	# Désinstallation avant l'installation
	. ./elastic.sh DESINSTALLATION

	# Log
	echo "Installation de l'ensemble de la pile elastic en version ${versionElastic}"

	# Téléchargement des archives
	echo "  Téléchargement des binaires (-x https://xxxx:xx au besoin sur les curl)"
	mkdir -p ${CHEMIN_ARCHIVE}
	echo "  ... 1/3 - elasticsearch"
	curl -k -o ${CHEMIN_ARCHIVE}elasticsearch-${versionElastic}-windows-x86_64.zip https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-${versionElastic}-windows-x86_64.zip
	echo "  ... 2/3 - filebeat"
	curl -k -o ${CHEMIN_ARCHIVE}filebeat-${versionElastic}-windows-x86_64.zip      https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-${versionElastic}-windows-x86_64.zip
	echo "  ... 3/3 - kibana"
	curl -k -o ${CHEMIN_ARCHIVE}kibana-${versionElastic}-windows-x86_64.zip        https://artifacts.elastic.co/downloads/kibana/kibana-${versionElastic}-windows-x86_64.zip
	echo "  ... terminé"

	# Extraction des archives
	echo "  Extraction des binaires"
	mkdir -p ./.elastic/bin
	echo "  ... 1/3 - elasticsearch"
	unzip -q -d ${CHEMIN_BIN} ${CHEMIN_ARCHIVE}elasticsearch-${versionElastic}-windows-x86_64.zip
	echo "  ... 2/3 - filebeat"
	unzip -q -d ${CHEMIN_BIN} ${CHEMIN_ARCHIVE}filebeat-${versionElastic}-windows-x86_64.zip
	echo "  ... 3/3 - kibana (beaucoup plus long à s'exécuter)"
	unzip -q -d ${CHEMIN_BIN} ${CHEMIN_ARCHIVE}kibana-${versionElastic}-windows-x86_64.zip
	echo "  ... terminé"

elif [ "$1" == "ARRETER_ELAS" ] ; then
	if [ ! -f "${ELAS_PID}" ] ; then
		echo "Aucun fichier PID trouvé pour arrêter ElasticSearch"
		return
	else
		pid=`cat ${ELAS_PID}`
		printf "Arrêt de ElasticSearch par un kill du PID %s.\n" "$pid"
		taskkill //F //PID $pid
		rm ${ELAS_PID}
	fi

elif [ "$1" == "ARRETER_FB" ] ; then
	if [ ! -f "${FB_PID}" ] ; then
		echo "Aucun fichier PID trouvé pour arrêter FileBeat"
		return
	else
		pid=`cat ${FB_PID}`
		printf "Arrêt de FileBeat par un kill du PID %s.\n" "$pid"
		taskkill //F //PID $pid
		rm ${FB_PID}
	fi

elif [ "$1" == "ARRETER_KIBANA" ] ; then
	if [ ! -f "${KIBANA_PID}" ] ; then
		echo "Aucun fichier PID trouvé pour arrêter Kibana"
		return
	else
		printf "Arrêt de Kibana par un kill du PID %s.\n" "`cat ${KIBANA_PID}`"
		arreterKibana
	fi

elif [ "$1" == "ARRETER_TOUT" ] ; then
	. ./elastic.sh ARRETER_FB
	. ./elastic.sh ARRETER_KIBANA
	. ./elastic.sh ARRETER_ELAS

elif [ "$1" == "PURGE" ] ; then

	# Arret de tout
	. ./elastic.sh ARRETER_TOUT

	# Suppression des répertoires
	echo "Suppression des répertoires ${CHEMIN_LOG}, ${CHEMIN_DATA} et ${CHEMIN_TMP_ELAS}"
	rm -rf ${CHEMIN_LOG}
	rm -rf ${CHEMIN_DATA}
	rm -rf ${CHEMIN_TMP_ELAS}
	
	# Suppression des fichiers
	echo "Suppression du fichier ${ES_PATH_CONF}/elasticsearch.keystore"
	rm -f ${ES_PATH_CONF}/elasticsearch.keystore

elif [ "$1" == "DEMARRER_ELAS" ] ; then

	# Vérification de la présence du répertoire des data d'ElasticSearch et initialisation des éléments si besoin
	initialiserElastic="false"
	if [ ! -d ${CHEMIN_DATA}elasticsearch ] ; then
		initialiserElastic="true"
	fi

	# Démarrage (-d pour démarrer en arrière plan et -p pour créer un PID @see https://www.elastic.co/guide/en/elasticsearch/reference/current/starting-elasticsearch.html#_run_as_a_daemon)
	echo "Démarrage de ElasticSearch avec ES_PATH_CONF=${ES_PATH_CONF}"
	mkdir -p ${CHEMIN_LOG_ELAS}
	mkdir -p ${CHEMIN_TMP_ELAS}/elasticsearch
	${CHEMIN_BIN}elasticsearch-${versionElastic}/bin/elasticsearch.bat -d -p ${ELAS_PID} >>${ELAS_LOG_BOOT} 2>&1
	
	# Vérification du bon démarrage
	if [ ! -f "${ELAS_PID}" ] ; then
		echo "  erreur de démarrage car aucun fichier PID trouvé (${ELAS_PID})"
		return
	else
		echo "  démarré avec le PID `cat ${ELAS_PID}`"
	fi

	# Initialisation d'Elastic
	if [ $initialiserElastic == "true" ] ; then
		. ./elastic.sh INIT_ELAS
	fi

	# Confirmation de fonctionnement
	MDP_KIBANA_SYSTEM="`cat ${CHEMIN_DATA}elasticsearch/mdp-kibana_system`"
	etat="`curl -s -o /dev/null -w "%{http_code}" --user "kibana_system:${MDP_KIBANA_SYSTEM}" -X GET "http://localhost:9200/?pretty"`"
	if [ $etat == "200" ] ; then
		echo "  connexion réussie à ElasticSearch avec le compte 'kibana_system' (mdp disponible dans ${CHEMIN_DATA}elasticsearch/mdp-kibana_system)"
	else 
		echo "  échec de la connexion à ElasticSearch avec le compte 'kibana_system' (code HTTP : $etat)"
		return
	fi

elif [ "$1" == "DEMARRER_FB" ] ; then

	# Démarrage
	echo "Démarrage de FileBeat"
	mkdir -p ${CHEMIN_LOG_FB}
	export MDP_ELASTIC="`cat ${CHEMIN_DATA}elasticsearch/mdp-elastic`"
	nohup ${CHEMIN_BIN}filebeat-${versionElastic}-windows-x86_64/filebeat.exe -c ${FB_PATH_CONF}filebeat.yml >>${FB_LOG_BOOT} 2>&1 &

	# Attente et création du PID
	sleep 10
	ps | grep filebeat | sed 's/^[ ]*[0-9]*[ ]*[0-9]*[ ]*[0-9]*[ ]*//' | sed 's/ .*$//' >${FB_PID}
	
	# validation du démarrage
	if [ "" == "`cat ${FB_PID}`" ] ; then
		echo "  erreur de démarrage car le fichier PID est vide (${FB_PID})"
		return
	else
		echo "  démarré avec le PID `cat ${FB_PID}`"
	fi

elif [ "$1" == "DEMARRER_KIBANA" ] ; then

	# Vérification de la présence du répertoire des data de Kibana et initialisation des éléments si besoin
	importerConfiguration="false"
	if [ ! -d ${CHEMIN_DATA}kibana ] ; then
		importerConfiguration="true"
	fi

	# Démarrage
	echo "Démarrage de Kibana avec KBN_PATH_CONF=${KBN_PATH_CONF}"
	mkdir -p ${CHEMIN_LOG_KIBANA}
	demarrerKibana
	echo "  démarré et disponible avec le PID `cat ${KIBANA_PID}`"

	# Import des configurations
	if [ $importerConfiguration == "true" ] ; then
		. ./elastic.sh INIT_KIBANA
	fi
	
	# Logs utiles pour le développeur
	echo ""
	echo "  connexion à http://localhost:5601 avec le compte elastic // `cat ${CHEMIN_DATA}elasticsearch/mdp-elastic`"

elif [ "$1" == "INIT_ELAS" ] ; then
	echo ""
	echo "Initialisation de la sécurité minimale dans ElasticSearch (@see https://www.elastic.co/guide/en/elasticsearch/reference/8.6/security-minimal-setup.html)"
	${CHEMIN_BIN}elasticsearch-${versionElastic}/bin/elasticsearch-reset-password.bat --auto --batch --url "http://localhost:9200" -u elastic | grep "New value" | sed "s/^.*: //" >${CHEMIN_DATA}elasticsearch/mdp-elastic
	${CHEMIN_BIN}elasticsearch-${versionElastic}/bin/elasticsearch-reset-password.bat --auto --batch --url "http://localhost:9200" -u kibana_system | grep "New value" | sed "s/^.*: //" >${CHEMIN_DATA}elasticsearch/mdp-kibana_system

elif [ "$1" == "INIT_KIBANA" ] ; then
	echo ""
	echo "Import des configurations dans Elasticsearch après le démarrage de Kibana"
	MDP_ELASTIC="`cat ${CHEMIN_DATA}elasticsearch/mdp-elastic`"
	curl -s -u elastic:${MDP_ELASTIC} -X PUT -H "Content-Type: application/json" -d '{"index_patterns" : ["logs-*"],"priority" : 1,"template": {"settings" : {"number_of_shards" : 1}}}' http://localhost:9200/_index_template/logs
	echo ""
	etat="`curl -s -u elastic:${MDP_ELASTIC} -o ${CHEMIN_LOG_KIBANA}/import.log -w "%{http_code}" -X POST -H "kbn-xsrf: true" -F "file=@./services-cloud/service-elastic/kibana/configuration.ndjson" http://localhost:5601/api/saved_objects/_import?overwrite=true`"
	if [ $etat == "200" ] ; then
		echo "  import réalisé (détails ci-dessous)"
		cat ${CHEMIN_LOG_KIBANA}/import.log
	else 
		echo "  échec de l'import (code HTTP : $etat)"
		return
	fi

elif [ "$1" == "DEMARRER_TOUT" ] ; then
	echo ""
	. ./elastic.sh ARRETER_TOUT

	echo ""
	. ./elastic.sh DEMARRER_ELAS

	echo ""
	echo "Attente de 20 secondes pour laisser démarrer ElasticSearch avant de démarrer KIBANA"
	sleep 20
	echo ""
	. ./elastic.sh DEMARRER_KIBANA

	echo ""
	echo "Attente de 20 secondes pour laisser démarrer KIBANA avant de démarrer FILEBEAT"
	sleep 20
	echo ""
	. ./elastic.sh DEMARRER_FB

#################################
# Documentation si erreur d'appel
#################################
else
	printf 'Option "%s" invalide. Les options sont :\n' $1
	printf '    DESINSTALLATION ou INSTALLATION\n'
	printf '    ARRETER_ELAS, ARRETER_KIBANA, ARRETER_FB ou ARRETER_TOUT\n'
	printf '    DEMARRER_ELAS, DEMARRER_KIBANA, DEMARRER_FB ou DEMARRER_TOUT\n'
	printf '    PURGE, INIT_ELAS, INIT_KIBANA\n'
	return
fi
