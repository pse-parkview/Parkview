repositories {
    mavenCentral()
}

plugins {
    kotlin("js") version "1.6.10"
}

dependencies {
    dependencies {
        testImplementation(kotlin("test-js"))
    }
}

group = "com.parkview"
version = "v1.0"

kotlin {
    js {
        browser {
        }
        useCommonJs()
    }
}