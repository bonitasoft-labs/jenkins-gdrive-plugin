package org.bonitasoft.gdrive.core

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GDriveMoveTask(googleCredentials: String,
                     private val sourceId: String,
                     private val destinationId: String,
                     private val logger: Logger,
                     private val renameTo: String) : GDriveTask(logger, googleCredentials) {

    override fun doExecute(drive: Drive) {

        val sourceElement = drive.files().get(sourceId).setFields("parents").setSupportsAllDrives(true).execute()

        logger.info("source parents= ${sourceElement.parents}")
        drive.files().update(sourceId, File().apply { name = renameTo })
                .setRemoveParents(sourceElement.parents.joinToString(","))
                .setAddParents(destinationId)
                .setSupportsAllDrives(true).execute()
        logger.info("moved $sourceId to ${destinationId}")

        return
    }

}