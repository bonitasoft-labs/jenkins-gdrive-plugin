package org.bonitasoft.gdrive.cli

import org.bonitasoft.gdrive.core.GDriveUploadTask
import picocli.CommandLine

@CommandLine.Command(name = "upload", mixinStandardHelpOptions = true, description = ["Upload a file or folder into a target directory"])
class UploadCommand : BaseCommand() {

	@CommandLine.Option(names = ["--renameTo"], description = ["Rename the file or folder in to that name."])
	var renameTo: String = ""

	@CommandLine.Parameters(paramLabel = "source", description = ["Source path to the file or folder to upload."])
	lateinit var source: String

	@CommandLine.Parameters(paramLabel = "destinationId", description = ["Id of the destination folder on Google drive where to upload to."])
	lateinit var destinationId: String


	override fun run() {
		GDriveUploadTask(parentCommand.creds.readText(), source, destinationId, gdriveLogger, renameTo).execute()
	}

}
