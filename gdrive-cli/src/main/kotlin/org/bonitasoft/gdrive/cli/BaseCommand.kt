package org.bonitasoft.gdrive.cli

import org.bonitasoft.gdrive.core.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine

@CommandLine.Command(mixinStandardHelpOptions = true)
abstract class BaseCommand : Runnable {

	val logger = LoggerFactory.getLogger(BaseCommand::class.java)

	@CommandLine.ParentCommand
	lateinit var parentCommand: App

	val gdriveLogger = object : Logger {
		override fun debug(message: String) = logger.debug(message)
		override fun info(message: String) = logger.info(message)
		override fun warn(message: String) = logger.warn(message)
		override fun error(message: String) = logger.error(message)
	}



}
