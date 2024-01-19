package org.bonitasoft.gdrive.core

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.security.MessageDigest
import java.io.BufferedWriter
import java.io.FileWriter

class GDriveUploadTask(googleCredentials: String,
					   private val source: String,
					   private val destinationId: String,
					   private val logger: Logger,
					   private val renameTo: String,
					   private val uploadChecksum: Boolean) : GDriveTask(logger, googleCredentials) {

	override fun doExecute(drive: Drive) {

		val destinationFolder = retrieveFolder(drive, destinationId)

		logger.info("Will upload $source inside Drive folder ${destinationFolder.name} with id ${destinationFolder.id}")
		copy(drive, java.io.File(source), destinationFolder, renameTo, uploadChecksum)
		logger.info("Uploaded all files to ${destinationFolder.id}")

		return
	}

	private fun copy(drive: Drive, file: java.io.File, destinationFolder: File, renameTo: String = "", uploadChecksum: Boolean) {
		logger.debug("Processing ${file.absolutePath}")
		val fileName = renameTo.trim().ifEmpty { file.name }
		if (fileName != file.name) {
			logger.info("'${file.name}' will be renamed to '$fileName'")
		}
		if (!file.exists()) {
			throw RuntimeException("The source file does not exists: ${file.absolutePath}");
		}
		if (file.isDirectory) {
			val newFolder = createFolder(drive, fileName, destinationFolder.id)
			logger.debug("Created folder $fileName with id ${newFolder.id}")
			file.listFiles().forEach { child ->
				copy(drive, child, newFolder, "", uploadChecksum)
			}
		} else {
			val uploadFile = uploadFile(drive, file, fileName, destinationFolder)
			logger.info("Uploaded file ${uploadFile.name} with id ${uploadFile.id}")
			if(uploadChecksum){
				val checksumFile = java.io.File.createTempFile(fileName, ".sha256")
				writeTextToFile(calculateSHA256(file), checksumFile.absolutePath)
				val uploadedCheckumFile = uploadFile(drive, checksumFile, fileName + ".sha256", destinationFolder)
				logger.info("Uploaded file ${uploadedCheckumFile.name} with id ${uploadedCheckumFile.id}")
			}
		}
	}

	fun calculateSHA256(file: java.io.File): String {
		val bytes = file.readBytes()
		val digest = MessageDigest.getInstance("SHA-256")
		val hashBytes = digest.digest(bytes)
		return hashBytes.joinToString("") { "%02x".format(it) }
	}

	fun writeTextToFile(text: String, filePath: String) {
		val file = java.io.File(filePath)
	
		try {
			BufferedWriter(FileWriter(file)).use { writer ->
				writer.write(text)
			}
		} catch (e: Exception) {
			logger.error("An error occurred while writing to the file: $e")
		}
	}

}
