package ru.bio4j.ng.commons.utils;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Amqs {
    private static final Logger LOG = LoggerFactory.getLogger(Amqs.class);

//    private Amqs() { /* hidden constructor */ }
//
//    public static Amqs getInstance() {
//        return SingletonContainer.INSTANCE;
//    }
//
//    private static class SingletonContainer {
//        public static final Amqs INSTANCE;
//
//        static {
//            INSTANCE = new Amqs();
//        }
//    }


    public static Connection createConnection(final String srvAddress, final String usrName, final String passwd, final String virtHost, final int port) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(usrName);
            factory.setPassword(passwd);
            factory.setVirtualHost(virtHost);
            factory.setHost(srvAddress);
            factory.setPort(port);
        return factory.newConnection();
    }

    public static void postMessage(final Connection conn, final String exchangeName, final String routingKey, final String messageBody) throws IOException, TimeoutException {
        if(!Strings.isNullOrEmpty(messageBody)) {
            byte[] messageBodyBytes = messageBody.getBytes();
            try(Channel channel = conn.createChannel()) {
                channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
            }
        }
    }


//    public static Consumer createConsumer(final Connection conn, final String exchangeName, final String queueName, final String routingKey) throws IOException, TimeoutException {
//        Channel channel = conn.createChannel();
//        final boolean durable = true;
//        final String exchangeType = "topic";
//        channel.exchangeDeclare(exchangeName, exchangeType, durable);
//        channel.queueDeclare(queueName, durable, false, false, null);
//        channel.queueBind(queueName, exchangeName, routingKey);
//        Consumer consumer = channel.;
//        channel.basicConsume(queueName, false, consumer);
//        return consumer;
//    }
//
//    public static QueueingConsumer.Delivery getNextMessage(QueueingConsumer consumer) throws InterruptedException {
//        return consumer.nextDelivery();
//    }

    public static String getMessage(final Connection conn, final String exchangeName, final String queueName, final String routingKey, boolean requeue) throws IOException, TimeoutException {
        try(Channel channel = conn.createChannel()) {
            final boolean autoAck = !requeue;
            final boolean durable = true;
            final String exchangeType = "topic";
            channel.exchangeDeclare(exchangeName, exchangeType, durable);
            channel.queueDeclare(queueName, durable, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
            GetResponse response = channel.basicGet(queueName, autoAck);
            if (response != null)
                return new String(response.getBody());
            return null;
        }
    }

}
