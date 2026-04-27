
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("io.ktor.plugin") version "3.4.3"
    application
}

group = "pl.bratosz"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

val ktorVersion      = "3.4.3"
val exposedVersion   = "1.2.0"
val postgresVersion  = "42.7.10"
val arrowVersion     = "2.2.2.1"
val kotestVersion = "6.1.11"
val testcontainersVersion = "2.0.5"
val hikariVersion = "7.0.2"
val flywayVersion = "12.4.0"
val logbackVersion = "1.5.32"
val kotlinCoroutinesVersion = "1.10.2"

dependencies {
    /* --- Ktor (core + silnik + JSON + helpery) --- */
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    /*  DB --- */
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    /* --- Arrow FP --- */
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    /* --- Coroutines --- */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    /* --- Logging --- */
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    /* --- Open API --- */
    implementation("io.ktor:ktor-server-openapi:${ktorVersion}")
    implementation("io.ktor:ktor-server-swagger:${ktorVersion}")

    /* --- Tests --- */
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation(kotlin("test"))
}

/* ─────────── KOTLIN / KOMPILATOR ─────────── */
kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("pl.bratosz.seniorcarebackend.ApplicationKt")
}

ktor {
    application {
        mainClass.set("pl.bratosz.seniorcarebackend.ApplicationKt")
    }
    docker {
        jreVersion = JavaVersion.VERSION_21
        localImageName = "senior-care-backend"
    }
}