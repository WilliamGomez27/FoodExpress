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
        // ACTUALIZACIONES DE SEGURIDAD PARA DEPENDABOT (Buildscript)
        classpath("org.bouncycastle:bcprov-jdk18on:1.86")
        classpath("io.netty:netty-handler:4.1.137.Final")
        classpath("io.netty:netty-codec-http2:4.1.137.Final")
        classpath("org.jdom:jdom2:2.0.6.1")
        classpath("com.google.protobuf:protobuf-java:4.29.3")
        classpath("commons-io:commons-io:2.18.0")
        classpath("org.apache.commons:commons-compress:1.28.0")
        classpath("org.mariadb.jdbc:mariadb-java-client:3.5.10")
        classpath("com.mysql:mysql-connector-j:9.2.0")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "FoodExpress"
