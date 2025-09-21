import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
    kotlin("plugin.jpa") version "2.0.0" apply false
    kotlin("plugin.allopen") version "2.0.0" apply false
    kotlin("plugin.noarg") version "2.0.0" apply false
    id("org.springframework.boot") version "3.5.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "harry"
version = "0.0.1-SNAPSHOT"

val javaVersion = JavaLanguageVersion.of(21)

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension>("kotlin") {
            jvmToolchain(javaVersion.asInt())
        }

        extensions.configure<JavaPluginExtension>("java") {
            toolchain.languageVersion.set(javaVersion)
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = javaVersion.asInt().toString()
            }
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.allopen") {
        extensions.configure<AllOpenExtension>("allOpen") {
            annotation("jakarta.persistence.Entity")
            annotation("jakarta.persistence.MappedSuperclass")
            annotation("jakarta.persistence.Embeddable")
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.noarg") {
        extensions.configure<NoArgExtension>("noArg") {
            annotation("jakarta.persistence.Entity")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
