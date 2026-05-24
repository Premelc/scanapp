rootProject.name = "Scan-App"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":shared")

include(":androidApp")

include(":designShowcase")

include(":uiComponent")

include(":core:persistence")
include(":core:permission")
include(":core:navigation")
include(":core:notification")
include(":core:utils")
include(":core:file")
include(":core:scan")
include(":core:capturable")
include(":core:ads")

include(":feature:scan:api")
include(":feature:scan:impl")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:collections:api")
include(":feature:collections:impl")
