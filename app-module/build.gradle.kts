plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id(Plugins.springBoot)
    id(Plugins.springDependencyManagement)
}

group = "com.masakaya"
version = "0.0.1"
description = "Main Application Module"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(Versions.java.toInt())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":persistence-module"))
    
    implementation(Libs.Spring.bootStarterWeb)
    implementation(Libs.Spring.bootStarterActuator)
    implementation(Libs.OpenApi.springdocWebmvcUi)
    implementation(Libs.Kotlin.reflect)
    implementation(Libs.Jackson.moduleKotlin)
    
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    testImplementation(Libs.Spring.bootStarterTest)
    testImplementation(Libs.Kotlin.testJunit5)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Ensure persistence-module is built before app-module
tasks.named("compileKotlin") {
    dependsOn(":persistence-module:build")
}

tasks.named("compileTestKotlin") {
    dependsOn(":persistence-module:build")
}