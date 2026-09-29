import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.1.10"
    id("com.android.application") version "9.4.1"
    id("org.jetbrains.compose") version "1.12.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
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

                // Volvemos a la versión 5.1.49 de MySQL Connector para Android
                // porque las versiones 8.0+ y 9.0+ usan java.sql.SQLType que NO existe en Android.
                implementation("mysql:mysql-connector-java:5.1.49")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.13.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("androidx.test.ext:junit:1.3.0")
                implementation("androidx.test.espresso:espresso-core:3.7.0")
                implementation("androidx.compose.ui:ui-test-junit4:1.12.1")
                implementation("androidx.compose.ui:ui-test-manifest:1.12.1")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.11.0")
            }
        }

        val desktopTest by getting {
            dependencies {
                // Testcontainers — MySQL real en Docker para integration tests
                implementation("org.testcontainers:testcontainers:1.21.4")
                implementation("org.testcontainers:mysql:1.21.4")
                // MockK — Mocking para Kotlin
                implementation("io.mockk:mockk:1.14.11")
                // SLF4J para suprimir warnings de Testcontainers en tests
                implementation("org.slf4j:slf4j-simple:2.0.20")
            }
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
        versionCode = 1
        versionName = "1.0"
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
