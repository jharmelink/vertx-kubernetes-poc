package net.harmelink.vertxkubernetespoc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.healthchecks.HealthCheckHandler
import io.vertx.ext.healthchecks.HealthChecks
import io.vertx.ext.web.Router
import io.vertx.spi.cluster.hazelcast.ClusterHealthCheck

class HttpVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        val router = createRouter()

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config().getInteger("http.port", 8080)) { result ->
                    if (result.succeeded()) {
                        startFuture.complete()
                    } else {
                        startFuture.fail(result.cause())
                    }
                }
    }

    private fun createRouter() = Router.router(vertx).apply {
        val procedure = ClusterHealthCheck.createProcedure(vertx)
        val checks = HealthChecks.create(vertx).register("cluster-health", procedure)
        get("/health").handler(HealthCheckHandler.createWithHealthChecks(checks))

        get("/").handler(RootHandler())
        get("/login").handler(LoginHandler())
    }
}
