package pl.bratosz.seniorcarebackend.modules.user.domain

import arrow.core.Either
import pl.bratosz.seniorcarebackend.modules.user.EditableField
import pl.bratosz.seniorcarebackend.modules.user.RegisterUser
import pl.bratosz.seniorcarebackend.modules.user.UserPersistence
import pl.bratosz.seniorcarebackend.shared.error.PersistenceError

interface UserRepository {
    fun create(user: RegisterUser): Either<PersistenceError, UserId>

    fun getAll(): Either<PersistenceError, List<User>>

    fun getUserFromEmail(email: String): Either<PersistenceError, User>
    fun update(userId: UserId, updatedFields: Map<EditableField, String>): Either<PersistenceError, Unit>
}

fun userRepository(userPersistence: UserPersistence) = object : UserRepository {

    override fun create(user: RegisterUser): Either<PersistenceError, UserId> =
        userPersistence.insertUser(user)

    override fun getAll(): Either<PersistenceError, List<User>> =
        userPersistence.getAllUsers()

    override fun getUserFromEmail(email: String): Either<PersistenceError, User> =
        userPersistence.getUserFromEmail(email)

    override fun update(userId: UserId, updatedFields: Map<EditableField, String>): Either<PersistenceError, Unit> =
        userPersistence.updateUser(userId, updatedFields)
}