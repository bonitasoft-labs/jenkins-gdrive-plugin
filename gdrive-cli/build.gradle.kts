plugins {
	kotlin("jvm")
	kotlin("kapt")
	application
}

dependencies {
	implementation(project(":gdrive-core"))
	kapt("info.picocli:picocli-codegen:4.5.2")
	implementation("info.picocli:picocli:4.5.2")
	implementation("io.github.microutils:kotlin-logging:1.12.0")
	implementation("org.apache.logging.log4j:log4j-core:2.13.3")
	implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")
}

application {
	mainClass.set("org.bonitasoft.gdrive.cli.AppKt")
}
