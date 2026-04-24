package pl.bratosz.seniorcarebackend.modules.warehouse

import arrow.core.Either
import arrow.core.raise.either
import kotlinx.serialization.Serializable
import pl.bratosz.seniorcarebackend.shared.error.DomainError

@Serializable data class UpdateWarehouse(val id: WarehouseId, val name: String)
data class UpsertStoredProductInput(
    val warehouseId: WarehouseId,
    val newData: NewStoredProduct
)

interface WarehouseService {
    suspend fun create(input: NewWarehouse): Either<DomainError, Warehouse>
    suspend fun getWarehouse(id: WarehouseId): Either<DomainError, Warehouse>
    suspend fun getAll(): Either<DomainError, List<Warehouse>>
    suspend fun update(input: UpdateWarehouse): Either<DomainError, Unit>
    suspend fun delete(id: WarehouseId): Either<DomainError, WarehouseId>
    suspend fun upsertStored(input: UpsertStoredProductInput): Either<DomainError, Unit>
    suspend fun removeProduct(warehouseId: WarehouseId, productId: ProductId): Either<DomainError, Unit>
}

fun warehouseService(repo: WarehousePersistence): WarehouseService = object : WarehouseService {

    //READ
    override suspend fun getWarehouse(id: WarehouseId): Either<DomainError, Warehouse> = either {

        repo.getById(id).bind()

    }

    override suspend fun getAll(): Either<DomainError, List<Warehouse>> = either {

        repo.getAll().bind()

    }

    //CREATE
    override suspend fun create(input: NewWarehouse): Either<DomainError, Warehouse> = either {

        val id = repo.insert(NewWarehouse(input.name)).bind()

        repo.getById(id).bind()

    }

    //UPDATE
    override suspend fun update(input: UpdateWarehouse): Either<DomainError, Unit> = either {

        repo.update(input.id, NewWarehouse(input.name))

    }

    //DELETE
    override suspend fun delete(id: WarehouseId): Either<DomainError, WarehouseId> = either {

        repo.delete(id).bind()

        WarehouseId(id.id)

    }

    //UPSERT STORED PRODUCT
    override suspend fun upsertStored(input: UpsertStoredProductInput): Either<DomainError, Unit> = either {

        repo.upsertStoredProduct(input.warehouseId, input.newData)

        }

    //REMOVE PRODUCT
    override suspend fun removeProduct(
        warehouseId: WarehouseId,
        productId: ProductId
    ): Either<DomainError, Unit> = either {

        repo.removeProduct(warehouseId, productId).bind()

    }
}
