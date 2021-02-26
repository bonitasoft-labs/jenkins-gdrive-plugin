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

class GDriveUploadTask(private val googleCredentials: String,
					   private val source: String,
					   private val destinationId: String,
					   private val logger: Logger,
					   private val renameTo: String) {

	companion object FileUtils {
		private val FOLDER_MIMETYPE = "application/vnd.google-apps.folder"
	}

	fun execute() {
		logger.info("Create client from credentials")
		val credentials = GoogleCredentials.fromStream(googleCredentials.byteInputStream())
				.createScoped(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
		val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

		val drive = Drive.Builder(ApacheHttpTransport(), JacksonFactory(), requestInitializer).setApplicationName("GDrive Upload").build()

		logger.debug("Retrieving destination folder with id $destinationId")
		val destinationFolder = retrieveFolder(drive)



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

	private fun retrieveFolder(drive: Drive): File {
		val destinationFolder = drive.files().get(destinationId).setSupportsAllDrives(true).execute()
		if (!destinationFolder.mimeType.equals(FOLDER_MIMETYPE)) {
			throw RuntimeException("The id $destinationId given as the destination folder is not a folder but ${destinationFolder.mimeType}")
		}
		return destinationFolder
	}

	private fun uploadFile(drive: Drive, file: java.io.File, fileName: String, destinationFolder: File): File {
		return drive.files().create(File().apply {
			name = fileName
			parents = mutableListOf(destinationFolder.id)
		}, FileContent(null, file))
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

}
