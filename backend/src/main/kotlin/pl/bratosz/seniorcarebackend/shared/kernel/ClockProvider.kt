package pl.bratosz.seniorcarebackend.shared.kernel

import kotlinx.datetime.Instant

interface ClockProvider {
    fun now(): Instant
}