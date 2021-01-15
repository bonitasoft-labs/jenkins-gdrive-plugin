package org.bonitasoft.gdrive.cli

import org.bonitasoft.gdrive.core.GDriveUploadTask
import org.bonitasoft.gdrive.core.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File

@CommandLine.Command(name = "upload", mixinStandardHelpOptions = true)
class UploadCommand : Runnable {

	val logger = LoggerFactory.getLogger(UploadCommand::class.java)

	@CommandLine.Option(names = ["-c", "--credentials"], required = true, description = ["Path to the credential file to use."])
	lateinit var creds: File

	@CommandLine.Option(names = ["--renameTo"], description = ["Rename the file or folder in to that name."])
	var renameTo: String = ""

	@CommandLine.Parameters(paramLabel = "source", description = ["Source path to the file or folder to upload."])
	lateinit var source: String

	@CommandLine.Parameters(paramLabel = "destinationId", description = ["Id of the destination folder on Google drive where to upload to."])
	lateinit var destinationId: String

	val gdriveLogger = object : Logger {
		override fun debug(message: String) = logger.debug(message)
		override fun info(message: String) = logger.info(message)
		override fun warn(message: String) = logger.warn(message)
		override fun error(message: String) = logger.error(message)
	}

	override fun run() {
		val task = GDriveUploadTask(creds.readText(), source, destinationId, gdriveLogger, renameTo)
		task.execute()
	}


}
