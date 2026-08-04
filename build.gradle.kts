plugins {
    // Usaremos la versión estable más reciente y compatible
    kotlin("jvm") version "2.1.10"
    id("org.jetbrains.compose") version "1.7.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
        mavenCentral()
        google() // <--- ESTA ES LA LÍNEA QUE FALTA
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Opcional, ayuda a veces
    }

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation(compose.desktop.currentOs)
}

kotlin {

        jvmToolchain(21)
    }


compose.desktop {
    application {
        mainClass = "FastFoodApp.MainKt"
        jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
    }
}

tasks.test {
    useJUnitPlatform()
}