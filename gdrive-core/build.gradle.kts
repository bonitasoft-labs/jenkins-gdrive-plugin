plugins {
	kotlin("jvm")
}

dependencies {
	implementation("com.google.api-client:google-api-client:1.31.1")
	implementation("com.google.apis:google-api-services-drive:v3-rev20201130-1.31.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:0.22.1")

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
	testImplementation("org.jetbrains.kotlin:kotlin-test")

}
