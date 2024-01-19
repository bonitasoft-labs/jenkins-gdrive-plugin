package org.bonitasoft.gdrive.jenkins

import hudson.AbortException
import hudson.FilePath
import hudson.FilePath.FileCallable
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import org.bonitasoft.gdrive.core.GDriveUploadTask
import org.bonitasoft.gdrive.core.Logger
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution
import org.jenkinsci.remoting.RoleChecker
import java.io.File
import java.io.Serializable


class GDriveUploadStepExecution(
		private val googleCredentials: String,
		private val source: String,
		private val destinationId: String,
		private val renameTo: String,
		private val uploadChecksum : Boolean,
		context: StepContext) : SynchronousNonBlockingStepExecution<Void>(context) {

	override fun run(): Void? {
		val logger = context.get(TaskListener::class.java)?.logger!!
		try {

			val workspace = context.get(FilePath::class.java)!!
			logger.println("Starting the upload of $source to the destination $destinationId, all logs will be displayed once the upload is completed")
			val logs = workspace.child(source).act(UploadFile(googleCredentials, destinationId, renameTo, uploadChecksum))
			logs.forEach { logger.println(it) }
			return null
		} catch (e: Throwable) {
			e.printStackTrace()
			throw AbortException("Exception: ${e::class.java.name} ${e.message}")
		}
	}


}

class UploadFile(val googleCredentials: String, val destinationId: String, val renameTo: String, val uploadChecksum: Boolean) : FileCallable<List<String>>, Serializable {

	override fun checkRoles(p0: RoleChecker?) = Unit
	override fun invoke(file: File?, p1: VirtualChannel?): List<String> {
		if (file == null) {
			return listOf("File argument is null")
		}
		val logs = mutableListOf<String>()
		val gdriveLogger = object : Logger {
			override fun debug(message: String) {
//				logs.add("DEBUG: $message")
			}

			override fun info(message: String) {
				logs.add("INFO: $message")
			}

			override fun warn(message: String) {
				logs.add("WARN: $message")
			}

			override fun error(message: String) {
				logs.add("ERROR: $message")
			}
		}
		GDriveUploadTask(googleCredentials, file.absolutePath, destinationId, gdriveLogger, renameTo, uploadChecksum).execute()
		return logs
	}
}
