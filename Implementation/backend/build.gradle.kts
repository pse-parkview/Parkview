repositories {
    mavenCentral()
}

plugins {
    kotlin("js") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(kotlin("stdlib-common"))

    testImplementation(kotlin("test-js"))
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