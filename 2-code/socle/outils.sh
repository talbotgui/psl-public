#!/usr/bin/env bash

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

###############################################################################################################################################
#
# Script utilitaire pour y capitaliser des commandes utiles sur le poste du développeur (suppression de fichiers, modifications en masse, ...).
#
# A appeler, avec la commande GitShell, avec la syntaxe ". ./outil.sh PURGE LOG"
#
###############################################################################################################################################

# Activation du proxy
if [ "$1" == "PROXY" ] && [ "$2" == "TRUE" ] ; then
	echo "Activation du proxy dans les configurations"
	sed -Ei "s/^#(.*proxy.*)$/\1/g" ./socle-communtest/src/main/resources/config/socle-*.properties

# Désactivation du proxy
elif [ "$1" == "PROXY" ] && [ "$2" != "TRUE" ] ; then
	echo "Désactivation du proxy dans les configurations"
	sed -Ei "s/^([^#]*proxy.*)$/#\1/g" ./socle-communtest/src/main/resources/config/socle-*.properties

# Nettoyage du répertoire LOG
elif [ "$1" == "PURGE" ] && [ "$2" == "LOG" ] ; then
	echo "Suppression du contenu du répertoire ${repertoirePidEtLog}"
	if ls ${repertoirePidEtLog}pid* 1>/dev/null 2>&1 ; then
		echo "  Suppression des logs impossibles car un (ou plusieurs) fichier PID existe encore"
	else
		rm ${repertoirePidEtLog}*.log 1>/dev/null 2>&1
		rm ${repertoirePidEtLog}*.boot 1>/dev/null 2>&1
		rm ${repertoirePidEtLog}.archive/* 1>/dev/null 2>&1
	fi

# Nettoyage du répertoire CACHE
elif [ "$1" == "PURGE" ] && [ "$2" == "CACHE" ] ; then
	echo "Suppression du contenu des répertoires .cache et .analyse"
	if ls ${repertoirePidEtLog}pid* 1>/dev/null 2>&1 ; then
		echo "  Suppression des logs impossibles car un (ou plusieurs) fichier PID existe encore"
	else
		rm ./.cache/* 1>/dev/null 2>&1
		rm ./.analyse/* 1>/dev/null 2>&1
	fi

# Initialisation du CACHE SIRET
elif [ "$1" == "INIT" ] && [ "$2" == "CACHE" ] ; then
	echo "Initialisation du cache des SIRETs (téléchargement/filtrage/stockage dans le cache)"
	
	echo "  Téléchargement du fichier complet (peut être très long car ZIP de 1,8Go donc on laisse les traces de CURL)"
	rm ./.cache/zipSiretComplet.zip 1>/dev/null 2>&1
	curl -L -o ./.cache/zipSiretComplet.zip https://www.data.gouv.fr/fr/datasets/r/0651fb76-bcf3-4f6a-a38d-bc04fa708576
	
	echo "  Extraction de l'archive (peut être long car 6Go de CSV)"
	rm ./.cache/StockEtablissement_utf8.csv 1>/dev/null 2>&1
	unzip -q ./.cache/zipSiretComplet.zip -d ./.cache
	rm ./.cache/zipSiretComplet.zip 1>/dev/null 2>&1
	
	echo "  Filtrage du fichier pour ne garder que les lignes utiles au micro-service REF"
	cat ./.cache/StockEtablissement_utf8.csv | grep ",A,MAIRIE" >./.cache/StockEtablissement_filtre.csv
	rm ./.cache/StockEtablissement_utf8.csv 1>/dev/null 2>&1
	
	echo "  Initialisation du cache dans socle/.cache"
	rm ./.cache/referentiel-dataGouv-siret 1>/dev/null 2>&1
	jar -Mcvf ./.cache/referentiel-dataGouv-siret -C .cache StockEtablissement_filtre.csv  1>/dev/null
	rm ./.cache/StockEtablissement_filtre.csv 1>/dev/null 2>&1

	echo "  Copie du cache dans socle/socle-referentiel/.cache"
	rm ./socle-referentiel/.cache/referentiel-dataGouv-siret 1>/dev/null 2>&1
	cp ./.cache/referentiel-dataGouv-siret ./socle-referentiel/.cache/referentiel-dataGouv-siret


# Nettoyage du répertoire des données de MongoDB
elif [ "$1" == "PURGE" ] && [ "$2" == "DB" ] ; then
	echo "Suppression du contenu du répertoire des données de MongoDB"
	if [ -e ${fichierPidNosql} ] ; then
		echo "  Suppression des logs impossibles car la base de données NoSQL fonctionne (fichier PID présent)"
	else
		rm -rf ./.embedmongo/data 1>/dev/null 2>&1
	fi

# Lister les processus JAVA
elif [ "$1" == "LIVRAISON" ] ; then
	echo "Création du répertoire ${livraison} avec tous les livrables applicatifs"
	echo ""

	if [ -d ${livraison} ] ; then
		echo "   purge du répertoire existant"
		rm -rf ${livraison}
	fi

	echo "   création du répertoire"
	mkdir ${livraison}
	mkdir ${livraison}/redis-dependencies
	mkdir ${livraison}/nosql-dependencies

	echo "   copie des livrables"
	cp ${cheminExecutableServiceAdmin} ${livraison}/
	cp ${cheminExecutableServiceConfig} ${livraison}/
	cp ${cheminExecutableServiceGateway} ${livraison}/
	cp ${cheminExecutableServiceRedis} ${livraison}/
	cp ./services-cloud/service-redis/target/dependency/* ${livraison}/redis-dependencies/
	cp ${cheminExecutableServiceNosql} ${livraison}/
	cp ./services-cloud/service-nosql/target/dependency/* ${livraison}/nosql-dependencies/
	cp ${cheminExecutableServiceRegistre} ${livraison}/
	cp ${cheminExecutableSocleDbbrouillon} ${livraison}/
	cp ${cheminExecutableSocleDbconfiguration} ${livraison}/
	cp ${cheminExecutableSocleDbdocument} ${livraison}/
	cp ${cheminExecutableSocleDbnotification} ${livraison}/
	cp ${cheminExecutableSocleReferentiel} ${livraison}/
	cp ${cheminExecutableSocleReferentielExterne} ${livraison}/
	cp ${cheminExecutableSocleSecurite} ${livraison}/
	cp ${cheminExecutableSocleSoumission} ${livraison}/
	cp ${cheminExecutableSocleTransfert} ${livraison}/
	
	echo "   copie des scripts"
	cp *.sh ${livraison}/
	cp *.properties ${livraison}/
	
	
# Lister les processus JAVA
# Le processus socle-transfert n'est pas dans les compteurs car il n'est pas actif dans le script demarrerTout.sh
elif [ "$1" == "PS" ] ; then
	echo "Liste des fichiers PID de service (dans ${repertoirePidEtLog}) :"
	echo ""
	ls ${repertoirePidEtLog}*service*.pid 2>/dev/null
	echo "`ls ${repertoirePidEtLog}*service*.pid 2>/dev/null | wc -l`/7 PID (nosql peut manquer)"
	echo ""
	echo "Liste des fichiers PID de micro-service sans MongoDB :"
	ls ${repertoirePidEtLog}*.pid 2>/dev/null | grep -vE "service|db|adminpsl"
	echo "`ls ${repertoirePidEtLog}*.pid 2>/dev/null | grep -vE "service|db|adminpsl" 2>/dev/null | wc -l`/5 PID"
	echo ""
	echo "Liste des fichiers PID de micro-service avec MongoDB :"
	ls ${repertoirePidEtLog}*db*.pid 2>/dev/null
	echo "`ls ${repertoirePidEtLog}*db*.pid 2>/dev/null | wc -l`/4 PID"
	echo ""
	echo "Liste des fichiers PID de l'adminPSL :"
	ls ${repertoirePidEtLog}*.pid 2>/dev/null | grep "adminpsl"
	echo "`ls ${repertoirePidEtLog}*.pid 2>/dev/null | grep "adminpsl" 2>/dev/null | wc -l`/1 PID"
	
	echo ""
	echo "Liste des processus JAVA/MongoDB (au besoin : taskkill //F //PID ...) :"
	liste=`tasklist //v | grep -E "java.exe|mongod|redis" 2>/dev/null`
	echo "$liste"
	echo "`wc -l <<<"$liste"`/19 processus (17 java + 1 mongo + 1 redis) (/!\ 1/19 s'affiche quand aucun processus n'est démarré)"

else 
	printf 'Parametres "%s" invalides. Les options possibles sont "PROXY TRUE", "PROXY FALSE", "PURGE LOG", "PURGE CACHE", "PURGE DB", "LIVRAISON", et "PS".' "$1 $2"
fi
