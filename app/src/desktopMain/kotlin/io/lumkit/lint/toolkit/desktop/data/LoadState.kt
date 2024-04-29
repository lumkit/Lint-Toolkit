package io.lumkit.lint.toolkit.desktop.data

sealed class LoadState {
    class Loading(val message: String) : LoadState()
    class Success(val message: String) : LoadState()
    class Failure(val message: String) : LoadState()
}