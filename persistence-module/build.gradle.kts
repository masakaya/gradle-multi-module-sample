
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Libs.Database.mysqlConnector)
        classpath(Libs.Database.flyway)
        classpath(Libs.Database.flywayMysql)
        classpath("nu.studer:gradle-jooq-plugin:10.1.1")
    }
}

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.spring")
    id("nu.studer.jooq")
    id(Plugins.flyway)
    id("maven-publish")
}

group = "com.masakaya"
version = "0.0.1"
description = "Infrastructure Persistence Module"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(Versions.java.toInt())
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api(Libs.Spring.bootStarterJooq)
    api(Libs.Spring.bootStarterJdbc)
    api(Libs.Database.flyway)
    api(Libs.Database.flywayMysql)
    api(Libs.Kotlin.reflect)
    
    runtimeOnly(Libs.Database.mysqlConnector)
    jooqGenerator(Libs.Database.mysqlConnector)
    jooqGenerator(Libs.Jooq.codegen)
    
    testImplementation(Libs.Spring.bootStarterTest)
    testImplementation(Libs.Kotlin.testJunit5)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// Database connection configuration (for jOOQ code generation)
val dbUrl = project.findProperty("db.url") as String? 
    ?: System.getenv("ORG_GRADLE_PROJECT_db_url") 
    ?: "jdbc:mysql://127.0.0.1:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
val dbUser = project.findProperty("db.user") as String? 
    ?: System.getenv("ORG_GRADLE_PROJECT_db_user") 
    ?: "dbuser"
val dbPassword = project.findProperty("db.password") as String? 
    ?: System.getenv("ORG_GRADLE_PROJECT_db_password") 
    ?: "dbpassword"

// Debug logging for database configuration
println("=== Database Configuration ===")
println("dbUrl: $dbUrl")
println("dbUser: $dbUser")
println("Environment ORG_GRADLE_PROJECT_db_url: ${System.getenv("ORG_GRADLE_PROJECT_db_url")}")
println("Project property db.url: ${project.findProperty("db.url")}")
println("==============================")

// jOOQ configuration
jooq {
    version.set(Versions.jooq)
    
    configurations {
        val mainConfig = create("main") as nu.studer.gradle.jooq.JooqConfig
        mainConfig.apply {
            generateSchemaSourceOnCompilation.set(true)
            
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "mydb"
                        excludes = "flyway_schema_history"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isKotlinNotNullPojoAttributes = true
                        isKotlinNotNullRecordAttributes = true
                    }
                    target.apply {
                        packageName = "com.masakaya.jooq.generated"
                        directory = "build/generated-src/jooq/main"
                    }
                    strategy.apply {
                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    }
                }
            }
        }
    }
}

// Flyway configuration
flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = arrayOf("mydb")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    cleanDisabled = false
}

tasks.named("generateJooq").configure {
    inputs.dir("src/main/resources/db/migration")
    outputs.dir("build/generated-src/jooq")
    
    doFirst {
        println("Generating jOOQ classes from database schema...")
        println("Make sure the database is running and migrations are applied!")
    }
}

// GitHub Packages publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("Infrastructure Persistence Module")
                description.set("Shared persistence layer for database operations")
                url.set("https://github.com/masakaya/gradle-shard-module-sample")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("masakaya")
                        name.set("Masakaya")
                        email.set("your-email@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/masakaya/gradle-shard-module-sample.git")
                    developerConnection.set("scm:git:ssh://github.com/masakaya/gradle-shard-module-sample.git")
                    url.set("https://github.com/masakaya/gradle-shard-module-sample")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/masakaya/gradle-shard-module-sample")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}