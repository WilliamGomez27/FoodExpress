pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
            }
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:9.4.1")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.bouncycastle:bcprov-jdk18on:1.86")
        classpath("io.netty:netty-handler:4.1.137.Final")
        classpath("io.netty:netty-codec-http2:4.1.137.Final")
        classpath("org.jdom:jdom2:2.0.6.1")
        classpath("com.google.protobuf:protobuf-java:4.36.2")
        classpath("commons-io:commons-io:2.18.0")
        classpath("mysql:mysql-connector-java:8.0.28")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "FoodExpress"
