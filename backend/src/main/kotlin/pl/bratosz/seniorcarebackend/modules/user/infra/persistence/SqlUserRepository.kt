package pl.bratosz.seniorcarebackend.modules.user.infra.persistence

class SqlUserRepository : UserRepository {

    override suspend fun save(user: User): User {
        UserTable.insert {
            it[id] = user.id.value
            it[email] = user.email.value
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[hashedPassword] = user.hashedPassword.value
            it[createdAt] = user.createdAt
        }

        return user
    }

    override suspend fun update(user: User): User {
        UserTable.update({ UserTable.id eq user.id.value }) {
            it[email] = user.email.value
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[hashedPassword] = user.hashedPassword.value
        }

        return user
    }

    override suspend fun delete(id: UserId): Boolean {
        return UserTable.deleteWhere {
            UserTable.id eq id.value
        } > 0
    }

    override suspend fun findByEmail(email: UserEmail): User? =
        UserTable
            .selectAll()
            .where { UserTable.email eq email.value }
            .singleOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<User> =
        UserTable
            .selectAll()
            .map { it.toDomain() }

    override suspend fun existsByEmail(email: UserEmail): Boolean =
        UserTable
            .select(UserTable.id)
            .where { UserTable.email eq email.value }
            .limit(1)
            .any()
}