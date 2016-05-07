#!/bin/bash

APP=ast-web-viewer
BASEDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export DISPLAY_SKIPPED_HOSTS=false

time ansible-playbook -i $BASEDIR/inventory/checkstyle_host $BASEDIR/main.yml -u root --tags $APP "$@"
