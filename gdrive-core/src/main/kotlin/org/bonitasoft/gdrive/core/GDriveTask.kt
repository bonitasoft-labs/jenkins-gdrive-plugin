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
import java.security.MessageDigest

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

    protected fun createFolder(drive: Drive, folderName: String, parentFolderId: String): File {
        var folder = File().apply {
            name = folderName
            mimeType = FOLDER_MIMETYPE
            parents = mutableListOf(parentFolderId)
        }
        // Check if the folder already exists
        val query = "mimeType='$FOLDER_MIMETYPE' and name='$folderName' and '$parentFolderId' in parents and trashed=false";
        val result = drive.files().list().setQ(query)
                        .setSupportsAllDrives(true)
                        .setIncludeItemsFromAllDrives(true)
                        .execute()
        if(!result.getFiles().isEmpty()){
            return result.getFiles().get(0)
        }
        folder = drive.files().create(folder).setFields("id").setSupportsAllDrives(true).execute()
        return folder
    }

    protected fun uploadFile(drive: Drive, file: java.io.File, fileName: String, destinationFolder: File): File {
         // Check if the file already exists
         val query = "name='$fileName' and '${destinationFolder.id}' in parents and trashed=false";
         val result = drive.files().list().setQ(query)
                         .setSupportsAllDrives(true)
                         .setIncludeItemsFromAllDrives(true)
                         .execute()
         if(!result.getFiles().isEmpty()){ // Update it
            val existingFile = result.getFiles().get(0)
            val uploadedFile = drive.files().update(existingFile.id, File().apply {
                name = fileName
            }, FileContent(null, file))
                    .setFields("id,name,md5Checksum").setSupportsAllDrives(true).execute()
            validateMd5Checksum(uploadedFile.md5Checksum, calculateMd5Checksum(file))
            return uploadedFile
         }else{ // Create it
            val uploadedFile = drive.files().create(File().apply {
                name = fileName
                parents = mutableListOf(destinationFolder.id)
            }, FileContent(null, file))
                    .setFields("id,name,md5Checksum").setSupportsAllDrives(true).execute()
            validateMd5Checksum(uploadedFile.md5Checksum, calculateMd5Checksum(file))
            return uploadedFile
         }
    }

    fun validateMd5Checksum(uploadedFileChecksum : String, originalFileChecksum : String){
        if(uploadedFileChecksum != originalFileChecksum){
            throw RuntimeException("Uploaded file MD5 checksum does not match !");
        }
    }

    fun calculateMd5Checksum(file: java.io.File): String {
        val bytes = file.readBytes()
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

}