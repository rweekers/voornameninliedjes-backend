import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import com.bmuschko.gradle.docker.tasks.container.*
import com.bmuschko.gradle.docker.tasks.image.*

val buildMyAppImage by tasks.creating(DockerBuildImage::class) {
    inputDir.set(file("."))
    images.add("mongo:latest")
}

val createMyAppContainer by tasks.creating(DockerCreateContainer::class) {
    dependsOn(buildMyAppImage)
    targetImageId(buildMyAppImage.getImageId())
    hostConfig.portBindings.set(listOf("27017:27017"))
}

val startMyAppContainer by tasks.creating(DockerStartContainer::class) {
    dependsOn(createMyAppContainer)
    targetContainerId(createMyAppContainer.getContainerId())
}

val stopMyAppContainer by tasks.creating(DockerStopContainer::class) {
    targetContainerId(createMyAppContainer.containerId)
}

plugins {
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.bmuschko.docker-remote-api") version "6.0.0"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
}

group = "nl.orangeflamingo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.create("functionalTestMyApp", Test::class) {
    dependsOn(startMyAppContainer)
    finalizedBy(stopMyAppContainer)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}