import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    // That plugin require to deactivate the incremental compilation because of bug on ICU: https://youtrack.jetbrains.com/issue/KT-37271
    // ICU is a dependency of jenkins
    kotlin("kapt") version "1.4.21"
    id("org.jenkins-ci.jpi") version ("0.41.0")
    id("com.bonitasoft.gradle.bonita-release") version "0.1.53"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}


jenkinsPlugin {
    displayName = "Bonitasoft Google drive upload"
    shortName = "bonitasoft-gdrive-upload"
    gitHubUrl = "https://github.com/bonitasoft/bonita-ci/"
    url = "https://github.com/bonitasoft/bonita-ci/"
    jenkinsVersion.set("2.263.2")
    pluginFirstClassLoader = true
}

kapt {
    correctErrorTypes = true
}

tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}