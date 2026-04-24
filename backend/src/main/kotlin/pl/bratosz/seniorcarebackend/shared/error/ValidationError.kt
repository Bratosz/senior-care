package pl.bratosz.seniorcarebackend.shared.error

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import io.ktor.server.plugins.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import pl.bratosz.seniorcarebackend.modules.user.InvalidField

sealed interface ValidationError : DomainError

data class IncorrectInput(val errors: NonEmptyList<InvalidField>) : ValidationError {
    constructor(head: InvalidField) : this(nonEmptyListOf(head))
}

data class EmptyUpdate(val message: String, val errorInfo: ErrorInfo = from(message)): ValidationError {
    companion object {
        private fun from(message: String): ErrorInfo =
            ErrorInfo(
                message = message,
                code = EMPTY_UPDATE
            )
    }
}

@OptIn(ExperimentalSerializationApi::class)
data class IncorrectJson(
    val cause: Throwable,
    val errorInfo: ErrorInfo = from(cause)
) : ValidationError {

    companion object {
        private fun from(cause: Throwable): ErrorInfo =
            when (cause) {
                is MissingFieldException ->
                    ErrorInfo(
                        message = "Json is missing fields: ${cause.missingFields.joinToString()}",
                        code = JSON_MISSING_FIELD
                    )

                is BadRequestException ->
                    ErrorInfo(
                        message = cause.getDeepestCauseMessage() ?: "Bad request while parsing JSON",
                        code = JSON_BAD_REQUEST
                    )

                else ->
                    ErrorInfo(
                        message = cause.message ?: "Unknown error while parsing request",
                        code = JSON_PARSING_ERROR
                    )
            }
    }
}

fun Throwable.getDeepestCauseMessage(): String? {
    var currentCause: Throwable? = this
    while (currentCause?.cause != null) {
        currentCause = currentCause.cause
    }
    return currentCause?.message
}