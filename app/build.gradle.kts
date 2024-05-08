import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinxSerialization)
    id("dev.hydraulic.conveyor") version "1.8"
}
val appPackageName: String by project

group = appPackageName
version = libs.versions.lint.toolkit.get()

kotlin {
//    jvm("desktop")
    jvm { withJava() }
    jvmToolchain(17)
    sourceSets {

        val commonMain: KotlinSourceSet by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)

                val composeBom = project.dependencies.platform(libs.androidx.compose.bom)
                implementation(composeBom)

                implementation(libs.androidx.compose.materialWindow)

                implementation(libs.coil)
            }
        }

        val jvmMain: KotlinSourceSet by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(libs.lint.ui)
                implementation(project(":terminal"))

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.kodein)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.java)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.encoding)

                implementation(libs.coil.compose.jvm)
                implementation(libs.coil.network.ktor.jvm)

                implementation(libs.rsyntaxtextarea)
            }
        }
    }
}

dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

compose.desktop {
    application {
        mainClass = "io.lumkit.lint.toolkit.desktop.LintApplicationKt"
        jvmArgs += listOf(
            "-Dfile.encoding=UTF-8",
            "-DappVersion=${libs.versions.lint.toolkit.get()}",
            "-DappPackageName=$appPackageName"
        )

//        javaHome = System.getenv("JDK_22")

//        buildTypes.release.proguard {
//            configurationFiles.from(project.file("compose-desktop.pro"))
//            obfuscate.set(false)
//        }

        nativeDistributions {
            modules("java.sql")

            targetFormats(TargetFormat.Msi, TargetFormat.Exe)

            packageName = appPackageName
            packageVersion = libs.versions.lint.toolkit.get()

            description = "An Android geek toolbox for Windows 7+ platform."
            copyright = "© 2024 io.github.lumkit. All rights reserved."
            vendor = "io.github.lumkit"
            licenseFile.set(project.file("static/LICENSE.txt"))

            windows {
                msiPackageVersion = libs.versions.lint.toolkit.get()
                exePackageVersion = libs.versions.lint.toolkit.get()

                dirChooser = true
                shortcut = true
                menuGroup = "start-menu-group"
                upgradeUuid = "d2f0923e-39a2-4636-a8b6-bf41b982a1f6".uppercase()

                iconFile.set(project.file("static/mipmap/ic_lint_toolkit_logo.ico"))
            }
        }
    }
}
