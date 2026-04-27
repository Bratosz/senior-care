package pl.bratosz.seniorcarebackend.modules.user.domain

import pl.bratosz.seniorcarebackend.shared.Email
import pl.bratosz.seniorcarebackend.shared.Name
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
                    id = UserId(UUID.randomUUID()),
                    email = email,
                    firstName = normalizedFirstName,
                    lastName = normalizedLastName,
                    hashedPassword = hashedPassword,
                    createdAt = createdAt
                )
            }
    }
}