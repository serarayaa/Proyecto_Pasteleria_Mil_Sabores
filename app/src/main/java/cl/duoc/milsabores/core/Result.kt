package cl.duoc.milsabores.core

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        val cause: Throwable? = null,
        val code: Int? = null // opcional (HTTP, business, etc.)
    ) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

/** Helpers funcionales */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    Result.Loading -> Result.Loading
}

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
    Result.Loading -> Result.Loading
}

fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data
fun <T> Result<T>.exceptionOrNull(): Throwable? = (this as? Result.Error)?.cause
