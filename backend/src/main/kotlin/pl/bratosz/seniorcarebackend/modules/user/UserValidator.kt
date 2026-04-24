package pl.bratosz.seniorcarebackend.modules.user

import arrow.core.Either
import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.leftNel
import arrow.core.nonEmptyListOf
import arrow.core.right
import pl.bratosz.seniorcarebackend.modules.user.domain.HashedPassword
import pl.bratosz.seniorcarebackend.shared.Email
import pl.bratosz.seniorcarebackend.shared.error.ErrorInfo
import pl.bratosz.seniorcarebackend.shared.error.IncorrectInput
import pl.bratosz.seniorcarebackend.shared.Name

sealed interface InvalidField {
    val errors: NonEmptyList<ErrorInfo>
    val field: String
}

private const val BLANK_STRING = "BLANK_STRING"
private const val STRING_TOO_SHORT = "STRING_TOO_SHORT"
private const val STRING_TOO_LONG = "STRING_TOO_LONG"
private const val INVALID_EMAIL = "INVALID_EMAIL"

data class InvalidUsername(override val errors: NonEmptyList<ErrorInfo>): InvalidField {
    override val field = "username"
}

data class InvalidFirstNameOrLastName(override val errors: NonEmptyList<ErrorInfo>) : InvalidField {
    override val field: String = "firstName or lastName"
}

data class InvalidEmail(override val errors: NonEmptyList<ErrorInfo>) : InvalidField {
    override val field: String = "email"
}

data class InvalidPassword(override val errors: NonEmptyList<ErrorInfo>) : InvalidField {
    override val field: String = "password"
}

data class StringField(val value: String, val fieldName: String)

fun RegisterUser.validate(): Either<IncorrectInput, RegisterUser> =
    zipOrAccumulate(
        email.valid(),
        password.valid(),
        firstName.valid(),
        lastName.valid(),
        ::RegisterUser
    ).mapLeft(::IncorrectInput)

fun UpdateUser.validate(): Either<IncorrectInput, UpdateUser> =
    zipOrAccumulate(
        firstName.mapOrAccumulate(Name::valid),
        lastName.mapOrAccumulate(Name::valid),
        email.mapOrAccumulate(Email::valid),
        password.mapOrAccumulate(HashedPassword::valid),
    ) {  firstName, lastName, email, password ->
        UpdateUser(userId, firstName, lastName, email, password)
    }.mapLeft(::IncorrectInput)

fun <E, A, B> A?.mapOrAccumulate(f: (A) -> EitherNel<E, B>): EitherNel<E, B?> =
    this?.let(f) ?: null.right()


private const val MIN_PASSWORD_LENGTH = 8
private const val MAX_PASSWORD_LENGTH = 100
private const val MAX_EMAIL_LENGTH = 350
private const val MIN_USERNAME_LENGTH = 1
private const val MAX_USERNAME_LENGTH = 25

private fun Email.valid(): EitherNel<InvalidEmail, Email> {
    val input = StringField(email, "EMAIL")
    val trimmed = input.trim()
    return zipOrAccumulate(
        trimmed.notBlank(),
        trimmed.maxSize(MAX_EMAIL_LENGTH),
        trimmed.looksLikeEmail(),
    ) { a, _, _ -> Email(a.value) }
        .mapLeft(toInvalidField(::InvalidEmail))
}

private fun Name.valid(): EitherNel<InvalidFirstNameOrLastName, Name> {
    val input = StringField(name, "NAME")
    val trimmed = input.trim()
    return zipOrAccumulate(
        trimmed.notBlank(),
        trimmed.minSize(MIN_USERNAME_LENGTH),
        trimmed.maxSize(MAX_USERNAME_LENGTH),
    ) { a, _, _ -> Name(a.value) }
        .mapLeft(toInvalidField(::InvalidFirstNameOrLastName))
}

private fun HashedPassword.valid(): EitherNel<InvalidPassword, HashedPassword> {
    val input = StringField(hashedPassword, "PASSWORD")
    val trimmed = input.trim()
    return zipOrAccumulate(
        trimmed.notBlank(),
        trimmed.minSize(MIN_PASSWORD_LENGTH),
        trimmed.maxSize(MAX_PASSWORD_LENGTH),
    ) { a, _, _ -> HashedPassword(a.value) }
        .mapLeft(toInvalidField(::InvalidPassword))
}

private fun StringField.trim(): StringField = this.copy(value = value.trim())

private fun StringField.notBlank(): EitherNel<ErrorInfo, StringField> =
    if (!value.isBlank()) right()
    else ErrorInfo("$fieldName cannot be blank", BLANK_STRING).leftNel()

private fun StringField.minSize(size: Int): EitherNel<ErrorInfo, StringField> =
    if (value.length >= size) right()
    else ErrorInfo("$fieldName is too short (minimum is $size characters)", STRING_TOO_SHORT).leftNel()

private fun StringField.maxSize(size: Int): EitherNel<ErrorInfo, StringField> =
    if (value.length <= size) right()
    else ErrorInfo("$fieldName is too long (maximum is $size characters)", STRING_TOO_LONG).leftNel()

private val emailPattern = ".+@.+\\..+".toRegex()

private fun StringField.looksLikeEmail(): EitherNel<ErrorInfo, StringField> =
    if (emailPattern.matches(value)) right() else ErrorInfo("$fieldName: '$value' is invalid", INVALID_EMAIL).leftNel()

private fun <A : InvalidField> toInvalidField(
    transform: (NonEmptyList<ErrorInfo>) -> A
): (NonEmptyList<ErrorInfo>) -> NonEmptyList<A> = { nel -> nonEmptyListOf(transform(nel)) }
