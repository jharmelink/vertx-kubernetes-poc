package net.harmelink.vertxkubernetespoc


import com.hazelcast.config.*
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.slf4j.LoggerFactory

private const val namespace = "vertx"
private const val serviceName = "vertx-kubernetes-poc-service"

private val logger = LoggerFactory.getLogger("main")!!

fun main(args: Array<String>) {
    when {
        args.contains("-l") -> deployHttpVerticle(Vertx.vertx())
        args.contains("-c") -> runClustered(HazelcastClusterManager())
        args.contains("-k") -> runKubernetes()
        else -> showHelp()
    }
}

private fun runKubernetes() {
    val discoveryStrategyConfig = DiscoveryStrategyConfig(HazelcastKubernetesDiscoveryStrategyFactory())
    val discoveryConfig = DiscoveryConfig()
    discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)

    val config = Config()
            .setNetworkConfig(NetworkConfig()
                    .setJoin(JoinConfig()
                            .setDiscoveryConfig(discoveryConfig)
                            .setMulticastConfig(MulticastConfig()
                                    .setEnabled(false))
                            .setKubernetesConfig(KubernetesConfig()
                                    .setEnabled(true)
                                    .setProperty("namespace", namespace)
                                    .setProperty("service-name", serviceName))))

    runClustered(HazelcastClusterManager(config))
}

private fun runClustered(clusterManager: HazelcastClusterManager) {
    val options = VertxOptions().setClusterManager(clusterManager)

    Vertx.clusteredVertx(options) { cluster ->
        if (cluster.succeeded()) {
            val vertx = cluster.result()
            val eventBus = vertx.eventBus()

            logger.info("Clustered event bus initiated: $eventBus")

            deployHttpVerticle(vertx)
        }
    }
}

private fun deployHttpVerticle(vertx: Vertx) {
    vertx.deployVerticle(HttpVerticle()) { result ->
        if (result.succeeded()) {
            logger.info("Deployment id is: " + result.result())
        } else {
            logger.error("Deployment failed!")
        }
    }
}

private fun showHelp() {
    System.out.println("Use one of the following commandline arguments:")
    System.out.println("-l to run locally for development purposes")
    System.out.println("-c to run in cluster mode")
    System.out.println("-k to run in cluster mode on kubernetes")
}
