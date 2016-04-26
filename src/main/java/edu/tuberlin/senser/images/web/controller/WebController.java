package edu.tuberlin.senser.images.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tuberlin.senser.images.domain.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * This is the dynamic controller which relays live output from the message queue "output"
 * via websocket to any listening web client.
 */
@RestController
@RequestMapping("/controller")
public class WebController {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    @JmsListener(destination = "output")
    public void receiveMessage(String jsonMessage) throws IOException {

        SimpleMessage message = mapper.readValue(jsonMessage, SimpleMessage.class);

        // tell everyone
        if(message.getCount() > 1) {
            LOG.info("Received > {} <", message);
            messagingTemplate.convertAndSend("/topic/stats",
                    message);
        }

    }

    @RequestMapping(value = "/trackinfo")
    public String[] keywords() {
        return new String[]{"Tracking Faces in Live Video"};
    }


}
