# Google drive upload plugin

This plugin performs upload of directory to a given drive folder

# Configuration

There is only the pipeline function available.

Configure as follow where 

* `googleCredentials` contains the private key content to access the API
* `toCopy` is the path on the filesystem of the directory to upload on google drive
* `destinationFolderId` is the id of the folder to upload that directory into (e.g. a team shared drive folder)


```groovy
pipeline {
    agent any
    stages {
        stage('prepare file ') {
            steps {
                sh "echo 'someContent' > toUpload.txt"
            }
        }
        stage("upload to gdrive") {
            steps {
                withCredentials([string(credentialsId: 'gdrive', variable: 'GDRIVE_CREDENTIALS')]) {
                    gdriveUpload(
                        googleCredentials: GDRIVE_CREDENTIALS,
                        source: "toUpload.txt",
                        destinationId: "14_Xpzuld0lGyg7HgZojwNsJsITW5jTh9",
                        renameTo: "uploaded.txt"
                    )
                }
            }
        }
        stage("move specificFile") {
            steps {
                withCredentials([string(credentialsId: 'gdrive', variable: 'GDRIVE_CREDENTIALS')]) {
                    gdriveMove(
                        googleCredentials: GDRIVE_CREDENTIALS,
                        sourceId: "k543jkh32HgZojwNsJsITW5jThdsfds",
                        destinationId: "14_Xpzuld0lGyg7HgZojwNsJsITW5jTh9",
                        renameTo: "moved folder"
                    )
                }
            }
        }
    }
}
```


# Build

`gw :plugins:gdrive-upload:server` to run jenkins with that plugin

`gw :plugins:gdrive-upload:jpi` to produce the plugin

The compilation uses `kapt` for annotation processing along with `net.java.sezpoz:sezpoz` to process `@Extension` annotations


