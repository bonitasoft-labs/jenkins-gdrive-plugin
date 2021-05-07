package org.bonitasoft.gdrive.jenkins

import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class GDriveMove
@DataBoundConstructor
constructor(
        val googleCredentials: String,
        val sourceId: String,
        val elementName: String,
        val destinationParentFolderId: String,
        val renameTo: String
) : Step() {

    private var destinationFolderName: String? = null

    @DataBoundSetter
    fun setDestinationFolderName(destinationFolderName: String) {
        this.destinationFolderName = destinationFolderName
    }


    override fun start(context: StepContext) = GDriveMoveStepExecution(
            googleCredentials,
            sourceId,
            elementName,
            destinationParentFolderId,
            destinationFolderName,
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
