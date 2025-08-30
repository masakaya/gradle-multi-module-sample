import org.jooq.meta.jaxb.*

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.mysql:mysql-connector-j:8.0.33")
        classpath("org.flywaydb:flyway-mysql:10.21.0")
    }
}

plugins {
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "8.2.1"
	id("org.flywaydb.flyway") version "10.21.0"
}

group = "com.masakaya"
version = "0.0.1"
description = "Gradle Infra shared module"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-mysql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	jooqGenerator("com.mysql:mysql-connector-j")
	jooqGenerator("org.jooq:jooq-codegen")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
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

// Database connection configuration (for jOOQ code generation)
val dbUrl = project.findProperty("db.url") as String? ?: "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
val dbUser = project.findProperty("db.user") as String? ?: "dbuser"
val dbPassword = project.findProperty("db.password") as String? ?: "dbpassword"

// jOOQ configuration
jooq {
	version.set("3.18.7")
	
	configurations {
		create("main") {
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
					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
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
	cleanDisabled = false  // 開発環境では有効にしておく
}

// Spring Boot runs Flyway automatically on startup, so we just need to ensure DB is ready for jOOQ
tasks.named("generateJooq").configure {
	inputs.dir("src/main/resources/db/migration")
	outputs.dir("build/generated-src/jooq")
	
	doFirst {
		println("Generating jOOQ classes from database schema...")
		println("Make sure the database is running and migrations are applied!")
	}
}
