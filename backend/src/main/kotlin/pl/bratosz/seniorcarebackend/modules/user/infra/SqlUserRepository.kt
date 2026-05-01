package pl.bratosz.seniorcarebackend.modules.user.infra

import arrow.core.getOrElse
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import pl.bratosz.seniorcarebackend.modules.user.domain.User
import pl.bratosz.seniorcarebackend.modules.user.domain.UserEmail
import pl.bratosz.seniorcarebackend.modules.user.domain.UserHashedPassword
import pl.bratosz.seniorcarebackend.modules.user.domain.UserId
import pl.bratosz.seniorcarebackend.modules.user.domain.UserRepository
import pl.bratosz.seniorcarebackend.modules.user.infra.UserTable.email
import pl.bratosz.seniorcarebackend.modules.user.infra.UserTable.firstName
import pl.bratosz.seniorcarebackend.modules.user.infra.UserTable.hashedPassword
import pl.bratosz.seniorcarebackend.modules.user.infra.UserTable.lastName
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SqlUserRepository : UserRepository {

    override suspend fun save(user: User): User {
        UserTable.insert {
            it[id] = user.id.id
            it[email] = user.email.value
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[hashedPassword] = user.hashedPassword.value
            it[createdAt] = user.createdAt
        }

        return user
    }

    override suspend fun update(user: User): User {
        UserTable.update({ UserTable.id eq user.id.id }) {
            it[email] = user.email.value
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[hashedPassword] = user.hashedPassword.value
        }

        return user
    }

    override suspend fun delete(id: UserId): Boolean {
        return UserTable.deleteWhere {
            UserTable.id eq id.id
        } > 0
    }

    override suspend fun findByEmail(email: UserEmail): User? =
        UserTable
            .selectAll()
            .where { UserTable.email eq email.value }
            .singleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<User> =
        UserTable
            .selectAll()
            .map { it.toDomain() }

    override suspend fun existsByEmail(email: UserEmail): Boolean =
        UserTable
            .selectAll()
            .where { UserTable.email eq email.value }
            .limit(1)
            .any()
}

@OptIn(ExperimentalUuidApi::class)
private fun ResultRow.toDomain(): User =
    User(
        id = UserId(this[UserTable.id].value),
        email = UserEmail.create(this[UserTable.email]).getOrElse { error("Invalid email in database") },
        firstName = this[UserTable.firstName],
        lastName = this[UserTable.lastName],
        hashedPassword = UserHashedPassword(this[UserTable.hashedPassword]),
        createdAt = this[UserTable.createdAt]
    )
