package net.harmelink.vertxkubernetespoc;

import com.hazelcast.config.*;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import net.harmelink.vertxkubernetespoc.api.PetsApiVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Runner {

    private static final Logger LOG = LoggerFactory.getLogger(Runner.class.getName());
    private static final String NAMESPACE = "vertx";
    private static final String SERVICE_NAME = "vertx-kubernetes-poc-service";

    public static void main(final String... args) {
        final List<String> argsList = Arrays.asList(args);

        if (argsList.contains("-l")) {
            deployHttpVerticle(Vertx.vertx());
        } else if (argsList.contains("-c")) {
            runClustered(new HazelcastClusterManager());
        } else if (argsList.contains("-k")) {
            runKubernetes();
        } else {
            showHelp();
        }
    }

    private static void runKubernetes() {
        final DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(
                new HazelcastKubernetesDiscoveryStrategyFactory());
        final DiscoveryConfig discoveryConfig = new DiscoveryConfig();
        discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig);

        final Config config = new Config()
                .setNetworkConfig(new NetworkConfig()
                        .setJoin(new JoinConfig()
                                .setDiscoveryConfig(discoveryConfig)
                                .setMulticastConfig(new MulticastConfig()
                                        .setEnabled(false))
                                .setKubernetesConfig(new KubernetesConfig()
                                        .setEnabled(true)
                                        .setProperty("namespace", NAMESPACE)
                                        .setProperty("service-name", SERVICE_NAME))));

        runClustered(new HazelcastClusterManager(config));
    }

    private static void runClustered(final HazelcastClusterManager clusterManager) {
        final VertxOptions options = new VertxOptions().setClusterManager(clusterManager);

        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                final Vertx vertx = cluster.result();
                final EventBus eventBus = vertx.eventBus();

                LOG.info("Clustered event bus initiated: {}", eventBus);

                deployHttpVerticle(vertx);
            }
        });
    }

    private static void deployHttpVerticle(final Vertx vertx) {
        vertx.deployVerticle(new PetsApiVerticle(), result -> {
            if (result.succeeded()) {
                LOG.info("Deployment id is: {}", result.result());
            } else {
                LOG.error("Deployment failed!");
            }
        });
    }

    private static void showHelp() {
        System.out.println("Use one of the following commandline arguments:");
        System.out.println("-l to run locally for development purposes");
        System.out.println("-c to run in cluster mode");
        System.out.println("-k to run in cluster mode on kubernetes");
    }
}
