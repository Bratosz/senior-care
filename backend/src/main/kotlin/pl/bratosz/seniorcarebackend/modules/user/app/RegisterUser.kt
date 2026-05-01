package pl.bratosz.seniorcarebackend.modules.user.app

import arrow.core.Either
import pl.bratosz.seniorcarebackend.modules.user.domain.User
import pl.bratosz.seniorcarebackend.modules.user.domain.UserRepository
import pl.bratosz.seniorcarebackend.shared.kernel.ClockProvider

data class RegisterUserCommand(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)


context(repo: UserRepository, clock: ClockProvider)
suspend fun registerUser(command: RegisterUserCommand): Either<UserError, User> =
    either {
        val email = UserEmail.create(command.email).bind()

        ensure(!repo.existsByEmail(email)) {
            UserError.EmailAlreadyTaken(email.value)
        }

        val hashedPassword = UserHashedPassword(command.password)

        val user = User.create(
            email = email,
            firstName = command.firstName,
            lastName = command.lastName,
            hashedPassword = hashedPassword,
            createdAt = clock.now()
        ).bind()

        repo.save(user)
    }