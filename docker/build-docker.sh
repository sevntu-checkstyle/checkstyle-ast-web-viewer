#!/bin/bash

set -e

BASEDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cd "$BASEDIR/.."
mvn clean install -DskipTests

cp $BASEDIR/../target/*.war "$BASEDIR/ast-web-viewer.war"

sudo docker build -t checkstyle/ast-web-viewer .

echo "Done. To execute the application, run:"
echo "docker run --rm --net host checkstyle/ast-web-viewer"
