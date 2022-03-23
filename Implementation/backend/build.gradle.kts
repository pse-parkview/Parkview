repositories {
    mavenCentral()
}

plugins {
    kotlin("js") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(kotlin("stdlib-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    testImplementation(kotlin("test-js"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

group = "com.parkview"
version = "v1.0"

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useFirefox()
                }
            }
        }
        useCommonJs()
    }
}