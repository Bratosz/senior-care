package pl.bratosz.seniorcarebackend.modules.warehouse

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import pl.bratosz.seniorcarebackend.shared.error.*
import pl.bratosz.seniorcarebackend.shared.runDb

object WarehouseTable : LongIdTable("warehouse") {
    val name: Column<String> = varchar("name", 255)
}

object StoredProductTable : Table("stored_product") {
    val warehouseId = reference("warehouse_id", WarehouseTable, onDelete = CASCADE)
    val productId = reference("product_id", ProductTable, onDelete = CASCADE)
    val limit = integer("limit_")
    val quantity = integer("quantity")
    val defaultOrderQuantity = integer("default_quantity")
    val lastOrderQuantity = integer("last_order_quantity")
    override val primaryKey = PrimaryKey(warehouseId, productId, name = "primary_key_stored_product")
}

interface WarehousePersistence {
    fun getAll(): Either<PersistenceError, List<Warehouse>>
    fun getById(id: WarehouseId): Either<PersistenceError, Warehouse>
    fun insert(warehouse: NewWarehouse): Either<PersistenceError, WarehouseId>
    fun update(id: WarehouseId, warehouse: NewWarehouse): Either<PersistenceError, Unit>
    fun delete(id: WarehouseId): Either<PersistenceError, Unit>
    fun upsertStoredProduct(warehouseId: WarehouseId, newData: NewStoredProduct): Either<PersistenceError, Unit>
    fun removeProduct(warehouseId: WarehouseId, productId: ProductId): Either<PersistenceError, Unit>
}

fun warehousePersistence(
    warehouseTable: WarehouseTable,
    productTable: ProductTable,
    storedProductTable: StoredProductTable
) = object : WarehousePersistence {

    override fun getAll(): Either<PersistenceError, List<Warehouse>> =
        runDb {

            val selected = (warehouseTable leftJoin storedProductTable leftJoin productTable).selectAll()
            val warehouses = selected.toWarehouses()
            warehouses

        }.mapLeft(::RetrievalError)



    override fun getById(id: WarehouseId): Either<PersistenceError, Warehouse> = either {
        val warehouse = runDb {

            (warehouseTable leftJoin storedProductTable leftJoin productTable)
                .selectAll()
                .adjustWhere { warehouseTable.id eq id.id }
//                .select(warehouseTable.id eq id.id)
                .toWarehouses()
                .firstOrNull()

        }.mapLeft(::RetrievalError).bind()

        ensureNotNull(warehouse) { ObjectNotFound("warehouse by id not found, id=$id", OBJECT_NOT_FOUND) }
    }

    private fun Query.toWarehouses(): List<Warehouse> = this
        .groupBy { it[warehouseTable.id] }
        .map { (whId, rows) ->
            val whName = rows.first()[warehouseTable.name]
            val stored = rows
                .filter { it[storedProductTable.warehouseId] == whId }
                .map { row ->
                val p = row.toProduct()
                row.toStoredProduct(p)
            }
            Warehouse(id = WarehouseId(whId.value), name = whName, products = stored)
        }

    override fun insert(warehouse: NewWarehouse): Either<PersistenceError, WarehouseId> = either {
        runDb {

            val id = warehouseTable.insertAndGetId { it[name] = warehouse.name }
            WarehouseId(id.value)

        }.mapLeft(::InsertionError).bind()
    }

    override fun update(id: WarehouseId, warehouse: NewWarehouse): Either<PersistenceError, Unit> = either {
        val affected = runDb {

            warehouseTable.update({ warehouseTable.id eq id.id }) {
                it[name] = warehouse.name
            }

        }.mapLeft(::InsertionError).bind()

        ensure(affected > 0) { ObjectNotFound("warehouse not found, id=$id", OBJECT_NOT_FOUND) }
    }

    override fun delete(id: WarehouseId): Either<PersistenceError, Unit> = either {
        val affected = runDb {

            warehouseTable.deleteWhere { warehouseTable.id eq id.id }

        }.mapLeft(::RetrievalError).bind()

        ensure(affected > 0) { ObjectNotFound("warehouse not found, id=$id", OBJECT_NOT_FOUND) }
    }

    override fun upsertStoredProduct(
        warehouseId: WarehouseId,
        newData: NewStoredProduct
    ): Either<PersistenceError, Unit> =
        runDb {

            storedProductTable.insertIgnore {
                it[storedProductTable.warehouseId]     = EntityID(warehouseId.id, warehouseTable)
                it[storedProductTable.productId]       = EntityID(newData.productId.id, productTable)
                it[limit]             = newData.limit
                it[quantity]          = newData.quantity
                it[defaultOrderQuantity]   = newData.defaultOrderQuantity
                it[lastOrderQuantity] = newData.lastOrderQuantity
            }

            storedProductTable.update({
                (storedProductTable.warehouseId eq warehouseId.id) and
                        (storedProductTable.productId   eq newData.productId.id)
            }) {
                it[limit]             = newData.limit
                it[quantity]          = newData.quantity
                it[defaultOrderQuantity]   = newData.defaultOrderQuantity
                it[lastOrderQuantity] = newData.lastOrderQuantity
            }

        }.mapLeft(::InsertionError).void()

    override fun removeProduct(warehouseId: WarehouseId, productId: ProductId): Either<PersistenceError, Unit> =
        runDb {

            storedProductTable.deleteWhere {
                (storedProductTable.warehouseId eq warehouseId.id) and
                        (storedProductTable.productId   eq productId.id)
            }

        }.mapLeft(::InsertionError).void()
}

private fun ResultRow.toStoredProduct(prod: Product) = StoredProduct(
    product                 = prod,
    limit                   = this[StoredProductTable.limit],
    quantity                = this[StoredProductTable.quantity],
    defaultOrderQuantity    = this[StoredProductTable.defaultOrderQuantity],
    lastOrderQuantity       = this[StoredProductTable.lastOrderQuantity],
)

private fun <E> Either<E, *>.void(): Either<E, Unit> = map { Unit }