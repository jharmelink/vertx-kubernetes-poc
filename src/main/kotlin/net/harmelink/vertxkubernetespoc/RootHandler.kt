package net.harmelink.vertxkubernetespoc

import io.vertx.ext.web.RoutingContext

class RootHandler : HttpHandler() {

    override fun handle(routingContext: RoutingContext) {
        logger.debug("Welcome")
        routingContext.response().end("Welcome!")
    }
}
