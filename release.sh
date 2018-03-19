#!/usr/bin/env bash

###############################################################################
# # Script for releasing the Entwine Functional Library

# Requirements:
# - Java 8
# - git
# - Access to GitHub [Entwine Functional Project](https://github.com/entwinemedia/functional)
# - The following environmental variables exported:
#   - Required
    #   - export ARTIFACT_REPOSITORY_URL="https://<URL>"
    #   - export ARTIFACT_REPOSITORY_USER="<USERNAME>"
    #   - export ARTIFACT_REPOSITORY_PASSWORD="<PASSWORD>"
    #   - export ARTIFACT_REPOSITORY_SNAPSHOT_URL="https://<SNAPSHOT_URL>"
    #   - export ARTIFACT_REPOSITORY_RELEASE_URL="https://<RELEASE_URL>"
    #   - export EMAIL_ADDRESS="<your@email.com>"
    #   - export FIRST_NAME_LAST_NAME="<Firstname Lastname>"
#   - Optional if running in interactive mode, otherwise must be exported
#       - export GITHUB_ACCESS_TOKEN="<TOKEN>"

# - square brackets [optional option]
# - angle brackets <required argument>
# - curly braces {default values}
# - parenthesis (miscellaneous info)

# Usage:
#    ./release.sh <release_version>
###############################################################################

# Usage instructions
USAGE="Usage: `basename $0` <release_version>"

if [ -z "$1" ]
  then
    echo "No version supplied"
    echo $USAGE
    exit 1
fi

RELEASE_VERSION=$1
GITHUB_PROJECT_URL=https://github.com/entwinemedia/functional

if [ -z "$GITHUB_ACCESS_TOKEN" ]
  then
    echo "Enter GitHub access token for $GITHUB_PROJECT_URL:"
    read -t 10 -s GITHUB_ACCESS_TOKEN || { echo "Error: GitHub access token not supplied."; exit 1; }
fi

if [ -z "$FIRST_NAME_LAST_NAME" ]
  then
    echo "Enter your first name and last name:"
    read -t 10 FIRST_NAME_LAST_NAME || { echo "Error: First name and last name not supplied."; exit 1; }
fi

if [ -z "$EMAIL_ADDRESS" ]
  then
    echo "Enter your email address:"
    read -t 10 EMAIL_ADDRESS || { echo "Error: Email address not supplied."; exit 1; }
fi

echo "###############################################################"
echo "# Starting Entwine Functional version $RELEASE_VERSION release!"
echo "###############################################################"


git checkout master &&
git pull &&

if [ $(git tag -l "$RELEASE_VERSION") ]
    then
        echo "Version $RELEASE_VERSION already exists. Release aborted!"
        exit 1
fi

./mvnw versions:set -DnewVersion=$RELEASE_VERSION versions:commit &&

git config user.name $FIRST_NAME_LAST_NAME
git config user.email $EMAIL_ADDRESS
git config push.default simple
git remote set-url origin "https://$GITHUB_ACCESS_TOKEN@github.com/entwinemedia/functional.git" &&

git add pom.xml &&
git commit -m "release version $RELEASE_VERSION" &&
git push origin &&
git tag -a $RELEASE_VERSION -m "release $RELEASE_VERSION" &&
git push origin $RELEASE_VERSION &&

./mvnw --settings=settings.xml deploy -Pdeploy &&

echo "####################################################################" &&
echo "# Entwine Functional version $RELEASE_VERSION successfully released!" &&
echo "####################################################################"