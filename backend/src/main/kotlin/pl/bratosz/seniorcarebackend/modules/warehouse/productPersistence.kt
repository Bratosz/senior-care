package pl.bratosz.seniorcarebackend.modules.warehouse

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import pl.bratosz.seniorcarebackend.shared.error.*
import pl.bratosz.seniorcarebackend.shared.runDb

object ProductTable : LongIdTable() {
    val name: Column<String> = varchar("name", 255)
    val storageUnit: Column<StorageUnit> = enumerationByName("unit", 32, StorageUnit::class)
}

interface ProductPersistence {
    fun getAll(): Either<PersistenceError, List<Product>>
    fun getById(id: ProductId): Either<PersistenceError, Product>
    fun insert(product: NewProduct): Either<PersistenceError, ProductId>
    fun update(id: ProductId, product: NewProduct): Either<PersistenceError, Unit>
    fun delete(id: ProductId): Either<PersistenceError, Unit>
}

fun productPersistence(productTable: ProductTable) = object : ProductPersistence {

    override fun getAll(): Either<PersistenceError, List<Product>> =
        runDb {

            productTable.selectAll().map { it.toProduct() }

        }.mapLeft(::RetrievalError)

    override fun getById(id: ProductId): Either<PersistenceError, Product> = either {
        val p = runDb {

            productTable.select(productTable.id eq id.id)
                .firstOrNull()
                ?.toProduct()

        }.mapLeft(::RetrievalError).bind()

        ensureNotNull(p) { ObjectNotFound("product id=$id", OBJECT_NOT_FOUND) }
    }

    override fun insert(product: NewProduct): Either<PersistenceError, ProductId> = either {
        runDb {

            val id = productTable.insertAndGetId {
                it[name]        = product.name
                it[storageUnit] = product.storageUnit
            }
            ProductId(id.value)

        }.mapLeft(::InsertionError).bind()
    }

    override fun update(id: ProductId, product: NewProduct): Either<PersistenceError, Unit> = either {
        val storageUnit = runDb {

            productTable.update({ productTable.id eq id.id }) {
                it[name]        = product.name
                it[storageUnit] = product.storageUnit
            }

        }.mapLeft(::InsertionError).bind()

        ensureNotNull(storageUnit) { ObjectNotFound("product id=$id", OBJECT_NOT_FOUND) }
    }

    override fun delete(id: ProductId): Either<PersistenceError, Unit> = either {
        val affected = runDb {

            productTable.deleteWhere { productTable.id eq id.id }

        }.mapLeft(::RetrievalError).bind()

        ensure(affected > 0) {
            ObjectNotFound("product not found, id=$id", OBJECT_NOT_FOUND)
        }
    }
}

fun ResultRow.toProduct(): Product = Product(
    id      = ProductId(this[ProductTable.id].value),
    name    = this[ProductTable.name],
    storageUnit    = this[ProductTable.storageUnit],
)