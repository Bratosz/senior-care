package pl.bratosz.seniorcarebackend.env

import java.lang.System.getenv
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/* ---------- Wartości domyślne (fallback do DEV) ---------- */
private const val PORT: Int             = 8080
private const val JDBC_URL: String      = "jdbc:postgresql://localhost:5432/warehouse"
private const val JDBC_USER: String     = "postgres"
private const val JDBC_PW: String       = "Admin1234$"
private const val JDBC_DRIVER: String   = "org.postgresql.Driver"
private const val JDBC_MAX_POOL: Int    = 10

private const val AUTH_SECRET: String   = "MySuperStrongSecret"
private const val AUTH_ISSUER: String   = "WarehouseIssuer"
private const val AUTH_DURATION_DAYS    = 30            // TTL tokenu JWT

/* ---------- Konfiguracja (ładuje z ENV lub bierze domyślne) ---------- */
data class Env(
    val dataSource: DataSource = DataSource(),
    val http: Http             = Http(),
    val auth: Auth             = Auth(),
    val isDev: Boolean         = getenv("ENVIRONMENT")?.equals("dev", ignoreCase = true) ?: true,
) {
    data class Http(
        val host: String = getenv("HOST")?.takeIf(String::isNotBlank) ?: "0.0.0.0",
        val port: Int    = getenv("SERVER_PORT")?.toIntOrNull()       ?: PORT,
//        val port: Int    =  PORT,
    )

    data class DataSource(
        val url: String      = getenv("POSTGRES_URL")      ?: JDBC_URL,
        val username: String = getenv("POSTGRES_USERNAME") ?: JDBC_USER,
        val password: String = getenv("POSTGRES_PASSWORD") ?: JDBC_PW,
        val driver: String   = JDBC_DRIVER,
        val maxPool: Int     = getenv("POSTGRES_MAX_POOL")?.toIntOrNull() ?: JDBC_MAX_POOL,
    )

    data class Auth(
        val secret: String     = getenv("JWT_SECRET")   ?: AUTH_SECRET,
        val issuer: String     = getenv("JWT_ISSUER")   ?: AUTH_ISSUER,
        val duration: Duration = getenv("JWT_DURATION")?.toIntOrNull()?.days
            ?: AUTH_DURATION_DAYS.days,
    )
}