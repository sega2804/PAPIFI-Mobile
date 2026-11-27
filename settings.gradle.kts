rootProject.name = "Papifi"

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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(
    ":core:model",
    ":core:common",
    ":core:designsystem",
    ":core:ui",
    ":core:network",
    ":core:security",
    ":feature:auth",
    ":feature:profile",
    ":feature:kyc",
    ":feature:wallet",
    ":feature:exchange",
    ":feature:addmoney",
    ":feature:send",
    ":feature:receive",
    ":feature:activity",
    ":feature:support",
    ":app"
)
