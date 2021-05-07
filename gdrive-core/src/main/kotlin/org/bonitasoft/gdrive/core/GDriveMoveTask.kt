package org.bonitasoft.gdrive.core

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GDriveMoveTask(googleCredentials: String,
                     private val sourceId: String,
                     private val elementName: String,
                     private val destinationParentFolderId: String,
                     private val destinationFolderName: String,
                     private val logger: Logger,
                     private val renameTo: String) : GDriveTask(logger, googleCredentials) {

    override fun doExecute(drive: Drive) {
        val execute = drive.files().list()
                .setQ("'$sourceId' in parents and name = '$elementName' and trashed = false")
                .setFields("files(id, name, parents)")
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setOrderBy("createdTime desc")
                .execute()

        val sourceElement = execute.files.firstOrNull() ?: throw Exception("No folder $elementName found $sourceId folder")

        var parentFolderId =  destinationParentFolderId

        if(!destinationFolderName.isBlank()) {
            parentFolderId = (drive.files().list()
                    .setQ("'$destinationParentFolderId' in parents and name = '$destinationFolderName' and trashed = false and  mimeType = '${FOLDER_MIMETYPE}'")
                    .setFields("files(id, name, parents)")
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute().files.firstOrNull()
                    ?: createFolder(drive, destinationFolderName, destinationParentFolderId)).id
        }
        drive.files().update(sourceElement.id, File().apply { name = renameTo })
                .setAddParents(parentFolderId)
                .setRemoveParents(sourceElement.parents[0])
                .setSupportsAllDrives(true)
                .execute()
        logger.info("moved $sourceId to ${destinationParentFolderId}")
        return
    }

}