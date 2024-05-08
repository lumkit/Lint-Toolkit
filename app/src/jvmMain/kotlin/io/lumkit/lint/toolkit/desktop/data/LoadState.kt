package io.lumkit.lint.toolkit.desktop.data

sealed class LoadState(val name: String) {
    class Loading(val message: String) : LoadState(message)
    class Success(val message: String) : LoadState(message)
    class Failure(val message: String) : LoadState(message)
}