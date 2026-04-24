package pl.bratosz.seniorcarebackend.env

import arrow.fx.coroutines.ResourceScope
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import pl.bratosz.seniorcarebackend.modules.user.domain.UserRepository
import pl.bratosz.seniorcarebackend.modules.user.UserService
import pl.bratosz.seniorcarebackend.modules.user.UserTable
import pl.bratosz.seniorcarebackend.modules.user.userPersistence
import pl.bratosz.seniorcarebackend.modules.user.domain.userRepository
import pl.bratosz.seniorcarebackend.modules.user.userService
import pl.bratosz.seniorcarebackend.modules.warehouse.*


class Dependencies(

    val productPersistence: ProductPersistence,

    val userService: UserService,
    val userRepository: UserRepository,


    val warehouseService: WarehouseService,
    val warehousePersistence: WarehousePersistence,

    //TO EDIT
    val database: Database,
)

suspend fun ResourceScope.dependencies(env: Env): Dependencies {
    val hikari: HikariDataSource = install(
        acquire = { createHikari(env.dataSource) },
        release = { resource, _ -> resource.close() }
    )
    val database = Database.connect(hikari)

    if (env.isDev) {
        transaction(database) {
            SchemaUtils.create(UserTable)

            SchemaUtils.create(WarehouseTable)
            SchemaUtils.create(ProductTable)
            SchemaUtils.create(StoredProductTable)

            //TO EDIT
        }
    } else {
        migrateDatabase(hikari)
    }

    val userPersistence = userPersistence(UserTable)
    val userRepository = userRepository(userPersistence)
    val userService = userService(userRepository)

    val warehousePersistence = warehousePersistence(WarehouseTable, ProductTable, StoredProductTable)
    val warehouseService = warehouseService(warehousePersistence)
    val productPersistence = productPersistence(ProductTable)

    //TO EDIT

    return Dependencies(
        userService             = userService,
        userRepository          = userRepository,

        warehouseService        = warehouseService,
        warehousePersistence    = warehousePersistence,

        productPersistence = productPersistence,

        //TO EDIT
        database                = database
    )
}

fun migrateDatabase(dataSource: HikariDataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .load()
    flyway.migrate()
}

private fun createHikari(ds: Env.DataSource): HikariDataSource =
    HikariDataSource(
        HikariConfig().apply {
        jdbcUrl            = ds.url
        driverClassName    = ds.driver
        username           = ds.username
        password           = ds.password
        maximumPoolSize    = ds.maxPool
        isAutoCommit       = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })