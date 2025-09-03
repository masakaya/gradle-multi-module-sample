plugins {
    kotlin("jvm") version Versions.kotlin apply false
    kotlin("plugin.spring") version Versions.kotlin apply false
    id(Plugins.springBoot) version Versions.springBoot apply false
    id(Plugins.springDependencyManagement) version "1.1.7" apply false
    id("nu.studer.jooq") version "8.2.1" apply false
    id(Plugins.flyway) version "10.21.0" apply false
}

allprojects {
    group = "com.masakaya"
    version = "0.0.1"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(Versions.java.toInt())
        }
    }
    
    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        
        implementation(platform(Libs.Spring.bootDependencies))
        testImplementation(Libs.Kotlin.testJunit5)
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
