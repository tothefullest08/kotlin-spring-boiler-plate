import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management")
    `java-library`
}

group = "harry"
version = "0.0.1-SNAPSHOT"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.security:spring-security-crypto")
    api("io.swagger.core.v3:swagger-annotations:2.2.0")

    implementation(kotlin("stdlib"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.12")
}

configurations {
    configureEach {
        exclude(group = "org.projectlombok", module = "lombok")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

kotlin {
    jvmToolchain(21)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

tasks.jar {
    isEnabled = true
}

tasks.bootJar {
    isEnabled = false
}
