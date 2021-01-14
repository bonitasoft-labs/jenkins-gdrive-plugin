package com.bonitasoft.jenkins.pipeline.gdrive.upload

import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.kohsuke.stapler.DataBoundConstructor

class GDriveUpload

@DataBoundConstructor
constructor(
	val googleCredentials: String,
	val toCopy: String,
	val destinationFolderId: String
) : Step() {
	override fun start(context: StepContext) = GDriveUploadStepExecution(
			googleCredentials,
			toCopy,
			destinationFolderId,
			context
	)


	@Extension
	open class GDriveUploadStepDescriptor : StepDescriptor() {
		override fun getFunctionName() = "gdriveUpload"

		override fun getDisplayName() = "Google drive upload"

		override fun getRequiredContext() = setOf(FilePath::class.java, TaskListener::class.java)

	}
}