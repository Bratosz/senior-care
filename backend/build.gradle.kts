val kotlinVersion: String by project
val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val arrowVersion: String by project
val kotestVersion: String by project
val testcontainersVersion: String by project
val hikariVersion: String by project
val flywayVersion: String by project
val logbackVersion: String by project
val kotlinCoroutinesVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    application
}

group = "pl.bratosz"
version = "1.0-SNAPSHOT"

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
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testImplementation(kotlin("test"))
}

/* ─────────── KOTLIN / KOMPILATOR ─────────── */
kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
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
