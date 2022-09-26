plugins {
    kotlin("multiplatform") version "1.7.10"
    id("org.flywaydb.flyway") version "9.3.0"
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    application
}

group = "com.experis.mangekamp"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        flyway {
            val dbUrl: String by project.extra.properties
            val dbUser: String by project.extra.properties
            val dbPassword: String by project.extra.properties

            url = dbUrl
            user = dbUser
            password = dbPassword
            driver = "org.postgresql.Driver"
            schemas = arrayOf("public")
            locations = arrayOf("filesystem:src/main/resources/db/migration", "classpath:db/migration")
        }
    }
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputPath = File(buildDir, "processedResources/jvm/main/static/")
                devServer?.proxy = mutableMapOf("/" to "http://localhost:8080")
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.flywaydb:flyway-core")
                implementation("org.postgresql:postgresql:42.5.0")
                implementation("org.springframework.boot:spring-boot-starter-data-jpa")
                implementation("org.springframework.boot:spring-boot-starter-security")
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.security:spring-security-test")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.0.0-pre.332-kotlin-1.6.21")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.0.0-pre.332-kotlin-1.6.21")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.0-pre.332-kotlin-1.6.21")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.3.0-pre.332-kotlin-1.6.21")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("com.experis.mangekamp.MangekampApplicationKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}