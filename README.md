# functional
A library for functional programming in Java.

## Release 

1. Export the following environmental variables:

    Required
    
    ```
    export ARTIFACT_REPOSITORY_URL="https://<URL>"
    export ARTIFACT_REPOSITORY_USER="<USERNAME>"
    export ARTIFACT_REPOSITORY_PASSWORD="<PASSWORD>"
    export ARTIFACT_REPOSITORY_SNAPSHOT_URL="https://<SNAPSHOT_URL>"
    export ARTIFACT_REPOSITORY_RELEASE_URL="https://<RELEASE_URL>"
    ```
    
    Optional
    
    ```
    export GITHUB_ACCESS_TOKEN="<TOKEN>"
    ```
    
    See [Creating a personal access token for the command line](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/)
    for obtaining the GITHUB_ACCESS_TOKEN value.

2. Run the release.sh script: 
    ```
    ./release.sh <release_version>
    ```