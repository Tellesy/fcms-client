plugins {
    kotlin("jvm") version "1.9.24"
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "io.github.tellesy"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val jacksonVersion = "2.17.1"
val okhttpVersion = "4.12.0"
val slf4jVersion = "2.0.13"
val junitVersion = "5.10.2"
val coroutinesVersion = "1.8.1"

dependencies {
    // Core
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("org.slf4j:slf4j-api:$slf4jVersion")

    // JSON (Jackson)
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Coroutines for suspend functions and Java CompletableFuture bridge
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")

    // Test
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpVersion")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "fcms-client"
            version = project.version.toString()
            pom {
                name.set("FCMS Client")
                description.set("Kotlin/Java SDK for FCMS APIs (Salaries, Accounts, Requests)")
                url.set("https://github.com/Tellesy/fcms-client")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("tellesy")
                        name.set("Tellesy")
                    }
                }
                scm {
                    url.set("https://github.com/Tellesy/fcms-client")
                    connection.set("scm:git:git://github.com/Tellesy/fcms-client.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Tellesy/fcms-client.git")
                }
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

// Sign artifacts for Maven Central (uses env vars SIGNING_KEY and SIGNING_PASSWORD)
signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

// Nexus (Sonatype) publishing configuration. Provide SONATYPE_USERNAME and SONATYPE_PASSWORD env vars.
nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

// Detekt defaults
detekt {
    buildUponDefaultConfig = true
    allRules = false
}

// Dokka HTML site
tasks.register("docs") {
    group = "documentation"
    description = "Generate Dokka HTML documentation"
    dependsOn("dokkaHtml")
}
