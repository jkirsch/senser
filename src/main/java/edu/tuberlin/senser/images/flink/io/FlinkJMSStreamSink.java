package edu.tuberlin.senser.images.flink.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tuberlin.senser.images.domain.SimpleMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import javax.jms.*;
import java.io.StringWriter;

/**
 */
public class FlinkJMSStreamSink extends RichSinkFunction<Tuple2<String, Integer>> {

    private transient volatile boolean running;

    private MessageProducer producer;
    private Connection connection;
    private Queue destination;
    private TextMessage textMessage;

    private final String outputQueue;
    private final ObjectMapper mapper = new ObjectMapper();

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
        running = false;
        connection.close();
    }

    @Override
    public void invoke(Tuple2<String, Integer> value) throws Exception {

        SimpleMessage simpleMessage = new SimpleMessage(value.f0, value.f1);
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue(stringWriter, simpleMessage);
        textMessage.setText(stringWriter.toString());

        producer.send(destination, textMessage);
    }
}
