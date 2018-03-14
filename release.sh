#!/usr/bin/env bash

# Usage instructions
USAGE="Usage: `basename $0` <release_version> <build_server_url>"

if [ -z "$1" ]
  then
    echo "No version supplied"
    echo $USAGE
    exit 1
fi

if [ -z "$2" ]
  then
    echo "No build server URL supplied"
    echo $USAGE
    exit 1
fi

RELEASE_VERSION=$1
BUILD_SERVER_URL=$2

echo "###############################################################"
echo "# Starting Entwine Functional version $RELEASE_VERSION release!"
echo "###############################################################"

echo "Enter username for $BUILD_SERVER_URL:"
read -s USERNAME

echo "Enter password for $BUILD_SERVER_URL:"
read -s PASSWORD

echo "Enter token for the Functional Library build on $BUILD_SERVER_URL:"
read -s TOKEN

git checkout master &&
git pull &&
./mvnw versions:set -DnewVersion=$RELEASE_VERSION versions:commit &&
git add pom.xml &&
git commit -m "release version $RELEASE_VERSION" &&
git push origin &&
git tag -a $RELEASE_VERSION -m "release $RELEASE_VERSION" &&
git push origin $RELEASE_VERSION &&

curl -X POST --user $USERNAME:$PASSWORD "$BUILD_SERVER_URL/job/Entwine%20-%20Functional%20-%20Release/buildWithParameters?token=$TOKEN&cause=Build%20Release%20$RELEASE_VERSION%20and%20Publish%20to%20Remote%20Repository" &&

echo "####################################################################"
echo "# Entwine Functional version $RELEASE_VERSION successfully released!"
echo "####################################################################"