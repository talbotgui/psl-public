#!/usr/bin/env bash

# Chargement du fichier des variables
eval "$(gpg -dq variablesPourScripts.properties.gpg)"

# Debut
echo ""
echo "Démarrage des processus $*"

# Pour chaque paramètre, on tente de démarrer un processus
for p in "$@"; do
	echo ""
	. ./demarrer.sh "$p"
done

# fin
echo "fin"