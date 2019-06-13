package net.harmelink.vertxkubernetespoc

import io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED
import io.vertx.ext.web.RoutingContext

class LoginHandler : HttpHandler() {

    override fun handle(routingContext: RoutingContext) {
        logger.debug("Access denied")
        routingContext.response().setStatusCode(UNAUTHORIZED.code()).end()
    }
}
