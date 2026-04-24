package pl.bratosz.seniorcarebackend.modules.user.web

import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route
import pl.bratosz.seniorcarebackend.modules.user.RegisterUser
import pl.bratosz.seniorcarebackend.modules.user.UserService
import pl.bratosz.seniorcarebackend.routes.RootResource
import pl.bratosz.seniorcarebackend.shared.Envelope
import pl.bratosz.seniorcarebackend.shared.ResponseMessage
import pl.bratosz.seniorcarebackend.shared.error.respond
import pl.bratosz.seniorcarebackend.shared.receiveEither



@Resource("/user")
data class UsersResource(val parent: RootResource = RootResource) {
    @Resource("/login")
    data class Login(val parent: UsersResource = UsersResource())
    @Resource("/info")
    data class Info(val parent: UsersResource = UsersResource())
}

fun Route.userRoutes(userService: UserService) {

    //POST /api/users
    post<UsersResource> {
        either {

            val (email, password, firstName, lastName) = call
                .receiveEither<NewUser>().bind()

            val userId = userService
                .register(RegisterUser.Companion.fromStrings(email, password, firstName, lastName)).bind()

            Envelope(userId.id)

        }.respond(HttpStatusCode.OK)
    }

    // GET /api/user/info
    get<UsersResource> {
        either {

            val users = userService.getAll().bind()

            Envelope(users)

        }.respond(HttpStatusCode.OK)
    }

    //PUT /api/user
    put<UsersResource> {
        either {

            val updateUserRequest = call.receiveEither<UpdateUserRequest>().bind()

            val result = userService.update(updateUserRequest).bind()

            Envelope(ResponseMessage("udałosie"))
        }.respond(HttpStatusCode.OK)
    }
}