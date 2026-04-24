package pl.bratosz.seniorcarebackend.shared.error

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

sealed interface DomainError

@Serializable
data class ErrorResponse(val errors: List<ErrorInfo>)

@Serializable
data class ErrorInfo(val code: String, val message: String) {
    override fun toString(): String = "$code: $message"
}

context(RoutingContext)
suspend inline fun <reified A : Any> Either<DomainError, A>.respond(status: HttpStatusCode): Unit =
    when (this) {
        is Either.Left -> respond(value)
        is Either.Right -> if (status == HttpStatusCode.NoContent) {
            call.respond(status)
        } else {
            call.respond(status, value)
        }
    }

@Suppress("ComplexMethod")
suspend fun RoutingContext.respond(error: DomainError): Unit =
    when (error) {
        is IncorrectInput -> unprocessable(
            error.errors
                .map { field -> ErrorInfo(INCORRECT_INPUT, field.errors.joinToString(";") { it -> it.toString() }) })

        is IncorrectJson -> unprocessable(error.errorInfo)
        is EmptyUpdate -> unprocessable(error.errorInfo)
        is InsertionError -> unprocessable(error.errorInfo)
        is RetrievalError -> unprocessable(error.errorInfo)
        is ObjectNotFound -> unprocessable(error.errorInfo)
    }

private suspend inline fun RoutingContext.unprocessable(error: ErrorInfo): Unit =
    call.respond(
        HttpStatusCode.UnprocessableEntity,
        ErrorResponse(listOf(error)),
    )

private suspend inline fun RoutingContext.unprocessable(errors: List<ErrorInfo>): Unit =
    call.respond(
        HttpStatusCode.UnprocessableEntity,
        ErrorResponse(errors),
    )





