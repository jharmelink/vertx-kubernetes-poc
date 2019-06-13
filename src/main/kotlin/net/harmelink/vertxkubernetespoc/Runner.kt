package net.harmelink.vertxkubernetespoc


import com.hazelcast.config.*
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.slf4j.LoggerFactory

private const val dns = "kubernetes.default.svc.cluster.local"

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
    discoveryStrategyConfig.addProperty("service-dns", dns)

    val discoveryConfig = DiscoveryConfig()
    discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)

    val multicastConfig = MulticastConfig()
            .setEnabled(false)
    val tcpIpConfig = TcpIpConfig()
            .setEnabled(false)
    val joinConfig = JoinConfig()
            .setMulticastConfig(multicastConfig)
            .setTcpIpConfig(tcpIpConfig)
            .setDiscoveryConfig(discoveryConfig)
    val networkConfig = NetworkConfig()
            .setJoin(joinConfig)
    val config = Config()
            .setProperty("hazelcast.discovery.enabled", "true")
            .setNetworkConfig(networkConfig)

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
