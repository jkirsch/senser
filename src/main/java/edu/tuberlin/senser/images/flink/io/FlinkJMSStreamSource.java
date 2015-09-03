package edu.tuberlin.senser.images.flink.io;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import javax.jms.*;

/**
 */
public class FlinkJMSStreamSource extends RichSourceFunction<String> {

    private transient volatile boolean running;

    private static SourceContext<String> source;
    private MessageConsumer consumer;
    private Connection connection;

    private void init() throws JMSException {
        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("input");

        // Create a MessageConsumer from the Session to the Topic or Queue
        consumer = session.createConsumer(destination);

    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        running = true;
        init();
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        // this source never completes

        while (running) {

            // Wait for a message
            Message message = consumer.receive(1000);

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                ctx.collect(text);
            } else {
            }

        }

    }

    @Override
    public void cancel() {

        running = false;
    }

    @Override
    public void close() throws Exception {
        super.close();
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Error while closing ActiveMQ connection ", e);
        }
    }

}
