import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.21"
//    // That plugin require to deactivate the incremental compilation because of bug on ICU: https://youtrack.jetbrains.com/issue/KT-37271
//    // ICU is a dependency of jenkins
	kotlin("kapt") version "1.4.21"
	id("com.bonitasoft.gradle.bonita-release") version "0.1.53"

}

allprojects {

	repositories {
		jcenter()
		mavenCentral()
	}

	afterEvaluate {
		java {
			sourceCompatibility = JavaVersion.VERSION_1_8
		}

		tasks.withType(KotlinCompile::class.java).all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}
}
