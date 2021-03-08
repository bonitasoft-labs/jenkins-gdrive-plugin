package org.bonitasoft.gdrive.jenkins

import hudson.AbortException
import hudson.model.TaskListener
import org.bonitasoft.gdrive.core.GDriveMoveTask
import org.bonitasoft.gdrive.core.Logger
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution


class GDriveMoveStepExecution(
		private val googleCredentials: String,
		private val sourceId: String,
		private val destinationId: String,
		private val renameTo: String,
		context: StepContext) : SynchronousNonBlockingStepExecution<Void>(context) {

	override fun run(): Void? {
		val logger = context.get(TaskListener::class.java)?.logger!!
		try {
			GDriveMoveTask(googleCredentials, sourceId, destinationId, object : Logger {
				override fun debug(message: String) = logger.println("DEBUG: $message")
				override fun info(message: String) = logger.println("INFO: $message")
				override fun warn(message: String) = logger.println("WARN: $message")
				override fun error(message: String) = logger.println("ERROR: $message")
			}, renameTo).execute()
			return null
		} catch (e: Throwable) {
			e.printStackTrace()
			throw AbortException("Exception: ${e::class.java.name} ${e.message}")
		}
	}


}
