import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(compose.runtimeSaveable)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.lint.ui)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.kodein)
        }
    }
}

compose.desktop {
    application {
        val appPackageName: String by project

        mainClass = "io.lumkit.lint.toolkit.desktop.LintApplicationKt"
        jvmArgs += listOf("-DappVersion=${libs.versions.lint.toolkit.get()}", "-DappPackageName=$appPackageName")

        javaHome = System.getenv("JDK_22")

//        buildTypes.release.proguard {
//            configurationFiles.from(project.file("compose-desktop.pro"))
//            obfuscate.set(true)
//        }

        nativeDistributions {
            modules("java.sql")

            targetFormats(TargetFormat.Msi, TargetFormat.Exe)

            packageName = appPackageName
            packageVersion = libs.versions.lint.toolkit.get()

            description = "An Android geek toolbox for Windows 7+ platform."
            copyright = "© 2024 io.github.lumkit. All rights reserved."
            vendor = "io.github.lumkit"
//            licenseFile.set(project.file("static/LICENSE.txt"))

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
