object Versions {
    const val springBoot = "3.5.5"
    const val kotlin = "1.9.25"
    const val jooq = "3.19.14"
    const val flyway = "10.10.0"
    const val mysqlConnector = "8.0.33"
    const val springdocOpenapi = "2.3.0"
    const val java = "21"
}

object Libs {
    object Spring {
        const val bootDependencies = "org.springframework.boot:spring-boot-dependencies:${Versions.springBoot}"
        const val bootStarter = "org.springframework.boot:spring-boot-starter"
        const val bootStarterWeb = "org.springframework.boot:spring-boot-starter-web"
        const val bootStarterJdbc = "org.springframework.boot:spring-boot-starter-jdbc"
        const val bootStarterJooq = "org.springframework.boot:spring-boot-starter-jooq"
        const val bootStarterActuator = "org.springframework.boot:spring-boot-starter-actuator"
        const val bootStarterTest = "org.springframework.boot:spring-boot-starter-test"
    }
    
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
        const val testJunit5 = "org.jetbrains.kotlin:kotlin-test-junit5"
    }
    
    object Database {
        const val mysqlConnector = "com.mysql:mysql-connector-j:${Versions.mysqlConnector}"
        const val flyway = "org.flywaydb:flyway-core:${Versions.flyway}"
        const val flywayMysql = "org.flywaydb:flyway-mysql:${Versions.flyway}"
    }
    
    object Jooq {
        const val core = "org.jooq:jooq:${Versions.jooq}"
        const val meta = "org.jooq:jooq-meta:${Versions.jooq}"
        const val codegen = "org.jooq:jooq-codegen:${Versions.jooq}"
    }
    
    object Jackson {
        const val moduleKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
    }
    
    object OpenApi {
        const val springdocWebmvcUi = "org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springdocOpenapi}"
    }
}

object Plugins {
    const val springBoot = "org.springframework.boot"
    const val springDependencyManagement = "io.spring.dependency-management"
    const val kotlin = "kotlin"
    const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    const val kotlinSpring = "org.jetbrains.kotlin.plugin.spring"
    const val kotlinJpa = "org.jetbrains.kotlin.plugin.jpa"
    const val jooq = "dev.monosoul.jooq-docker"
    const val flyway = "org.flywaydb.flyway"
}