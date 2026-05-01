package pl.bratosz.seniorcarebackend.modules.user.infra


import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Instant

object UserTable : UuidTable("users") {
    val firstName: Column<String> = varchar("first_name", 100)
    val lastName: Column<String> = varchar("last_name", 100)
    val email: Column<String> = varchar("email", 255)
    val hashedPassword: Column<String> = varchar("hashed_password", 255)
    val createdAt: Column<Instant> = timestamp("created_at")
}