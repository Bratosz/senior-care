package pl.bratosz.seniorcarebackend.modules.user.domain

import pl.bratosz.seniorcarebackend.shared.Email
import pl.bratosz.seniorcarebackend.shared.Name

@JvmInline
value class UserId(val id: Long)

@JvmInline
value class HashedPassword(val hashedPassword: String)

data class User(
    val id: UserId = UserId(-1),
    val firstName: Name,
    val lastName: Name,
    val email: Email,
    val hashedPassword: HashedPassword,
)