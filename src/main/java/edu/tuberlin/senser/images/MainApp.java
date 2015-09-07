package edu.tuberlin.senser.images;

import edu.tuberlin.senser.images.flink.StreamExample;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;

/**
 */
@SpringBootApplication
@EnableJms
public class MainApp {

    public static void main(String[] args) throws Exception {

        System.setProperty("hawtio.authenticationEnabled", "false");

        // Launch the application
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MainApp.class);

        final ConfigurableApplicationContext context = builder.headless(false).run(args);

        // start
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StreamExample.startFlinkStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


     //   JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

//        while (true) {
            //jmsTemplate.convertAndSend("input", "Hello");
            //jmsTemplate.convertAndSend("input", "Hello World");
        //}



    }
}

