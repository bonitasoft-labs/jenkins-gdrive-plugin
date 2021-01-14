package org.bonitasoft.gdrive.cli

import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess


@Command(name = "gdrive-cli", mixinStandardHelpOptions = true, version = ["0.0"],
		subcommands = [
			UploadCommand::class,
		]
)
class App {

}

fun main(args: Array<String>) {
	exitProcess(CommandLine(App()).execute(*args))
}
