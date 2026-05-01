package pl.bratosz.seniorcarebackend.modules.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlin.time.Instant
import kotlin.uuid.Uuid

@JvmInline
value class UserId(val id: Uuid)

data class User(
    val id: UserId,
    val email: UserEmail,
    val firstName: String,
    val lastName: String,
    val hashedPassword: UserHashedPassword,
    val createdAt: Instant
) {
    companion object {
        fun create(
            email: UserEmail,
            firstName: String,
            lastName: String,
            hashedPassword: UserHashedPassword,
            createdAt: Instant
        ): Either<UserError, User> =
            either {
                val normalizedFirstName = firstName.trim()
                val normalizedLastName = lastName.trim()

                ensure(normalizedFirstName.length in 1..100) {
                    UserError.InvalidFirstName
                }

                ensure(normalizedLastName.length in 1..100) {
                    UserError.InvalidLastName
                }

                User(
                    id = UserId(Uuid),
                    email = email,
                    firstName = normalizedFirstName,
                    lastName = normalizedLastName,
                    hashedPassword = hashedPassword,
                    createdAt = createdAt
                )
            }
    }
}