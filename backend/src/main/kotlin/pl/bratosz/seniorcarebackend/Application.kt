package pl.bratosz.seniorcarebackend


import arrow.fx.coroutines.resourceScope
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.resources.Resources
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import pl.bratosz.seniorcarebackend.env.*
import pl.bratosz.seniorcarebackend.routes.routes


fun main(): Unit = runBlocking {
    println("SeniorCare backend is starting...")
    val env = Env()
    resourceScope {
        val dependencies = dependencies(env)
        embeddedServer(Netty, host = env.http.host, port = env.http.port) { app(dependencies) }
            .start(wait = true)
        awaitCancellation()
    }
}

fun Application.app(module: Dependencies) {
    install(Resources)
    configure()
    routes(module)
//    install(Cohort) { healthcheck("/readiness", module.healthCheck) }
}