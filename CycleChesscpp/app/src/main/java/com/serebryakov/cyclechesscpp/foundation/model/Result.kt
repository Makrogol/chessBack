package com.serebryakov.cyclechesscpp.foundation.model


import java.lang.Exception


//Состояния асинхронных операций

typealias Mapper<Input, Output> = (Input) -> Output

sealed class Result<T> {

    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> = when (this) {
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.exception)
        is SuccessResult -> {
            if (mapper == null) throw IllegalArgumentException("Mapper should not be null for success result")
            SuccessResult(mapper(this.data))
        }
        is EmptyResult -> {
            EmptyResult()
        }
    }

}

sealed class FinalResult<T> : Result<T>()

class SuccessResult<T>(
    val data: T
) : FinalResult<T>()

class ErrorResult<T>(
    val exception: Exception
) : FinalResult<T>()

class PendingResult<T> : Result<T>()

class EmptyResult<T> : FinalResult<T>()


fun <T> Result<T>?.takeSuccess(): T? {
    return if (this is SuccessResult)
        this.data
    else
        null
}