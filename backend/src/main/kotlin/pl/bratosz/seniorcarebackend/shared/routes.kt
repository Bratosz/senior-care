package pl.bratosz.seniorcarebackend.shared

import arrow.core.Either
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import kotlinx.serialization.Serializable
import pl.bratosz.seniorcarebackend.shared.error.DomainError
import pl.bratosz.seniorcarebackend.shared.error.IncorrectJson

suspend inline fun <reified A : Any> ApplicationCall.receiveEither(): Either<DomainError, A> =
    Either.catch { receive<A>() }
        .mapLeft { IncorrectJson(it) }

@Serializable data class Envelope <T : Any>(val data: T)
@Serializable data class IdBody (val id: Long)
@Serializable data class ResponseMessage(val message: String)
@Serializable data class NoContent(val message: String = "No content")