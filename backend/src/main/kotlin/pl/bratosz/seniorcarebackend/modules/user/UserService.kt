package pl.bratosz.seniorcarebackend.modules.user

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.serialization.Serializable
import pl.bratosz.seniorcarebackend.modules.user.domain.HashedPassword
import pl.bratosz.seniorcarebackend.modules.user.domain.UserId
import pl.bratosz.seniorcarebackend.modules.user.domain.UserRepository
import pl.bratosz.seniorcarebackend.modules.user.web.UpdateUserRequest
import pl.bratosz.seniorcarebackend.shared.Email
import pl.bratosz.seniorcarebackend.shared.Name
import pl.bratosz.seniorcarebackend.shared.error.DomainError
import pl.bratosz.seniorcarebackend.shared.error.EmptyUpdate

data class UpdateUser(
    val userId: UserId,
    val firstName: Name?,
    val lastName: Name?,
    val email: Email?,
    val password: HashedPassword?
) {
    companion object {
        fun fromRequest(request: UpdateUserRequest): UpdateUser = UpdateUser(
            userId = UserId(request.id),
            firstName = request.firstName?.let { Name(it) },
            lastName = request.lastName?.let { Name(it) },
            email = request.email?.let { Email(it) },
            password = request.password?.let { HashedPassword(it) }
        )
    }
}

data class RegisterUser(val email: Email, val password: HashedPassword, val firstName: Name, val lastName: Name) {
    companion object {
        fun fromStrings(email: String, password: String, firstName: String, lastName: String): RegisterUser =
            RegisterUser(Email(email), HashedPassword(password), Name(firstName), Name(lastName))
    }
}

data class Login(val email: String, val password: String)

@Serializable
data class UserInfo(val id: Long, val email: String, val firstName: String, val lastName: String)

interface UserService {
    suspend fun getAll(): Either<DomainError, List<UserInfo>>
    suspend fun getUser(email: String): Either<DomainError, UserInfo>
    suspend fun register(input: RegisterUser): Either<DomainError, UserId>
    suspend fun update(input: UpdateUserRequest): Either<DomainError, Unit>
    //    suspend fun update(input: UpdateUser): Either<DomainError, UserInfo>
    //    suspend fun login(input: Login): Either<DomainError, Pair<UserId, UserInfo>>
    //    suspend fun login(input: Login): Either<DomainError, Pair<JwtToken, UserInfo>>
//    suspend fun getUser(userId: Long): Either<DomainError, UserInfo>
}

fun userService(repo: UserRepository): UserService = object : UserService {



    override suspend fun getUser(email: String): Either<DomainError, UserInfo> = either {
        val u = repo.getUserFromEmail(email).bind()
        UserInfo(u.id.id,u.email.email, u.firstName.name, u.lastName.name)
    }

    override suspend fun getAll(): Either<DomainError, List<UserInfo>> = either {
        repo.getAll()
            .bind()
            .map { UserInfo(it.id.id, it.email.email, it.firstName.name, it.lastName.name) }
    }

    override suspend fun register(input: RegisterUser): Either<DomainError, UserId> = either {
        val userData = input.validate().bind()
        repo.create(userData).bind()
    }

//    override suspend fun update(input: UpdateUser): Either<DomainError, UserInfo> = either {
//        val (userId, firstName, lastName, email, password) = input.validate().bind()
//        ensureAtLeastOneIsNotNull(firstName, lastName, email, password) {
//            EmptyUpdate("Cannot update user with $userId with only null values")
//        }
//        repo.update(userId, firstName, lastName, email, password)
//    }

    override suspend fun update(input: UpdateUserRequest): Either<DomainError, Unit> = either {
        val user = UpdateUser.fromRequest(input)
        val (userId, firstName, lastName, email, password) = user.validate().bind()
        val updatedFields = buildMap<EditableField, String> {
            user.firstName?.let { put(EditableField.FIRST_NAME, it.name) }
            user.lastName?.let { put(EditableField.LAST_NAME, it.name) }
            user.email?.let { put(EditableField.EMAIL, it.email) }
            user.password?.let { put(EditableField.PASSWORD, it.hashedPassword) }
        }

        ensure(updatedFields.isNotEmpty()) {
            EmptyUpdate("Cannot update user with ${input.id} with only null values")
        }

        repo.update(UserId(input.id), updatedFields)
    }
}

enum class EditableField (val fieldName: String) {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PASSWORD("password")
}