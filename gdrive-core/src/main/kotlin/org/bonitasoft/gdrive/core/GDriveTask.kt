package org.bonitasoft.gdrive.core

import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials

abstract class GDriveTask(private val logger: Logger, private val googleCredentials: String) {


    companion object {
        val FOLDER_MIMETYPE = "application/vnd.google-apps.folder"
    }

    fun execute() {

        logger.info("Create client from credentials")
        val credentials = GoogleCredentials.fromStream(googleCredentials.byteInputStream())
                .createScoped(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

        val drive = Drive.Builder(ApacheHttpTransport(), JacksonFactory(), requestInitializer).setApplicationName("GDrive Upload").build()
        doExecute(drive)

    }
    abstract fun doExecute(drive: Drive)

    protected fun retrieveFolder(drive: Drive, folderId: String): File {
        logger.debug("Retrieving destination folder with id $folderId")
        val destinationFolder = drive.files().get(folderId).setSupportsAllDrives(true).execute()
        if (destinationFolder.mimeType != FOLDER_MIMETYPE) {
            throw RuntimeException("The id $folderId given as the destination folder is not a folder but ${destinationFolder.mimeType}")
        }
        return destinationFolder
    }

    protected fun createFolder(drive: Drive, folderName: String, destinationFolder: File): File {
        var folder = File().apply {
            name = folderName
            mimeType = FOLDER_MIMETYPE
            parents = mutableListOf(destinationFolder.id)
        }
        folder = drive.files().create(folder).setFields("id").setSupportsAllDrives(true).execute()
        return folder
    }

    protected fun uploadFile(drive: Drive, file: java.io.File, fileName: String, destinationFolder: File): File {
        return drive.files().create(File().apply {
            name = fileName
            parents = mutableListOf(destinationFolder.id)
        }, FileContent(null, file))
                .setFields("id").setSupportsAllDrives(true).execute()
    }

}