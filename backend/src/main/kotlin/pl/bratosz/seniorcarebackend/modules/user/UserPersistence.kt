package pl.bratosz.seniorcarebackend.modules.user

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import pl.bratosz.seniorcarebackend.shared.Email
import pl.bratosz.seniorcarebackend.shared.error.InsertionError
import pl.bratosz.seniorcarebackend.shared.Name
import pl.bratosz.seniorcarebackend.shared.error.OBJECT_NOT_FOUND
import pl.bratosz.seniorcarebackend.shared.error.ObjectNotFound
import pl.bratosz.seniorcarebackend.shared.error.PersistenceError
import pl.bratosz.seniorcarebackend.shared.error.RetrievalError
import pl.bratosz.seniorcarebackend.shared.runDb
import pl.bratosz.seniorcarebackend.modules.user.UserTable.email
import pl.bratosz.seniorcarebackend.modules.user.UserTable.firstName
import pl.bratosz.seniorcarebackend.modules.user.UserTable.hashedPassword
import pl.bratosz.seniorcarebackend.modules.user.UserTable.id
import pl.bratosz.seniorcarebackend.modules.user.UserTable.lastName
import pl.bratosz.seniorcarebackend.modules.user.domain.HashedPassword
import pl.bratosz.seniorcarebackend.modules.user.domain.User
import pl.bratosz.seniorcarebackend.modules.user.domain.UserId

object UserTable : LongIdTable() {
    val firstName: Column<String> = text("first_name")
    val lastName: Column<String> = text("last_name")
    val email: Column<String> = text("email")
    val hashedPassword: Column<String> = text("password")
}

interface UserPersistence {
    fun getAllUsers(): Either<PersistenceError, List<User>>

    fun getUserFromEmail(email: String): Either<PersistenceError, User>

    fun insertUser(user: RegisterUser): Either<PersistenceError, UserId>
    fun updateUser(userId: UserId, updatedFields: Map<EditableField, String>): Either<PersistenceError, Unit>
}

fun userPersistence(userTable: UserTable) = object : UserPersistence {

    override fun getAllUsers(): Either<PersistenceError, List<User>> =
        runDb {
            userTable.selectAll().map { it.toUser() }
        }
            .mapLeft { RetrievalError(it) }

    override fun getUserFromEmail(email: String): Either<PersistenceError, User> = either {
        val user = runDb {
            userTable.select(userTable.email eq email)
                .firstOrNull()
                ?.toUser()
        }
            .mapLeft(::RetrievalError)
            .bind()

        ensureNotNull(user) { ObjectNotFound("user by email not found, email=$email", OBJECT_NOT_FOUND) }
    }

    private fun ResultRow.toUser(): User =
        User(
            id = UserId(this[id].value),
            firstName = Name(this[firstName]),
            lastName = Name(this[lastName]),
            email = Email(this[email]),
            hashedPassword = HashedPassword(this[hashedPassword])
        )

    override fun insertUser(user: RegisterUser): Either<PersistenceError, UserId> = either {
        val userId = runDb {
            userTable.insertAndGetId {
                it[firstName] = user.firstName.name
                it[lastName] = user.lastName.name
                it[email] = user.email.email
                it[hashedPassword] = user.password.hashedPassword
            }
        }
            .mapLeft(::InsertionError)
            .bind()

        UserId(userId.value)
    }

    override fun updateUser(userId: UserId, updatedFields: Map<EditableField, String>): Either<PersistenceError, Unit> = either {
            runDb {
                userTable.update({ userTable.id eq userId.id }) {
                    updatedFields.forEach { (field, value) ->
                        when (field) {
                            EditableField.FIRST_NAME -> it[firstName] = value
                            EditableField.LAST_NAME -> it[lastName] = value
                            EditableField.EMAIL -> it[email] = value
                            EditableField.PASSWORD -> it[hashedPassword] = value
                        }
                    }
                }
            }.mapLeft { InsertionError(it) }
        }
}




