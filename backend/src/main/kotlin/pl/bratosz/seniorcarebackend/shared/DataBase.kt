package pl.bratosz.seniorcarebackend.shared

import arrow.core.Either
import org.jetbrains.exposed.sql.transactions.transaction

inline fun <A> runDb(crossinline block: () -> A) =
    Either.catch { transaction { block() } }