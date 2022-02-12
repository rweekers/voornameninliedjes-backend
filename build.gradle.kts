import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val buildMyPostgresAppImage by tasks.creating(DockerBuildImage::class) {
    dependsOn(
        tasks.jar,
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
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.bmuschko.docker-remote-api") version "7.0.0"
    id("org.sonarqube") version "3.3"
    id("com.gorylenko.gradle-git-properties") version "2.3.1"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
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
version = "1.1.0-RELEASE"
java.sourceCompatibility = JavaVersion.VERSION_17

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
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.flywaydb:flyway-core:8.4.2")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("io.github.furstenheim:copy_down:1.0")
    implementation("org.apache.commons:commons-imaging:1.0-alpha2")
    implementation("commons-io:commons-io:2.11.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("commons-codec:commons-codec:1.15")
    testImplementation("org.junit.platform:junit-platform-commons:1.8.2")
    testImplementation("org.junit.platform:junit-platform-engine:1.8.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.2")
    testImplementation("org.junit.platform:junit-platform-suite-engine:1.8.2")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.cucumber:cucumber-java:7.2.3")
    testImplementation("io.cucumber:cucumber-spring:7.2.3")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.2.3")
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
        jvmTarget = "17"
    }
}