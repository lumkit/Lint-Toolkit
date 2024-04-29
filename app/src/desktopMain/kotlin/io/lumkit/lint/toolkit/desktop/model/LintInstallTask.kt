package io.lumkit.lint.toolkit.desktop.model

abstract class LintInstallTask(
    val name: String,
    val url: String,
) {
    abstract suspend fun run(
        onProgressChanged: (Float) -> Unit,
    )
}
