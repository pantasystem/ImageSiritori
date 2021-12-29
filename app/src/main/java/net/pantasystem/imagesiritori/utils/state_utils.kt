package net.pantasystem.imagesiritori.utils

sealed class AppState<out T>(val content: StateContent<T>) {
    class Fixed<out T>(content: StateContent<T>) : AppState<T>(content)
    class Loading<out T>(content: StateContent<T>) : AppState<T>(content)
    class Error<out T>(content: StateContent<T>, val throwable: Throwable) : AppState<T>(content)
}
sealed class StateContent<out T> {
    data class Exist<out T>(val rawContent: T) : StateContent<T>()
    class NotExist<out T> : StateContent<T>()
}