package org.projectodd.vertx.activemq.integration;

import org.junit.Test;
import org.projectodd.vertx.jmsclient.JMSBridgeVerticle;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class JMSBridgeVerticleTest extends TestVerticle {

    @Override
    public void start() {
        container.deployModule("org.projectodd~vertx-activemq-module~0.1.0-SNAPSHOT", new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> brokerStartResult) {
                initialize();
                VertxAssert.assertTrue(brokerStartResult.succeeded());
                startTests();
            }
        });

    }

    @Test
    public void testBidirectionalBridgeOfQueueWithJsonContentType() throws InterruptedException {
        vertx.eventBus().registerLocalHandler("queues.foo.inbound", new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                JsonObject body = event.body();
                VertxAssert.assertEquals("howdy", body.getString("payload"));
                event.reply(true);
                VertxAssert.testComplete();
            }
        });

        JsonObject bridgeConfig = new JsonObject()
                .putString("inbound_address", "queues.foo.inbound")
                .putString("outbound_address", "queues.foo.outbound")
                .putString("queue", "/queues/FOO");

        container.deployVerticle(JMSBridgeVerticle.class.getName(), bridgeConfig, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
                System.err.println(" bridge deployed: " + event.succeeded());
                vertx.eventBus().send("queues.foo.outbound", new JsonObject().putString("payload", "howdy"));
            }
        });
    }

}
