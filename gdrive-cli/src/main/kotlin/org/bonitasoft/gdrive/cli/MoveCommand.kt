package org.bonitasoft.gdrive.cli

import org.bonitasoft.gdrive.core.GDriveMoveTask
import picocli.CommandLine

@CommandLine.Command(name = "move", mixinStandardHelpOptions = true, description = ["Move a file or folder into a target directory"])
class MoveCommand : BaseCommand() {

	@CommandLine.Option(names = ["--renameTo"], description = ["Rename the file or folder in to that name."])
	var renameTo: String = ""

	@CommandLine.Parameters(paramLabel = "sourceId", description = ["Id of the source folder or file to move"])
	lateinit var sourceId: String

	@CommandLine.Parameters(paramLabel = "destinationId", description = ["Id of the destination folder on Google drive where to upload to."])
	lateinit var destinationId: String


	override fun run() {
		GDriveMoveTask(parentCommand.creds.readText(), sourceId, destinationId, gdriveLogger, renameTo).execute()
	}

}
