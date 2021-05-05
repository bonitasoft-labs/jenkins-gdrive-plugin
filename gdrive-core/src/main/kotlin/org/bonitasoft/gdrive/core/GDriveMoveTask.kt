package org.bonitasoft.gdrive.core

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GDriveMoveTask(googleCredentials: String,
                     private val sourceId: String,
                     private val elementName: String,
                     private val destinationId: String,
                     private val logger: Logger,
                     private val renameTo: String) : GDriveTask(logger, googleCredentials) {

    override fun doExecute(drive: Drive) {
        val execute  = drive.files().list()
                .setQ("'$sourceId' in parents and name = '$elementName' and trashed = false")
                .setFields("files(id, name, parents)")
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setOrderBy("createdTime desc")
                .execute()

        val sourceElement = execute.files.first() ?: throw Exception("No folder $elementName found $sourceId folder")

        drive.files().update(sourceElement.id, File().apply { name = renameTo })
                .setAddParents(destinationId)
                .setRemoveParents(sourceElement.parents[0])
                .setSupportsAllDrives(true)
                .execute()

        logger.info("moved $sourceId to ${destinationId}")
        return
    }

}