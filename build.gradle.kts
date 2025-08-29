plugins {
    kotlin("jvm") version "1.9.24"
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("signing")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.tellesy"
version = "1.0.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val jacksonVersion = "2.17.1"
val okhttpVersion = "4.12.0"
val slf4jVersion = "2.0.13"
val junitVersion = "5.10.2"
val coroutinesVersion = "1.8.1"

// Detect presence of signing keys to conditionally enable signing (useful for local publishes)
// Support multiple sources:
// - Env: SIGNING_KEY / SIGNING_PASSWORD
// - Gradle props: signingInMemoryKey, signing.key, signingKeyFile
// - Password props: signingInMemoryKeyPassword, signing.password, signingPassword
val signingKeyFilePath: String? = (findProperty("signingKeyFile") as? String)
val signingKeyFromFile: String? = signingKeyFilePath?.let { path ->
    val f = file(path)
    if (f.exists()) f.readText(Charsets.UTF_8) else null
}
// Prefer file-based key first (most reliable), then env/in-memory props
val signingKeyRaw: String? = signingKeyFromFile
    ?: System.getenv("SIGNING_KEY")
    ?: (findProperty("signingInMemoryKey") as? String)
    ?: (findProperty("signing.key") as? String)

// Determine source for diagnostics (no secrets logged)
val signingKeySource: String = when {
    signingKeyFromFile != null -> "file"
    System.getenv("SIGNING_KEY") != null -> "env"
    (findProperty("signingInMemoryKey") as? String) != null -> "prop:signingInMemoryKey"
    (findProperty("signing.key") as? String) != null -> "prop:signing.key"
    else -> "none"
}

// Normalize key text: replace escaped newlines, strip potential BOM/leading spaces before header
val signingKeyProp: String? = signingKeyRaw
    ?.replace("\\n", "\n")
    ?.replaceFirst("^\uFEFF".toRegex(), "")
    ?.replaceFirst("^\\s*-----BEGIN ", "-----BEGIN ")
val signingPasswordProp: String? = System.getenv("SIGNING_PASSWORD")
    ?: (findProperty("signingInMemoryKeyPassword") as? String)
    ?: (findProperty("signing.password") as? String)
    ?: (findProperty("signingPassword") as? String)
// Detect whether we're doing a local publish
val isPublishToMavenLocal: Boolean = gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }

// Safe diagnostics (no secrets): show source and header validity
println("Signing diagnostics -> source=" + signingKeySource + ", keyPresent=" + (signingKeyProp != null) + ", headerOk=" + (signingKeyProp?.trimStart()?.startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----") == true))

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



// Publishing with the Vanniktech plugin via Central Portal (automatic release)
mavenPublishing {
    // Configure artifacts for Kotlin JVM: use Dokka HTML for javadoc jar and include sources jar
    configure(
        com.vanniktech.maven.publish.KotlinJvm(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true,
        )
    )
    publishToMavenCentral(automaticRelease = true)
    if (signingKeyProp != null && signingPasswordProp != null) {
        signAllPublications()
    }
    pom {
        name.set("FCMS Client")
        description.set("Dependency-light JVM SDK for FCMS APIs (Salaries, Accounts, Requests). Kotlin & Java, no Spring.")
        url.set("https://github.com/tellesy/fcms-client")
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        scm {
            url.set("https://github.com/tellesy/fcms-client")
            connection.set("scm:git:git://github.com/tellesy/fcms-client.git")
            developerConnection.set("scm:git:ssh://git@github.com/tellesy/fcms-client.git")
        }
        developers {
            developer {
                id.set("tellesy")
                name.set("Muhammad Tellesy")
                url.set("https://github.com/tellesy")
            }
        }
    }
}

signing {
    isRequired = signingKeyProp != null && signingPasswordProp != null
    if (signingKeyProp != null && signingPasswordProp != null) {
        useInMemoryPgpKeys(signingKeyProp, signingPasswordProp)
        sign(publishing.publications)
    }
}

// Guard all signing tasks to skip execution when no keys are present
tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf { signingKeyProp != null && signingPasswordProp != null && !isPublishToMavenLocal }
}

// Additionally, disable sign tasks entirely for local publish or when no keys are present
tasks.configureEach {
    if (name.startsWith("sign") && (isPublishToMavenLocal || signingKeyProp == null || signingPasswordProp == null)) {
        enabled = false
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
