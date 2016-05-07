#!/bin/bash

# This script does 3 things:
# 1. Removes all 'exited' containers
# 2. Removes all untagged images
# 3. Removes all unused images

# TODO: Do not remove 'scratch' and 'ubuntu:14.04' images (add them to exclusions list)
declare -a EXCLUDE_IMAGES=(
  'ubuntu:14.04',
  'docker.dev.confyrm.com:scratch'
)

[ `whoami` = root ] || { sudo "$0" "$@"; exit $?; }  # Ensure that user uses 'sudo' or runs this script under root

set -e

EXITED_CONTAINERS=`docker ps -a -f status=exited -q`
if [ -n "$EXITED_CONTAINERS" ]; then
   echo "Removing exited Docker containers: [ $EXITED_CONTAINERS ]"
   docker rm -v $EXITED_CONTAINERS
fi

UNTAGGED_IMAGES=`docker images --no-trunc | grep "<none>" | awk '{print $3}'`
echo "Untagged images: $UNTAGGED_IMAGES"
if [ -n "$UNTAGGED_IMAGES" ]; then
   echo "Removing untagged Docker images ..."
   docker rmi $UNTAGGED_IMAGES
fi

echo "Removing unused docker images ..."
images=`docker images | tail -n +2 | awk '{print $1":"$2}'`
echo -e "Found Docker images:\n$images"

containers=`docker ps -a | tail -n +2 | awk '{print $2}'`
echo -e "Found Docker containers:\n$containers"

containers_reg=" ${containers[*]} "
remove=()

for item in ${images[@]}; do
if [[ ! $containers_reg =~ " $item " ]]; then
remove+=($item)
fi
done

IMAGES_TO_REMOVE=" ${remove[*]} "

if [ -n "$IMAGES_TO_REMOVE" ]; then
   echo -e "Removing unused Docker images:\n$IMAGES_TO_REMOVE ..."
   docker rmi -f $IMAGES_TO_REMOVE
fi
echo "Done"
