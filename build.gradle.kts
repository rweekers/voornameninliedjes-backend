import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val buildMyMongoAppImage by tasks.creating(DockerBuildImage::class) {
    inputDir.set(file("."))
    dockerFile.set(file("DockerfileMongo"))
    images.add("mongo:latest")
}

val buildMyPostgresAppImage by tasks.creating(DockerBuildImage::class) {
    inputDir.set(file("."))
    dockerFile.set(file("DockerfilePostgres"))
    images.add("postgres:latest")
}

val createMyMongoAppContainer by tasks.creating(DockerCreateContainer::class) {
    dependsOn(buildMyMongoAppImage)
    targetImageId(buildMyMongoAppImage.imageId)
    containerName.set("some-mongo")
    hostConfig.portBindings.set(listOf("27017:27017"))
}

val createMyPostgresAppContainer by tasks.creating(DockerCreateContainer::class) {
    dependsOn(buildMyPostgresAppImage)
    targetImageId(buildMyPostgresAppImage.imageId)
    containerName.set("some-postgres")
    hostConfig.portBindings.set(listOf("5432:5432"))
}

val removeMyMongoAppContainer by tasks.creating(DockerRemoveContainer::class) {
    force.set(true)
    removeVolumes.set(true)
    targetContainerId(createMyMongoAppContainer.containerId)
}

val removeMyPostgresAppContainer by tasks.creating(DockerRemoveContainer::class) {
    force.set(true)
    removeVolumes.set(true)
    targetContainerId(createMyPostgresAppContainer.containerId)
}

val startMyMongoAppContainer by tasks.creating(DockerStartContainer::class) {
    dependsOn(createMyMongoAppContainer)
    targetContainerId(createMyMongoAppContainer.containerId)
}

val stopMyMongoAppContainer by tasks.creating(DockerStopContainer::class) {
    targetContainerId(createMyMongoAppContainer.containerId)
}

val startMyPostgresAppContainer by tasks.creating(DockerStartContainer::class) {
    dependsOn(createMyPostgresAppContainer)
    targetContainerId(createMyPostgresAppContainer.containerId)
}

val stopMyPostgresAppContainer by tasks.creating(DockerStopContainer::class) {
    targetContainerId(createMyPostgresAppContainer.containerId)
}

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("com.bmuschko.docker-remote-api") version "6.6.1"
    id("org.sonarqube") version "3.0"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    jacoco
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = false
        html.destination = file("$buildDir/reports/coverage")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.3".toBigDecimal()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()

    dependsOn(startMyMongoAppContainer)
    dependsOn(startMyPostgresAppContainer)
    finalizedBy(removeMyMongoAppContainer)
    finalizedBy(removeMyPostgresAppContainer)
}

sonarqube {
    properties {
        property("sonar.projectKey", "nl.orangeflamingo:voornameninliedjes-backend")
    }
}

group = "nl.orangeflamingo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.postgresql:postgresql:42.2.18")
    implementation("org.flywaydb:flyway-core:7.3.2")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:3.0.0")

}

configurations {
    "implementation" {
        exclude(group = "org.springframework.boot", module="spring-boot-starter-logging")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}