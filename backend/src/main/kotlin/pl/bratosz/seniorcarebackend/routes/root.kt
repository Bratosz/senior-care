package pl.bratosz.seniorcarebackend.routes

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import pl.bratosz.seniorcarebackend.env.Dependencies
import pl.bratosz.seniorcarebackend.modules.user.web.userRoutes
import pl.bratosz.seniorcarebackend.modules.warehouse.warehouseRoutes


fun Application.routes(deps: Dependencies) = routing {
    userRoutes(deps.userService)
    warehouseRoutes(deps.warehouseService)
}

@Resource("/api") data object RootResource