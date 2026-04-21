plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("MainKt")
}

tasks.shadowJar {
    archiveBaseName.set("servidor-dni")
    archiveVersion.set("1.0")
    archiveClassifier.set("")
}

kotlin {
    jvmToolchain(17)  // Cambiado a 17 para compatibilidad con Docker
}