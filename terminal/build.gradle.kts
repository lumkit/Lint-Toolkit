plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
}

group = "io.lumkit.lint.terminal"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.components.resources)
    implementation(compose.materialIconsExtended)
    implementation(compose.desktop.common)

    implementation(libs.lint.ui)

    implementation(project(":terminal:ui"))
    implementation(project(":terminal:core"))

    implementation("org.jetbrains.pty4j:pty4j:0.12.25")
    implementation("org.jetbrains:annotations:24.0.1")
}
