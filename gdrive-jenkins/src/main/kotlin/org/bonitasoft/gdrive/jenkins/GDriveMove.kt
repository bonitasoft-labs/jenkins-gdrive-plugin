package org.bonitasoft.gdrive.jenkins

import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.kohsuke.stapler.DataBoundConstructor

class GDriveMove

@DataBoundConstructor
constructor(
		val googleCredentials: String,
		val sourceId: String,
		val elementName: String,
		val destinationId: String,
		val renameTo: String
) : Step() {
	override fun start(context: StepContext) = GDriveMoveStepExecution(
			googleCredentials,
			sourceId,
			elementName,
			destinationId,
			renameTo,
			context
	)


	@Extension
	open class GDriveMoveStepDescriptor : StepDescriptor() {
		override fun getFunctionName() = "gdriveMove"

		override fun getDisplayName() = "Google drive move"

		override fun getRequiredContext() = setOf(FilePath::class.java, TaskListener::class.java)

	}
}
