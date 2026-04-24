package pl.bratosz.seniorcarebackend.modules.warehouse

import kotlinx.serialization.Serializable

@JvmInline @Serializable value class WarehouseId(val id: Long)
@JvmInline @Serializable value class ProductId  (val id: Long)

@Serializable data class Warehouse(
    val id: WarehouseId,
    val name: String,
    val products: List<StoredProduct>,
//    val orders: List<Order>
)

@Serializable data class StoredProduct(
    val product: Product,
    val limit: Int,
    val quantity: Int,
    val defaultOrderQuantity: Int,
    val lastOrderQuantity: Int,)

@Serializable data class Product(
    val id: ProductId,
    val name: String,
    val storageUnit: StorageUnit,
//    val suppliers: List<OrderInfo>
)

data class OrderInfo(
    val id: Long,
    val product: Product,
    val suppliers: List<Supplier>,)

data class Supplier(
    val id: Long,
    val orderChannel: OrderChannel,
    val orderTemplate: OrderTemplate,
    val contactPerson: String,
    val contact: List<Contact>,
    val email: String,
    val phoneNumber: String,
    val onlineShop: String)

data class OrderChannel(
    val id: Long,
    val channelType: ContactChannel,
    val contactPerson: String,)

data class Contact(
    val id: Long,
    val channel: ContactChannel,
    val contact: String)

data class OrderTemplate(
    val id: Long,
    val greeting: String,
    val description: String,
    val additionalInfo: String)

enum class ContactChannel {
    EMAIL,
    SMS,
    PHONE,
    WEB
}

enum class StorageUnit {
    KG,
    LITER,
    PIECE,
    BOX,
    PALLET
}

data class NewProduct(val name: String, val storageUnit: StorageUnit)

data class NewStoredProduct(
    val productId: ProductId,
    val limit: Int,
    val quantity: Int,
    val defaultOrderQuantity: Int,
    val lastOrderQuantity: Int)
