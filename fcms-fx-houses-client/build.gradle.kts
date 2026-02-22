plugins {
    kotlin("jvm") version "2.0.21"
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("signing")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.tellesy"
version = "1.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

configurations.matching {
    it.name.startsWith("runtime") || it.name.startsWith("compile") || it.name == "api" || it.name == "implementation"
}.all {
    resolutionStrategy {
        force("com.fasterxml.jackson.core:jackson-databind:2.19.2")
        force("com.fasterxml.jackson.core:jackson-core:2.19.2")
        force("com.fasterxml.jackson.core:jackson-annotations:2.19.2")
        force("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
        force("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    }
}

val jacksonVersion = "2.19.2"
val okhttpVersion = "4.12.0"
val slf4jVersion = "2.0.13"
val junitVersion = "5.10.2"
val coroutinesVersion = "1.8.1"

val signingKeyFilePath: String? = (findProperty("signingKeyFile") as? String)
val signingKeyFromFile: String? = signingKeyFilePath?.let { path ->
    val f = file(path)
    if (f.exists()) f.readText(Charsets.UTF_8) else null
}
val signingKeyRaw: String? = signingKeyFromFile
    ?: System.getenv("SIGNING_KEY")
    ?: (findProperty("signingInMemoryKey") as? String)
    ?: (findProperty("signing.key") as? String)

val signingKeySource: String = when {
    signingKeyFromFile != null -> "file"
    System.getenv("SIGNING_KEY") != null -> "env"
    (findProperty("signingInMemoryKey") as? String) != null -> "prop:signingInMemoryKey"
    (findProperty("signing.key") as? String) != null -> "prop:signing.key"
    else -> "none"
}

val signingKeyProp: String? = signingKeyRaw
    ?.replace("\\n", "\n")
    ?.replaceFirst("^\uFEFF".toRegex(), "")
    ?.replaceFirst("^\\s*-----BEGIN ", "-----BEGIN ")
val signingPasswordProp: String? = System.getenv("SIGNING_PASSWORD")
    ?: (findProperty("signingInMemoryKeyPassword") as? String)
    ?: (findProperty("signing.password") as? String)
    ?: (findProperty("signingPassword") as? String)
val isPublishToMavenLocal: Boolean = gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }

println("Signing diagnostics -> source=" + signingKeySource + ", keyPresent=" + (signingKeyProp != null) + ", headerOk=" + (signingKeyProp?.trimStart()?.startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----") == true))

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.21"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:$jacksonVersion"))

    api("com.squareup.okhttp3:okhttp:$okhttpVersion")
    api("org.slf4j:slf4j-api:$slf4jVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpVersion")
}

kotlin { jvmToolchain(21) }

tasks.test { useJUnitPlatform() }

mavenPublishing {
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
        name.set("FCMS FX Houses Client")
        description.set("FX Houses JVM SDK for FCMS APIs (contracts, purchase requests, miscs, exchange rates, reports). Kotlin & Java, no Spring.")
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

tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf { signingKeyProp != null && signingPasswordProp != null && !isPublishToMavenLocal }
}

tasks.configureEach {
    if (name.startsWith("sign") && (isPublishToMavenLocal || signingKeyProp == null || signingPasswordProp == null)) {
        enabled = false
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
}

tasks.register("docs") {
    group = "documentation"
    description = "Generate Dokka HTML documentation"
    dependsOn("dokkaHtml")
}
