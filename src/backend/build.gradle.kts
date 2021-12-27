plugins {
    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.dokka") version "1.5.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.18.0"

    kotlin("kapt") version "1.5.21"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    jacoco
}

group = "com.parkview"
version = "v1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

jacoco {
    toolVersion = "0.8.7"
}

repositories {
    mavenCentral()
}

val exposedVersion: String by project
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.exposed:exposed-core:0.31.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.31.1")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("com.zaxxer:HikariCP:2.3.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("io.zonky.test:embedded-postgres:1.2.10")

    // doc
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.0")

    // test deps
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation(kotlin("test"))

    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        csv.isEnabled = true
    }
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.

    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
    }
}
