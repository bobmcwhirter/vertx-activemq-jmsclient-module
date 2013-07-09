package org.projectodd.vertx.activemq.jmsclient;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.platform.Verticle;

public class ActiveMQJMSClientModule extends Verticle {

    @Override
    public void start(final Future<Void> startedResult) {
        System.err.println( "deploy AMQ client module" );
        container.deployVerticle("org.projectodd.vertx.jmsclient.JMSClientVerticle", new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
                if (event.succeeded()) {
                    startedResult.setResult(null);
                } else {
                    startedResult.setFailure( new Exception( "unable to start JMS client" ) );
                }
            }
        });
    }

    @Override
    public void stop() {
    }

}