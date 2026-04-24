package pl.bratosz.seniorcarebackend.modules.user.web

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class UpdateUserRequest(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null
)