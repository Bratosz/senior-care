package pl.bratosz.seniorcarebackend.env

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.maxAgeDuration
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import kotlin.time.Duration.Companion.days
import kotlinx.serialization.json.Json

//val kotlinXSerializersModule = SerializersModule {
//    contextual(UserWrapper::class) { UserWrapper.serializer(LoginUser.serializer()) }
//    polymorphic(Any::class) { subclass(LoginUser::class, LoginUser.serializer()) }
//}

fun Application.configure() {
    install(DefaultHeaders)
//    install(Resources) { serializersModule = kotlinXSerializersModule }
    install(ContentNegotiation) {
        json(
            Json {
//                serializersModule = kotlinXSerializersModule
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    install(io.ktor.server.plugins.cors.routing.CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)


        allowHost("localhost:3000", schemes = listOf("http"))

        allowCredentials = true
        allowNonSimpleContentTypes = true
        maxAgeDuration = 3.days
    }
}
