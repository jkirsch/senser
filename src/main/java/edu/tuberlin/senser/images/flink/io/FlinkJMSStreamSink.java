package edu.tuberlin.senser.images.flink.io;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.windowing.StreamWindow;

import javax.jms.*;
import java.util.function.Consumer;

/**
 */
public class FlinkJMSStreamSink extends RichSinkFunction<StreamWindow<Tuple2<String, Integer>>> {

    private transient volatile boolean running;

    private MessageProducer producer;
    private Connection connection;
    private Queue destination;
    private TextMessage textMessage;

    private final String outputQueue;

    public FlinkJMSStreamSink(String outputQueue) {
        this.outputQueue = outputQueue;
    }

    private void init() throws JMSException {
        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        destination = session.createQueue(outputQueue);

        // Create a MessageConsumer from the Session to the Topic or Queue
        producer = session.createProducer(destination);

        textMessage = session.createTextMessage();
    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        running = true;
        init();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public void invoke(String string) throws Exception {
        textMessage.setText(string);
        producer.send(destination, textMessage);
    }

    @Override
    public void invoke(StreamWindow<Tuple2<String, Integer>> data) throws Exception {


        data.forEach(new Consumer<Tuple2<String, Integer>>() {
            @Override
            public void accept(Tuple2<String, Integer> stringIntegerTuple2) {
                try {
                    invoke(stringIntegerTuple2.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
