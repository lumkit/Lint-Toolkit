import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    jvm("desktop")

    jvmToolchain(17)
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            val composeBom = project.dependencies.platform(libs.androidx.compose.bom)
            implementation(composeBom)

            implementation(libs.androidx.compose.materialWindow)

            implementation(libs.coil)
        }

        desktopMain.dependencies {
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
        }
    }
}

compose.desktop {
    application {
        val appPackageName: String by project

        mainClass = "io.lumkit.lint.toolkit.desktop.LintApplicationKt"
        jvmArgs += listOf("-Dfile.encoding=UTF-8", "-DappVersion=${libs.versions.lint.toolkit.get()}", "-DappPackageName=$appPackageName")

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
