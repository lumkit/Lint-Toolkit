rootProject.name = "LintToolkit"
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
        maven("https://jitpack.io")
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.hq.hydraulic.software")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repositories/jcenter")
        maven("https://maven.aliyun.com/repositories/google")
        maven("https://maven.aliyun.com/repositories/central")
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
        maven("https://jitpack.io")
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repositories/jcenter")
        maven("https://maven.aliyun.com/repositories/google")
        maven("https://maven.aliyun.com/repositories/central")
    }
}

include(":app")