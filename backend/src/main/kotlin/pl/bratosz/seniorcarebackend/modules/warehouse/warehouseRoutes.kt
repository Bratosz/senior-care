package pl.bratosz.seniorcarebackend.modules.warehouse

import arrow.core.raise.either
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.resources.Resource
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import pl.bratosz.seniorcarebackend.routes.RootResource
import pl.bratosz.seniorcarebackend.shared.Envelope
import pl.bratosz.seniorcarebackend.shared.ResponseMessage
import pl.bratosz.seniorcarebackend.shared.error.respond
import pl.bratosz.seniorcarebackend.shared.receiveEither

@Serializable data class NewWarehouse(val name: String)

@Resource("/warehouse")
data class WarehouseResource(val parent: RootResource = RootResource) {
    @Resource("/{warehouseId}") data class WarehouseId(val parent: WarehouseResource = WarehouseResource(), val warehouseId: Long)
    @Resource("/orders") data class Orders(val parent: WarehouseResource = WarehouseResource())
    @Resource("/products") data class Products(val parent: WarehouseResource = WarehouseResource())
}

fun Route.warehouseRoutes(service: WarehouseService) {

    //DELETE /api/warehouse
    delete<WarehouseResource.WarehouseId> { pathVariable ->
        either {

            val warehouseId = service.delete(WarehouseId(pathVariable.warehouseId)).bind()

            Envelope(warehouseId)

        }.respond(OK)
    }

    //GET /api/warehouse
    get<WarehouseResource> {
        either {

            val warehouses = service.getAll().bind()

            Envelope(warehouses)

        }.respond(OK)
    }

    //POST /api/warehouse  
    post<WarehouseResource> {
        either {

            val newWarehouse = call.receiveEither<NewWarehouse>().bind()

            val createdWarehouse = service.create(newWarehouse).bind()


            Envelope(createdWarehouse)

        }.respond(Created)
    }

    //PUT /api/warehouse
    put<WarehouseResource> {
        either {

            val editWarehouse = call.receiveEither<UpdateWarehouse>().bind()

            service.update(editWarehouse).bind()

            Envelope(ResponseMessage("Warehouse updated successfully"))

        }.respond(OK)
    }
}