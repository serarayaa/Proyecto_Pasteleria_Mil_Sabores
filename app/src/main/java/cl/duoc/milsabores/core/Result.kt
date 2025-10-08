package cl.duoc.milsabores.core

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error(val msg: String): Result<Nothing>()
    data object Loading: Result<Nothing>()
}
