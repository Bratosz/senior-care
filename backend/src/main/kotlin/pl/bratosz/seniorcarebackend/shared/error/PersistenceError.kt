package pl.bratosz.seniorcarebackend.shared.error

sealed interface PersistenceError : DomainError

data class InsertionError(val e: Throwable,
                          val errorInfo: ErrorInfo = ErrorInfo(
                              message = e.message ?: "Insertion error",
                              code = INSERTION_ERROR)): PersistenceError

data class RetrievalError(val e: Throwable,
                          val errorInfo: ErrorInfo = ErrorInfo(
                              message = e.message ?: "Retrieval error",
                              code = RETRIEVAL_ERROR)): PersistenceError

data class ObjectNotFound(val message: String,
                          val code: String,
                          val errorInfo: ErrorInfo = ErrorInfo(
                              message = message,
                              code = RETRIEVAL_ERROR)): PersistenceError
