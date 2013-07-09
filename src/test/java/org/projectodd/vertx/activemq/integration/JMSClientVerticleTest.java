package org.projectodd.vertx.activemq.integration;

import org.junit.Test;
import org.projectodd.vertx.jmsclient.JMSClientVerticle;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class JMSClientVerticleTest extends TestVerticle {

    @Override
    public void start() {
        container.deployModule("org.projectodd~vertx-activemq-module~0.1.0-SNAPSHOT", new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> brokerStartResult) {
                initialize();
                VertxAssert.assertTrue( brokerStartResult.succeeded() );
                System.err.println( "==== " + System.getProperty( "vertx.modulename" ) );
                System.err.println( this.getClass().getClassLoader().getResource("mod.json" ) );
                container.deployModule(System.getProperty("vertx.modulename"), new AsyncResultHandler<String>() {
                    @Override
                    public void handle(AsyncResult<String> asyncResult) {
                        VertxAssert.assertTrue(asyncResult.succeeded());
                        VertxAssert.assertNotNull("deploymentID should not be null", asyncResult.result());
                        startTests();
                    }
                });
            }

        });

    }

    @Test
    public void testBroker() throws InterruptedException {
        vertx.eventBus().registerLocalHandler("my.queues.foo.handler", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> event) {
                String body = event.body();
                VertxAssert.assertEquals("howdy!", body);
                System.err.println("HANDLED: " + body);
                event.reply(true);
                VertxAssert.testComplete();
            }
        });

        vertx.eventBus().send(JMSClientVerticle.DEFAULT_ADDRESS, new JsonObject().putString("subscribe", "/queues/foo").putString("address", "my.queues.foo.handler"),
                new Handler<Message<Boolean>>() {
                    @Override
                    public void handle(Message<Boolean> event) {
                        vertx.eventBus().send(JMSClientVerticle.DEFAULT_ADDRESS, new JsonObject().putString("body", "howdy!").putString("send", "/queues/foo"));
                    }
                });

    }

}
