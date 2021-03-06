import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val buildMyPostgresAppImage by tasks.creating(DockerBuildImage::class) {
    dependsOn(
        tasks.processTestResources,
        tasks.bootJar,
        tasks.bootJarMainClassName,
        tasks.compileJava,
        tasks.compileKotlin,
        tasks.compileTestKotlin,
        tasks.generateGitProperties,
        tasks.inspectClassesForKotlinIC,
        tasks.processResources
    )
    inputDir.set(file("."))
    dockerFile.set(file("DockerfilePostgres"))
    images.add("postgres:13.1")
}

val createMyPostgresAppContainer by tasks.creating(DockerCreateContainer::class) {
    dependsOn(buildMyPostgresAppImage)
    targetImageId(buildMyPostgresAppImage.imageId)
    containerName.set("some-postgres")
    hostConfig.portBindings.set(listOf("5432:5432"))
}

val removeMyPostgresAppContainer by tasks.creating(DockerRemoveContainer::class) {
    force.set(true)
    removeVolumes.set(true)
    targetContainerId(createMyPostgresAppContainer.containerId)
}

val startMyPostgresAppContainer by tasks.creating(DockerStartContainer::class) {
    dependsOn(createMyPostgresAppContainer)
    targetContainerId(createMyPostgresAppContainer.containerId)
}

val stopMyPostgresAppContainer by tasks.creating(DockerStopContainer::class) {
    targetContainerId(createMyPostgresAppContainer.containerId)
}

plugins {
    id("jacoco")
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.bmuschko.docker-remote-api") version "7.0.0"
    id("org.sonarqube") version "3.0"
    id("com.gorylenko.gradle-git-properties") version "2.3.1"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

tasks.test {
    useJUnitPlatform()

    dependsOn(startMyPostgresAppContainer)
    finalizedBy(removeMyPostgresAppContainer, tasks.jacocoTestReport)
}

sonarqube {
    properties {
        property("sonar.projectKey", "nl.orangeflamingo:voornameninliedjes-backend")
    }
}

group = "nl.orangeflamingo"
version = "1.0.0-RELEASE"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.postgresql:postgresql:42.2.20")
    implementation("org.flywaydb:flyway-core:7.9.0")
    implementation("com.google.guava:guava:30.1.1-jre")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:2.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:latest.release")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")
    testImplementation("io.cucumber:cucumber-java8:6.8.1")
    testImplementation("io.cucumber:cucumber-spring:6.8.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:6.8.1")
    testImplementation("com.beust:klaxon:5.5")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

}

configurations {
    "implementation" {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}