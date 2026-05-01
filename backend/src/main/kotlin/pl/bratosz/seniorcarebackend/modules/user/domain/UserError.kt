package pl.bratosz.seniorcarebackend.modules.user.domain

import pl.bratosz.seniorcarebackend.shared.kernel.AppError

sealed interface UserError : AppError {

    data object NotFound : UserError {
        override val code: String = "USER_NOT_FOUND"
        override val message: String = "User not found"
    }

    data class InvalidEmail(val email: String) : UserError {
        override val code: String = "USER_INVALID_EMAIL"
        override val message: String = "Invalid email"
    }

    data class EmailAlreadyTaken(val email: String) : UserError {
        override val code: String = "USER_EMAIL_ALREADY_TAKEN"
        override val message: String = "Email already taken"
    }

    data object InvalidFirstName : UserError {
        override val code = "USER_INVALID_FIRST_NAME"
        override val message = "Invalid first name"
    }

    data object InvalidLastName : UserError {
        override val code = "USER_INVALID_LAST_NAME"
        override val message = "Invalid last name"
    }
}
