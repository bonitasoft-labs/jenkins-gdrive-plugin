package org.bonitasoft.gdrive.cli

import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.File
import kotlin.system.exitProcess


@Command(name = "gdrive-cli", mixinStandardHelpOptions = true, version = ["0.0"],
		subcommands = [
			UploadCommand::class,
			MoveCommand::class
		]
)
class App {

	@CommandLine.Option(names = ["-c", "--credentials"], required = true, description = ["Path to the credential file to use."])
	lateinit var creds: File



}

fun main(args: Array<String>) {
	exitProcess(CommandLine(App()).execute(*args))
}
