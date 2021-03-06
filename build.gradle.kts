import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  java
  idea
  eclipse
  id("org.jetbrains.kotlin.jvm") version "1.6.21" apply false
  id("org.jetbrains.kotlin.plugin.spring") version "1.6.21" apply false
  id("org.springframework.boot") version "2.6.7" apply false
  id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

allprojects {
  group = "me.rhysxia.explore"
  version = "1.0-SNAPSHOT"
}

val applicationModules by extra(
  listOf(
    project(":server"),
  )
)

subprojects {
  repositories {
    mavenCentral()
  }

  apply {
    plugin("org.jetbrains.kotlin.jvm")
    plugin("org.jetbrains.kotlin.plugin.spring")
    plugin("org.springframework.boot")
    plugin("io.spring.dependency-management")
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "11"
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }
}

configure(subprojects.filterNot { it in applicationModules }) {
  apply {
    plugin("java-library")
  }
  tasks.findByName("bootJar")?.enabled = false
}

configure(subprojects.filter { it in applicationModules }) {
  apply {
  }
}

