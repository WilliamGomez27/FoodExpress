import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.1.10"
    id("com.android.application") version "9.4.1"
    id("org.jetbrains.compose") version "1.12.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

group = "org.example"
version = "1.1.1"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Drivers JDBC modernos y seguros (MariaDB para Android/KMP y MySQL 9 para Desktop JVM)
            implementation("org.mariadb.jdbc:mariadb-java-client:3.5.10")
            implementation("com.mysql:mysql-connector-j:9.2.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

            // Coil 3 para carga de imágenes en Compose Multiplatform
            // noinspection GradleDependency
            implementation("io.coil-kt.coil3:coil-compose:3.1.0")
            // noinspection GradleDependency
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.1.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.13.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
        }

        androidInstrumentedTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation("androidx.test.ext:junit:1.3.0")
            implementation("androidx.test.espresso:espresso-core:3.7.0")
            implementation("androidx.compose.ui:ui-test-junit4:1.12.1")
        }

        sourceSets.getByName("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.11.0")
        }

        sourceSets.getByName("desktopTest").dependencies {
            // Testcontainers — MySQL real en Docker para integration tests
            // noinspection GradleDependency
            implementation("org.testcontainers:testcontainers:1.21.4")
            // noinspection GradleDependency
            implementation("org.testcontainers:mysql:1.21.4")
            // MockK — Mocking para Kotlin
            implementation("io.mockk:mockk:1.14.11")
            // SLF4J para suprimir warnings de Testcontainers en tests
            implementation("org.slf4j:slf4j-simple:2.0.20")
        }
    }
}

android {
    namespace = "com.foodexpress.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.foodexpress.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "1.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildToolsVersion = "36.0.0"
}

compose.desktop {
    application {
        mainClass = "FoodExpress.MainKt"
        jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.12.1")
}

// Configuración global para forzar versiones sin vulnerabilidades conocidas (Dependabot security fixes)
configurations.all {
    resolutionStrategy {
        force("org.bouncycastle:bcprov-jdk18on:1.86")
        force("io.netty:netty-handler:4.1.118.Final")
        force("io.netty:netty-codec-http2:4.1.118.Final")
        force("com.google.protobuf:protobuf-java:4.29.3")
        force("commons-io:commons-io:2.18.0")
        force("org.apache.commons:commons-compress:1.28.0")
        force("org.jdom:jdom2:2.0.6.1")

        dependencySubstitution {
            substitute(module("org.bouncycastle:bcprov-jdk15on"))
                .using(module("org.bouncycastle:bcprov-jdk18on:1.86"))
            substitute(module("org.bouncycastle:bcprov-jdk15to18"))
                .using(module("org.bouncycastle:bcprov-jdk18on:1.86"))
            substitute(module("mysql:mysql-connector-java"))
                .using(module("com.mysql:mysql-connector-j:9.2.0"))
        }
    }
}

