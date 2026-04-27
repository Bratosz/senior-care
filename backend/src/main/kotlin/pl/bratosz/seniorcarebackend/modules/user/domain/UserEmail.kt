package pl.bratosz.seniorcarebackend.modules.user.domain

@JvmInline
value class UserEmail private constructor(val value: String) {
    companion object {
        fun create(raw: String): Either<UserError.InvalidEmail, UserEmail> =
            either {
                val normalized = raw.trim().lowercase()

                ensure(normalized.contains("@")) {
                    UserError.InvalidEmail(raw)
                }

                ensure(normalized.length <= 255) {
                    UserError.InvalidEmail(raw)
                }

                UserEmail(normalized)
            }
    }
}