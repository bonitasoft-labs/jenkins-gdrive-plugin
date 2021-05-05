package org.bonitasoft.gdrive.cli

import org.bonitasoft.gdrive.core.GDriveMoveTask
import picocli.CommandLine

@CommandLine.Command(name = "move", mixinStandardHelpOptions = true, description = ["Move a file or folder into a target directory"])
class MoveCommand : BaseCommand() {

	@CommandLine.Option(names = ["--renameTo"], description = ["Rename the file or folder in to that name."])
	var renameTo: String = ""

	@CommandLine.Parameters(paramLabel = "sourceId", description = ["Id of the folder containing the element to move"])
	lateinit var sourceId: String

	@CommandLine.Parameters(paramLabel = "elementName", description = ["Name of element (file or folder) to move"])
	lateinit var elementName: String

	@CommandLine.Parameters(paramLabel = "destinationParentFolderId", description = ["Id of the folder containing the destination folder."])
	lateinit var destinationParentFolderId: String

	@CommandLine.Parameters(paramLabel = "destinationFolderName", description = ["Name of folder which will contain the element to move."])
	lateinit var destinationFolderName: String


	override fun run() {
		GDriveMoveTask(parentCommand.creds.readText(), sourceId,elementName, destinationParentFolderId, destinationFolderName, gdriveLogger, renameTo).execute()
	}

}
