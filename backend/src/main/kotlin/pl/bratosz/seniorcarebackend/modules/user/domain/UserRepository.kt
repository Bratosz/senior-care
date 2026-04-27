package pl.bratosz.seniorcarebackend.modules.user.domain

import arrow.core.Either
import pl.bratosz.seniorcarebackend.modules.user.EditableField
import pl.bratosz.seniorcarebackend.modules.user.RegisterUser
import pl.bratosz.seniorcarebackend.modules.user.UserPersistence
import pl.bratosz.seniorcarebackend.shared.error.PersistenceError

interface UserRepository {

    suspend fun save(user: User): User
    suspend fun update(user: User): User
    suspend fun delete(id: UserId): Boolean

    suspend fun findByEmail(email: UserEmail): User?
    suspend fun findAll(): List<User>

    suspend fun existsByEmail(email: UserEmail): Boolean
}