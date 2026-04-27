package pl.bratosz.seniorcarebackend.shared.kernel

interface AppError {
    val code: String
    val message: String
}