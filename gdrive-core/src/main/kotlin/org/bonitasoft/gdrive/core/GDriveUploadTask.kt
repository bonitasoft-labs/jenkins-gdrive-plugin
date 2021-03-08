package org.bonitasoft.gdrive.core

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GDriveUploadTask(googleCredentials: String,
					   private val source: String,
					   private val destinationId: String,
					   private val logger: Logger,
					   private val renameTo: String) : GDriveTask(logger, googleCredentials) {

	override fun doExecute(drive: Drive) {

		val destinationFolder = retrieveFolder(drive, destinationId)

		logger.info("Will upload $source inside Drive folder ${destinationFolder.name} with id ${destinationFolder.id}")
		copy(drive, java.io.File(source), destinationFolder, renameTo)
		logger.info("Uploaded all files to ${destinationFolder.id}")

		return
	}

	private fun copy(drive: Drive, file: java.io.File, destinationFolder: File, renameTo: String = "") {
		logger.debug("Processing ${file.absolutePath}")
		val fileName = renameTo.trim().ifEmpty { file.name }
		if (fileName != file.name) {
			logger.info("'${file.name}' will be renamed to '$fileName'")
		}
		if (!file.exists()) {
			throw RuntimeException("The source file does not exists: ${file.absolutePath}");
		}
		if (file.isDirectory) {
			val newFolder = createFolder(drive, fileName, destinationFolder)
			logger.debug("Created folder $fileName with id ${newFolder.id}")
			file.listFiles().forEach { child ->
				copy(drive, child, newFolder)
			}
		} else {
			val uploadFile = uploadFile(drive, file, fileName, destinationFolder)
			logger.info("Uploaded file ${uploadFile.name} with id ${uploadFile.id}")
		}
	}

}
