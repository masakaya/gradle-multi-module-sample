
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
    
    // MySQL driver needs to be available at multiple classpaths for Flyway and jOOQ
    implementation(Libs.Database.mysqlConnector)
    runtimeOnly(Libs.Database.mysqlConnector)  // For Flyway runtime
    
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
val dbUrl = System.getenv("ORG_GRADLE_PROJECT_db_url") 
    ?: project.findProperty("db.url") as String?
    ?: "jdbc:mysql://127.0.0.1:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
val dbUser = System.getenv("ORG_GRADLE_PROJECT_db_user")
    ?: project.findProperty("db.user") as String?
    ?: "dbuser"
val dbPassword = System.getenv("ORG_GRADLE_PROJECT_db_password")
    ?: project.findProperty("db.password") as String?
    ?: "dbpassword"

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
    driver = "com.mysql.cj.jdbc.Driver"  // Explicitly specify the MySQL driver
    schemas = arrayOf("mydb")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    cleanDisabled = false
}

// Configure Flyway tasks to ensure MySQL driver is available
tasks.named("flywayMigrate") {
    doFirst {
        println("=== Flyway Migration Debug Info ===")
        println("Database URL: $dbUrl")
        println("Database User: $dbUser") 
        println("Database Password: ${if (dbPassword.isNotEmpty()) "***SET***" else "***EMPTY***"}")
        println("Flyway schemas: ${flyway.schemas.joinToString(", ")}")
        println("Flyway locations: ${flyway.locations.joinToString(", ")}")
        
        // Check buildscript classpath
        println("=== Buildscript Classpath Info ===")
        try {
            val mysqlDriverClass = Class.forName("com.mysql.cj.jdbc.Driver")
            println("MySQL Driver found in classpath: ${mysqlDriverClass.canonicalName}")
        } catch (e: ClassNotFoundException) {
            println("ERROR: MySQL Driver NOT found in classpath!")
            println("ClassNotFoundException: ${e.message}")
        }
        
        // Print system properties
        println("=== Java System Properties ===")
        println("java.class.path contains mysql: ${System.getProperty("java.class.path").contains("mysql")}")
        
        println("=== End Debug Info ===")
    }
}

tasks.named("generateJooq").configure {
    // Only depend on flywayMigrate when not excluded
    if (project.gradle.startParameter.excludedTaskNames.none { it.contains("flywayMigrate") }) {
        dependsOn("flywayMigrate")
    }
    inputs.dir("src/main/resources/db/migration")
    outputs.dir("build/generated-src/jooq")
    
    doFirst {
        println("Generating jOOQ classes from database schema...")
        println("Make sure the database is running and migrations are applied!")
    }
}

// Ensure compileKotlin runs after jOOQ code generation
tasks.named("compileKotlin").configure {
    dependsOn("generateJooq")
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