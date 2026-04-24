package pl.bratosz.seniorcarebackend.shared

fun <T> ensureAtLeastOneIsNotNull(vararg values: T?, onError: () -> Nothing) {
    if (values.all { it == null }) {
        onError()
    }
}