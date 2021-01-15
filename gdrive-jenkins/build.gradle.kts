plugins {
	kotlin("jvm")
	// That plugin require to deactivate the incremental compilation because of bug on ICU: https://youtrack.jetbrains.com/issue/KT-37271
	// ICU is a dependency of jenkins
	kotlin("kapt")
	id("org.jenkins-ci.jpi") version ("0.41.0")
}


dependencies {

	implementation(project(":gdrive-core"))

	implementation("org.jenkins-ci.plugins.workflow:workflow-step-api:2.23@jar")
	implementation("com.google.api-client:google-api-client:1.31.1")
	implementation("com.google.apis:google-api-services-drive:v3-rev20201130-1.31.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:0.22.1")

	// only used when running the test server locally
	jenkinsServer("org.jenkins-ci.plugins.workflow", "workflow-step-api", "2.23")
	jenkinsServer("org.jenkins-ci.plugins.workflow", "workflow-aggregator", "2.6")

	// SezPoz is used to process @hudson.Extension and other annotations
	kapt("net.java.sezpoz:sezpoz:1.13")
}

jenkinsPlugin {
	displayName = "Jenkins Google Drive plugin"
	shortName = "jenkins-gdrive-plugin"
	description = "This plugin file or folders to google drive"
	gitHubUrl = "https://github.com/bonitasoft-labs/jenkins-gdrive-plugin/"
	url = "https://github.com/bonitasoft-labs/jenkins-gdrive-plugin/"
	jenkinsVersion.set("2.249.3")
	pluginFirstClassLoader = true
	workDir = buildDir.resolve("work")
}

kapt {
	correctErrorTypes = true
}
