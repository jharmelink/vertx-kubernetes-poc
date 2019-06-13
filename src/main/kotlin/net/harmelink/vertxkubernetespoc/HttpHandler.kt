package net.harmelink.vertxkubernetespoc

import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

abstract class HttpHandler : Handler<RoutingContext> {

    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

    internal companion object {
        internal val logger = LoggerFactory.getLogger(this::class.java)
    }
}
