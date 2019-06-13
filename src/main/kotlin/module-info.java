module net.harmelink.vertxkubernetespoc {
    requires jdk.unsupported;
    requires java.base;
    requires java.se;
    requires java.transaction.xa;
    requires vertx.core;
    requires vertx.web;
    requires vertx.hazelcast;
    requires vertx.health.check;
    requires vertx.lang.kotlin;
    requires com.hazelcast.core;
    requires hazelcast.kubernetes;
    requires slf4j.api;
    requires kotlin.stdlib;
    requires io.netty.codec.http;

    exports net.harmelink.vertxkubernetespoc;
}
