package com.bonitasoft.jenkins.pipeline.gdrive.upload

import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes.DRIVE
import com.google.api.services.drive.DriveScopes.DRIVE_FILE
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import hudson.AbortException
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution
import java.io.PrintStream
import javax.activation.MimetypesFileTypeMap


class GDriveUploadStepExecution(
        val googleCredentials: String,
        val toCopy: String,
        val destinationFolderId: String,
        context: StepContext,
        val newOwnerEmail: String? = null) : SynchronousNonBlockingStepExecution<Void>(context) {

    companion object FileUtils {
        private val FOLDER_MIMETYPE = "application/vnd.google-apps.folder"

        val mimeTypes = MimetypesFileTypeMap()
    }

    override fun run(): Void? {
        val logger = context.get(TaskListener::class.java)?.logger!!
        try {
            logger.println("Create client from credentials")
            val credentials = GoogleCredentials.fromStream(googleCredentials.byteInputStream())
                    .createScoped(DRIVE, DRIVE_FILE)
            val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

            val drive = Drive.Builder(ApacheHttpTransport(), JacksonFactory(), requestInitializer).setApplicationName("GDrive Upload").build()

            logger.println("Retrieving destination folder with id $destinationFolderId")
            val destinationFolder = retrieveFolder(drive)



            logger.println("Will upload $toCopy inside Drive folder ${destinationFolder.name} with id ${destinationFolder.id}")
            copy(logger, drive, java.io.File(toCopy), destinationFolder)
            logger.println("Uploaded all files to ${destinationFolder.id}")
            return null
        } catch (e: Throwable) {
            e.printStackTrace()
            throw AbortException("Exception: ${e::class.java.name} ${e.message}")
        }
    }

    private fun copy(logger: PrintStream, drive: Drive, file: java.io.File, destinationFolder: File) {
        logger.println("Processing ${file.absolutePath}")
        if (file.isDirectory) {
            val newFolder = createFolder(drive, file.name, destinationFolder)
            logger.println("created folder ${newFolder.name} with id ${newFolder.id}")
            setOwner(logger, drive, newFolder)
            file.listFiles().forEach { child ->
                copy(logger, drive, child, newFolder)
            }
        } else {
            val uploadFile = uploadFile(drive, file, destinationFolder)
            setOwner(logger, drive, uploadFile)
            logger.println("Uploaded file folder ${uploadFile.name} with id ${uploadFile.id}")
        }
    }

    private fun copyPermissions(logger: PrintStream, drive: Drive, from: File, to: File) {
        val permissions = drive.permissions().list(from.id).execute()

        permissions.permissions.forEach { permission ->
            logger.println("Adding permission ${permission}")
            if (permission.emailAddress != null) {
                drive.permissions().create(to.id, permission.clone().apply {
                    id = null
                }).execute()
            }
        }
    }

    private fun retrieveFolder(drive: Drive): File {
        val destinationFolder = drive.files().get(destinationFolderId).setSupportsAllDrives(true).execute()
        if (!destinationFolder.mimeType.equals(FOLDER_MIMETYPE)) {
            throw AbortException("The id $destinationFolderId given as the destination folder is not a folder but ${destinationFolder.mimeType}")
        }
        return destinationFolder
    }

    private fun uploadFile(drive: Drive, file: java.io.File, destinationFolder: File): File {
        val mimeType = mimeTypes.getContentType(file)
        return drive.files().create(File().apply {
            name = file.name
            this.mimeType = mimeType
            parents = mutableListOf(destinationFolder.id)

        }, FileContent(mimeType, file))
                .setFields("id").setSupportsAllDrives(true).execute()
    }


    private fun createFolder(drive: Drive, folderName: String, destinationFolder: File): File {
        var folder = File().apply {
            name = folderName
            mimeType = FOLDER_MIMETYPE
            parents = mutableListOf(destinationFolder.id)
        }
        folder = drive.files().create(folder).setFields("id").setSupportsAllDrives(true).execute()
        return folder
    }

    private fun setOwner(logger :PrintStream, drive: Drive, folder: File) {
        if (newOwnerEmail == null) {
            return
        }
        logger.println("Setting owner of the document ${folder.id} to $newOwnerEmail")
        drive.permissions().create(folder.id, Permission().apply {
            emailAddress = newOwnerEmail
            type = "user"
            role = "owner"
        }).setTransferOwnership(true).execute()
    }
}
