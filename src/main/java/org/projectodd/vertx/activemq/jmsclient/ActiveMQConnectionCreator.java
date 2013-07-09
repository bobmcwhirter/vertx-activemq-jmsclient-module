package org.projectodd.vertx.activemq.jmsclient;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.projectodd.vertx.jmsclient.ConnectionCreator;
import org.vertx.java.core.json.JsonObject;

public class ActiveMQConnectionCreator implements ConnectionCreator {

    @Override
    public Connection create(JsonObject body) throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory( body.getString("url" ) );
        return factory.createConnection();
    }

}
