plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("com.diffplug.spotless") version "8.0.0"
    jacoco
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
    
    implementation("org.apache.commons:commons-lang3:3.20.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(14)
    }
}

spotless{
	java{
		googleJavaFormat().aosp()
	}
}


tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
