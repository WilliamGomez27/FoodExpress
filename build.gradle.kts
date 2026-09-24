import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.1.10"
    id("com.android.application") version "8.2.2" // Version estable de AGP
    id("org.jetbrains.compose") version "1.7.0" // Version moderna de Compose Multiplatform
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" // Debe coincidir con la version de Kotlin
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // En Android no se puede conectar directamente con MySQL sin librerías externas que rompen,
                // pero si el proyecto es KMP, lo ideal es mover esta librería solo al target desktopMain.
                // Sin embargo, como tienes DAOs en commonMain, haremos un downgrade de la librería a una versión 
                // más antigua que no requiera java.sql.SQLType (incorporado en Java 8 pero que Android no soportó completamente en sus inicios).
                implementation("mysql:mysql-connector-java:5.1.49")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.9.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("androidx.test.ext:junit:1.2.1")
                implementation("androidx.test.espresso:espresso-core:3.6.1")
                implementation("androidx.compose.ui:ui-test-junit4:1.7.0")
                implementation("androidx.compose.ui:ui-test-manifest:1.7.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation("org.testcontainers:testcontainers:1.20.4")
                implementation("org.testcontainers:mysql:1.20.4")
                implementation("io.mockk:mockk:1.13.12")
                implementation("org.slf4j:slf4j-simple:2.0.13")
            }
        }
    }
}

android {
    namespace = "com.fastfood.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fastfood.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

compose.desktop {
    application {
        mainClass = "FastFoodApp.MainKt"
        jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")
}